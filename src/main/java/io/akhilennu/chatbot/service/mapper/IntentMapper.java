package io.akhilennu.chatbot.service.mapper;

import io.akhilennu.chatbot.domain.Bot;
import io.akhilennu.chatbot.domain.Intent;
import io.akhilennu.chatbot.domain.IntentEntity;
import io.akhilennu.chatbot.domain.IntentResponse;
import io.akhilennu.chatbot.service.dto.BotDTO;
import io.akhilennu.chatbot.service.dto.IntentDTO;
import io.akhilennu.chatbot.service.dto.IntentEntityDTO;
import io.akhilennu.chatbot.service.dto.IntentResponseDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Intent} and its DTO {@link IntentDTO}.
 */
@Mapper(componentModel = "spring")
public interface IntentMapper extends EntityMapper<IntentDTO, Intent> {
    @Mapping(target = "response", source = "response", qualifiedByName = "intentResponseId")
    @Mapping(target = "bot", source = "bot", qualifiedByName = "botName")
    @Mapping(target = "entities", source = "entities", qualifiedByName = "intentEntityNameSet")
    IntentDTO toDto(Intent s);

    @Mapping(target = "removeEntities", ignore = true)
    Intent toEntity(IntentDTO intentDTO);

    @Named("intentResponseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    IntentResponseDTO toDtoIntentResponseId(IntentResponse intentResponse);

    @Named("botName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    BotDTO toDtoBotName(Bot bot);

    @Named("intentEntityName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    IntentEntityDTO toDtoIntentEntityName(IntentEntity intentEntity);

    @Named("intentEntityNameSet")
    default Set<IntentEntityDTO> toDtoIntentEntityNameSet(Set<IntentEntity> intentEntity) {
        return intentEntity.stream().map(this::toDtoIntentEntityName).collect(Collectors.toSet());
    }
}
