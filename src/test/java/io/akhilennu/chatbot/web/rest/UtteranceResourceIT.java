package io.akhilennu.chatbot.web.rest;

import static io.akhilennu.chatbot.domain.UtteranceAsserts.*;
import static io.akhilennu.chatbot.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.akhilennu.chatbot.IntegrationTest;
import io.akhilennu.chatbot.domain.Utterance;
import io.akhilennu.chatbot.repository.UtteranceRepository;
import io.akhilennu.chatbot.service.UtteranceService;
import io.akhilennu.chatbot.service.dto.UtteranceDTO;
import io.akhilennu.chatbot.service.mapper.UtteranceMapper;
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
 * Integration tests for the {@link UtteranceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UtteranceResourceIT {

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final String DEFAULT_LANGUAGE = "AAAAAAAAAA";
    private static final String UPDATED_LANGUAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/utterances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UtteranceRepository utteranceRepository;

    @Mock
    private UtteranceRepository utteranceRepositoryMock;

    @Autowired
    private UtteranceMapper utteranceMapper;

    @Mock
    private UtteranceService utteranceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUtteranceMockMvc;

    private Utterance utterance;

    private Utterance insertedUtterance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Utterance createEntity() {
        return new Utterance().text(DEFAULT_TEXT).language(DEFAULT_LANGUAGE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Utterance createUpdatedEntity() {
        return new Utterance().text(UPDATED_TEXT).language(UPDATED_LANGUAGE);
    }

    @BeforeEach
    void initTest() {
        utterance = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUtterance != null) {
            utteranceRepository.delete(insertedUtterance);
            insertedUtterance = null;
        }
    }

    @Test
    @Transactional
    void createUtterance() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Utterance
        UtteranceDTO utteranceDTO = utteranceMapper.toDto(utterance);
        var returnedUtteranceDTO = om.readValue(
            restUtteranceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(utteranceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UtteranceDTO.class
        );

        // Validate the Utterance in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUtterance = utteranceMapper.toEntity(returnedUtteranceDTO);
        assertUtteranceUpdatableFieldsEquals(returnedUtterance, getPersistedUtterance(returnedUtterance));

        insertedUtterance = returnedUtterance;
    }

    @Test
    @Transactional
    void createUtteranceWithExistingId() throws Exception {
        // Create the Utterance with an existing ID
        utterance.setId(1L);
        UtteranceDTO utteranceDTO = utteranceMapper.toDto(utterance);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUtteranceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(utteranceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Utterance in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTextIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        utterance.setText(null);

        // Create the Utterance, which fails.
        UtteranceDTO utteranceDTO = utteranceMapper.toDto(utterance);

        restUtteranceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(utteranceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUtterances() throws Exception {
        // Initialize the database
        insertedUtterance = utteranceRepository.saveAndFlush(utterance);

        // Get all the utteranceList
        restUtteranceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(utterance.getId().intValue())))
            .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT)))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUtterancesWithEagerRelationshipsIsEnabled() throws Exception {
        when(utteranceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUtteranceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(utteranceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUtterancesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(utteranceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUtteranceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(utteranceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getUtterance() throws Exception {
        // Initialize the database
        insertedUtterance = utteranceRepository.saveAndFlush(utterance);

        // Get the utterance
        restUtteranceMockMvc
            .perform(get(ENTITY_API_URL_ID, utterance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(utterance.getId().intValue()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE));
    }

    @Test
    @Transactional
    void getNonExistingUtterance() throws Exception {
        // Get the utterance
        restUtteranceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUtterance() throws Exception {
        // Initialize the database
        insertedUtterance = utteranceRepository.saveAndFlush(utterance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the utterance
        Utterance updatedUtterance = utteranceRepository.findById(utterance.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUtterance are not directly saved in db
        em.detach(updatedUtterance);
        updatedUtterance.text(UPDATED_TEXT).language(UPDATED_LANGUAGE);
        UtteranceDTO utteranceDTO = utteranceMapper.toDto(updatedUtterance);

        restUtteranceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, utteranceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(utteranceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Utterance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUtteranceToMatchAllProperties(updatedUtterance);
    }

    @Test
    @Transactional
    void putNonExistingUtterance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        utterance.setId(longCount.incrementAndGet());

        // Create the Utterance
        UtteranceDTO utteranceDTO = utteranceMapper.toDto(utterance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUtteranceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, utteranceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(utteranceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Utterance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUtterance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        utterance.setId(longCount.incrementAndGet());

        // Create the Utterance
        UtteranceDTO utteranceDTO = utteranceMapper.toDto(utterance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtteranceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(utteranceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Utterance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUtterance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        utterance.setId(longCount.incrementAndGet());

        // Create the Utterance
        UtteranceDTO utteranceDTO = utteranceMapper.toDto(utterance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtteranceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(utteranceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Utterance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUtteranceWithPatch() throws Exception {
        // Initialize the database
        insertedUtterance = utteranceRepository.saveAndFlush(utterance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the utterance using partial update
        Utterance partialUpdatedUtterance = new Utterance();
        partialUpdatedUtterance.setId(utterance.getId());

        partialUpdatedUtterance.text(UPDATED_TEXT);

        restUtteranceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUtterance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUtterance))
            )
            .andExpect(status().isOk());

        // Validate the Utterance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUtteranceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUtterance, utterance),
            getPersistedUtterance(utterance)
        );
    }

    @Test
    @Transactional
    void fullUpdateUtteranceWithPatch() throws Exception {
        // Initialize the database
        insertedUtterance = utteranceRepository.saveAndFlush(utterance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the utterance using partial update
        Utterance partialUpdatedUtterance = new Utterance();
        partialUpdatedUtterance.setId(utterance.getId());

        partialUpdatedUtterance.text(UPDATED_TEXT).language(UPDATED_LANGUAGE);

        restUtteranceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUtterance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUtterance))
            )
            .andExpect(status().isOk());

        // Validate the Utterance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUtteranceUpdatableFieldsEquals(partialUpdatedUtterance, getPersistedUtterance(partialUpdatedUtterance));
    }

    @Test
    @Transactional
    void patchNonExistingUtterance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        utterance.setId(longCount.incrementAndGet());

        // Create the Utterance
        UtteranceDTO utteranceDTO = utteranceMapper.toDto(utterance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUtteranceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, utteranceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(utteranceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Utterance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUtterance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        utterance.setId(longCount.incrementAndGet());

        // Create the Utterance
        UtteranceDTO utteranceDTO = utteranceMapper.toDto(utterance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtteranceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(utteranceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Utterance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUtterance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        utterance.setId(longCount.incrementAndGet());

        // Create the Utterance
        UtteranceDTO utteranceDTO = utteranceMapper.toDto(utterance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtteranceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(utteranceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Utterance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUtterance() throws Exception {
        // Initialize the database
        insertedUtterance = utteranceRepository.saveAndFlush(utterance);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the utterance
        restUtteranceMockMvc
            .perform(delete(ENTITY_API_URL_ID, utterance.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return utteranceRepository.count();
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

    protected Utterance getPersistedUtterance(Utterance utterance) {
        return utteranceRepository.findById(utterance.getId()).orElseThrow();
    }

    protected void assertPersistedUtteranceToMatchAllProperties(Utterance expectedUtterance) {
        assertUtteranceAllPropertiesEquals(expectedUtterance, getPersistedUtterance(expectedUtterance));
    }

    protected void assertPersistedUtteranceToMatchUpdatableProperties(Utterance expectedUtterance) {
        assertUtteranceAllUpdatablePropertiesEquals(expectedUtterance, getPersistedUtterance(expectedUtterance));
    }
}
