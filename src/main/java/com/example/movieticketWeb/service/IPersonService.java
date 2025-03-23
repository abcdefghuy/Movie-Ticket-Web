package com.example.movieticketWeb.service;

import com.example.movieticketWeb.dto.request.UserRequest;
import com.example.movieticketWeb.dto.response.PersonInfoResponse;
import com.example.movieticketWeb.entity.Person;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IPersonService {
    public Optional<Person> findByEmail(String email);
    public PersonInfoResponse getAdminInfo(Person person);

    Page<PersonInfoResponse> getUsers(int page, int soluong);

    Page<PersonInfoResponse> searchUser(String keyword, int i, int recordsPerPage);

    boolean deleteUser(Long id);

    boolean updateUser(Long id, UserRequest user);

    PersonInfoResponse getUserById(Long id);
    boolean addUser(UserRequest user);

}
