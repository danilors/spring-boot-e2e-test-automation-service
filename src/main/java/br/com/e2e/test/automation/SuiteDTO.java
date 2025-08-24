package br.com.e2e.test.automation;

import br.com.e2e.test.automation.entity.Suite;

public record SuiteDTO(long id, String name, String repoUrl, String repoPath) {
    public Suite toEntity() {
        return Suite.builder()
                .id(changeZeroId(id))
                .name(name)
                .repoUrl(repoUrl)
                .repoPath(repoPath)
                .build();
    }

    private Long changeZeroId(long id) {
        return id > 0L ? id : null;
    }

    public void setId(Long id) {
    }
}
