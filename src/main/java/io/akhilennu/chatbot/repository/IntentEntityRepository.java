package io.akhilennu.chatbot.repository;

import io.akhilennu.chatbot.domain.IntentEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the IntentEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IntentEntityRepository extends JpaRepository<IntentEntity, Long> {}
