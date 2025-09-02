package io.akhilennu.chatbot.repository;

import io.akhilennu.chatbot.domain.Followup;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Followup entity.
 */
@Repository
public interface FollowupRepository extends JpaRepository<Followup, Long> {
    default Optional<Followup> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Followup> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Followup> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select followup from Followup followup left join fetch followup.intent",
        countQuery = "select count(followup) from Followup followup"
    )
    Page<Followup> findAllWithToOneRelationships(Pageable pageable);

    @Query("select followup from Followup followup left join fetch followup.intent")
    List<Followup> findAllWithToOneRelationships();

    @Query("select followup from Followup followup left join fetch followup.intent where followup.id =:id")
    Optional<Followup> findOneWithToOneRelationships(@Param("id") Long id);
}
