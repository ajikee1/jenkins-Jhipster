package com.ajith.com.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ajith.com.IntegrationTest;
import com.ajith.com.domain.JenkinsJob;
import com.ajith.com.repository.JenkinsJobRepository;
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
 * Integration tests for the {@link JenkinsJobResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class JenkinsJobResourceIT {

    private static final String DEFAULT_JOB_NAME = "AAAAAAAAAA";
    private static final String UPDATED_JOB_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_BUILD_NUMBER = 1;
    private static final Integer UPDATED_BUILD_NUMBER = 2;

    private static final String ENTITY_API_URL = "/api/jenkins-jobs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private JenkinsJobRepository jenkinsJobRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJenkinsJobMockMvc;

    private JenkinsJob jenkinsJob;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JenkinsJob createEntity(EntityManager em) {
        JenkinsJob jenkinsJob = new JenkinsJob().jobName(DEFAULT_JOB_NAME).buildNumber(DEFAULT_BUILD_NUMBER);
        return jenkinsJob;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JenkinsJob createUpdatedEntity(EntityManager em) {
        JenkinsJob jenkinsJob = new JenkinsJob().jobName(UPDATED_JOB_NAME).buildNumber(UPDATED_BUILD_NUMBER);
        return jenkinsJob;
    }

    @BeforeEach
    public void initTest() {
        jenkinsJob = createEntity(em);
    }

    @Test
    @Transactional
    void createJenkinsJob() throws Exception {
        int databaseSizeBeforeCreate = jenkinsJobRepository.findAll().size();
        // Create the JenkinsJob
        restJenkinsJobMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jenkinsJob))
            )
            .andExpect(status().isCreated());

        // Validate the JenkinsJob in the database
        List<JenkinsJob> jenkinsJobList = jenkinsJobRepository.findAll();
        assertThat(jenkinsJobList).hasSize(databaseSizeBeforeCreate + 1);
        JenkinsJob testJenkinsJob = jenkinsJobList.get(jenkinsJobList.size() - 1);
        assertThat(testJenkinsJob.getJobName()).isEqualTo(DEFAULT_JOB_NAME);
        assertThat(testJenkinsJob.getBuildNumber()).isEqualTo(DEFAULT_BUILD_NUMBER);
    }

    @Test
    @Transactional
    void createJenkinsJobWithExistingId() throws Exception {
        // Create the JenkinsJob with an existing ID
        jenkinsJob.setId(1L);

        int databaseSizeBeforeCreate = jenkinsJobRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restJenkinsJobMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jenkinsJob))
            )
            .andExpect(status().isBadRequest());

        // Validate the JenkinsJob in the database
        List<JenkinsJob> jenkinsJobList = jenkinsJobRepository.findAll();
        assertThat(jenkinsJobList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkJobNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = jenkinsJobRepository.findAll().size();
        // set the field null
        jenkinsJob.setJobName(null);

        // Create the JenkinsJob, which fails.

        restJenkinsJobMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jenkinsJob))
            )
            .andExpect(status().isBadRequest());

        List<JenkinsJob> jenkinsJobList = jenkinsJobRepository.findAll();
        assertThat(jenkinsJobList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBuildNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = jenkinsJobRepository.findAll().size();
        // set the field null
        jenkinsJob.setBuildNumber(null);

        // Create the JenkinsJob, which fails.

        restJenkinsJobMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jenkinsJob))
            )
            .andExpect(status().isBadRequest());

        List<JenkinsJob> jenkinsJobList = jenkinsJobRepository.findAll();
        assertThat(jenkinsJobList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllJenkinsJobs() throws Exception {
        // Initialize the database
        jenkinsJobRepository.saveAndFlush(jenkinsJob);

        // Get all the jenkinsJobList
        restJenkinsJobMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jenkinsJob.getId().intValue())))
            .andExpect(jsonPath("$.[*].jobName").value(hasItem(DEFAULT_JOB_NAME)))
            .andExpect(jsonPath("$.[*].buildNumber").value(hasItem(DEFAULT_BUILD_NUMBER)));
    }

    @Test
    @Transactional
    void getJenkinsJob() throws Exception {
        // Initialize the database
        jenkinsJobRepository.saveAndFlush(jenkinsJob);

        // Get the jenkinsJob
        restJenkinsJobMockMvc
            .perform(get(ENTITY_API_URL_ID, jenkinsJob.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(jenkinsJob.getId().intValue()))
            .andExpect(jsonPath("$.jobName").value(DEFAULT_JOB_NAME))
            .andExpect(jsonPath("$.buildNumber").value(DEFAULT_BUILD_NUMBER));
    }

    @Test
    @Transactional
    void getNonExistingJenkinsJob() throws Exception {
        // Get the jenkinsJob
        restJenkinsJobMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewJenkinsJob() throws Exception {
        // Initialize the database
        jenkinsJobRepository.saveAndFlush(jenkinsJob);

        int databaseSizeBeforeUpdate = jenkinsJobRepository.findAll().size();

        // Update the jenkinsJob
        JenkinsJob updatedJenkinsJob = jenkinsJobRepository.findById(jenkinsJob.getId()).get();
        // Disconnect from session so that the updates on updatedJenkinsJob are not directly saved in db
        em.detach(updatedJenkinsJob);
        updatedJenkinsJob.jobName(UPDATED_JOB_NAME).buildNumber(UPDATED_BUILD_NUMBER);

        restJenkinsJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedJenkinsJob.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedJenkinsJob))
            )
            .andExpect(status().isOk());

        // Validate the JenkinsJob in the database
        List<JenkinsJob> jenkinsJobList = jenkinsJobRepository.findAll();
        assertThat(jenkinsJobList).hasSize(databaseSizeBeforeUpdate);
        JenkinsJob testJenkinsJob = jenkinsJobList.get(jenkinsJobList.size() - 1);
        assertThat(testJenkinsJob.getJobName()).isEqualTo(UPDATED_JOB_NAME);
        assertThat(testJenkinsJob.getBuildNumber()).isEqualTo(UPDATED_BUILD_NUMBER);
    }

    @Test
    @Transactional
    void putNonExistingJenkinsJob() throws Exception {
        int databaseSizeBeforeUpdate = jenkinsJobRepository.findAll().size();
        jenkinsJob.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJenkinsJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, jenkinsJob.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jenkinsJob))
            )
            .andExpect(status().isBadRequest());

        // Validate the JenkinsJob in the database
        List<JenkinsJob> jenkinsJobList = jenkinsJobRepository.findAll();
        assertThat(jenkinsJobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchJenkinsJob() throws Exception {
        int databaseSizeBeforeUpdate = jenkinsJobRepository.findAll().size();
        jenkinsJob.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJenkinsJobMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jenkinsJob))
            )
            .andExpect(status().isBadRequest());

        // Validate the JenkinsJob in the database
        List<JenkinsJob> jenkinsJobList = jenkinsJobRepository.findAll();
        assertThat(jenkinsJobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJenkinsJob() throws Exception {
        int databaseSizeBeforeUpdate = jenkinsJobRepository.findAll().size();
        jenkinsJob.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJenkinsJobMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(jenkinsJob))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the JenkinsJob in the database
        List<JenkinsJob> jenkinsJobList = jenkinsJobRepository.findAll();
        assertThat(jenkinsJobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateJenkinsJobWithPatch() throws Exception {
        // Initialize the database
        jenkinsJobRepository.saveAndFlush(jenkinsJob);

        int databaseSizeBeforeUpdate = jenkinsJobRepository.findAll().size();

        // Update the jenkinsJob using partial update
        JenkinsJob partialUpdatedJenkinsJob = new JenkinsJob();
        partialUpdatedJenkinsJob.setId(jenkinsJob.getId());

        partialUpdatedJenkinsJob.buildNumber(UPDATED_BUILD_NUMBER);

        restJenkinsJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJenkinsJob.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJenkinsJob))
            )
            .andExpect(status().isOk());

        // Validate the JenkinsJob in the database
        List<JenkinsJob> jenkinsJobList = jenkinsJobRepository.findAll();
        assertThat(jenkinsJobList).hasSize(databaseSizeBeforeUpdate);
        JenkinsJob testJenkinsJob = jenkinsJobList.get(jenkinsJobList.size() - 1);
        assertThat(testJenkinsJob.getJobName()).isEqualTo(DEFAULT_JOB_NAME);
        assertThat(testJenkinsJob.getBuildNumber()).isEqualTo(UPDATED_BUILD_NUMBER);
    }

    @Test
    @Transactional
    void fullUpdateJenkinsJobWithPatch() throws Exception {
        // Initialize the database
        jenkinsJobRepository.saveAndFlush(jenkinsJob);

        int databaseSizeBeforeUpdate = jenkinsJobRepository.findAll().size();

        // Update the jenkinsJob using partial update
        JenkinsJob partialUpdatedJenkinsJob = new JenkinsJob();
        partialUpdatedJenkinsJob.setId(jenkinsJob.getId());

        partialUpdatedJenkinsJob.jobName(UPDATED_JOB_NAME).buildNumber(UPDATED_BUILD_NUMBER);

        restJenkinsJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJenkinsJob.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJenkinsJob))
            )
            .andExpect(status().isOk());

        // Validate the JenkinsJob in the database
        List<JenkinsJob> jenkinsJobList = jenkinsJobRepository.findAll();
        assertThat(jenkinsJobList).hasSize(databaseSizeBeforeUpdate);
        JenkinsJob testJenkinsJob = jenkinsJobList.get(jenkinsJobList.size() - 1);
        assertThat(testJenkinsJob.getJobName()).isEqualTo(UPDATED_JOB_NAME);
        assertThat(testJenkinsJob.getBuildNumber()).isEqualTo(UPDATED_BUILD_NUMBER);
    }

    @Test
    @Transactional
    void patchNonExistingJenkinsJob() throws Exception {
        int databaseSizeBeforeUpdate = jenkinsJobRepository.findAll().size();
        jenkinsJob.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJenkinsJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, jenkinsJob.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jenkinsJob))
            )
            .andExpect(status().isBadRequest());

        // Validate the JenkinsJob in the database
        List<JenkinsJob> jenkinsJobList = jenkinsJobRepository.findAll();
        assertThat(jenkinsJobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJenkinsJob() throws Exception {
        int databaseSizeBeforeUpdate = jenkinsJobRepository.findAll().size();
        jenkinsJob.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJenkinsJobMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jenkinsJob))
            )
            .andExpect(status().isBadRequest());

        // Validate the JenkinsJob in the database
        List<JenkinsJob> jenkinsJobList = jenkinsJobRepository.findAll();
        assertThat(jenkinsJobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJenkinsJob() throws Exception {
        int databaseSizeBeforeUpdate = jenkinsJobRepository.findAll().size();
        jenkinsJob.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJenkinsJobMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(jenkinsJob))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the JenkinsJob in the database
        List<JenkinsJob> jenkinsJobList = jenkinsJobRepository.findAll();
        assertThat(jenkinsJobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteJenkinsJob() throws Exception {
        // Initialize the database
        jenkinsJobRepository.saveAndFlush(jenkinsJob);

        int databaseSizeBeforeDelete = jenkinsJobRepository.findAll().size();

        // Delete the jenkinsJob
        restJenkinsJobMockMvc
            .perform(delete(ENTITY_API_URL_ID, jenkinsJob.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<JenkinsJob> jenkinsJobList = jenkinsJobRepository.findAll();
        assertThat(jenkinsJobList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
