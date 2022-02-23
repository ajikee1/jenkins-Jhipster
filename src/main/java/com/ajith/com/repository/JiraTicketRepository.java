package com.ajith.com.repository;

import com.ajith.com.domain.JiraTicket;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the JiraTicket entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JiraTicketRepository extends JpaRepository<JiraTicket, Long> {}
