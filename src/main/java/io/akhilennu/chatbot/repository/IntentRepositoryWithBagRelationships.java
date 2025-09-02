package io.akhilennu.chatbot.repository;

import io.akhilennu.chatbot.domain.Intent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface IntentRepositoryWithBagRelationships {
    Optional<Intent> fetchBagRelationships(Optional<Intent> intent);

    List<Intent> fetchBagRelationships(List<Intent> intents);

    Page<Intent> fetchBagRelationships(Page<Intent> intents);
}
