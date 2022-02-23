package com.ajith.com.repository;

import com.ajith.com.domain.JenkinsJob;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the JenkinsJob entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JenkinsJobRepository extends JpaRepository<JenkinsJob, Long> {}
