package com.example.movieticketWeb.service.impl;

import com.example.movieticketWeb.dto.response.PersonInfoResponse;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.mapper.PersonMapper;
import com.example.movieticketWeb.repository.IUserRepository;
import com.example.movieticketWeb.service.IPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonServiceImpl implements IPersonService {
    private final IUserRepository userRepository;
    private final PersonMapper personMapper ;
    @Autowired
    public PersonServiceImpl(IUserRepository userRepository, PersonMapper personMapper) {
        this.userRepository = userRepository;
        this.personMapper = new PersonMapper();
    }
    public Optional<Person> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public PersonInfoResponse getAdminInfo(Person person) {
        return personMapper.toDTO(person);
    }
}
