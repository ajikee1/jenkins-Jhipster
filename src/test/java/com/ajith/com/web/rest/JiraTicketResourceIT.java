package com.ajith.com.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ajith.com.IntegrationTest;
import com.ajith.com.domain.JiraTicket;
import com.ajith.com.repository.JiraTicketRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link JiraTicketResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class JiraTicketResourceIT {

    private static final String DEFAULT_JIRA_TICKET_NAME = "AAAAAAAAAA";
    private static final String UPDATED_JIRA_TICKET_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/jira-tickets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private JiraTicketRepository jiraTicketRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJiraTicketMockMvc;

    private JiraTicket jiraTicket;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JiraTicket createEntity(EntityManager em) {
        JiraTicket jiraTicket = new JiraTicket().jiraTicketName(DEFAULT_JIRA_TICKET_NAME);
        return jiraTicket;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JiraTicket createUpdatedEntity(EntityManager em) {
        JiraTicket jiraTicket = new JiraTicket().jiraTicketName(UPDATED_JIRA_TICKET_NAME);
        return jiraTicket;
    }

    @BeforeEach
    public void initTest() {
        jiraTicket = createEntity(em);
    }

    @Test
    @Transactional
    void createJiraTicket() throws Exception {
        int databaseSizeBeforeCreate = jiraTicketRepository.findAll().size();
        // Create the JiraTicket
        restJiraTicketMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jiraTicket))
            )
            .andExpect(status().isCreated());

        // Validate the JiraTicket in the database
        List<JiraTicket> jiraTicketList = jiraTicketRepository.findAll();
        assertThat(jiraTicketList).hasSize(databaseSizeBeforeCreate + 1);
        JiraTicket testJiraTicket = jiraTicketList.get(jiraTicketList.size() - 1);
        assertThat(testJiraTicket.getJiraTicketName()).isEqualTo(DEFAULT_JIRA_TICKET_NAME);
    }

    @Test
    @Transactional
    void createJiraTicketWithExistingId() throws Exception {
        // Create the JiraTicket with an existing ID
        jiraTicket.setId(1L);

        int databaseSizeBeforeCreate = jiraTicketRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restJiraTicketMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jiraTicket))
            )
            .andExpect(status().isBadRequest());

        // Validate the JiraTicket in the database
        List<JiraTicket> jiraTicketList = jiraTicketRepository.findAll();
        assertThat(jiraTicketList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkJiraTicketNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = jiraTicketRepository.findAll().size();
        // set the field null
        jiraTicket.setJiraTicketName(null);

        // Create the JiraTicket, which fails.

        restJiraTicketMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jiraTicket))
            )
            .andExpect(status().isBadRequest());

        List<JiraTicket> jiraTicketList = jiraTicketRepository.findAll();
        assertThat(jiraTicketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllJiraTickets() throws Exception {
        // Initialize the database
        jiraTicketRepository.saveAndFlush(jiraTicket);

        // Get all the jiraTicketList
        restJiraTicketMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jiraTicket.getId().intValue())))
            .andExpect(jsonPath("$.[*].jiraTicketName").value(hasItem(DEFAULT_JIRA_TICKET_NAME)));
    }

    @Test
    @Transactional
    void getJiraTicket() throws Exception {
        // Initialize the database
        jiraTicketRepository.saveAndFlush(jiraTicket);

        // Get the jiraTicket
        restJiraTicketMockMvc
            .perform(get(ENTITY_API_URL_ID, jiraTicket.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(jiraTicket.getId().intValue()))
            .andExpect(jsonPath("$.jiraTicketName").value(DEFAULT_JIRA_TICKET_NAME));
    }

    @Test
    @Transactional
    void getNonExistingJiraTicket() throws Exception {
        // Get the jiraTicket
        restJiraTicketMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewJiraTicket() throws Exception {
        // Initialize the database
        jiraTicketRepository.saveAndFlush(jiraTicket);

        int databaseSizeBeforeUpdate = jiraTicketRepository.findAll().size();

        // Update the jiraTicket
        JiraTicket updatedJiraTicket = jiraTicketRepository.findById(jiraTicket.getId()).get();
        // Disconnect from session so that the updates on updatedJiraTicket are not directly saved in db
        em.detach(updatedJiraTicket);
        updatedJiraTicket.jiraTicketName(UPDATED_JIRA_TICKET_NAME);

        restJiraTicketMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedJiraTicket.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedJiraTicket))
            )
            .andExpect(status().isOk());

        // Validate the JiraTicket in the database
        List<JiraTicket> jiraTicketList = jiraTicketRepository.findAll();
        assertThat(jiraTicketList).hasSize(databaseSizeBeforeUpdate);
        JiraTicket testJiraTicket = jiraTicketList.get(jiraTicketList.size() - 1);
        assertThat(testJiraTicket.getJiraTicketName()).isEqualTo(UPDATED_JIRA_TICKET_NAME);
    }

    @Test
    @Transactional
    void putNonExistingJiraTicket() throws Exception {
        int databaseSizeBeforeUpdate = jiraTicketRepository.findAll().size();
        jiraTicket.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJiraTicketMockMvc
            .perform(
                put(ENTITY_API_URL_ID, jiraTicket.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jiraTicket))
            )
            .andExpect(status().isBadRequest());

        // Validate the JiraTicket in the database
        List<JiraTicket> jiraTicketList = jiraTicketRepository.findAll();
        assertThat(jiraTicketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchJiraTicket() throws Exception {
        int databaseSizeBeforeUpdate = jiraTicketRepository.findAll().size();
        jiraTicket.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJiraTicketMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jiraTicket))
            )
            .andExpect(status().isBadRequest());

        // Validate the JiraTicket in the database
        List<JiraTicket> jiraTicketList = jiraTicketRepository.findAll();
        assertThat(jiraTicketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJiraTicket() throws Exception {
        int databaseSizeBeforeUpdate = jiraTicketRepository.findAll().size();
        jiraTicket.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJiraTicketMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jiraTicket))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the JiraTicket in the database
        List<JiraTicket> jiraTicketList = jiraTicketRepository.findAll();
        assertThat(jiraTicketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateJiraTicketWithPatch() throws Exception {
        // Initialize the database
        jiraTicketRepository.saveAndFlush(jiraTicket);

        int databaseSizeBeforeUpdate = jiraTicketRepository.findAll().size();

        // Update the jiraTicket using partial update
        JiraTicket partialUpdatedJiraTicket = new JiraTicket();
        partialUpdatedJiraTicket.setId(jiraTicket.getId());

        partialUpdatedJiraTicket.jiraTicketName(UPDATED_JIRA_TICKET_NAME);

        restJiraTicketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJiraTicket.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJiraTicket))
            )
            .andExpect(status().isOk());

        // Validate the JiraTicket in the database
        List<JiraTicket> jiraTicketList = jiraTicketRepository.findAll();
        assertThat(jiraTicketList).hasSize(databaseSizeBeforeUpdate);
        JiraTicket testJiraTicket = jiraTicketList.get(jiraTicketList.size() - 1);
        assertThat(testJiraTicket.getJiraTicketName()).isEqualTo(UPDATED_JIRA_TICKET_NAME);
    }

    @Test
    @Transactional
    void fullUpdateJiraTicketWithPatch() throws Exception {
        // Initialize the database
        jiraTicketRepository.saveAndFlush(jiraTicket);

        int databaseSizeBeforeUpdate = jiraTicketRepository.findAll().size();

        // Update the jiraTicket using partial update
        JiraTicket partialUpdatedJiraTicket = new JiraTicket();
        partialUpdatedJiraTicket.setId(jiraTicket.getId());

        partialUpdatedJiraTicket.jiraTicketName(UPDATED_JIRA_TICKET_NAME);

        restJiraTicketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJiraTicket.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJiraTicket))
            )
            .andExpect(status().isOk());

        // Validate the JiraTicket in the database
        List<JiraTicket> jiraTicketList = jiraTicketRepository.findAll();
        assertThat(jiraTicketList).hasSize(databaseSizeBeforeUpdate);
        JiraTicket testJiraTicket = jiraTicketList.get(jiraTicketList.size() - 1);
        assertThat(testJiraTicket.getJiraTicketName()).isEqualTo(UPDATED_JIRA_TICKET_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingJiraTicket() throws Exception {
        int databaseSizeBeforeUpdate = jiraTicketRepository.findAll().size();
        jiraTicket.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJiraTicketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, jiraTicket.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jiraTicket))
            )
            .andExpect(status().isBadRequest());

        // Validate the JiraTicket in the database
        List<JiraTicket> jiraTicketList = jiraTicketRepository.findAll();
        assertThat(jiraTicketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJiraTicket() throws Exception {
        int databaseSizeBeforeUpdate = jiraTicketRepository.findAll().size();
        jiraTicket.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJiraTicketMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jiraTicket))
            )
            .andExpect(status().isBadRequest());

        // Validate the JiraTicket in the database
        List<JiraTicket> jiraTicketList = jiraTicketRepository.findAll();
        assertThat(jiraTicketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJiraTicket() throws Exception {
        int databaseSizeBeforeUpdate = jiraTicketRepository.findAll().size();
        jiraTicket.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJiraTicketMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jiraTicket))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the JiraTicket in the database
        List<JiraTicket> jiraTicketList = jiraTicketRepository.findAll();
        assertThat(jiraTicketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteJiraTicket() throws Exception {
        // Initialize the database
        jiraTicketRepository.saveAndFlush(jiraTicket);

        int databaseSizeBeforeDelete = jiraTicketRepository.findAll().size();

        // Delete the jiraTicket
        restJiraTicketMockMvc
            .perform(delete(ENTITY_API_URL_ID, jiraTicket.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<JiraTicket> jiraTicketList = jiraTicketRepository.findAll();
        assertThat(jiraTicketList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
