package io.akhilennu.chatbot.web.rest;

import io.akhilennu.chatbot.repository.FollowupRepository;
import io.akhilennu.chatbot.service.FollowupService;
import io.akhilennu.chatbot.service.dto.FollowupDTO;
import io.akhilennu.chatbot.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.akhilennu.chatbot.domain.Followup}.
 */
@RestController
@RequestMapping("/api/followups")
public class FollowupResource {

    private static final Logger LOG = LoggerFactory.getLogger(FollowupResource.class);

    private static final String ENTITY_NAME = "followup";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FollowupService followupService;

    private final FollowupRepository followupRepository;

    public FollowupResource(FollowupService followupService, FollowupRepository followupRepository) {
        this.followupService = followupService;
        this.followupRepository = followupRepository;
    }

    /**
     * {@code POST  /followups} : Create a new followup.
     *
     * @param followupDTO the followupDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new followupDTO, or with status {@code 400 (Bad Request)} if the followup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FollowupDTO> createFollowup(@Valid @RequestBody FollowupDTO followupDTO) throws URISyntaxException {
        LOG.debug("REST request to save Followup : {}", followupDTO);
        if (followupDTO.getId() != null) {
            throw new BadRequestAlertException("A new followup cannot already have an ID", ENTITY_NAME, "idexists");
        }
        followupDTO = followupService.save(followupDTO);
        return ResponseEntity.created(new URI("/api/followups/" + followupDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, followupDTO.getId().toString()))
            .body(followupDTO);
    }

    /**
     * {@code PUT  /followups/:id} : Updates an existing followup.
     *
     * @param id the id of the followupDTO to save.
     * @param followupDTO the followupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated followupDTO,
     * or with status {@code 400 (Bad Request)} if the followupDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the followupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FollowupDTO> updateFollowup(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FollowupDTO followupDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Followup : {}, {}", id, followupDTO);
        if (followupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, followupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!followupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        followupDTO = followupService.update(followupDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, followupDTO.getId().toString()))
            .body(followupDTO);
    }

    /**
     * {@code PATCH  /followups/:id} : Partial updates given fields of an existing followup, field will ignore if it is null
     *
     * @param id the id of the followupDTO to save.
     * @param followupDTO the followupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated followupDTO,
     * or with status {@code 400 (Bad Request)} if the followupDTO is not valid,
     * or with status {@code 404 (Not Found)} if the followupDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the followupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FollowupDTO> partialUpdateFollowup(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FollowupDTO followupDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Followup partially : {}, {}", id, followupDTO);
        if (followupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, followupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!followupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FollowupDTO> result = followupService.partialUpdate(followupDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, followupDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /followups} : get all the followups.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of followups in body.
     */
    @GetMapping("")
    public List<FollowupDTO> getAllFollowups(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all Followups");
        return followupService.findAll();
    }

    /**
     * {@code GET  /followups/:id} : get the "id" followup.
     *
     * @param id the id of the followupDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the followupDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FollowupDTO> getFollowup(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Followup : {}", id);
        Optional<FollowupDTO> followupDTO = followupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(followupDTO);
    }

    /**
     * {@code DELETE  /followups/:id} : delete the "id" followup.
     *
     * @param id the id of the followupDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFollowup(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Followup : {}", id);
        followupService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
