package io.akhilennu.chatbot.web.rest;

import io.akhilennu.chatbot.repository.BotRepository;
import io.akhilennu.chatbot.service.BotService;
import io.akhilennu.chatbot.service.dto.BotDTO;
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
 * REST controller for managing {@link io.akhilennu.chatbot.domain.Bot}.
 */
@RestController
@RequestMapping("/api/bots")
public class BotResource {

    private static final Logger LOG = LoggerFactory.getLogger(BotResource.class);

    private static final String ENTITY_NAME = "bot";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BotService botService;

    private final BotRepository botRepository;

    public BotResource(BotService botService, BotRepository botRepository) {
        this.botService = botService;
        this.botRepository = botRepository;
    }

    /**
     * {@code POST  /bots} : Create a new bot.
     *
     * @param botDTO the botDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new botDTO, or with status {@code 400 (Bad Request)} if the bot has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BotDTO> createBot(@Valid @RequestBody BotDTO botDTO) throws URISyntaxException {
        LOG.debug("REST request to save Bot : {}", botDTO);
        if (botDTO.getId() != null) {
            throw new BadRequestAlertException("A new bot cannot already have an ID", ENTITY_NAME, "idexists");
        }
        botDTO = botService.save(botDTO);
        return ResponseEntity.created(new URI("/api/bots/" + botDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, botDTO.getId().toString()))
            .body(botDTO);
    }

    /**
     * {@code PUT  /bots/:id} : Updates an existing bot.
     *
     * @param id the id of the botDTO to save.
     * @param botDTO the botDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated botDTO,
     * or with status {@code 400 (Bad Request)} if the botDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the botDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BotDTO> updateBot(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody BotDTO botDTO)
        throws URISyntaxException {
        LOG.debug("REST request to update Bot : {}, {}", id, botDTO);
        if (botDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, botDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!botRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        botDTO = botService.update(botDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, botDTO.getId().toString()))
            .body(botDTO);
    }

    /**
     * {@code PATCH  /bots/:id} : Partial updates given fields of an existing bot, field will ignore if it is null
     *
     * @param id the id of the botDTO to save.
     * @param botDTO the botDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated botDTO,
     * or with status {@code 400 (Bad Request)} if the botDTO is not valid,
     * or with status {@code 404 (Not Found)} if the botDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the botDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BotDTO> partialUpdateBot(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BotDTO botDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Bot partially : {}, {}", id, botDTO);
        if (botDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, botDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!botRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BotDTO> result = botService.partialUpdate(botDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, botDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /bots} : get all the bots.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bots in body.
     */
    @GetMapping("")
    public List<BotDTO> getAllBots() {
        LOG.debug("REST request to get all Bots");
        return botService.findAll();
    }

    /**
     * {@code GET  /bots/:id} : get the "id" bot.
     *
     * @param id the id of the botDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the botDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BotDTO> getBot(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Bot : {}", id);
        Optional<BotDTO> botDTO = botService.findOne(id);
        return ResponseUtil.wrapOrNotFound(botDTO);
    }

    /**
     * {@code DELETE  /bots/:id} : delete the "id" bot.
     *
     * @param id the id of the botDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBot(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Bot : {}", id);
        botService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
