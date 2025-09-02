package io.akhilennu.chatbot.service.mapper;

import io.akhilennu.chatbot.domain.Intent;
import io.akhilennu.chatbot.domain.IntentEntity;
import io.akhilennu.chatbot.service.dto.IntentDTO;
import io.akhilennu.chatbot.service.dto.IntentEntityDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link IntentEntity} and its DTO {@link IntentEntityDTO}.
 */
@Mapper(componentModel = "spring")
public interface IntentEntityMapper extends EntityMapper<IntentEntityDTO, IntentEntity> {
    @Mapping(target = "intents", source = "intents", qualifiedByName = "intentIdSet")
    IntentEntityDTO toDto(IntentEntity s);

    @Mapping(target = "intents", ignore = true)
    @Mapping(target = "removeIntents", ignore = true)
    IntentEntity toEntity(IntentEntityDTO intentEntityDTO);

    @Named("intentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    IntentDTO toDtoIntentId(Intent intent);

    @Named("intentIdSet")
    default Set<IntentDTO> toDtoIntentIdSet(Set<Intent> intent) {
        return intent.stream().map(this::toDtoIntentId).collect(Collectors.toSet());
    }
}
