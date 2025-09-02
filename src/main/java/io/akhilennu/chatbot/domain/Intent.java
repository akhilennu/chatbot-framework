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
 * A Intent.
 */
@Entity
@Table(name = "intent")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Intent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @JsonIgnoreProperties(value = { "intent" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private IntentResponse response;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "intent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "intent" }, allowSetters = true)
    private Set<Utterance> utterances = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "intent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "intent" }, allowSetters = true)
    private Set<Followup> followups = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Bot bot;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_intent__entities",
        joinColumns = @JoinColumn(name = "intent_id"),
        inverseJoinColumns = @JoinColumn(name = "entities_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "intents" }, allowSetters = true)
    private Set<IntentEntity> entities = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Intent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Intent name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Intent description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IntentResponse getResponse() {
        return this.response;
    }

    public void setResponse(IntentResponse intentResponse) {
        this.response = intentResponse;
    }

    public Intent response(IntentResponse intentResponse) {
        this.setResponse(intentResponse);
        return this;
    }

    public Set<Utterance> getUtterances() {
        return this.utterances;
    }

    public void setUtterances(Set<Utterance> utterances) {
        if (this.utterances != null) {
            this.utterances.forEach(i -> i.setIntent(null));
        }
        if (utterances != null) {
            utterances.forEach(i -> i.setIntent(this));
        }
        this.utterances = utterances;
    }

    public Intent utterances(Set<Utterance> utterances) {
        this.setUtterances(utterances);
        return this;
    }

    public Intent addUtterances(Utterance utterance) {
        this.utterances.add(utterance);
        utterance.setIntent(this);
        return this;
    }

    public Intent removeUtterances(Utterance utterance) {
        this.utterances.remove(utterance);
        utterance.setIntent(null);
        return this;
    }

    public Set<Followup> getFollowups() {
        return this.followups;
    }

    public void setFollowups(Set<Followup> followups) {
        if (this.followups != null) {
            this.followups.forEach(i -> i.setIntent(null));
        }
        if (followups != null) {
            followups.forEach(i -> i.setIntent(this));
        }
        this.followups = followups;
    }

    public Intent followups(Set<Followup> followups) {
        this.setFollowups(followups);
        return this;
    }

    public Intent addFollowups(Followup followup) {
        this.followups.add(followup);
        followup.setIntent(this);
        return this;
    }

    public Intent removeFollowups(Followup followup) {
        this.followups.remove(followup);
        followup.setIntent(null);
        return this;
    }

    public Bot getBot() {
        return this.bot;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public Intent bot(Bot bot) {
        this.setBot(bot);
        return this;
    }

    public Set<IntentEntity> getEntities() {
        return this.entities;
    }

    public void setEntities(Set<IntentEntity> intentEntities) {
        this.entities = intentEntities;
    }

    public Intent entities(Set<IntentEntity> intentEntities) {
        this.setEntities(intentEntities);
        return this;
    }

    public Intent addEntities(IntentEntity intentEntity) {
        this.entities.add(intentEntity);
        return this;
    }

    public Intent removeEntities(IntentEntity intentEntity) {
        this.entities.remove(intentEntity);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Intent)) {
            return false;
        }
        return getId() != null && getId().equals(((Intent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Intent{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
