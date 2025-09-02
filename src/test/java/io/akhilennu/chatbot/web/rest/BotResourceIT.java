package io.akhilennu.chatbot.web.rest;

import static io.akhilennu.chatbot.domain.BotAsserts.*;
import static io.akhilennu.chatbot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.akhilennu.chatbot.IntegrationTest;
import io.akhilennu.chatbot.domain.Bot;
import io.akhilennu.chatbot.repository.BotRepository;
import io.akhilennu.chatbot.service.dto.BotDTO;
import io.akhilennu.chatbot.service.mapper.BotMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BotResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BotResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/bots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private BotMapper botMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBotMockMvc;

    private Bot bot;

    private Bot insertedBot;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bot createEntity() {
        return new Bot().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bot createUpdatedEntity() {
        return new Bot().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        bot = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBot != null) {
            botRepository.delete(insertedBot);
            insertedBot = null;
        }
    }

    @Test
    @Transactional
    void createBot() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Bot
        BotDTO botDTO = botMapper.toDto(bot);
        var returnedBotDTO = om.readValue(
            restBotMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(botDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BotDTO.class
        );

        // Validate the Bot in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBot = botMapper.toEntity(returnedBotDTO);
        assertBotUpdatableFieldsEquals(returnedBot, getPersistedBot(returnedBot));

        insertedBot = returnedBot;
    }

    @Test
    @Transactional
    void createBotWithExistingId() throws Exception {
        // Create the Bot with an existing ID
        bot.setId(1L);
        BotDTO botDTO = botMapper.toDto(bot);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(botDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Bot in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        bot.setName(null);

        // Create the Bot, which fails.
        BotDTO botDTO = botMapper.toDto(bot);

        restBotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(botDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBots() throws Exception {
        // Initialize the database
        insertedBot = botRepository.saveAndFlush(bot);

        // Get all the botList
        restBotMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bot.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getBot() throws Exception {
        // Initialize the database
        insertedBot = botRepository.saveAndFlush(bot);

        // Get the bot
        restBotMockMvc
            .perform(get(ENTITY_API_URL_ID, bot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bot.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingBot() throws Exception {
        // Get the bot
        restBotMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBot() throws Exception {
        // Initialize the database
        insertedBot = botRepository.saveAndFlush(bot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bot
        Bot updatedBot = botRepository.findById(bot.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBot are not directly saved in db
        em.detach(updatedBot);
        updatedBot.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).active(UPDATED_ACTIVE);
        BotDTO botDTO = botMapper.toDto(updatedBot);

        restBotMockMvc
            .perform(put(ENTITY_API_URL_ID, botDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(botDTO)))
            .andExpect(status().isOk());

        // Validate the Bot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBotToMatchAllProperties(updatedBot);
    }

    @Test
    @Transactional
    void putNonExistingBot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bot.setId(longCount.incrementAndGet());

        // Create the Bot
        BotDTO botDTO = botMapper.toDto(bot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBotMockMvc
            .perform(put(ENTITY_API_URL_ID, botDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(botDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Bot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bot.setId(longCount.incrementAndGet());

        // Create the Bot
        BotDTO botDTO = botMapper.toDto(bot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(botDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bot.setId(longCount.incrementAndGet());

        // Create the Bot
        BotDTO botDTO = botMapper.toDto(bot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBotMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(botDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBotWithPatch() throws Exception {
        // Initialize the database
        insertedBot = botRepository.saveAndFlush(bot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bot using partial update
        Bot partialUpdatedBot = new Bot();
        partialUpdatedBot.setId(bot.getId());

        partialUpdatedBot.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restBotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBot.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBot))
            )
            .andExpect(status().isOk());

        // Validate the Bot in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBotUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBot, bot), getPersistedBot(bot));
    }

    @Test
    @Transactional
    void fullUpdateBotWithPatch() throws Exception {
        // Initialize the database
        insertedBot = botRepository.saveAndFlush(bot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bot using partial update
        Bot partialUpdatedBot = new Bot();
        partialUpdatedBot.setId(bot.getId());

        partialUpdatedBot.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).active(UPDATED_ACTIVE);

        restBotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBot.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBot))
            )
            .andExpect(status().isOk());

        // Validate the Bot in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBotUpdatableFieldsEquals(partialUpdatedBot, getPersistedBot(partialUpdatedBot));
    }

    @Test
    @Transactional
    void patchNonExistingBot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bot.setId(longCount.incrementAndGet());

        // Create the Bot
        BotDTO botDTO = botMapper.toDto(bot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, botDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(botDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bot.setId(longCount.incrementAndGet());

        // Create the Bot
        BotDTO botDTO = botMapper.toDto(bot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(botDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bot.setId(longCount.incrementAndGet());

        // Create the Bot
        BotDTO botDTO = botMapper.toDto(bot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBotMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(botDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBot() throws Exception {
        // Initialize the database
        insertedBot = botRepository.saveAndFlush(bot);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the bot
        restBotMockMvc.perform(delete(ENTITY_API_URL_ID, bot.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return botRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Bot getPersistedBot(Bot bot) {
        return botRepository.findById(bot.getId()).orElseThrow();
    }

    protected void assertPersistedBotToMatchAllProperties(Bot expectedBot) {
        assertBotAllPropertiesEquals(expectedBot, getPersistedBot(expectedBot));
    }

    protected void assertPersistedBotToMatchUpdatableProperties(Bot expectedBot) {
        assertBotAllUpdatablePropertiesEquals(expectedBot, getPersistedBot(expectedBot));
    }
}
