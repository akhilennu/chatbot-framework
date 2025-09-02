package io.akhilennu.chatbot.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link io.akhilennu.chatbot.domain.Followup} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FollowupDTO implements Serializable {

    private Long id;

    @NotNull
    private String question;

    @NotNull
    private String targetEntity;

    private Integer order;

    private IntentDTO intent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
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
        if (!(o instanceof FollowupDTO)) {
            return false;
        }

        FollowupDTO followupDTO = (FollowupDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, followupDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FollowupDTO{" +
            "id=" + getId() +
            ", question='" + getQuestion() + "'" +
            ", targetEntity='" + getTargetEntity() + "'" +
            ", order=" + getOrder() +
            ", intent=" + getIntent() +
            "}";
    }
}
