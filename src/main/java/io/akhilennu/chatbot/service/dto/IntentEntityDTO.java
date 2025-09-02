package io.akhilennu.chatbot.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link io.akhilennu.chatbot.domain.IntentEntity} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IntentEntityDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private Boolean optional;

    private Set<IntentDTO> intents = new HashSet<>();

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

    public Boolean getOptional() {
        return optional;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    public Set<IntentDTO> getIntents() {
        return intents;
    }

    public void setIntents(Set<IntentDTO> intents) {
        this.intents = intents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntentEntityDTO)) {
            return false;
        }

        IntentEntityDTO intentEntityDTO = (IntentEntityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, intentEntityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IntentEntityDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", optional='" + getOptional() + "'" +
            ", intents=" + getIntents() +
            "}";
    }
}
