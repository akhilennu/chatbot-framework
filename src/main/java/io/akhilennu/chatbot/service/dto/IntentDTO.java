package io.akhilennu.chatbot.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link io.akhilennu.chatbot.domain.Intent} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IntentDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String description;

    private IntentResponseDTO response;

    private BotDTO bot;

    private Set<IntentEntityDTO> entities = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IntentResponseDTO getResponse() {
        return response;
    }

    public void setResponse(IntentResponseDTO response) {
        this.response = response;
    }

    public BotDTO getBot() {
        return bot;
    }

    public void setBot(BotDTO bot) {
        this.bot = bot;
    }

    public Set<IntentEntityDTO> getEntities() {
        return entities;
    }

    public void setEntities(Set<IntentEntityDTO> entities) {
        this.entities = entities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntentDTO)) {
            return false;
        }

        IntentDTO intentDTO = (IntentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, intentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IntentDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", response=" + getResponse() +
            ", bot=" + getBot() +
            ", entities=" + getEntities() +
            "}";
    }
}
