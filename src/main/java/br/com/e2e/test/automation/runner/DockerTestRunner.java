package br.com.e2e.test.automation.runner;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class DockerTestRunner {

    private static final Logger log = LoggerFactory.getLogger(DockerTestRunner.class);

    @Value("${e2e.docker.image:maven:3.8.7-openjdk-17}")
    private String dockerImage;

    @Value("${e2e.git.repo.url:https://github.com/seu-usuario/seu-repositorio.git}")
    private String gitRepoUrl;

    @Value("${e2e.reports.container-path:/app/target/surefire-reports}")
    private String reportsContainerPath;

    @Value("${e2e.reports.host-path:./test-reports}")
    private String reportsHostPath;

    @Async
    public void runE2ETestsInDocker() {
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        try (ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();
             DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient)) {

            String command = String.format("git clone %s /app && cd /app && mvn clean test", gitRepoUrl);

            CreateContainerResponse container = dockerClient.createContainerCmd(dockerImage)
                    .withCmd("sh", "-c", command)
                    .exec();

            String containerId = container.getId();
            log.info("Container created with ID: {}", containerId);

            try {
                // Configura o streaming de logs antes de iniciar o contêiner
                ResultCallback.Adapter<Frame> logCallback = new ResultCallback.Adapter<>() {
                    @Override
                    public void onNext(Frame frame) {
                        log.info(new String(frame.getPayload()).trim());
                    }
                };

                dockerClient.logContainerCmd(containerId)
                        .withStdOut(true)
                        .withStdErr(true)
                        .withFollowStream(true)
                        .exec(logCallback);

                // Inicia o contêiner
                dockerClient.startContainerCmd(containerId).exec();

                // Aguarda a finalização
                int exitCode = dockerClient.waitContainerCmd(containerId).start().awaitStatusCode();
                log.info("Container finished with exit code: {}", exitCode);

                // After execution, copy the reports from the container to the host.
                // This is done regardless of the exit code, as even failed tests generate reports.
                log.info("Attempting to copy test reports from container {}...", containerId);
                copyReportsFromContainer(dockerClient, containerId);

            } finally {
                log.info("Removing container {}", containerId);
                dockerClient.removeContainerCmd(containerId).withForce(true).exec();
                log.info("Container {} removed.", containerId);
            }
        } catch (IOException e) {
            log.error("Failed to close Docker client resources.", e);
        }
    }

    /**
     * Copies a directory from the container, which is delivered as a TAR stream,
     * and extracts it to the configured host path.
     *
     * @param dockerClient The active DockerClient.
     * @param containerId  The ID of the container to copy from.
     */
    private void copyReportsFromContainer(DockerClient dockerClient, String containerId) {
        try (InputStream tarStream = dockerClient.copyArchiveFromContainerCmd(containerId, reportsContainerPath).exec()) {
            untar(tarStream, reportsHostPath);
            log.info("Successfully copied and extracted reports to host path: {}", new File(reportsHostPath).getAbsolutePath());
        } catch (NotFoundException e) {
            log.warn("Could not find report directory '{}' in container. Tests may have failed before reports were generated.", reportsContainerPath);
        } catch (IOException e) {
            log.error("Failed to copy or extract reports from container {}.", containerId, e);
        }
    }

    /**
     * Extracts a TAR input stream to a destination directory on the host.
     *
     * @param tarInputStream  The TAR stream to extract.
     * @param destinationPath The path on the host to extract files to.
     * @throws IOException if an I/O error occurs.
     */
    private void untar(InputStream tarInputStream, String destinationPath) throws IOException {
        File destDir = new File(destinationPath);
        destDir.mkdirs(); // Ensure the destination directory exists
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(tarInputStream)) {
            TarArchiveEntry entry;
            while ((entry = tarIn.getNextTarEntry()) != null) {
                File destFile = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    destFile.mkdirs();
                } else {
                    try (OutputStream out = new FileOutputStream(destFile)) {
                        tarIn.transferTo(out);
                    }
                }
            }
        }
    }
}
