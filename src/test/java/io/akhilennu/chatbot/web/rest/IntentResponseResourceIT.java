package io.akhilennu.chatbot.web.rest;

import static io.akhilennu.chatbot.domain.IntentResponseAsserts.*;
import static io.akhilennu.chatbot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.akhilennu.chatbot.IntegrationTest;
import io.akhilennu.chatbot.domain.IntentResponse;
import io.akhilennu.chatbot.repository.IntentResponseRepository;
import io.akhilennu.chatbot.service.dto.IntentResponseDTO;
import io.akhilennu.chatbot.service.mapper.IntentResponseMapper;
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
 * Integration tests for the {@link IntentResponseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class IntentResponseResourceIT {

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/intent-responses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private IntentResponseRepository intentResponseRepository;

    @Autowired
    private IntentResponseMapper intentResponseMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restIntentResponseMockMvc;

    private IntentResponse intentResponse;

    private IntentResponse insertedIntentResponse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IntentResponse createEntity() {
        return new IntentResponse().message(DEFAULT_MESSAGE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static IntentResponse createUpdatedEntity() {
        return new IntentResponse().message(UPDATED_MESSAGE);
    }

    @BeforeEach
    void initTest() {
        intentResponse = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedIntentResponse != null) {
            intentResponseRepository.delete(insertedIntentResponse);
            insertedIntentResponse = null;
        }
    }

    @Test
    @Transactional
    void createIntentResponse() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the IntentResponse
        IntentResponseDTO intentResponseDTO = intentResponseMapper.toDto(intentResponse);
        var returnedIntentResponseDTO = om.readValue(
            restIntentResponseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(intentResponseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            IntentResponseDTO.class
        );

        // Validate the IntentResponse in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedIntentResponse = intentResponseMapper.toEntity(returnedIntentResponseDTO);
        assertIntentResponseUpdatableFieldsEquals(returnedIntentResponse, getPersistedIntentResponse(returnedIntentResponse));

        insertedIntentResponse = returnedIntentResponse;
    }

    @Test
    @Transactional
    void createIntentResponseWithExistingId() throws Exception {
        // Create the IntentResponse with an existing ID
        intentResponse.setId(1L);
        IntentResponseDTO intentResponseDTO = intentResponseMapper.toDto(intentResponse);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restIntentResponseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(intentResponseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the IntentResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMessageIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        intentResponse.setMessage(null);

        // Create the IntentResponse, which fails.
        IntentResponseDTO intentResponseDTO = intentResponseMapper.toDto(intentResponse);

        restIntentResponseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(intentResponseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllIntentResponses() throws Exception {
        // Initialize the database
        insertedIntentResponse = intentResponseRepository.saveAndFlush(intentResponse);

        // Get all the intentResponseList
        restIntentResponseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(intentResponse.getId().intValue())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)));
    }

    @Test
    @Transactional
    void getIntentResponse() throws Exception {
        // Initialize the database
        insertedIntentResponse = intentResponseRepository.saveAndFlush(intentResponse);

        // Get the intentResponse
        restIntentResponseMockMvc
            .perform(get(ENTITY_API_URL_ID, intentResponse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(intentResponse.getId().intValue()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE));
    }

    @Test
    @Transactional
    void getNonExistingIntentResponse() throws Exception {
        // Get the intentResponse
        restIntentResponseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingIntentResponse() throws Exception {
        // Initialize the database
        insertedIntentResponse = intentResponseRepository.saveAndFlush(intentResponse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the intentResponse
        IntentResponse updatedIntentResponse = intentResponseRepository.findById(intentResponse.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedIntentResponse are not directly saved in db
        em.detach(updatedIntentResponse);
        updatedIntentResponse.message(UPDATED_MESSAGE);
        IntentResponseDTO intentResponseDTO = intentResponseMapper.toDto(updatedIntentResponse);

        restIntentResponseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, intentResponseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(intentResponseDTO))
            )
            .andExpect(status().isOk());

        // Validate the IntentResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedIntentResponseToMatchAllProperties(updatedIntentResponse);
    }

    @Test
    @Transactional
    void putNonExistingIntentResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intentResponse.setId(longCount.incrementAndGet());

        // Create the IntentResponse
        IntentResponseDTO intentResponseDTO = intentResponseMapper.toDto(intentResponse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIntentResponseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, intentResponseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(intentResponseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntentResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchIntentResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intentResponse.setId(longCount.incrementAndGet());

        // Create the IntentResponse
        IntentResponseDTO intentResponseDTO = intentResponseMapper.toDto(intentResponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntentResponseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(intentResponseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntentResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamIntentResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intentResponse.setId(longCount.incrementAndGet());

        // Create the IntentResponse
        IntentResponseDTO intentResponseDTO = intentResponseMapper.toDto(intentResponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntentResponseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(intentResponseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the IntentResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateIntentResponseWithPatch() throws Exception {
        // Initialize the database
        insertedIntentResponse = intentResponseRepository.saveAndFlush(intentResponse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the intentResponse using partial update
        IntentResponse partialUpdatedIntentResponse = new IntentResponse();
        partialUpdatedIntentResponse.setId(intentResponse.getId());

        partialUpdatedIntentResponse.message(UPDATED_MESSAGE);

        restIntentResponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIntentResponse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedIntentResponse))
            )
            .andExpect(status().isOk());

        // Validate the IntentResponse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIntentResponseUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedIntentResponse, intentResponse),
            getPersistedIntentResponse(intentResponse)
        );
    }

    @Test
    @Transactional
    void fullUpdateIntentResponseWithPatch() throws Exception {
        // Initialize the database
        insertedIntentResponse = intentResponseRepository.saveAndFlush(intentResponse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the intentResponse using partial update
        IntentResponse partialUpdatedIntentResponse = new IntentResponse();
        partialUpdatedIntentResponse.setId(intentResponse.getId());

        partialUpdatedIntentResponse.message(UPDATED_MESSAGE);

        restIntentResponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIntentResponse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedIntentResponse))
            )
            .andExpect(status().isOk());

        // Validate the IntentResponse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertIntentResponseUpdatableFieldsEquals(partialUpdatedIntentResponse, getPersistedIntentResponse(partialUpdatedIntentResponse));
    }

    @Test
    @Transactional
    void patchNonExistingIntentResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intentResponse.setId(longCount.incrementAndGet());

        // Create the IntentResponse
        IntentResponseDTO intentResponseDTO = intentResponseMapper.toDto(intentResponse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIntentResponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, intentResponseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(intentResponseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntentResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchIntentResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intentResponse.setId(longCount.incrementAndGet());

        // Create the IntentResponse
        IntentResponseDTO intentResponseDTO = intentResponseMapper.toDto(intentResponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntentResponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(intentResponseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the IntentResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamIntentResponse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        intentResponse.setId(longCount.incrementAndGet());

        // Create the IntentResponse
        IntentResponseDTO intentResponseDTO = intentResponseMapper.toDto(intentResponse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restIntentResponseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(intentResponseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the IntentResponse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteIntentResponse() throws Exception {
        // Initialize the database
        insertedIntentResponse = intentResponseRepository.saveAndFlush(intentResponse);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the intentResponse
        restIntentResponseMockMvc
            .perform(delete(ENTITY_API_URL_ID, intentResponse.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return intentResponseRepository.count();
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

    protected IntentResponse getPersistedIntentResponse(IntentResponse intentResponse) {
        return intentResponseRepository.findById(intentResponse.getId()).orElseThrow();
    }

    protected void assertPersistedIntentResponseToMatchAllProperties(IntentResponse expectedIntentResponse) {
        assertIntentResponseAllPropertiesEquals(expectedIntentResponse, getPersistedIntentResponse(expectedIntentResponse));
    }

    protected void assertPersistedIntentResponseToMatchUpdatableProperties(IntentResponse expectedIntentResponse) {
        assertIntentResponseAllUpdatablePropertiesEquals(expectedIntentResponse, getPersistedIntentResponse(expectedIntentResponse));
    }
}
