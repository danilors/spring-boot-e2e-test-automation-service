package br.com.e2e.test.automation.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

@Service
public class DockerTestRunner {
    //add logger
    private static final Logger logger = LoggerFactory.getLogger(DockerTestRunner.class.getSimpleName());

   private final  String repoName = "spring-boot-chain-services";
    private final String repoUrl = "git@github.com:danilors/spring-boot-chain-services.git";

    @Async
    public void runTestsAndCopyReport(String destination) {
        Path tempDir = null;
        try {
            // 1. Create temp directory
            Path reportDestination = Path.of(destination);
            tempDir = Files.createTempDirectory("cloned-repo-" + repoName + "-");
            // 2. Clone the repository
            runCommand(List.of("git", "clone", repoUrl, tempDir.toString()), Paths.get("."));

            // 3. Detect project type
            boolean isMaven = Files.exists(tempDir.resolve("pom.xml"));
            boolean isGradle = Files.exists(tempDir.resolve("build.gradle"));
            boolean isNode = Files.exists(tempDir.resolve("package.json"));
            boolean isDockerfile = Files.exists(tempDir.resolve("Dockerfile"));

            // 4. Run tests
            if (isDockerfile) {
                // Build and run Docker image, mounting report destination
                String imageName = "temp-test-image";

                runCommand(List.of("docker", "build", "-t", imageName, "."), tempDir);
                // You may need to adjust the container report path based on the Dockerfile/project
                String containerReportPath = isMaven ? "/app/target/surefire-reports" :
                        isNode ? "/app/cypress/reports" : "/app/reports";

                runCommand(List.of("docker", "run", "--rm", "-v", reportDestination + ":" + containerReportPath, imageName), tempDir);
            } else if (isMaven) {
                runCommand(List.of("mvn", "clean", "package"), tempDir.resolve("profile"));
                copyDirectory(tempDir.resolve("profile/target"), reportDestination);
            } else if (isGradle) {
                runCommand(List.of("gradle", "build"), tempDir);
                copyDirectory(tempDir.resolve("build/reports/tests"), reportDestination);
            } else if (isNode) {
                runCommand(List.of("npm", "test"), tempDir);
                copyDirectory(tempDir.resolve("cypress/reports"), reportDestination);
            } else {
                throw new IllegalArgumentException("Unknown project type or no test reports found");
            }
            deleteDirectory(tempDir);
        } catch (InterruptedException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void runCommand(List<String> command, Path workingDir) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workingDir.toFile());
        pb.redirectErrorStream(true); // Combine stdout and stderr
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Or log
            }
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed: " + String.join(" ", command));
        }
    }

    private void copyDirectory(Path source, Path destination) throws IOException {
        if (!Files.isDirectory(source)) return;
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = destination.resolve(source.relativize(dir));
                if (!Files.isDirectory(targetDir)) {
                    Files.createDirectories(targetDir);
                }
                return FileVisitResult.CONTINUE;
            }

            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, destination.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void deleteDirectory(Path path) throws IOException {
        if (!Files.exists(path)) return;
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
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