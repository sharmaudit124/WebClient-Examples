package com.practice.WebclientExamples.services;


import com.practice.WebclientExamples.dto.ApiJoiner;
import com.practice.WebclientExamples.dto.GenderResponseDTO;
import com.practice.WebclientExamples.dto.RandomUserResponse;
import com.practice.WebclientExamples.dto.UserNationalityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.*;

@Service
public class RandomUserServiceImpl implements RandomUserService {

    @Qualifier("webClientAPI1")
    private final WebClient webClientAPI1;

    @Qualifier("webClientAPI2")
    private final WebClient webClientAPI2;

    @Qualifier("webClientAPI3")
    private final WebClient webClientAPI3;

    @Autowired
    public RandomUserServiceImpl(WebClient webClientAPI1, WebClient webClientAPI2, WebClient webClientAPI3) {
        this.webClientAPI1 = webClientAPI1;
        this.webClientAPI2 = webClientAPI2;
        this.webClientAPI3 = webClientAPI3;
    }

    @Override
    public Mono<RandomUserResponse> getRandomUser(int val) {
        return webClientAPI1
                .get()
                .uri(uriBuilder ->
                        uriBuilder.queryParam("results", val)
                                .build())
                .retrieve()
                .onStatus(
                        httpStatusCode -> !httpStatusCode.is2xxSuccessful(),
                        clientResponse -> Mono.error(new RuntimeException("Something went wrong"))
                )
                .bodyToMono(RandomUserResponse.class);
    }

    @Override
    public Mono<ApiJoiner> callingApiSimultaneously(String name) throws InterruptedException {
        Mono<UserNationalityDTO> userNationalityDTOMono = null;
        Mono<GenderResponseDTO> genderResponseDTOMono = null;

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<Mono<UserNationalityDTO>> apiCall1 = () -> webClientAPI2
                .get()
                .uri(uriBuilder ->
                        uriBuilder.queryParam("name", name)
                                .build()
                )
                .retrieve()
                .onStatus(
                        httpStatusCode -> !httpStatusCode.is2xxSuccessful(),
                        clientResponse -> Mono.error(new RuntimeException("Something went wrong"))
                ).bodyToMono(UserNationalityDTO.class);

        Callable<Mono<GenderResponseDTO>> apiCall2 = () -> webClientAPI3
                .get()
                .uri(uriBuilder ->
                        uriBuilder.queryParam("name", name)
                                .build()
                ).retrieve()
                .onStatus(
                        httpStatusCode -> !httpStatusCode.is2xxSuccessful(),
                        clientResponse -> Mono.error(new RuntimeException("Something went wrong"))
                ).bodyToMono(GenderResponseDTO.class);


        try {
            Future<Mono<UserNationalityDTO>> api1Future = executorService.submit(apiCall1);
            Future<Mono<GenderResponseDTO>> api2Future = executorService.submit(apiCall2);

            userNationalityDTOMono = api1Future.get();
            genderResponseDTOMono = api2Future.get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();

        }
        return genderResponseDTOMono.zipWith(userNationalityDTOMono,
                ApiJoiner::new);

    }

    public Mono<ApiJoiner> callingApiSimultaneouslyUsingZip(String name) {
        Mono<UserNationalityDTO> userNationalityDTOMono = webClientAPI2
                .get()
                .uri(uriBuilder ->
                        uriBuilder.queryParam("name", name)
                                .build()
                )
                .retrieve()
                .onStatus(
                        httpStatusCode -> !httpStatusCode.is2xxSuccessful(),
                        clientResponse -> Mono.error(new RuntimeException("Something went wrong"))
                )
                .bodyToMono(UserNationalityDTO.class)
                .subscribeOn(Schedulers.parallel());

        Mono<GenderResponseDTO> genderResponseDTOMono = webClientAPI3
                .get()
                .uri(uriBuilder ->
                        uriBuilder.queryParam("name", name)
                                .build()
                )
                .retrieve()
                .onStatus(
                        httpStatusCode -> !httpStatusCode.is2xxSuccessful(),
                        clientResponse -> Mono.error(new RuntimeException("Something went wrong"))
                )
                .bodyToMono(GenderResponseDTO.class)
                .subscribeOn(Schedulers.parallel());

        return Mono.zip(
                userNationalityDTOMono,
                genderResponseDTOMono,
                (userNationalityDTO, genderResponseDTO) -> new ApiJoiner(genderResponseDTO, userNationalityDTO)
        );
    }
}
