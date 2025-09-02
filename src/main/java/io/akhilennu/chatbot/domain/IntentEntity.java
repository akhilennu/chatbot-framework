package io.akhilennu.chatbot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A IntentEntity.
 */
@Entity
@Table(name = "intent_entity")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IntentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "optional")
    private Boolean optional;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "entities")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "response", "utterances", "followups", "bot", "entities" }, allowSetters = true)
    private Set<Intent> intents = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public IntentEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public IntentEntity name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOptional() {
        return this.optional;
    }

    public IntentEntity optional(Boolean optional) {
        this.setOptional(optional);
        return this;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    public Set<Intent> getIntents() {
        return this.intents;
    }

    public void setIntents(Set<Intent> intents) {
        if (this.intents != null) {
            this.intents.forEach(i -> i.removeEntities(this));
        }
        if (intents != null) {
            intents.forEach(i -> i.addEntities(this));
        }
        this.intents = intents;
    }

    public IntentEntity intents(Set<Intent> intents) {
        this.setIntents(intents);
        return this;
    }

    public IntentEntity addIntents(Intent intent) {
        this.intents.add(intent);
        intent.getEntities().add(this);
        return this;
    }

    public IntentEntity removeIntents(Intent intent) {
        this.intents.remove(intent);
        intent.getEntities().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntentEntity)) {
            return false;
        }
        return getId() != null && getId().equals(((IntentEntity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IntentEntity{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", optional='" + getOptional() + "'" +
            "}";
    }
}
