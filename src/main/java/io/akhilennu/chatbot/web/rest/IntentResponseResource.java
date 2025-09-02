package io.akhilennu.chatbot.web.rest;

import io.akhilennu.chatbot.repository.IntentResponseRepository;
import io.akhilennu.chatbot.service.IntentResponseService;
import io.akhilennu.chatbot.service.dto.IntentResponseDTO;
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
 * REST controller for managing {@link io.akhilennu.chatbot.domain.IntentResponse}.
 */
@RestController
@RequestMapping("/api/intent-responses")
public class IntentResponseResource {

    private static final Logger LOG = LoggerFactory.getLogger(IntentResponseResource.class);

    private static final String ENTITY_NAME = "intentResponse";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IntentResponseService intentResponseService;

    private final IntentResponseRepository intentResponseRepository;

    public IntentResponseResource(IntentResponseService intentResponseService, IntentResponseRepository intentResponseRepository) {
        this.intentResponseService = intentResponseService;
        this.intentResponseRepository = intentResponseRepository;
    }

    /**
     * {@code POST  /intent-responses} : Create a new intentResponse.
     *
     * @param intentResponseDTO the intentResponseDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new intentResponseDTO, or with status {@code 400 (Bad Request)} if the intentResponse has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<IntentResponseDTO> createIntentResponse(@Valid @RequestBody IntentResponseDTO intentResponseDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save IntentResponse : {}", intentResponseDTO);
        if (intentResponseDTO.getId() != null) {
            throw new BadRequestAlertException("A new intentResponse cannot already have an ID", ENTITY_NAME, "idexists");
        }
        intentResponseDTO = intentResponseService.save(intentResponseDTO);
        return ResponseEntity.created(new URI("/api/intent-responses/" + intentResponseDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, intentResponseDTO.getId().toString()))
            .body(intentResponseDTO);
    }

    /**
     * {@code PUT  /intent-responses/:id} : Updates an existing intentResponse.
     *
     * @param id the id of the intentResponseDTO to save.
     * @param intentResponseDTO the intentResponseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated intentResponseDTO,
     * or with status {@code 400 (Bad Request)} if the intentResponseDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the intentResponseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<IntentResponseDTO> updateIntentResponse(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody IntentResponseDTO intentResponseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update IntentResponse : {}, {}", id, intentResponseDTO);
        if (intentResponseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, intentResponseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!intentResponseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        intentResponseDTO = intentResponseService.update(intentResponseDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, intentResponseDTO.getId().toString()))
            .body(intentResponseDTO);
    }

    /**
     * {@code PATCH  /intent-responses/:id} : Partial updates given fields of an existing intentResponse, field will ignore if it is null
     *
     * @param id the id of the intentResponseDTO to save.
     * @param intentResponseDTO the intentResponseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated intentResponseDTO,
     * or with status {@code 400 (Bad Request)} if the intentResponseDTO is not valid,
     * or with status {@code 404 (Not Found)} if the intentResponseDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the intentResponseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<IntentResponseDTO> partialUpdateIntentResponse(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody IntentResponseDTO intentResponseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update IntentResponse partially : {}, {}", id, intentResponseDTO);
        if (intentResponseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, intentResponseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!intentResponseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<IntentResponseDTO> result = intentResponseService.partialUpdate(intentResponseDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, intentResponseDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /intent-responses} : get all the intentResponses.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of intentResponses in body.
     */
    @GetMapping("")
    public List<IntentResponseDTO> getAllIntentResponses(@RequestParam(name = "filter", required = false) String filter) {
        if ("intent-is-null".equals(filter)) {
            LOG.debug("REST request to get all IntentResponses where intent is null");
            return intentResponseService.findAllWhereIntentIsNull();
        }
        LOG.debug("REST request to get all IntentResponses");
        return intentResponseService.findAll();
    }

    /**
     * {@code GET  /intent-responses/:id} : get the "id" intentResponse.
     *
     * @param id the id of the intentResponseDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the intentResponseDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<IntentResponseDTO> getIntentResponse(@PathVariable("id") Long id) {
        LOG.debug("REST request to get IntentResponse : {}", id);
        Optional<IntentResponseDTO> intentResponseDTO = intentResponseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(intentResponseDTO);
    }

    /**
     * {@code DELETE  /intent-responses/:id} : delete the "id" intentResponse.
     *
     * @param id the id of the intentResponseDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIntentResponse(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete IntentResponse : {}", id);
        intentResponseService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
