package br.com.e2e.test.automation.controllers;


import br.com.e2e.test.automation.exceptions.SuiteNotFoundException;
import br.com.e2e.test.automation.services.AutomationTestService;
import br.com.e2e.test.automation.services.SuiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tests/")
public class AutomationTestController {

    private static final Logger logger = LoggerFactory.getLogger(AutomationTestController.class);

    private final AutomationTestService automationTestService;

    private final SuiteService suiteService;

    public AutomationTestController(AutomationTestService automationTestService, SuiteService suiteService) {
        this.automationTestService = automationTestService;
        this.suiteService = suiteService;
    }

  @GetMapping("/start/{id}")
  public ResponseEntity<Void> startTests(@PathVariable("id") Long suiteId) {
        logger.info("Starting runSuiteTestsById with suiteId={}", suiteId);
        suiteService.findById(suiteId)
                .ifPresentOrElse(
                        suite -> {
                            logger.info("Suite found: {}", suite.name());
                            automationTestService.runTestsAndCopyReport(suite);
                        },
                        () -> {
                            logger.warn("Suite with ID: {} not found", suiteId);
                            throw new SuiteNotFoundException(String.format("Suite with ID: %s not found", suiteId));
                        }
                );

        return ResponseEntity.ok().build();
    }
}
