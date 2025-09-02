package io.akhilennu.chatbot.web.rest;

import static io.akhilennu.chatbot.domain.FollowupAsserts.*;
import static io.akhilennu.chatbot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.akhilennu.chatbot.IntegrationTest;
import io.akhilennu.chatbot.domain.Followup;
import io.akhilennu.chatbot.repository.FollowupRepository;
import io.akhilennu.chatbot.service.FollowupService;
import io.akhilennu.chatbot.service.dto.FollowupDTO;
import io.akhilennu.chatbot.service.mapper.FollowupMapper;
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
 * Integration tests for the {@link FollowupResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FollowupResourceIT {

    private static final String DEFAULT_QUESTION = "AAAAAAAAAA";
    private static final String UPDATED_QUESTION = "BBBBBBBBBB";

    private static final String DEFAULT_TARGET_ENTITY = "AAAAAAAAAA";
    private static final String UPDATED_TARGET_ENTITY = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDER = 1;
    private static final Integer UPDATED_ORDER = 2;

    private static final String ENTITY_API_URL = "/api/followups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FollowupRepository followupRepository;

    @Mock
    private FollowupRepository followupRepositoryMock;

    @Autowired
    private FollowupMapper followupMapper;

    @Mock
    private FollowupService followupServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFollowupMockMvc;

    private Followup followup;

    private Followup insertedFollowup;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Followup createEntity() {
        return new Followup().question(DEFAULT_QUESTION).targetEntity(DEFAULT_TARGET_ENTITY).order(DEFAULT_ORDER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Followup createUpdatedEntity() {
        return new Followup().question(UPDATED_QUESTION).targetEntity(UPDATED_TARGET_ENTITY).order(UPDATED_ORDER);
    }

    @BeforeEach
    void initTest() {
        followup = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFollowup != null) {
            followupRepository.delete(insertedFollowup);
            insertedFollowup = null;
        }
    }

    @Test
    @Transactional
    void createFollowup() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Followup
        FollowupDTO followupDTO = followupMapper.toDto(followup);
        var returnedFollowupDTO = om.readValue(
            restFollowupMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followupDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FollowupDTO.class
        );

        // Validate the Followup in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFollowup = followupMapper.toEntity(returnedFollowupDTO);
        assertFollowupUpdatableFieldsEquals(returnedFollowup, getPersistedFollowup(returnedFollowup));

        insertedFollowup = returnedFollowup;
    }

    @Test
    @Transactional
    void createFollowupWithExistingId() throws Exception {
        // Create the Followup with an existing ID
        followup.setId(1L);
        FollowupDTO followupDTO = followupMapper.toDto(followup);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFollowupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followupDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Followup in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuestionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        followup.setQuestion(null);

        // Create the Followup, which fails.
        FollowupDTO followupDTO = followupMapper.toDto(followup);

        restFollowupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followupDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTargetEntityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        followup.setTargetEntity(null);

        // Create the Followup, which fails.
        FollowupDTO followupDTO = followupMapper.toDto(followup);

        restFollowupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followupDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFollowups() throws Exception {
        // Initialize the database
        insertedFollowup = followupRepository.saveAndFlush(followup);

        // Get all the followupList
        restFollowupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(followup.getId().intValue())))
            .andExpect(jsonPath("$.[*].question").value(hasItem(DEFAULT_QUESTION)))
            .andExpect(jsonPath("$.[*].targetEntity").value(hasItem(DEFAULT_TARGET_ENTITY)))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFollowupsWithEagerRelationshipsIsEnabled() throws Exception {
        when(followupServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFollowupMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(followupServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFollowupsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(followupServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFollowupMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(followupRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFollowup() throws Exception {
        // Initialize the database
        insertedFollowup = followupRepository.saveAndFlush(followup);

        // Get the followup
        restFollowupMockMvc
            .perform(get(ENTITY_API_URL_ID, followup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(followup.getId().intValue()))
            .andExpect(jsonPath("$.question").value(DEFAULT_QUESTION))
            .andExpect(jsonPath("$.targetEntity").value(DEFAULT_TARGET_ENTITY))
            .andExpect(jsonPath("$.order").value(DEFAULT_ORDER));
    }

    @Test
    @Transactional
    void getNonExistingFollowup() throws Exception {
        // Get the followup
        restFollowupMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFollowup() throws Exception {
        // Initialize the database
        insertedFollowup = followupRepository.saveAndFlush(followup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the followup
        Followup updatedFollowup = followupRepository.findById(followup.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFollowup are not directly saved in db
        em.detach(updatedFollowup);
        updatedFollowup.question(UPDATED_QUESTION).targetEntity(UPDATED_TARGET_ENTITY).order(UPDATED_ORDER);
        FollowupDTO followupDTO = followupMapper.toDto(updatedFollowup);

        restFollowupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, followupDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(followupDTO))
            )
            .andExpect(status().isOk());

        // Validate the Followup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFollowupToMatchAllProperties(updatedFollowup);
    }

    @Test
    @Transactional
    void putNonExistingFollowup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followup.setId(longCount.incrementAndGet());

        // Create the Followup
        FollowupDTO followupDTO = followupMapper.toDto(followup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFollowupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, followupDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(followupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Followup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFollowup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followup.setId(longCount.incrementAndGet());

        // Create the Followup
        FollowupDTO followupDTO = followupMapper.toDto(followup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(followupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Followup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFollowup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followup.setId(longCount.incrementAndGet());

        // Create the Followup
        FollowupDTO followupDTO = followupMapper.toDto(followup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowupMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followupDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Followup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFollowupWithPatch() throws Exception {
        // Initialize the database
        insertedFollowup = followupRepository.saveAndFlush(followup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the followup using partial update
        Followup partialUpdatedFollowup = new Followup();
        partialUpdatedFollowup.setId(followup.getId());

        partialUpdatedFollowup.question(UPDATED_QUESTION).targetEntity(UPDATED_TARGET_ENTITY);

        restFollowupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFollowup.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFollowup))
            )
            .andExpect(status().isOk());

        // Validate the Followup in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFollowupUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFollowup, followup), getPersistedFollowup(followup));
    }

    @Test
    @Transactional
    void fullUpdateFollowupWithPatch() throws Exception {
        // Initialize the database
        insertedFollowup = followupRepository.saveAndFlush(followup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the followup using partial update
        Followup partialUpdatedFollowup = new Followup();
        partialUpdatedFollowup.setId(followup.getId());

        partialUpdatedFollowup.question(UPDATED_QUESTION).targetEntity(UPDATED_TARGET_ENTITY).order(UPDATED_ORDER);

        restFollowupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFollowup.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFollowup))
            )
            .andExpect(status().isOk());

        // Validate the Followup in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFollowupUpdatableFieldsEquals(partialUpdatedFollowup, getPersistedFollowup(partialUpdatedFollowup));
    }

    @Test
    @Transactional
    void patchNonExistingFollowup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followup.setId(longCount.incrementAndGet());

        // Create the Followup
        FollowupDTO followupDTO = followupMapper.toDto(followup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFollowupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, followupDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(followupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Followup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFollowup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followup.setId(longCount.incrementAndGet());

        // Create the Followup
        FollowupDTO followupDTO = followupMapper.toDto(followup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(followupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Followup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFollowup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followup.setId(longCount.incrementAndGet());

        // Create the Followup
        FollowupDTO followupDTO = followupMapper.toDto(followup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowupMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(followupDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Followup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFollowup() throws Exception {
        // Initialize the database
        insertedFollowup = followupRepository.saveAndFlush(followup);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the followup
        restFollowupMockMvc
            .perform(delete(ENTITY_API_URL_ID, followup.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return followupRepository.count();
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

    protected Followup getPersistedFollowup(Followup followup) {
        return followupRepository.findById(followup.getId()).orElseThrow();
    }

    protected void assertPersistedFollowupToMatchAllProperties(Followup expectedFollowup) {
        assertFollowupAllPropertiesEquals(expectedFollowup, getPersistedFollowup(expectedFollowup));
    }

    protected void assertPersistedFollowupToMatchUpdatableProperties(Followup expectedFollowup) {
        assertFollowupAllUpdatablePropertiesEquals(expectedFollowup, getPersistedFollowup(expectedFollowup));
    }
}
