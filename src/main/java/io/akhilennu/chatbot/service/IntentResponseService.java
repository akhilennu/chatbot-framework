package io.akhilennu.chatbot.service;

import io.akhilennu.chatbot.domain.IntentResponse;
import io.akhilennu.chatbot.repository.IntentResponseRepository;
import io.akhilennu.chatbot.service.dto.IntentResponseDTO;
import io.akhilennu.chatbot.service.mapper.IntentResponseMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.akhilennu.chatbot.domain.IntentResponse}.
 */
@Service
@Transactional
public class IntentResponseService {

    private static final Logger LOG = LoggerFactory.getLogger(IntentResponseService.class);

    private final IntentResponseRepository intentResponseRepository;

    private final IntentResponseMapper intentResponseMapper;

    public IntentResponseService(IntentResponseRepository intentResponseRepository, IntentResponseMapper intentResponseMapper) {
        this.intentResponseRepository = intentResponseRepository;
        this.intentResponseMapper = intentResponseMapper;
    }

    /**
     * Save a intentResponse.
     *
     * @param intentResponseDTO the entity to save.
     * @return the persisted entity.
     */
    public IntentResponseDTO save(IntentResponseDTO intentResponseDTO) {
        LOG.debug("Request to save IntentResponse : {}", intentResponseDTO);
        IntentResponse intentResponse = intentResponseMapper.toEntity(intentResponseDTO);
        intentResponse = intentResponseRepository.save(intentResponse);
        return intentResponseMapper.toDto(intentResponse);
    }

    /**
     * Update a intentResponse.
     *
     * @param intentResponseDTO the entity to save.
     * @return the persisted entity.
     */
    public IntentResponseDTO update(IntentResponseDTO intentResponseDTO) {
        LOG.debug("Request to update IntentResponse : {}", intentResponseDTO);
        IntentResponse intentResponse = intentResponseMapper.toEntity(intentResponseDTO);
        intentResponse = intentResponseRepository.save(intentResponse);
        return intentResponseMapper.toDto(intentResponse);
    }

    /**
     * Partially update a intentResponse.
     *
     * @param intentResponseDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<IntentResponseDTO> partialUpdate(IntentResponseDTO intentResponseDTO) {
        LOG.debug("Request to partially update IntentResponse : {}", intentResponseDTO);

        return intentResponseRepository
            .findById(intentResponseDTO.getId())
            .map(existingIntentResponse -> {
                intentResponseMapper.partialUpdate(existingIntentResponse, intentResponseDTO);

                return existingIntentResponse;
            })
            .map(intentResponseRepository::save)
            .map(intentResponseMapper::toDto);
    }

    /**
     * Get all the intentResponses.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<IntentResponseDTO> findAll() {
        LOG.debug("Request to get all IntentResponses");
        return intentResponseRepository
            .findAll()
            .stream()
            .map(intentResponseMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the intentResponses where Intent is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<IntentResponseDTO> findAllWhereIntentIsNull() {
        LOG.debug("Request to get all intentResponses where Intent is null");
        return StreamSupport.stream(intentResponseRepository.findAll().spliterator(), false)
            .filter(intentResponse -> intentResponse.getIntent() == null)
            .map(intentResponseMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one intentResponse by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<IntentResponseDTO> findOne(Long id) {
        LOG.debug("Request to get IntentResponse : {}", id);
        return intentResponseRepository.findById(id).map(intentResponseMapper::toDto);
    }

    /**
     * Delete the intentResponse by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete IntentResponse : {}", id);
        intentResponseRepository.deleteById(id);
    }
}
