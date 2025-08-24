package br.com.e2e.test.automation.entity;

import br.com.e2e.test.automation.SuiteDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "suite")
public class Suite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "repo_url", nullable = false)
    private String repoUrl;

    @Column(name = "repo_path", nullable = false)
    private String repoPath;

    @Column(name = "report_path", nullable = false)
    private String reportPath;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getRepoPath() {
        return repoPath;
    }

    public void setRepoPath(String repoPath) {
        this.repoPath = repoPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public Suite() {
    }

    private Suite(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.repoUrl = builder.repoUrl;
        this.repoPath = builder.repoPath;
        this.reportPath = builder.reportPath;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String repoUrl;
        private String repoPath;
        private String reportPath;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder repoUrl(String repoUrl) {
            this.repoUrl = repoUrl;
            return this;
        }

        public Builder repoPath(String repoPath) {
            this.repoPath = repoPath;
            return this;
        }

        public Builder reportPath(String reportPath) {
            this.reportPath = reportPath;
            return this;
        }

        public Suite build() {
            return new Suite(this);
        }
    }

    public SuiteDTO toDTO() {
        return new SuiteDTO(
                this.getId(),
                this.getName(),
                this.getRepoUrl(),
                this.getRepoPath(),
                this.getReportPath()
        );
    }

}