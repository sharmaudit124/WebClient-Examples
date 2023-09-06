package com.practice.WebclientExamples.services;

import com.practice.WebclientExamples.dto.*;
import com.practice.WebclientExamples.dto.randomUserResponseDto.Result;
import com.practice.WebclientExamples.entities.User;
import com.practice.WebclientExamples.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;

@Service
public class UserValidationServiceImpl {

    private final RandomUserServiceImpl randomUserService;

    private final UserRepository userRepository;

    @Autowired
    public UserValidationServiceImpl(RandomUserServiceImpl randomUserService, UserRepository userRepository) {
        this.randomUserService = randomUserService;
        this.userRepository = userRepository;
    }

    public List<User> saveAndValidateUser(UserRequestDTO userRequestDTO) throws ExecutionException, InterruptedException {

        if (userRequestDTO.getSize() < 1 || userRequestDTO.getSize() > 5) {
            throw new RuntimeException("Size not applicable");
        }
        RandomUserResponse randomUserResponse = randomUserService.getRandomUser(userRequestDTO.getSize()).toFuture().get();
        ExecutorService executorService = Executors.newFixedThreadPool(userRequestDTO.getSize());

        List<Callable<User>> tasks = new ArrayList<>();
        for (Result result : randomUserResponse.getResults()) {
            tasks.add(() -> validateUserDetails(result));
        }

        List<Future<User>> futures = executorService.invokeAll(tasks);
        List<User> userList = new ArrayList<>();

        for (Future<User> future : futures) {
            try {
                userList.add(future.get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();

        return saveAllUser(userList);
    }


    private User validateUserDetails(Result result) throws ExecutionException, InterruptedException {
        String name = result.getName().getFirst();

        ApiJoiner apiJoiner = randomUserService.callingApiSimultaneously(name).toFuture().get();

        GenderResponseDTO genderResponseDTO = apiJoiner.genderResponseDTO();
        UserNationalityDTO userNationalityDTO = apiJoiner.userNationalityDTO();

        String nat = result.getNat();
        String gender = result.getGender();

        OffsetDateTime odt = OffsetDateTime.parse(result.getDob().getDate());
        LocalDateTime formattedDate = odt.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);

        User user = new User();
        user.setName(result.getName().getFirst() + " " + result.getName().getLast());
        user.setGender(gender);
        user.setAge(result.getDob().getAge());
        user.setNationality(nat);
        user.setDob(formattedDate.format(formatter));
        user.setDateCreated(LocalDate.now());

        if (isMatchedNat(userNationalityDTO.getCountry(), nat) && gender.equals(genderResponseDTO.getGender())) {
            user.setVerificationStatus("AUTHORIZED");
        } else {
            user.setVerificationStatus("NON_AUTHORIZED");
        }

        return user;
    }

    private boolean isMatchedNat(List<CountryInfo> ls, String nat) {
        return ls.stream()
                .anyMatch(countryInfo -> countryInfo.getCountry_id().equals(nat));
    }

    @Transactional
    private List<User> saveAllUser(List<User> userList) {
        return userRepository.saveAll(userList);
    }
}
