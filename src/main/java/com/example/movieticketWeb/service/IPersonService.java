package com.example.movieticketWeb.service;

import com.example.movieticketWeb.dto.response.PersonInfoResponse;
import com.example.movieticketWeb.entity.Person;

import java.util.Optional;

public interface IPersonService {
    public Optional<Person> findByEmail(String email);
    public PersonInfoResponse getAdminInfo(Person person);
}
