package io.akhilennu.chatbot.web.rest;

import io.akhilennu.chatbot.repository.UtteranceRepository;
import io.akhilennu.chatbot.service.UtteranceService;
import io.akhilennu.chatbot.service.dto.UtteranceDTO;
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
 * REST controller for managing {@link io.akhilennu.chatbot.domain.Utterance}.
 */
@RestController
@RequestMapping("/api/utterances")
public class UtteranceResource {

    private static final Logger LOG = LoggerFactory.getLogger(UtteranceResource.class);

    private static final String ENTITY_NAME = "utterance";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UtteranceService utteranceService;

    private final UtteranceRepository utteranceRepository;

    public UtteranceResource(UtteranceService utteranceService, UtteranceRepository utteranceRepository) {
        this.utteranceService = utteranceService;
        this.utteranceRepository = utteranceRepository;
    }

    /**
     * {@code POST  /utterances} : Create a new utterance.
     *
     * @param utteranceDTO the utteranceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new utteranceDTO, or with status {@code 400 (Bad Request)} if the utterance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UtteranceDTO> createUtterance(@Valid @RequestBody UtteranceDTO utteranceDTO) throws URISyntaxException {
        LOG.debug("REST request to save Utterance : {}", utteranceDTO);
        if (utteranceDTO.getId() != null) {
            throw new BadRequestAlertException("A new utterance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        utteranceDTO = utteranceService.save(utteranceDTO);
        return ResponseEntity.created(new URI("/api/utterances/" + utteranceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, utteranceDTO.getId().toString()))
            .body(utteranceDTO);
    }

    /**
     * {@code PUT  /utterances/:id} : Updates an existing utterance.
     *
     * @param id the id of the utteranceDTO to save.
     * @param utteranceDTO the utteranceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated utteranceDTO,
     * or with status {@code 400 (Bad Request)} if the utteranceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the utteranceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UtteranceDTO> updateUtterance(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UtteranceDTO utteranceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Utterance : {}, {}", id, utteranceDTO);
        if (utteranceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, utteranceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!utteranceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        utteranceDTO = utteranceService.update(utteranceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, utteranceDTO.getId().toString()))
            .body(utteranceDTO);
    }

    /**
     * {@code PATCH  /utterances/:id} : Partial updates given fields of an existing utterance, field will ignore if it is null
     *
     * @param id the id of the utteranceDTO to save.
     * @param utteranceDTO the utteranceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated utteranceDTO,
     * or with status {@code 400 (Bad Request)} if the utteranceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the utteranceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the utteranceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UtteranceDTO> partialUpdateUtterance(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UtteranceDTO utteranceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Utterance partially : {}, {}", id, utteranceDTO);
        if (utteranceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, utteranceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!utteranceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UtteranceDTO> result = utteranceService.partialUpdate(utteranceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, utteranceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /utterances} : get all the utterances.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of utterances in body.
     */
    @GetMapping("")
    public List<UtteranceDTO> getAllUtterances(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all Utterances");
        return utteranceService.findAll();
    }

    /**
     * {@code GET  /utterances/:id} : get the "id" utterance.
     *
     * @param id the id of the utteranceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the utteranceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UtteranceDTO> getUtterance(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Utterance : {}", id);
        Optional<UtteranceDTO> utteranceDTO = utteranceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(utteranceDTO);
    }

    /**
     * {@code DELETE  /utterances/:id} : delete the "id" utterance.
     *
     * @param id the id of the utteranceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtterance(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Utterance : {}", id);
        utteranceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
