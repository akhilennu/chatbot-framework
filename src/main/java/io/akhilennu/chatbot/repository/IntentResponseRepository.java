package io.akhilennu.chatbot.repository;

import io.akhilennu.chatbot.domain.IntentResponse;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the IntentResponse entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IntentResponseRepository extends JpaRepository<IntentResponse, Long> {}
