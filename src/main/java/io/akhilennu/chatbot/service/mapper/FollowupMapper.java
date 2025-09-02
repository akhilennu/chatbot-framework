package io.akhilennu.chatbot.service.mapper;

import io.akhilennu.chatbot.domain.Followup;
import io.akhilennu.chatbot.domain.Intent;
import io.akhilennu.chatbot.service.dto.FollowupDTO;
import io.akhilennu.chatbot.service.dto.IntentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Followup} and its DTO {@link FollowupDTO}.
 */
@Mapper(componentModel = "spring")
public interface FollowupMapper extends EntityMapper<FollowupDTO, Followup> {
    @Mapping(target = "intent", source = "intent", qualifiedByName = "intentName")
    FollowupDTO toDto(Followup s);

    @Named("intentName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    IntentDTO toDtoIntentName(Intent intent);
}
