package io.akhilennu.chatbot.repository;

import io.akhilennu.chatbot.domain.Intent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class IntentRepositoryWithBagRelationshipsImpl implements IntentRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String INTENTS_PARAMETER = "intents";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Intent> fetchBagRelationships(Optional<Intent> intent) {
        return intent.map(this::fetchEntities);
    }

    @Override
    public Page<Intent> fetchBagRelationships(Page<Intent> intents) {
        return new PageImpl<>(fetchBagRelationships(intents.getContent()), intents.getPageable(), intents.getTotalElements());
    }

    @Override
    public List<Intent> fetchBagRelationships(List<Intent> intents) {
        return Optional.of(intents).map(this::fetchEntities).orElse(Collections.emptyList());
    }

    Intent fetchEntities(Intent result) {
        return entityManager
            .createQuery("select intent from Intent intent left join fetch intent.entities where intent.id = :id", Intent.class)
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Intent> fetchEntities(List<Intent> intents) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, intents.size()).forEach(index -> order.put(intents.get(index).getId(), index));
        List<Intent> result = entityManager
            .createQuery("select intent from Intent intent left join fetch intent.entities where intent in :intents", Intent.class)
            .setParameter(INTENTS_PARAMETER, intents)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
