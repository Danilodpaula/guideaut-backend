package com.guideaut.project.artefatos.dto;

import com.guideaut.project.artefatos.model.*;
import java.util.List;

public record CreateEmpathyDto (
     String name,
     Integer age,
     String gender,
     String reasons,
     String expectations,
     List<String> interactionItems,
     List<String> cognitionItems,
     List<String> communicationItems,
     List<String> behaviorItems
)
    {
        public Empathy toEntity() {
            Empathy empathy = new Empathy();
            empathy.setName(this.name);
            empathy.setAge(this.age);
            empathy.setGender(this.gender);
            empathy.setReasons(this.reasons);
            empathy.setExpectations(this.expectations);
            empathy.setComportamento(this.behaviorItems.stream().map((item) -> {
                EmpathyBehavior behavior = new EmpathyBehavior();
                behavior.setDescription(item);
                return behavior;
            }).toList());
            empathy.setCognicao(this.cognitionItems.stream().map((item) -> {
                EmpathyCognition cognition = new EmpathyCognition();
                cognition.setDescription(item);
                return cognition;
            }).toList());
            empathy.setComunicacao(this.communicationItems.stream().map((item) -> {
                EmpathyCommunication communication = new EmpathyCommunication();
                communication.setDescription(item);
                return communication;
            }).toList());
            empathy.setInteracao(this.interactionItems.stream().map((item) -> {
                EmpathyInteraction interaction = new EmpathyInteraction();
                interaction.setDescription(item);
                return interaction;
            }).toList());

            return empathy;
        }
}
