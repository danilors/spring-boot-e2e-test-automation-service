package br.com.e2e.test.automation.controllers;


import br.com.e2e.test.automation.services.AutomationTestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tests/")
public class AutomationTestController {

    private final AutomationTestService automationTestService;

    public AutomationTestController(AutomationTestService automationTestService) {
        this.automationTestService = automationTestService;
    }

    @GetMapping
    @RequestMapping("/start/{id}")
    public ResponseEntity<Void> startTests(@PathVariable Long id) {
        automationTestService.runSuiteTestsById(id);
        return ResponseEntity.ok().build();
    }
}
