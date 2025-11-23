package com.guideaut.project.artefatos.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreatePersonaDto {
    private String name;
    private Integer age;
    private String gender;
    private String language;
    private String supportLevel;
    private String model;
    private String about;
    private List<String> interaction = new ArrayList<>();
    private List<String> cognition = new ArrayList<>();
    private List<String> communication = new ArrayList<>();
    private List<String> behavior = new ArrayList<>();
    private List<String> stressfulActivities = new ArrayList<>();
    private List<String> calmingActivities = new ArrayList<>();
    private List<String> stereotypes = new ArrayList<>();
    private List<String> softwareAspects = new ArrayList<>();
    private List<String> socialAspects = new ArrayList<>();
}
