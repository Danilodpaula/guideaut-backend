package com.guideaut.project.artefatos.model;

import com.guideaut.project.artefatos.dto.FindEmpathyDto;
import com.guideaut.project.artefatos.dto.UpdateEmpathyDto;
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
@Table(name = "empatias")
public class Empathy {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private Integer age;

    private String gender;

    private String reasons;

    private String expectations;

    @OneToMany(mappedBy = "empathy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmpathyBehavior> comportamento = new ArrayList<>();

    @OneToMany(mappedBy = "empathy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmpathyCognition> cognicao = new ArrayList<>();

    @OneToMany(mappedBy = "empathy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmpathyCommunication> comunicacao = new ArrayList<>();

    @OneToMany(mappedBy = "empathy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmpathyInteraction> interacao = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public FindEmpathyDto toDto() {

        return new FindEmpathyDto(
                this.id,
                this.name,
                this.age,
                this.gender,
                this.reasons,
                this.expectations,
                this.interacao.stream().map(EmpathyInteraction::getDescription).toList(),
                this.cognicao.stream().map(EmpathyCognition::getDescription).toList(),
                this.comunicacao.stream().map(EmpathyCommunication::getDescription).toList(),
                this.comportamento.stream().map(EmpathyBehavior::getDescription).toList()
        );

    }

    public void update(UpdateEmpathyDto dto) {
        this.name = dto.name();
        this.age = dto.age();
        this.gender = dto.gender();
        this.reasons = dto.reasons();
        this.expectations = dto.expectations();
        this.interacao = dto.interactionItems().stream().map((item) -> {
            EmpathyInteraction interaction = new EmpathyInteraction();
            interaction.setDescription(item);
            return interaction;
        }).toList();
        this.cognicao = dto.cognitionItems().stream().map((item) -> {
            EmpathyCognition cognition = new EmpathyCognition();
            cognition.setDescription(item);
            return cognition;
        }).toList();
        this.comunicacao = dto.communicationItems().stream().map((item) -> {
            EmpathyCommunication communication = new EmpathyCommunication();
            communication.setDescription(item);
            return communication;
        }).toList();
        this.comportamento = dto.behaviorItems().stream().map((item) -> {
            EmpathyBehavior behavior = new EmpathyBehavior();
            behavior.setDescription(item);
            return behavior;
        }).toList();
    }
}
