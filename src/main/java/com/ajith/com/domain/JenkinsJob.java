package com.ajith.com.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A JenkinsJob.
 */
@Entity
@Table(name = "jenkins_job")
public class JenkinsJob implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "job_name", nullable = false)
    private String jobName;

    @NotNull
    @Column(name = "build_number", nullable = false)
    private Integer buildNumber;

    @ManyToOne
    @JsonIgnoreProperties(value = { "jiraTicketToJobRels" }, allowSetters = true)
    private JiraTicket jiraTicket;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public JenkinsJob id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobName() {
        return this.jobName;
    }

    public JenkinsJob jobName(String jobName) {
        this.setJobName(jobName);
        return this;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Integer getBuildNumber() {
        return this.buildNumber;
    }

    public JenkinsJob buildNumber(Integer buildNumber) {
        this.setBuildNumber(buildNumber);
        return this;
    }

    public void setBuildNumber(Integer buildNumber) {
        this.buildNumber = buildNumber;
    }

    public JiraTicket getJiraTicket() {
        return this.jiraTicket;
    }

    public void setJiraTicket(JiraTicket jiraTicket) {
        this.jiraTicket = jiraTicket;
    }

    public JenkinsJob jiraTicket(JiraTicket jiraTicket) {
        this.setJiraTicket(jiraTicket);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JenkinsJob)) {
            return false;
        }
        return id != null && id.equals(((JenkinsJob) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JenkinsJob{" +
            "id=" + getId() +
            ", jobName='" + getJobName() + "'" +
            ", buildNumber=" + getBuildNumber() +
            "}";
    }
}
