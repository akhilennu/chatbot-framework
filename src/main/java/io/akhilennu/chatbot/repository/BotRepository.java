package io.akhilennu.chatbot.repository;

import io.akhilennu.chatbot.domain.Bot;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Bot entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BotRepository extends JpaRepository<Bot, Long> {}
