package io.akhilennu.chatbot.web.rest;

import io.akhilennu.chatbot.repository.IntentEntityRepository;
import io.akhilennu.chatbot.service.IntentEntityService;
import io.akhilennu.chatbot.service.dto.IntentEntityDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.akhilennu.chatbot.domain.IntentEntity}.
 */
@RestController
@RequestMapping("/api/intent-entities")
public class IntentEntityResource {

    private static final Logger LOG = LoggerFactory.getLogger(IntentEntityResource.class);

    private static final String ENTITY_NAME = "intentEntity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IntentEntityService intentEntityService;

    private final IntentEntityRepository intentEntityRepository;

    public IntentEntityResource(IntentEntityService intentEntityService, IntentEntityRepository intentEntityRepository) {
        this.intentEntityService = intentEntityService;
        this.intentEntityRepository = intentEntityRepository;
    }

    /**
     * {@code POST  /intent-entities} : Create a new intentEntity.
     *
     * @param intentEntityDTO the intentEntityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new intentEntityDTO, or with status {@code 400 (Bad Request)} if the intentEntity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<IntentEntityDTO> createIntentEntity(@Valid @RequestBody IntentEntityDTO intentEntityDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save IntentEntity : {}", intentEntityDTO);
        if (intentEntityDTO.getId() != null) {
            throw new BadRequestAlertException("A new intentEntity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        intentEntityDTO = intentEntityService.save(intentEntityDTO);
        return ResponseEntity.created(new URI("/api/intent-entities/" + intentEntityDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, intentEntityDTO.getId().toString()))
            .body(intentEntityDTO);
    }

    /**
     * {@code PUT  /intent-entities/:id} : Updates an existing intentEntity.
     *
     * @param id the id of the intentEntityDTO to save.
     * @param intentEntityDTO the intentEntityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated intentEntityDTO,
     * or with status {@code 400 (Bad Request)} if the intentEntityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the intentEntityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<IntentEntityDTO> updateIntentEntity(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody IntentEntityDTO intentEntityDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update IntentEntity : {}, {}", id, intentEntityDTO);
        if (intentEntityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, intentEntityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!intentEntityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        intentEntityDTO = intentEntityService.update(intentEntityDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, intentEntityDTO.getId().toString()))
            .body(intentEntityDTO);
    }

    /**
     * {@code PATCH  /intent-entities/:id} : Partial updates given fields of an existing intentEntity, field will ignore if it is null
     *
     * @param id the id of the intentEntityDTO to save.
     * @param intentEntityDTO the intentEntityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated intentEntityDTO,
     * or with status {@code 400 (Bad Request)} if the intentEntityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the intentEntityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the intentEntityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<IntentEntityDTO> partialUpdateIntentEntity(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody IntentEntityDTO intentEntityDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update IntentEntity partially : {}, {}", id, intentEntityDTO);
        if (intentEntityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, intentEntityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!intentEntityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<IntentEntityDTO> result = intentEntityService.partialUpdate(intentEntityDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, intentEntityDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /intent-entities} : get all the intentEntities.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of intentEntities in body.
     */
    @GetMapping("")
    public ResponseEntity<List<IntentEntityDTO>> getAllIntentEntities(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of IntentEntities");
        Page<IntentEntityDTO> page = intentEntityService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /intent-entities/:id} : get the "id" intentEntity.
     *
     * @param id the id of the intentEntityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the intentEntityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<IntentEntityDTO> getIntentEntity(@PathVariable("id") Long id) {
        LOG.debug("REST request to get IntentEntity : {}", id);
        Optional<IntentEntityDTO> intentEntityDTO = intentEntityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(intentEntityDTO);
    }

    /**
     * {@code DELETE  /intent-entities/:id} : delete the "id" intentEntity.
     *
     * @param id the id of the intentEntityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIntentEntity(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete IntentEntity : {}", id);
        intentEntityService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
