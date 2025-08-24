package br.com.e2e.test.automation.services;

import br.com.e2e.test.automation.SuiteDTO;
import br.com.e2e.test.automation.entity.Suite;
import br.com.e2e.test.automation.exceptions.SuiteNotFoundException;
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

    public SuiteDTO save(SuiteDTO suite) {
        Suite saved = suiteRepository.save(suite.toEntity());
        return saved.toDTO();
    }

    public SuiteDTO update(long id, SuiteDTO suite) {
        return findById(id)
                .map(found -> save(suite))
                .orElseThrow(() -> new SuiteNotFoundException(String.format("Suite ID: %d not found", id)));
    }

    public Optional<SuiteDTO> findById(Long id) {
        return suiteRepository.findById(id).map(Suite::toDTO);
    }

    public void deleteById(Long id) {
        suiteRepository.findById(id)
                .ifPresentOrElse(
                        suite -> suiteRepository.deleteById(id),
                        () -> {
                            throw new SuiteNotFoundException(String.format("Suite ID: %d not found", id));
                        }
                );
    }

    public List<SuiteDTO> findAll() {
        return suiteRepository.findAll()
                .stream()
                .map(Suite::toDTO)
                .collect(Collectors.toList());
    }


}