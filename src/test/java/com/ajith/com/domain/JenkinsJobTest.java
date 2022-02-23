package com.ajith.com.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ajith.com.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class JenkinsJobTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JenkinsJob.class);
        JenkinsJob jenkinsJob1 = new JenkinsJob();
        jenkinsJob1.setId(1L);
        JenkinsJob jenkinsJob2 = new JenkinsJob();
        jenkinsJob2.setId(jenkinsJob1.getId());
        assertThat(jenkinsJob1).isEqualTo(jenkinsJob2);
        jenkinsJob2.setId(2L);
        assertThat(jenkinsJob1).isNotEqualTo(jenkinsJob2);
        jenkinsJob1.setId(null);
        assertThat(jenkinsJob1).isNotEqualTo(jenkinsJob2);
    }
}
