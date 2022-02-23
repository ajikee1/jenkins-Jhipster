package com.ajith.com.web.rest;

import com.ajith.com.domain.JiraTicket;
import com.ajith.com.repository.JiraTicketRepository;
import com.ajith.com.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ajith.com.domain.JiraTicket}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class JiraTicketResource {

    private final Logger log = LoggerFactory.getLogger(JiraTicketResource.class);

    private static final String ENTITY_NAME = "jiraTicket";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final JiraTicketRepository jiraTicketRepository;

    public JiraTicketResource(JiraTicketRepository jiraTicketRepository) {
        this.jiraTicketRepository = jiraTicketRepository;
    }

    /**
     * {@code POST  /jira-tickets} : Create a new jiraTicket.
     *
     * @param jiraTicket the jiraTicket to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new jiraTicket, or with status {@code 400 (Bad Request)} if the jiraTicket has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/jira-tickets")
    public ResponseEntity<JiraTicket> createJiraTicket(@Valid @RequestBody JiraTicket jiraTicket) throws URISyntaxException {
        log.debug("REST request to save JiraTicket : {}", jiraTicket);
        if (jiraTicket.getId() != null) {
            throw new BadRequestAlertException("A new jiraTicket cannot already have an ID", ENTITY_NAME, "idexists");
        }
        JiraTicket result = jiraTicketRepository.save(jiraTicket);
        return ResponseEntity
            .created(new URI("/api/jira-tickets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /jira-tickets/:id} : Updates an existing jiraTicket.
     *
     * @param id the id of the jiraTicket to save.
     * @param jiraTicket the jiraTicket to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jiraTicket,
     * or with status {@code 400 (Bad Request)} if the jiraTicket is not valid,
     * or with status {@code 500 (Internal Server Error)} if the jiraTicket couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/jira-tickets/{id}")
    public ResponseEntity<JiraTicket> updateJiraTicket(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody JiraTicket jiraTicket
    ) throws URISyntaxException {
        log.debug("REST request to update JiraTicket : {}, {}", id, jiraTicket);
        if (jiraTicket.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, jiraTicket.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!jiraTicketRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        JiraTicket result = jiraTicketRepository.save(jiraTicket);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, jiraTicket.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /jira-tickets/:id} : Partial updates given fields of an existing jiraTicket, field will ignore if it is null
     *
     * @param id the id of the jiraTicket to save.
     * @param jiraTicket the jiraTicket to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jiraTicket,
     * or with status {@code 400 (Bad Request)} if the jiraTicket is not valid,
     * or with status {@code 404 (Not Found)} if the jiraTicket is not found,
     * or with status {@code 500 (Internal Server Error)} if the jiraTicket couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/jira-tickets/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<JiraTicket> partialUpdateJiraTicket(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody JiraTicket jiraTicket
    ) throws URISyntaxException {
        log.debug("REST request to partial update JiraTicket partially : {}, {}", id, jiraTicket);
        if (jiraTicket.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, jiraTicket.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!jiraTicketRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<JiraTicket> result = jiraTicketRepository
            .findById(jiraTicket.getId())
            .map(existingJiraTicket -> {
                if (jiraTicket.getJiraTicketName() != null) {
                    existingJiraTicket.setJiraTicketName(jiraTicket.getJiraTicketName());
                }

                return existingJiraTicket;
            })
            .map(jiraTicketRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, jiraTicket.getId().toString())
        );
    }

    /**
     * {@code GET  /jira-tickets} : get all the jiraTickets.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of jiraTickets in body.
     */
    @GetMapping("/jira-tickets")
    public List<JiraTicket> getAllJiraTickets() {
        log.debug("REST request to get all JiraTickets");
        return jiraTicketRepository.findAll();
    }

    /**
     * {@code GET  /jira-tickets/:id} : get the "id" jiraTicket.
     *
     * @param id the id of the jiraTicket to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the jiraTicket, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/jira-tickets/{id}")
    public ResponseEntity<JiraTicket> getJiraTicket(@PathVariable Long id) {
        log.debug("REST request to get JiraTicket : {}", id);
        Optional<JiraTicket> jiraTicket = jiraTicketRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(jiraTicket);
    }

    /**
     * {@code DELETE  /jira-tickets/:id} : delete the "id" jiraTicket.
     *
     * @param id the id of the jiraTicket to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/jira-tickets/{id}")
    public ResponseEntity<Void> deleteJiraTicket(@PathVariable Long id) {
        log.debug("REST request to delete JiraTicket : {}", id);
        jiraTicketRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
