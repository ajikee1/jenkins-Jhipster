package com.ajith.com.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A JiraTicket.
 */
@Entity
@Table(name = "jira_ticket")
public class JiraTicket implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "jira_ticket_name", nullable = false)
    private String jiraTicketName;

    @OneToMany(mappedBy = "jiraTicket")
    @JsonIgnoreProperties(value = { "jiraTicket" }, allowSetters = true)
    private Set<JenkinsJob> jiraTicketToJobRels = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public JiraTicket id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJiraTicketName() {
        return this.jiraTicketName;
    }

    public JiraTicket jiraTicketName(String jiraTicketName) {
        this.setJiraTicketName(jiraTicketName);
        return this;
    }

    public void setJiraTicketName(String jiraTicketName) {
        this.jiraTicketName = jiraTicketName;
    }

    public Set<JenkinsJob> getJiraTicketToJobRels() {
        return this.jiraTicketToJobRels;
    }

    public void setJiraTicketToJobRels(Set<JenkinsJob> jenkinsJobs) {
        if (this.jiraTicketToJobRels != null) {
            this.jiraTicketToJobRels.forEach(i -> i.setJiraTicket(null));
        }
        if (jenkinsJobs != null) {
            jenkinsJobs.forEach(i -> i.setJiraTicket(this));
        }
        this.jiraTicketToJobRels = jenkinsJobs;
    }

    public JiraTicket jiraTicketToJobRels(Set<JenkinsJob> jenkinsJobs) {
        this.setJiraTicketToJobRels(jenkinsJobs);
        return this;
    }

    public JiraTicket addJiraTicketToJobRel(JenkinsJob jenkinsJob) {
        this.jiraTicketToJobRels.add(jenkinsJob);
        jenkinsJob.setJiraTicket(this);
        return this;
    }

    public JiraTicket removeJiraTicketToJobRel(JenkinsJob jenkinsJob) {
        this.jiraTicketToJobRels.remove(jenkinsJob);
        jenkinsJob.setJiraTicket(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JiraTicket)) {
            return false;
        }
        return id != null && id.equals(((JiraTicket) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JiraTicket{" +
            "id=" + getId() +
            ", jiraTicketName='" + getJiraTicketName() + "'" +
            "}";
    }
}
