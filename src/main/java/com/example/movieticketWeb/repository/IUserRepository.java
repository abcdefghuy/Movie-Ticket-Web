package com.example.movieticketWeb.repository;

import com.example.movieticketWeb.entity.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends CrudRepository<Person, Long> {
    Optional<Person> findByEmail(String email);
    Optional<Person> findByVerificationCode(String verificationCode);
}
