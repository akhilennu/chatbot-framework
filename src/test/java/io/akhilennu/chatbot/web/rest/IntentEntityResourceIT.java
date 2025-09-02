package io.akhilennu.chatbot.web.rest;

import static io.akhilennu.chatbot.domain.IntentEntityAsserts.*;
import static io.akhilennu.chatbot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.akhilennu.chatbot.IntegrationTest;
import io.akhilennu.chatbot.domain.IntentEntity;
import io.akhilennu.chatbot.repository.IntentEntityRepository;
import io.akhilennu.chatbot.service.dto.IntentEntityDTO;
import io.akhilennu.chatbot.service.mapper.IntentEntityMapper;
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
 * Integration tests for the {@link IntentEntityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class IntentEntityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_OPTIONAL = false;
    private static final Boolean UPDATED_OPTIONAL = true;

    private static final String ENTITY_API_URL = "/api/intent-entities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private IntentEntityRepository intentEntityRepository;

    @Autowired
    private IntentEntityMapper intentEntityMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIntentEntityMockMvc;

    private IntentEntity intentEntity;

    private IntentEntity insertedIntentEntity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IntentEntity createEntity() {
        return new IntentEntity().name(DEFAULT_NAME).optional(DEFAULT_OPTIONAL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IntentEntity createUpdatedEntity() {
        return new IntentEntity().name(UPDATED_NAME).optional(UPDATED_OPTIONAL);
    }

    @BeforeEach
    void initTest() {
        intentEntity = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedIntentEntity != null) {
            intentEntityRepository.delete(insertedIntentEntity);
            insertedIntentEntity = null;
        }
    }

    @Test
    @Transactional
    void createIntentEntity() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the IntentEntity
        IntentEntityDTO intentEntityDTO = intentEntityMapper.toDto(intentEntity);
        var returnedIntentEntityDTO = om.readValue(
            restIntentEntityMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(intentEntityDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            IntentEntityDTO.class
        );

        // Validate the IntentEntity in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedIntentEntity = intentEntityMapper.toEntity(returnedIntentEntityDTO);
        assertIntentEntityUpdatableFieldsEquals(returnedIntentEntity, getPersistedIntentEntity(returnedIntentEntity));

        insertedIntentEntity = returnedIntentEntity;
    }

    @Test
    @Transactional
    void createIntentEntityWithExistingId() throws Exception {
        // Create the IntentEntity with an existing ID
        intentEntity.setId(1L);
        IntentEntityDTO intentEntityDTO = intentEntityMapper.toDto(intentEntity);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restIntentEntityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(intentEntityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the IntentEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        intentEntity.setName(null);

        // Create the IntentEntity, which fails.
        IntentEntityDTO intentEntityDTO = intentEntityMapper.toDto(intentEntity);

        restIntentEntityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(intentEntityDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllIntentEntities() throws Exception {
        // Initialize the database
        insertedIntentEntity = intentEntityRepository.saveAndFlush(intentEntity);

        // Get all the intentEntityList
        restIntentEntityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(intentEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].optional").value(hasItem(DEFAULT_OPTIONAL)));
    }

    @Test
    @Transactional
    void getIntentEntity() throws Exception {
        // Initialize the database
        insertedIntentEntity = intentEntityRepository.saveAndFlush(intentEntity);

        // Get the intentEntity
        restIntentEntityMockMvc
            .perform(get(ENTITY_API_URL_ID, intentEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(intentEntity.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.optional").value(DEFAULT_OPTIONAL));
    }

    @Test
    @Transactional
    void getNonExistingIntentEntity() throws Exception {
        // Get the intentEntity
        restIntentEntityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingIntentEntity() throws Exception {
        // Initialize the database
        insertedIntentEntity = intentEntityRepository.saveAndFlush(intentEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the intentEntity
        IntentEntity updatedIntentEntity = intentEntityRepository.findById(intentEntity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedIntentEntity are not directly saved in db
        em.detach(updatedIntentEntity);
        updatedIntentEntity.name(UPDATED_NAME).optional(UPDATED_OPTIONAL);
        IntentEntityDTO intentEntityDTO = intentEntityMapper.toDto(updatedIntentEntity);

        restIntentEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, intentEntityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(intentEntityDTO))
            )
            .andExpect(status().isOk());

        // Validate the IntentEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedIntentEntityToMatchAllProperties(updatedIntentEntity);
    }

    @Test
    @Transactional
    void putNonExistingIntentEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intentEntity.setId(longCount.incrementAndGet());

        // Create the IntentEntity
        IntentEntityDTO intentEntityDTO = intentEntityMapper.toDto(intentEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIntentEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, intentEntityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(intentEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntentEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchIntentEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intentEntity.setId(longCount.incrementAndGet());

        // Create the IntentEntity
        IntentEntityDTO intentEntityDTO = intentEntityMapper.toDto(intentEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntentEntityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(intentEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntentEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIntentEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intentEntity.setId(longCount.incrementAndGet());

        // Create the IntentEntity
        IntentEntityDTO intentEntityDTO = intentEntityMapper.toDto(intentEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntentEntityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(intentEntityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the IntentEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateIntentEntityWithPatch() throws Exception {
        // Initialize the database
        insertedIntentEntity = intentEntityRepository.saveAndFlush(intentEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the intentEntity using partial update
        IntentEntity partialUpdatedIntentEntity = new IntentEntity();
        partialUpdatedIntentEntity.setId(intentEntity.getId());

        partialUpdatedIntentEntity.optional(UPDATED_OPTIONAL);

        restIntentEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIntentEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedIntentEntity))
            )
            .andExpect(status().isOk());

        // Validate the IntentEntity in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIntentEntityUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedIntentEntity, intentEntity),
            getPersistedIntentEntity(intentEntity)
        );
    }

    @Test
    @Transactional
    void fullUpdateIntentEntityWithPatch() throws Exception {
        // Initialize the database
        insertedIntentEntity = intentEntityRepository.saveAndFlush(intentEntity);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the intentEntity using partial update
        IntentEntity partialUpdatedIntentEntity = new IntentEntity();
        partialUpdatedIntentEntity.setId(intentEntity.getId());

        partialUpdatedIntentEntity.name(UPDATED_NAME).optional(UPDATED_OPTIONAL);

        restIntentEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIntentEntity.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedIntentEntity))
            )
            .andExpect(status().isOk());

        // Validate the IntentEntity in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIntentEntityUpdatableFieldsEquals(partialUpdatedIntentEntity, getPersistedIntentEntity(partialUpdatedIntentEntity));
    }

    @Test
    @Transactional
    void patchNonExistingIntentEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intentEntity.setId(longCount.incrementAndGet());

        // Create the IntentEntity
        IntentEntityDTO intentEntityDTO = intentEntityMapper.toDto(intentEntity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIntentEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, intentEntityDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(intentEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntentEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIntentEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intentEntity.setId(longCount.incrementAndGet());

        // Create the IntentEntity
        IntentEntityDTO intentEntityDTO = intentEntityMapper.toDto(intentEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntentEntityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(intentEntityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntentEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIntentEntity() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intentEntity.setId(longCount.incrementAndGet());

        // Create the IntentEntity
        IntentEntityDTO intentEntityDTO = intentEntityMapper.toDto(intentEntity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntentEntityMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(intentEntityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the IntentEntity in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteIntentEntity() throws Exception {
        // Initialize the database
        insertedIntentEntity = intentEntityRepository.saveAndFlush(intentEntity);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the intentEntity
        restIntentEntityMockMvc
            .perform(delete(ENTITY_API_URL_ID, intentEntity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return intentEntityRepository.count();
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

    protected IntentEntity getPersistedIntentEntity(IntentEntity intentEntity) {
        return intentEntityRepository.findById(intentEntity.getId()).orElseThrow();
    }

    protected void assertPersistedIntentEntityToMatchAllProperties(IntentEntity expectedIntentEntity) {
        assertIntentEntityAllPropertiesEquals(expectedIntentEntity, getPersistedIntentEntity(expectedIntentEntity));
    }

    protected void assertPersistedIntentEntityToMatchUpdatableProperties(IntentEntity expectedIntentEntity) {
        assertIntentEntityAllUpdatablePropertiesEquals(expectedIntentEntity, getPersistedIntentEntity(expectedIntentEntity));
    }
}
