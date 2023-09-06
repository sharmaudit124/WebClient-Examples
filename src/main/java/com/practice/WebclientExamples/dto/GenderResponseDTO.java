package com.practice.WebclientExamples.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GenderResponseDTO {
    private int count;
    private String name;
    private String gender;
    private double probability;
}
