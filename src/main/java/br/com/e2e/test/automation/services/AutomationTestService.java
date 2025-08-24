package br.com.e2e.test.automation.services;

import br.com.e2e.test.automation.SuiteDTO;
import br.com.e2e.test.automation.entity.Suite;
import br.com.e2e.test.automation.exceptions.SuiteNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.List;

@Service
public class AutomationTestService {

    private static final Logger logger = LoggerFactory.getLogger(AutomationTestService.class.getSimpleName());

    private final SuiteService suiteService;

    private final String destination = "";

    public AutomationTestService(SuiteService suiteService) {
        this.suiteService = suiteService;
    }

    public void runSuiteTestsById(long suiteId) {
        suiteService.findById(suiteId)
                .ifPresentOrElse(
                        this::runTestsAndCopyReport,
                        () -> {
                            throw new SuiteNotFoundException(String.format("Suite with ID: %s not found", suiteId));
                        }
                );
    }

    @Async
    public void runTestsAndCopyReport(SuiteDTO suite) {
        Path tempDir = null;
        try {
            Path reportDestination = Path.of(destination);
            tempDir = Files.createTempDirectory("cloned-repo-" + suite.name() + "-");
            runCommand(List.of("git", "clone", suite.repoUrl(), tempDir.toString()), Paths.get(suite.repoPath()));

            String imageName = "temp-test-image";
            runCommand(List.of("docker", "build", "-t", imageName, "."), tempDir);

            String containerReportPath = "/app/target/surefire-reports";
            runCommand(List.of("docker", "run", "--rm", "-v", reportDestination + ":" + containerReportPath, imageName), tempDir);

            deleteDirectory(tempDir);
        } catch (InterruptedException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void runCommand(List<String> command, Path workingDir) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workingDir.toFile());
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed: " + String.join(" ", command));
        }
    }

    private void deleteDirectory(Path path) throws IOException {
        if (!Files.exists(path)) return;
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, java.nio.file.attribute.BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}