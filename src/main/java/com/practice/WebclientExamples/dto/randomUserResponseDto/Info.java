package com.practice.WebclientExamples.dto.randomUserResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Info {
    private String seed;
    private int results;
    private int page;
    private String version;
}
