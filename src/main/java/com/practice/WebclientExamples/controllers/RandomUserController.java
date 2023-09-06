package com.practice.WebclientExamples.controllers;

import com.practice.WebclientExamples.dto.ApiJoiner;
import com.practice.WebclientExamples.dto.RandomUserResponse;
import com.practice.WebclientExamples.dto.UserRequestDTO;
import com.practice.WebclientExamples.entities.User;
import com.practice.WebclientExamples.services.RandomUserServiceImpl;
import com.practice.WebclientExamples.services.UserValidationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class RandomUserController {

    private final RandomUserServiceImpl randomUserService;
    private final UserValidationServiceImpl userValidationService;

    @Autowired
    public RandomUserController(RandomUserServiceImpl randomUserService, UserValidationServiceImpl userValidationService) {
        this.randomUserService = randomUserService;
        this.userValidationService = userValidationService;
    }

    @GetMapping(value = "/random-user/{val}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<RandomUserResponse>> getRandomUser(@PathVariable int val) {
        return new ResponseEntity<>(randomUserService.getRandomUser(val), HttpStatus.OK);
    }

    @GetMapping(value = "/user-details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiJoiner> getUserDetails() throws InterruptedException, ExecutionException {
        return new ResponseEntity<>(randomUserService.callingApiSimultaneously("Rishaan").toFuture().get(), HttpStatus.OK);
    }

    @GetMapping(value = "/user-details-zip", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiJoiner> getUserDetailsUsingZip() throws InterruptedException, ExecutionException {
        return new ResponseEntity<>(randomUserService.callingApiSimultaneouslyUsingZip("Rishaan").toFuture().get(), HttpStatus.OK);
    }

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> saveUsers(@RequestBody UserRequestDTO userRequestDTO) throws ExecutionException, InterruptedException {
        return new ResponseEntity<>(userValidationService.saveAndValidateUser(userRequestDTO), HttpStatus.OK);
    }


}
