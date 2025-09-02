package io.akhilennu.chatbot.service;

import io.akhilennu.chatbot.domain.Bot;
import io.akhilennu.chatbot.repository.BotRepository;
import io.akhilennu.chatbot.service.dto.BotDTO;
import io.akhilennu.chatbot.service.mapper.BotMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link io.akhilennu.chatbot.domain.Bot}.
 */
@Service
@Transactional
public class BotService {

    private static final Logger LOG = LoggerFactory.getLogger(BotService.class);

    private final BotRepository botRepository;

    private final BotMapper botMapper;

    public BotService(BotRepository botRepository, BotMapper botMapper) {
        this.botRepository = botRepository;
        this.botMapper = botMapper;
    }

    /**
     * Save a bot.
     *
     * @param botDTO the entity to save.
     * @return the persisted entity.
     */
    public BotDTO save(BotDTO botDTO) {
        LOG.debug("Request to save Bot : {}", botDTO);
        Bot bot = botMapper.toEntity(botDTO);
        bot = botRepository.save(bot);
        return botMapper.toDto(bot);
    }

    /**
     * Update a bot.
     *
     * @param botDTO the entity to save.
     * @return the persisted entity.
     */
    public BotDTO update(BotDTO botDTO) {
        LOG.debug("Request to update Bot : {}", botDTO);
        Bot bot = botMapper.toEntity(botDTO);
        bot = botRepository.save(bot);
        return botMapper.toDto(bot);
    }

    /**
     * Partially update a bot.
     *
     * @param botDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BotDTO> partialUpdate(BotDTO botDTO) {
        LOG.debug("Request to partially update Bot : {}", botDTO);

        return botRepository
            .findById(botDTO.getId())
            .map(existingBot -> {
                botMapper.partialUpdate(existingBot, botDTO);

                return existingBot;
            })
            .map(botRepository::save)
            .map(botMapper::toDto);
    }

    /**
     * Get all the bots.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BotDTO> findAll() {
        LOG.debug("Request to get all Bots");
        return botRepository.findAll().stream().map(botMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one bot by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BotDTO> findOne(Long id) {
        LOG.debug("Request to get Bot : {}", id);
        return botRepository.findById(id).map(botMapper::toDto);
    }

    /**
     * Delete the bot by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Bot : {}", id);
        botRepository.deleteById(id);
    }
}
