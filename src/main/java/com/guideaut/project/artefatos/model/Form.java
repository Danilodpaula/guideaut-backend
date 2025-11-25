package com.guideaut.project.artefatos.model;

import com.guideaut.project.artefatos.dto.FindFormDto;
import com.guideaut.project.artefatos.dto.FormItemDto;
import com.guideaut.project.artefatos.dto.UpdateFormDto;
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
@Table(name = "formularios")
public class Form {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String type;

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public FindFormDto toDto() {
        return new FindFormDto(
                this.id,
                this.name,
                this.type,
                this.items.stream().map((i) -> new FormItemDto(i.getSection(), i.getQuestion())).toList()
        );
    }

    public void update(UpdateFormDto dto) {
        this.name = dto.name();
        this.type = dto.type();
        this.items = dto.items().stream().map((i) -> i.toEntity(this)).toList();
    }
}
