package com.guideaut.project.artefatos.dto;

import com.guideaut.project.artefatos.model.*;
import java.util.List;

public record CreatePersonaDto (
        String name,
        Integer age,
        String gender,
        String language,
        String supportLevel,
        String model,
        String about,
        List<String> interaction,
        List<String> cognition,
        List<String> communication,
        List<String> behavior,
        List<String> stressfulActivities,
        List<String> calmingActivities,
        List<String> stereotypes,
        List<String> softwareAspects,
        List<String> socialAspects
) {
    public Persona toEntity() {
        Persona persona = new Persona();
        persona.setName(this.name);
        persona.setAge(this.age);
        persona.setGender(this.gender);
        persona.setLanguage(this.language);
        persona.setSupportLevel(this.supportLevel);
        persona.setModel(this.model);
        persona.setAbout(this.about);

        persona.setComportamento(this.behavior.stream().map((item) -> {
            PersonaBehavior behavior = new PersonaBehavior();
            behavior.setPersona(persona);
            behavior.setDescription(item);
            return behavior;
        }).toList());
        persona.setCognicao(this.cognition.stream().map((item) -> {
            PersonaCognition cognition = new PersonaCognition();
            cognition.setPersona(persona);
            cognition.setDescription(item);
            return cognition;
        }).toList());
        persona.setComunicacao(this.communication.stream().map((item) -> {
            PersonaCommunication communication = new PersonaCommunication();
            communication.setPersona(persona);
            communication.setDescription(item);
            return communication;
        }).toList());
        persona.setInteracao(this.interaction.stream().map((item) -> {
            PersonaInteraction interaction = new PersonaInteraction();
            interaction.setPersona(persona);
            interaction.setDescription(item);
            return interaction;
        }).toList());
        persona.setCalmante(this.calmingActivities.stream().map((item) -> {
            PersonaCalmingActivity activity = new PersonaCalmingActivity();
            activity.setPersona(persona);
            activity.setDescription(item);
            return activity;
        }).toList());
        persona.setEstereotipo(this.stereotypes.stream().map((item) -> {
            PersonaStereotype stereotype = new PersonaStereotype();
            stereotype.setPersona(persona);
            stereotype.setDescription(item);
            return stereotype;
        }).toList());
        persona.setEstressante(this.stressfulActivities.stream().map((item) -> {
            PersonaStressfulActivity activity = new PersonaStressfulActivity();
            activity.setPersona(persona);
            activity.setDescription(item);
            return activity;
        }).toList());
        persona.setSoftware(this.softwareAspects.stream().map((item) -> {
            PersonaSoftwareAspect aspect = new PersonaSoftwareAspect();
            aspect.setPersona(persona);
            aspect.setDescription(item);
            return aspect;
        }).toList());
        persona.setSocial(this.socialAspects.stream().map((item) -> {
            PersonaSocialAspect aspect = new PersonaSocialAspect();
            aspect.setPersona(persona);
            aspect.setDescription(item);
            return aspect;
        }).toList());

        return persona;
    }

}
