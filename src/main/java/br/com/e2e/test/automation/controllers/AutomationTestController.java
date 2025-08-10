package br.com.e2e.test.automation.controllers;


import br.com.e2e.test.automation.runner.DockerTestRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tests/")
public class AutomationTestController {

    private final DockerTestRunner dockerTestRunner;

    public AutomationTestController(DockerTestRunner dockerTestRunner) {
        this.dockerTestRunner = dockerTestRunner;
    }

    @GetMapping
    @RequestMapping("/start")
    public ResponseEntity<Void> startTests() {
        dockerTestRunner.runE2ETestsInDocker();
        return ResponseEntity.ok().build();
    }
}
