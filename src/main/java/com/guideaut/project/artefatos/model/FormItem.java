package com.guideaut.project.artefatos.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "formularios_itens")
public class FormItem {

    @Id
    @GeneratedValue
    private UUID id;

    private String section;

    private String question;

    @ManyToOne
    @JoinColumn(name = "formulario_id", nullable = false)
    private Form form;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
