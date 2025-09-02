package io.akhilennu.chatbot.repository;

import io.akhilennu.chatbot.domain.Utterance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Utterance entity.
 */
@Repository
public interface UtteranceRepository extends JpaRepository<Utterance, Long> {
    default Optional<Utterance> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Utterance> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Utterance> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select utterance from Utterance utterance left join fetch utterance.intent",
        countQuery = "select count(utterance) from Utterance utterance"
    )
    Page<Utterance> findAllWithToOneRelationships(Pageable pageable);

    @Query("select utterance from Utterance utterance left join fetch utterance.intent")
    List<Utterance> findAllWithToOneRelationships();

    @Query("select utterance from Utterance utterance left join fetch utterance.intent where utterance.id =:id")
    Optional<Utterance> findOneWithToOneRelationships(@Param("id") Long id);
}
