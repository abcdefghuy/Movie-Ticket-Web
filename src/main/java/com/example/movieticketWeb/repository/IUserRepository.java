package com.example.movieticketWeb.repository;

import com.example.movieticketWeb.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends CrudRepository<Person, Long> {
    Optional<Person> findByEmail(String email);
    Optional<Person> findByVerificationCode(String verificationCode);
    Page<Person> findAll(Pageable pageable);
    @Query("SELECT m FROM Person m WHERE LOWER(m.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Person> searchUserByKeyword(String keyword, PageRequest of);

    List<Person> findAll();
}
