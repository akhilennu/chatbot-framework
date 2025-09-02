package io.akhilennu.chatbot.service;

import io.akhilennu.chatbot.domain.Intent;
import io.akhilennu.chatbot.repository.IntentRepository;
import io.akhilennu.chatbot.service.dto.IntentDTO;
import io.akhilennu.chatbot.service.mapper.IntentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.akhilennu.chatbot.domain.Intent}.
 */
@Service
@Transactional
public class IntentService {

    private static final Logger LOG = LoggerFactory.getLogger(IntentService.class);

    private final IntentRepository intentRepository;

    private final IntentMapper intentMapper;

    public IntentService(IntentRepository intentRepository, IntentMapper intentMapper) {
        this.intentRepository = intentRepository;
        this.intentMapper = intentMapper;
    }

    /**
     * Save a intent.
     *
     * @param intentDTO the entity to save.
     * @return the persisted entity.
     */
    public IntentDTO save(IntentDTO intentDTO) {
        LOG.debug("Request to save Intent : {}", intentDTO);
        Intent intent = intentMapper.toEntity(intentDTO);
        intent = intentRepository.save(intent);
        return intentMapper.toDto(intent);
    }

    /**
     * Update a intent.
     *
     * @param intentDTO the entity to save.
     * @return the persisted entity.
     */
    public IntentDTO update(IntentDTO intentDTO) {
        LOG.debug("Request to update Intent : {}", intentDTO);
        Intent intent = intentMapper.toEntity(intentDTO);
        intent = intentRepository.save(intent);
        return intentMapper.toDto(intent);
    }

    /**
     * Partially update a intent.
     *
     * @param intentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<IntentDTO> partialUpdate(IntentDTO intentDTO) {
        LOG.debug("Request to partially update Intent : {}", intentDTO);

        return intentRepository
            .findById(intentDTO.getId())
            .map(existingIntent -> {
                intentMapper.partialUpdate(existingIntent, intentDTO);

                return existingIntent;
            })
            .map(intentRepository::save)
            .map(intentMapper::toDto);
    }

    /**
     * Get all the intents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<IntentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Intents");
        return intentRepository.findAll(pageable).map(intentMapper::toDto);
    }

    /**
     * Get all the intents with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<IntentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return intentRepository.findAllWithEagerRelationships(pageable).map(intentMapper::toDto);
    }

    /**
     * Get one intent by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<IntentDTO> findOne(Long id) {
        LOG.debug("Request to get Intent : {}", id);
        return intentRepository.findOneWithEagerRelationships(id).map(intentMapper::toDto);
    }

    /**
     * Delete the intent by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Intent : {}", id);
        intentRepository.deleteById(id);
    }
}
