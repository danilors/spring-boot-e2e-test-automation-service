package br.com.e2e.test.automation;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DockerTestRunner {

    private static final Logger log = LoggerFactory.getLogger(DockerTestRunner.class);

    @Value("${e2e.docker.image:maven:3.8.7-openjdk-17}")
    private String dockerImage;

    @Value("${e2e.git.repo.url:https://github.com/seu-usuario/seu-repositorio.git}")
    private String gitRepoUrl;

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

            } finally {
                log.info("Removing container {}", containerId);
                dockerClient.removeContainerCmd(containerId).withForce(true).exec();
                log.info("Container {} removed.", containerId);
            }
        } catch (IOException e) {
            log.error("Failed to close Docker client resources.", e);
        }
    }
}
