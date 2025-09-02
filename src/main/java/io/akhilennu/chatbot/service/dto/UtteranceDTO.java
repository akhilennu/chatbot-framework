package io.akhilennu.chatbot.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link io.akhilennu.chatbot.domain.Utterance} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UtteranceDTO implements Serializable {

    private Long id;

    @NotNull
    private String text;

    private String language;

    private IntentDTO intent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public IntentDTO getIntent() {
        return intent;
    }

    public void setIntent(IntentDTO intent) {
        this.intent = intent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UtteranceDTO)) {
            return false;
        }

        UtteranceDTO utteranceDTO = (UtteranceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, utteranceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UtteranceDTO{" +
            "id=" + getId() +
            ", text='" + getText() + "'" +
            ", language='" + getLanguage() + "'" +
            ", intent=" + getIntent() +
            "}";
    }
}
