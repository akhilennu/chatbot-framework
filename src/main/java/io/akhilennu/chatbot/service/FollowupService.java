package io.akhilennu.chatbot.service;

import io.akhilennu.chatbot.domain.Followup;
import io.akhilennu.chatbot.repository.FollowupRepository;
import io.akhilennu.chatbot.service.dto.FollowupDTO;
import io.akhilennu.chatbot.service.mapper.FollowupMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.akhilennu.chatbot.domain.Followup}.
 */
@Service
@Transactional
public class FollowupService {

    private static final Logger LOG = LoggerFactory.getLogger(FollowupService.class);

    private final FollowupRepository followupRepository;

    private final FollowupMapper followupMapper;

    public FollowupService(FollowupRepository followupRepository, FollowupMapper followupMapper) {
        this.followupRepository = followupRepository;
        this.followupMapper = followupMapper;
    }

    /**
     * Save a followup.
     *
     * @param followupDTO the entity to save.
     * @return the persisted entity.
     */
    public FollowupDTO save(FollowupDTO followupDTO) {
        LOG.debug("Request to save Followup : {}", followupDTO);
        Followup followup = followupMapper.toEntity(followupDTO);
        followup = followupRepository.save(followup);
        return followupMapper.toDto(followup);
    }

    /**
     * Update a followup.
     *
     * @param followupDTO the entity to save.
     * @return the persisted entity.
     */
    public FollowupDTO update(FollowupDTO followupDTO) {
        LOG.debug("Request to update Followup : {}", followupDTO);
        Followup followup = followupMapper.toEntity(followupDTO);
        followup = followupRepository.save(followup);
        return followupMapper.toDto(followup);
    }

    /**
     * Partially update a followup.
     *
     * @param followupDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FollowupDTO> partialUpdate(FollowupDTO followupDTO) {
        LOG.debug("Request to partially update Followup : {}", followupDTO);

        return followupRepository
            .findById(followupDTO.getId())
            .map(existingFollowup -> {
                followupMapper.partialUpdate(existingFollowup, followupDTO);

                return existingFollowup;
            })
            .map(followupRepository::save)
            .map(followupMapper::toDto);
    }

    /**
     * Get all the followups.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FollowupDTO> findAll() {
        LOG.debug("Request to get all Followups");
        return followupRepository.findAll().stream().map(followupMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the followups with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<FollowupDTO> findAllWithEagerRelationships(Pageable pageable) {
        return followupRepository.findAllWithEagerRelationships(pageable).map(followupMapper::toDto);
    }

    /**
     * Get one followup by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FollowupDTO> findOne(Long id) {
        LOG.debug("Request to get Followup : {}", id);
        return followupRepository.findOneWithEagerRelationships(id).map(followupMapper::toDto);
    }

    /**
     * Delete the followup by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Followup : {}", id);
        followupRepository.deleteById(id);
    }
}
