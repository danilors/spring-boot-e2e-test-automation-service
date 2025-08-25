package br.com.e2e.test.automation;

import br.com.e2e.test.automation.entity.Suite;

public record SuiteDTO(long id, String name, String repoUrl, String repoPath, String reportPath) {
    public Suite toEntity() {
        return Suite.builder()
                .id(changeZeroId(id))
                .name(name)
                .repoUrl(repoUrl)
                .repoPath(repoPath)
                .reportPath(reportPath)
                .build();
    }

    private Long changeZeroId(long id) {
        return id > 0L ? id : null;
    }

}
