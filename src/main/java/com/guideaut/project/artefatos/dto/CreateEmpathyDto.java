package com.guideaut.project.artefatos.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class CreateEmpathyDto {
    private String name;
    private Integer age;
    private String gender;
    private String reasons;
    private String expectations;
    private List<String> interactionItems = new ArrayList<>();
    private List<String> cognitionItems = new ArrayList<>();
    private List<String> communicationItems = new ArrayList<>();
    private List<String> behaviorItems = new ArrayList<>();

}
