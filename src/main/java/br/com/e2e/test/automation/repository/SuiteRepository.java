package br.com.e2e.test.automation.repository;

import br.com.e2e.test.automation.entity.Suite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuiteRepository extends JpaRepository<Suite, Long> {
}