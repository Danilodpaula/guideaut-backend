package com.guideaut.project.artefatos.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "empatias_comportamento")
public class EmpathyBehavior {

    @Id
    @GeneratedValue
    private UUID id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "empatia_id", nullable = false)
    private Empathy empathy;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
