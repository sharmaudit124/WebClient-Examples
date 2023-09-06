package com.practice.WebclientExamples.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserNationalityDTO {
    private int count;
    private String name;
    private List<CountryInfo> country;
}
