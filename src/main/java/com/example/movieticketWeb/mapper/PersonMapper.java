package com.example.movieticketWeb.mapper;

import com.example.movieticketWeb.dto.response.PersonInfoResponse;
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
                .gender(user.getGender() == 1 ? "Nam" : "Nữ")
                .build();
    }
}
