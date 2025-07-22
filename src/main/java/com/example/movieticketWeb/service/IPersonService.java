package com.example.movieticketWeb.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.example.movieticketWeb.dto.request.UserRequest;
import com.example.movieticketWeb.dto.response.PersonInfoResponse;
import com.example.movieticketWeb.entity.Person;

public interface IPersonService {
    public Optional<Person> findByEmail(String email);

    public PersonInfoResponse getInfo(Person person);

    Page<PersonInfoResponse> getUsers(int page, int soluong);

    Page<PersonInfoResponse> searchUser(String keyword, int i, int recordsPerPage);

    boolean deleteUser(Long id);

    boolean updateUser(Long id, UserRequest user);

    PersonInfoResponse getUserById(Long id);

    boolean addUser(UserRequest user);
}
