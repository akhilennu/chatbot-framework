package io.akhilennu.chatbot.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link io.akhilennu.chatbot.domain.IntentResponse} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IntentResponseDTO implements Serializable {

    private Long id;

    @NotNull
    private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntentResponseDTO)) {
            return false;
        }

        IntentResponseDTO intentResponseDTO = (IntentResponseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, intentResponseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IntentResponseDTO{" +
            "id=" + getId() +
            ", message='" + getMessage() + "'" +
            "}";
    }
}
