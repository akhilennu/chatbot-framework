package io.akhilennu.chatbot.service;

import io.akhilennu.chatbot.domain.IntentEntity;
import io.akhilennu.chatbot.repository.IntentEntityRepository;
import io.akhilennu.chatbot.service.dto.IntentEntityDTO;
import io.akhilennu.chatbot.service.mapper.IntentEntityMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.akhilennu.chatbot.domain.IntentEntity}.
 */
@Service
@Transactional
public class IntentEntityService {

    private static final Logger LOG = LoggerFactory.getLogger(IntentEntityService.class);

    private final IntentEntityRepository intentEntityRepository;

    private final IntentEntityMapper intentEntityMapper;

    public IntentEntityService(IntentEntityRepository intentEntityRepository, IntentEntityMapper intentEntityMapper) {
        this.intentEntityRepository = intentEntityRepository;
        this.intentEntityMapper = intentEntityMapper;
    }

    /**
     * Save a intentEntity.
     *
     * @param intentEntityDTO the entity to save.
     * @return the persisted entity.
     */
    public IntentEntityDTO save(IntentEntityDTO intentEntityDTO) {
        LOG.debug("Request to save IntentEntity : {}", intentEntityDTO);
        IntentEntity intentEntity = intentEntityMapper.toEntity(intentEntityDTO);
        intentEntity = intentEntityRepository.save(intentEntity);
        return intentEntityMapper.toDto(intentEntity);
    }

    /**
     * Update a intentEntity.
     *
     * @param intentEntityDTO the entity to save.
     * @return the persisted entity.
     */
    public IntentEntityDTO update(IntentEntityDTO intentEntityDTO) {
        LOG.debug("Request to update IntentEntity : {}", intentEntityDTO);
        IntentEntity intentEntity = intentEntityMapper.toEntity(intentEntityDTO);
        intentEntity = intentEntityRepository.save(intentEntity);
        return intentEntityMapper.toDto(intentEntity);
    }

    /**
     * Partially update a intentEntity.
     *
     * @param intentEntityDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<IntentEntityDTO> partialUpdate(IntentEntityDTO intentEntityDTO) {
        LOG.debug("Request to partially update IntentEntity : {}", intentEntityDTO);

        return intentEntityRepository
            .findById(intentEntityDTO.getId())
            .map(existingIntentEntity -> {
                intentEntityMapper.partialUpdate(existingIntentEntity, intentEntityDTO);

                return existingIntentEntity;
            })
            .map(intentEntityRepository::save)
            .map(intentEntityMapper::toDto);
    }

    /**
     * Get all the intentEntities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<IntentEntityDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all IntentEntities");
        return intentEntityRepository.findAll(pageable).map(intentEntityMapper::toDto);
    }

    /**
     * Get one intentEntity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<IntentEntityDTO> findOne(Long id) {
        LOG.debug("Request to get IntentEntity : {}", id);
        return intentEntityRepository.findById(id).map(intentEntityMapper::toDto);
    }

    /**
     * Delete the intentEntity by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete IntentEntity : {}", id);
        intentEntityRepository.deleteById(id);
    }
}
