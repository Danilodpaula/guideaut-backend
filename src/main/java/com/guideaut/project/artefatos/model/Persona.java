package com.guideaut.project.artefatos.model;

import com.guideaut.project.artefatos.dto.FindPersonaDto;
import com.guideaut.project.artefatos.dto.UpdatePersonaDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "personas")
public class Persona {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private Integer age;

    private String gender;

    private String language;

    @Column(name = "support_level")
    private String supportLevel;

    private String model;

    private String about;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonaBehavior> comportamento = new ArrayList<>();

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonaStressfulActivity> estressante = new ArrayList<>();

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonaInteraction> interacao = new ArrayList<>();

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonaSoftwareAspect> software = new ArrayList<>();

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonaCognition> cognicao = new ArrayList<>();

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonaCommunication> comunicacao = new ArrayList<>();

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonaCalmingActivity> calmante = new ArrayList<>();

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonaStereotype> estereotipo = new ArrayList<>();

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonaSocialAspect> social = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public FindPersonaDto toDto() {
        return new FindPersonaDto(
                this.id,
                this.name,
                this.age,
                this.gender,
                this.language,
                this.supportLevel,
                this.model,
                this.about,
                this.interacao.stream().map(PersonaInteraction::getDescription).toList(),
                this.cognicao.stream().map(PersonaCognition::getDescription).toList(),
                this.comunicacao.stream().map(PersonaCommunication::getDescription).toList(),
                this.comportamento.stream().map(PersonaBehavior::getDescription).toList(),
                this.estressante.stream().map(PersonaStressfulActivity::getDescription).toList(),
                this.calmante.stream().map(PersonaCalmingActivity::getDescription).toList(),
                this.estereotipo.stream().map(PersonaStereotype::getDescription).toList(),
                this.software.stream().map(PersonaSoftwareAspect::getDescription).toList(),
                this.social.stream().map(PersonaSocialAspect::getDescription).toList()
        );
    }

    public void update(UpdatePersonaDto dto) {
        this.name = dto.name();
        this.age = dto.age();
        this.gender = dto.gender();
        this.language = dto.language();
        this.supportLevel = dto.supportLevel();
        this.model = dto.model();
        this.about = dto.about();

        this.comportamento = dto.behavior().stream().map((item) -> {
            PersonaBehavior behavior = new PersonaBehavior();
            behavior.setDescription(item);
            return behavior;
        }).toList();
        this.cognicao = dto.cognition().stream().map((item) -> {
            PersonaCognition cognition = new PersonaCognition();
            cognition.setDescription(item);
            return cognition;
        }).toList();
        this.comunicacao = dto.communication().stream().map((item) -> {
            PersonaCommunication communication = new PersonaCommunication();
            communication.setDescription(item);
            return communication;
        }).toList();
        this.interacao = dto.interaction().stream().map((item) -> {
            PersonaInteraction interaction = new PersonaInteraction();
            interaction.setDescription(item);
            return interaction;
        }).toList();
        this.calmante = dto.calmingActivities().stream().map((item) -> {
            PersonaCalmingActivity activity = new PersonaCalmingActivity();
            activity.setDescription(item);
            return activity;
        }).toList();
        this.estereotipo = dto.stereotypes().stream().map((item) -> {
            PersonaStereotype stereotype = new PersonaStereotype();
            stereotype.setDescription(item);
            return stereotype;
        }).toList();
        this.estressante = dto.stressfulActivities().stream().map((item) -> {
            PersonaStressfulActivity activity = new PersonaStressfulActivity();
            activity.setDescription(item);
            return activity;
        }).toList();
        this.software = dto.softwareAspects().stream().map((item) -> {
            PersonaSoftwareAspect aspect = new PersonaSoftwareAspect();
            aspect.setDescription(item);
            return aspect;
        }).toList();
        this.social = dto.socialAspects().stream().map((item) -> {
            PersonaSocialAspect aspect = new PersonaSocialAspect();
            aspect.setDescription(item);
            return aspect;
        }).toList();
    }
}
