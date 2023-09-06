package com.practice.WebclientExamples.dto;

import com.practice.WebclientExamples.dto.randomUserResponseDto.Info;
import com.practice.WebclientExamples.dto.randomUserResponseDto.Result;
import lombok.Data;

import java.util.List;

@Data
public class RandomUserResponse {
    private List<Result> results;
    private Info info;
}
