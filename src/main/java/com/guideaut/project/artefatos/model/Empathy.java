package com.guideaut.project.artefatos.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
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

}
