package br.com.e2e.test.automation.services;

import br.com.e2e.test.automation.SuiteDTO;
import br.com.e2e.test.automation.exceptions.SuiteNotFoundException;
import ch.qos.logback.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;

@Service
public class AutomationTestService {

    private static final Logger logger = LoggerFactory.getLogger(AutomationTestService.class.getSimpleName());

    private final SuiteService suiteService;

    private final String destination = System.getProperty("user.dir").concat("/report");

    public AutomationTestService(SuiteService suiteService) {
        this.suiteService = suiteService;
    }

    @Async
    public void runSuiteTestsById(long suiteId) {
        logger.info("Starting runSuiteTestsById with suiteId={}", suiteId);
        suiteService.findById(suiteId)
                .ifPresentOrElse(
                        suite -> {
                            logger.info("Suite found: {}", suite.name());
                            runTestsAndCopyReport(suite);
                        },
                        () -> {
                            logger.warn("Suite with ID: {} not found", suiteId);
                            throw new SuiteNotFoundException(String.format("Suite with ID: %s not found", suiteId));
                        }
                );
    }


    public void runTestsAndCopyReport(SuiteDTO suite) {
        logger.info("Running tests and copying report for suite: {}", suite.name());
        Path tempDir = null;
        try {

            tempDir = Files.createTempDirectory("cloned-repo-" + suite.name() + "-");
            logger.info("Created temp directory: {}", tempDir);

            var repoPath = StringUtil.isNullOrEmpty(suite.repoPath()) ? "." : suite.repoPath();
            logger.info("Cloning repo {} into {}", suite.repoUrl(), tempDir);
            runCommand(List.of("git", "clone", suite.repoUrl(), tempDir.toString()), Paths.get(repoPath));

            String imageName = String.format("temp-test-image-%s", suite.name());
            logger.info("Building Docker image: {}", imageName);
            runCommand(List.of("docker", "build", "-t", imageName, "."), tempDir);

            Path reportDestination = cleanOrCreateDestination(destination);
            Files.createDirectories(reportDestination);

            String containerName = String.format("temp-test-container-%s", suite.name()) + System.currentTimeMillis();

            // 1. Run the container (without --rm, with a name)
            runCommand(List.of("docker", "run", "--name", containerName, imageName), tempDir);

            // 2. Copy the report from the container to the host
            runCommand(List.of("docker", "cp", containerName + ":/app/" + suite.reportPath(), reportDestination.toString()), tempDir);

            // 3. Remove the container
            runCommand(List.of("docker", "rm", containerName), tempDir);
            logger.info("Test execution and report copy completed for suite: {}", suite.name());
        } catch (InterruptedException | IOException e) {
            logger.error("Error during test execution for suite {}: {}", suite.name(), e.getMessage(), e);
        }
    }

    private Path cleanOrCreateDestination(String destination) throws IOException {
        Path reportDestination = Path.of(destination);
        if (Files.exists(reportDestination)) {
            // Clean directory
            try (var paths = Files.walk(reportDestination)) {
                paths.sorted(Comparator.reverseOrder())
                        .filter(path -> !path.equals(reportDestination))
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to clean report directory", e);
                            }
                        });
            }
            return reportDestination;
        } else {
            return Files.createDirectories(reportDestination);
        }
    }

    private void runCommand(List<String> command, Path workingDir) throws IOException, InterruptedException {
        logger.info("Executing command: {} in directory: {}", String.join(" ", command), workingDir);
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
        logger.info("Command exited with code: {}", exitCode);
        if (exitCode != 0) {
            logger.error("Command failed: {}", String.join(" ", command));
            throw new RuntimeException("Command failed: " + String.join(" ", command));
        }
    }

    private void deleteDirectory(Path path) throws IOException {
        if (!Files.exists(path)) {
            logger.info("Directory does not exist: {}", path);
            return;
        }
        logger.info("Deleting directory: {}", path);
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, java.nio.file.attribute.BasicFileAttributes attrs) throws IOException {
                logger.debug("Deleting file: {}", file);
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                logger.debug("Deleting directory: {}", dir);
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}