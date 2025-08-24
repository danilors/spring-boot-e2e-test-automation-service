package br.com.e2e.test.automation;

import br.com.e2e.test.automation.entity.Suite;

public record SuiteDTO(long id, String name, String repoUrl, String repoPath) {
    public Suite toEntity() {
        return Suite.builder()
                .id(this.id)
                .name(this.name)
                .repoUrl(this.repoUrl)
                .repoPath(this.repoPath)
                .build();
    }

    public void setId(Long id) {
    }
}
