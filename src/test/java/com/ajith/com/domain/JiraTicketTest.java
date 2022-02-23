package com.ajith.com.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ajith.com.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class JiraTicketTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JiraTicket.class);
        JiraTicket jiraTicket1 = new JiraTicket();
        jiraTicket1.setId(1L);
        JiraTicket jiraTicket2 = new JiraTicket();
        jiraTicket2.setId(jiraTicket1.getId());
        assertThat(jiraTicket1).isEqualTo(jiraTicket2);
        jiraTicket2.setId(2L);
        assertThat(jiraTicket1).isNotEqualTo(jiraTicket2);
        jiraTicket1.setId(null);
        assertThat(jiraTicket1).isNotEqualTo(jiraTicket2);
    }
}
