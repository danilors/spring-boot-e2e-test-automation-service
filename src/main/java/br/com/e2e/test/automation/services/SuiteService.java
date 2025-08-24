package br.com.e2e.test.automation.services;

import br.com.e2e.test.automation.SuiteDTO;
import br.com.e2e.test.automation.entity.Suite;
import br.com.e2e.test.automation.repository.SuiteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SuiteService {

    private final SuiteRepository suiteRepository;

    public SuiteService(SuiteRepository suiteRepository) {
        this.suiteRepository = suiteRepository;
    }

    public SuiteDTO save(Suite suite) {
        Suite saved = suiteRepository.save(suite);
        return toDTO(saved);
    }

    public SuiteDTO update(Suite suite) {
        Suite updated = suiteRepository.save(suite);
        return toDTO(updated);
    }

    public Optional<SuiteDTO> findById(Long id) {
        return suiteRepository.findById(id).map(this::toDTO);
    }

    public void deleteById(Long id) {
        suiteRepository.deleteById(id);
    }

    public List<SuiteDTO> findAll() {
        return suiteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private SuiteDTO toDTO(Suite suite) {
        return new SuiteDTO(
                suite.getId(),
                suite.getName(),
                suite.getRepoUrl(),
                suite.getRepoPath()
        );
    }
}