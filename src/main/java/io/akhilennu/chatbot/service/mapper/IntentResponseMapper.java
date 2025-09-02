package io.akhilennu.chatbot.service.mapper;

import io.akhilennu.chatbot.domain.IntentResponse;
import io.akhilennu.chatbot.service.dto.IntentResponseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link IntentResponse} and its DTO {@link IntentResponseDTO}.
 */
@Mapper(componentModel = "spring")
public interface IntentResponseMapper extends EntityMapper<IntentResponseDTO, IntentResponse> {}
