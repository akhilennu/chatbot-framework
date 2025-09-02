package io.akhilennu.chatbot.web.rest;

import io.akhilennu.chatbot.repository.IntentRepository;
import io.akhilennu.chatbot.service.IntentService;
import io.akhilennu.chatbot.service.dto.IntentDTO;
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
 * REST controller for managing {@link io.akhilennu.chatbot.domain.Intent}.
 */
@RestController
@RequestMapping("/api/intents")
public class IntentResource {

    private static final Logger LOG = LoggerFactory.getLogger(IntentResource.class);

    private static final String ENTITY_NAME = "intent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IntentService intentService;

    private final IntentRepository intentRepository;

    public IntentResource(IntentService intentService, IntentRepository intentRepository) {
        this.intentService = intentService;
        this.intentRepository = intentRepository;
    }

    /**
     * {@code POST  /intents} : Create a new intent.
     *
     * @param intentDTO the intentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new intentDTO, or with status {@code 400 (Bad Request)} if the intent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<IntentDTO> createIntent(@Valid @RequestBody IntentDTO intentDTO) throws URISyntaxException {
        LOG.debug("REST request to save Intent : {}", intentDTO);
        if (intentDTO.getId() != null) {
            throw new BadRequestAlertException("A new intent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        intentDTO = intentService.save(intentDTO);
        return ResponseEntity.created(new URI("/api/intents/" + intentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, intentDTO.getId().toString()))
            .body(intentDTO);
    }

    /**
     * {@code PUT  /intents/:id} : Updates an existing intent.
     *
     * @param id the id of the intentDTO to save.
     * @param intentDTO the intentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated intentDTO,
     * or with status {@code 400 (Bad Request)} if the intentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the intentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<IntentDTO> updateIntent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody IntentDTO intentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Intent : {}, {}", id, intentDTO);
        if (intentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, intentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!intentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        intentDTO = intentService.update(intentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, intentDTO.getId().toString()))
            .body(intentDTO);
    }

    /**
     * {@code PATCH  /intents/:id} : Partial updates given fields of an existing intent, field will ignore if it is null
     *
     * @param id the id of the intentDTO to save.
     * @param intentDTO the intentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated intentDTO,
     * or with status {@code 400 (Bad Request)} if the intentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the intentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the intentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<IntentDTO> partialUpdateIntent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody IntentDTO intentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Intent partially : {}, {}", id, intentDTO);
        if (intentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, intentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!intentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<IntentDTO> result = intentService.partialUpdate(intentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, intentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /intents} : get all the intents.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of intents in body.
     */
    @GetMapping("")
    public ResponseEntity<List<IntentDTO>> getAllIntents(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Intents");
        Page<IntentDTO> page;
        if (eagerload) {
            page = intentService.findAllWithEagerRelationships(pageable);
        } else {
            page = intentService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /intents/:id} : get the "id" intent.
     *
     * @param id the id of the intentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the intentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<IntentDTO> getIntent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Intent : {}", id);
        Optional<IntentDTO> intentDTO = intentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(intentDTO);
    }

    /**
     * {@code DELETE  /intents/:id} : delete the "id" intent.
     *
     * @param id the id of the intentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIntent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Intent : {}", id);
        intentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
