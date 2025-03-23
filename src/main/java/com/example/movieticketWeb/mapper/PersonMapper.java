package com.example.movieticketWeb.mapper;

import com.example.movieticketWeb.dto.request.UserRequest;
import com.example.movieticketWeb.dto.response.PersonInfoResponse;
import com.example.movieticketWeb.entity.Movie;
import com.example.movieticketWeb.entity.Person;
import org.springframework.stereotype.Service;

@Service
public class PersonMapper {
    public PersonInfoResponse toDTO(Person user) {
        return PersonInfoResponse.builder()
                .id(user.getPerID())
                .userName(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .birthday(user.getBirthDate())
                .regions(user.getRegion())
                .gender(user.getGender() == 1 ? "MALE" : "FEMALE")
                .role(user.getRole().replace("ROLE_", ""))
                .build();
    }
    public Person toEntity(UserRequest user) {
        return Person.builder()
                .birthDate(user.getBirthDate())
                .email(user.getEmail())
                .gender(user.getGender())
                .password(user.getPassword())
                .phone(user.getPhone())
                .role(user.getRole())
                .username(user.getUsername())
                .region(user.getRegion())
                .build();
    }
}
