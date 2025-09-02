package io.akhilennu.chatbot.web.rest;

import static io.akhilennu.chatbot.domain.IntentAsserts.*;
import static io.akhilennu.chatbot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.akhilennu.chatbot.IntegrationTest;
import io.akhilennu.chatbot.domain.Intent;
import io.akhilennu.chatbot.repository.IntentRepository;
import io.akhilennu.chatbot.service.IntentService;
import io.akhilennu.chatbot.service.dto.IntentDTO;
import io.akhilennu.chatbot.service.mapper.IntentMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link IntentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class IntentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/intents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private IntentRepository intentRepository;

    @Mock
    private IntentRepository intentRepositoryMock;

    @Autowired
    private IntentMapper intentMapper;

    @Mock
    private IntentService intentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIntentMockMvc;

    private Intent intent;

    private Intent insertedIntent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Intent createEntity() {
        return new Intent().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Intent createUpdatedEntity() {
        return new Intent().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        intent = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedIntent != null) {
            intentRepository.delete(insertedIntent);
            insertedIntent = null;
        }
    }

    @Test
    @Transactional
    void createIntent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Intent
        IntentDTO intentDTO = intentMapper.toDto(intent);
        var returnedIntentDTO = om.readValue(
            restIntentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(intentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            IntentDTO.class
        );

        // Validate the Intent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedIntent = intentMapper.toEntity(returnedIntentDTO);
        assertIntentUpdatableFieldsEquals(returnedIntent, getPersistedIntent(returnedIntent));

        insertedIntent = returnedIntent;
    }

    @Test
    @Transactional
    void createIntentWithExistingId() throws Exception {
        // Create the Intent with an existing ID
        intent.setId(1L);
        IntentDTO intentDTO = intentMapper.toDto(intent);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restIntentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(intentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Intent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        intent.setName(null);

        // Create the Intent, which fails.
        IntentDTO intentDTO = intentMapper.toDto(intent);

        restIntentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(intentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllIntents() throws Exception {
        // Initialize the database
        insertedIntent = intentRepository.saveAndFlush(intent);

        // Get all the intentList
        restIntentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(intent.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIntentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(intentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restIntentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(intentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllIntentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(intentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restIntentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(intentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getIntent() throws Exception {
        // Initialize the database
        insertedIntent = intentRepository.saveAndFlush(intent);

        // Get the intent
        restIntentMockMvc
            .perform(get(ENTITY_API_URL_ID, intent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(intent.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingIntent() throws Exception {
        // Get the intent
        restIntentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingIntent() throws Exception {
        // Initialize the database
        insertedIntent = intentRepository.saveAndFlush(intent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the intent
        Intent updatedIntent = intentRepository.findById(intent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedIntent are not directly saved in db
        em.detach(updatedIntent);
        updatedIntent.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        IntentDTO intentDTO = intentMapper.toDto(updatedIntent);

        restIntentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, intentDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(intentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Intent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedIntentToMatchAllProperties(updatedIntent);
    }

    @Test
    @Transactional
    void putNonExistingIntent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intent.setId(longCount.incrementAndGet());

        // Create the Intent
        IntentDTO intentDTO = intentMapper.toDto(intent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIntentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, intentDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(intentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Intent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchIntent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intent.setId(longCount.incrementAndGet());

        // Create the Intent
        IntentDTO intentDTO = intentMapper.toDto(intent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(intentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Intent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIntent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intent.setId(longCount.incrementAndGet());

        // Create the Intent
        IntentDTO intentDTO = intentMapper.toDto(intent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(intentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Intent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateIntentWithPatch() throws Exception {
        // Initialize the database
        insertedIntent = intentRepository.saveAndFlush(intent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the intent using partial update
        Intent partialUpdatedIntent = new Intent();
        partialUpdatedIntent.setId(intent.getId());

        partialUpdatedIntent.description(UPDATED_DESCRIPTION);

        restIntentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIntent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedIntent))
            )
            .andExpect(status().isOk());

        // Validate the Intent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIntentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedIntent, intent), getPersistedIntent(intent));
    }

    @Test
    @Transactional
    void fullUpdateIntentWithPatch() throws Exception {
        // Initialize the database
        insertedIntent = intentRepository.saveAndFlush(intent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the intent using partial update
        Intent partialUpdatedIntent = new Intent();
        partialUpdatedIntent.setId(intent.getId());

        partialUpdatedIntent.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restIntentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIntent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedIntent))
            )
            .andExpect(status().isOk());

        // Validate the Intent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIntentUpdatableFieldsEquals(partialUpdatedIntent, getPersistedIntent(partialUpdatedIntent));
    }

    @Test
    @Transactional
    void patchNonExistingIntent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intent.setId(longCount.incrementAndGet());

        // Create the Intent
        IntentDTO intentDTO = intentMapper.toDto(intent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIntentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, intentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(intentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Intent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIntent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intent.setId(longCount.incrementAndGet());

        // Create the Intent
        IntentDTO intentDTO = intentMapper.toDto(intent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(intentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Intent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIntent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intent.setId(longCount.incrementAndGet());

        // Create the Intent
        IntentDTO intentDTO = intentMapper.toDto(intent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(intentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Intent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteIntent() throws Exception {
        // Initialize the database
        insertedIntent = intentRepository.saveAndFlush(intent);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the intent
        restIntentMockMvc
            .perform(delete(ENTITY_API_URL_ID, intent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return intentRepository.count();
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

    protected Intent getPersistedIntent(Intent intent) {
        return intentRepository.findById(intent.getId()).orElseThrow();
    }

    protected void assertPersistedIntentToMatchAllProperties(Intent expectedIntent) {
        assertIntentAllPropertiesEquals(expectedIntent, getPersistedIntent(expectedIntent));
    }

    protected void assertPersistedIntentToMatchUpdatableProperties(Intent expectedIntent) {
        assertIntentAllUpdatablePropertiesEquals(expectedIntent, getPersistedIntent(expectedIntent));
    }
}
