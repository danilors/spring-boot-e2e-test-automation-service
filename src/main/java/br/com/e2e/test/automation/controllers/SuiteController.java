package br.com.e2e.test.automation.controllers;

import br.com.e2e.test.automation.SuiteDTO;
import br.com.e2e.test.automation.entity.Suite;
import br.com.e2e.test.automation.services.SuiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suites")
public class SuiteController {

    private final SuiteService suiteService;

    public SuiteController(SuiteService suiteService) {
        this.suiteService = suiteService;
    }

    @GetMapping
    public List<SuiteDTO> getAllSuites() {
        return suiteService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuiteDTO> getSuiteById(@PathVariable Long id) {
        return suiteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public SuiteDTO createSuite(@RequestBody Suite suite) {
        return suiteService.save(suite);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuiteDTO> updateSuite(@PathVariable Long id, @RequestBody Suite suite) {
        return suiteService.findById(id)
                .map(existing -> {
                    suite.setId(id);
                    return ResponseEntity.ok(suiteService.update(suite));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSuite(@PathVariable Long id) {
        return suiteService.findById(id)
                .map(existing -> {
                    suiteService.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.<Void>notFound().build());
    }
}