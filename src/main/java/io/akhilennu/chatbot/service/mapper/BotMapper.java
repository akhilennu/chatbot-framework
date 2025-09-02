package io.akhilennu.chatbot.service.mapper;

import io.akhilennu.chatbot.domain.Bot;
import io.akhilennu.chatbot.service.dto.BotDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Bot} and its DTO {@link BotDTO}.
 */
@Mapper(componentModel = "spring")
public interface BotMapper extends EntityMapper<BotDTO, Bot> {}
