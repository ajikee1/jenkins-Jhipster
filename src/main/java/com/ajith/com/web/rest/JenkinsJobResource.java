package com.ajith.com.web.rest;

import com.ajith.com.domain.JenkinsJob;
import com.ajith.com.repository.JenkinsJobRepository;
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
 * REST controller for managing {@link com.ajith.com.domain.JenkinsJob}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class JenkinsJobResource {

    private final Logger log = LoggerFactory.getLogger(JenkinsJobResource.class);

    private static final String ENTITY_NAME = "jenkinsJob";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final JenkinsJobRepository jenkinsJobRepository;

    public JenkinsJobResource(JenkinsJobRepository jenkinsJobRepository) {
        this.jenkinsJobRepository = jenkinsJobRepository;
    }

    /**
     * {@code POST  /jenkins-jobs} : Create a new jenkinsJob.
     *
     * @param jenkinsJob the jenkinsJob to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new jenkinsJob, or with status {@code 400 (Bad Request)} if the jenkinsJob has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/jenkins-jobs")
    public ResponseEntity<JenkinsJob> createJenkinsJob(@Valid @RequestBody JenkinsJob jenkinsJob) throws URISyntaxException {
        log.debug("REST request to save JenkinsJob : {}", jenkinsJob);
        if (jenkinsJob.getId() != null) {
            throw new BadRequestAlertException("A new jenkinsJob cannot already have an ID", ENTITY_NAME, "idexists");
        }
        JenkinsJob result = jenkinsJobRepository.save(jenkinsJob);
        return ResponseEntity
            .created(new URI("/api/jenkins-jobs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /jenkins-jobs/:id} : Updates an existing jenkinsJob.
     *
     * @param id the id of the jenkinsJob to save.
     * @param jenkinsJob the jenkinsJob to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jenkinsJob,
     * or with status {@code 400 (Bad Request)} if the jenkinsJob is not valid,
     * or with status {@code 500 (Internal Server Error)} if the jenkinsJob couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/jenkins-jobs/{id}")
    public ResponseEntity<JenkinsJob> updateJenkinsJob(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody JenkinsJob jenkinsJob
    ) throws URISyntaxException {
        log.debug("REST request to update JenkinsJob : {}, {}", id, jenkinsJob);
        if (jenkinsJob.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, jenkinsJob.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!jenkinsJobRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        JenkinsJob result = jenkinsJobRepository.save(jenkinsJob);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, jenkinsJob.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /jenkins-jobs/:id} : Partial updates given fields of an existing jenkinsJob, field will ignore if it is null
     *
     * @param id the id of the jenkinsJob to save.
     * @param jenkinsJob the jenkinsJob to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jenkinsJob,
     * or with status {@code 400 (Bad Request)} if the jenkinsJob is not valid,
     * or with status {@code 404 (Not Found)} if the jenkinsJob is not found,
     * or with status {@code 500 (Internal Server Error)} if the jenkinsJob couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/jenkins-jobs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<JenkinsJob> partialUpdateJenkinsJob(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody JenkinsJob jenkinsJob
    ) throws URISyntaxException {
        log.debug("REST request to partial update JenkinsJob partially : {}, {}", id, jenkinsJob);
        if (jenkinsJob.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, jenkinsJob.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!jenkinsJobRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<JenkinsJob> result = jenkinsJobRepository
            .findById(jenkinsJob.getId())
            .map(existingJenkinsJob -> {
                if (jenkinsJob.getJobName() != null) {
                    existingJenkinsJob.setJobName(jenkinsJob.getJobName());
                }
                if (jenkinsJob.getBuildNumber() != null) {
                    existingJenkinsJob.setBuildNumber(jenkinsJob.getBuildNumber());
                }

                return existingJenkinsJob;
            })
            .map(jenkinsJobRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, jenkinsJob.getId().toString())
        );
    }

    /**
     * {@code GET  /jenkins-jobs} : get all the jenkinsJobs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of jenkinsJobs in body.
     */
    @GetMapping("/jenkins-jobs")
    public List<JenkinsJob> getAllJenkinsJobs() {
        log.debug("REST request to get all JenkinsJobs");
        return jenkinsJobRepository.findAll();
    }

    /**
     * {@code GET  /jenkins-jobs/:id} : get the "id" jenkinsJob.
     *
     * @param id the id of the jenkinsJob to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the jenkinsJob, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/jenkins-jobs/{id}")
    public ResponseEntity<JenkinsJob> getJenkinsJob(@PathVariable Long id) {
        log.debug("REST request to get JenkinsJob : {}", id);
        Optional<JenkinsJob> jenkinsJob = jenkinsJobRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(jenkinsJob);
    }

    /**
     * {@code DELETE  /jenkins-jobs/:id} : delete the "id" jenkinsJob.
     *
     * @param id the id of the jenkinsJob to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/jenkins-jobs/{id}")
    public ResponseEntity<Void> deleteJenkinsJob(@PathVariable Long id) {
        log.debug("REST request to delete JenkinsJob : {}", id);
        jenkinsJobRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
