package com.practice.WebclientExamples.services;


import com.practice.WebclientExamples.dto.ApiJoiner;
import com.practice.WebclientExamples.dto.RandomUserResponse;
import reactor.core.publisher.Mono;

public interface RandomUserService {

    Mono<RandomUserResponse> getRandomUser(int val);

    Mono<ApiJoiner> callingApiSimultaneously(String name) throws InterruptedException;
}
