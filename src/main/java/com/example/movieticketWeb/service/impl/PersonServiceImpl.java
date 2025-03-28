package com.example.movieticketWeb.service.impl;

import com.example.movieticketWeb.dto.request.MovieRequest;
import com.example.movieticketWeb.dto.request.UserRequest;
import com.example.movieticketWeb.dto.response.MovieResponse;
import com.example.movieticketWeb.dto.response.PersonInfoResponse;
import com.example.movieticketWeb.entity.Movie;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.mapper.PersonMapper;
import com.example.movieticketWeb.repository.IUserRepository;
import com.example.movieticketWeb.service.IPersonService;
import com.fasterxml.jackson.core.Base64Variant;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements IPersonService {
    private final IUserRepository userRepository;
    private final PersonMapper personMapper ;
    private static final String USER_CACHE_KEY = "allUsers";
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public PersonServiceImpl(IUserRepository userRepository, PersonMapper personMapper, RedisTemplate<String, Object> redisTemplate, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;
        this.personMapper = new PersonMapper();
    }
    public Optional<Person> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public PersonInfoResponse getInfo(Person person) {
        return personMapper.toDTO(person);
    }

    @Override
    public Page<PersonInfoResponse> getUsers(int page, int soluong) {
        List<PersonInfoResponse> cachedUsers = getUsersFromCache();
        if (!cachedUsers.isEmpty()) {
            return paginateUser(cachedUsers, page, soluong);
        }
// Nếu cache không có, lấy từ database và lưu vào cache
        List<PersonInfoResponse> users = userRepository.findAll().stream()
                .map(personMapper::toDTO)
                .collect(Collectors.toList());
        redisTemplate.opsForValue().set(USER_CACHE_KEY, users, Duration.ofMinutes(30));
        return paginateUser(users, page, soluong);
    }
    private Page<PersonInfoResponse> fetchAndCacheUsers(int page, int soluong) {
        Page<Person> userPage = userRepository.findAll(PageRequest.of(page, soluong));
        Page<PersonInfoResponse> responsePage = userPage.map(personMapper::toDTO);
        redisTemplate.opsForValue().set(USER_CACHE_KEY, responsePage, Duration.ofMinutes(30));
        return responsePage;
    }

    @Override
    public Page<PersonInfoResponse> searchUser(String keyword, int page, int recordsPerPage) {
        List<PersonInfoResponse> cachedUsers = getUsersFromCache();

        if (!cachedUsers.isEmpty()) {
            // Nếu có dữ liệu trong Redis, lọc theo từ khóa và phân trang
            List<PersonInfoResponse> filteredMovies = filterUsersByKeyword(cachedUsers, keyword);
            return paginateUser(filteredMovies, page, recordsPerPage);
        }

        // Nếu không có trong Redis, lấy từ Database
        Page<Person> userPage = userRepository.searchUserByKeyword(keyword, PageRequest.of(page - 1, recordsPerPage));

        // Chuyển đổi từ Entity -> DTO và lưu vào cache
        List<PersonInfoResponse> movieResponses = userPage.getContent().stream().map(personMapper::toDTO).toList();

        return new PageImpl<>(movieResponses, PageRequest.of(page - 1, recordsPerPage), userPage.getTotalElements());
    }

    @Override
    public boolean deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
            redisTemplate.delete(USER_CACHE_KEY);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateUser(Long id, UserRequest user) {
        try{
            Person person = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Movie not found"));
            //add mat khau vao
            if(user.getPassword() != null){
                person.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            person.setUsername(user.getUsername());
            person.setEmail(user.getEmail());
            person.setPhone(user.getPhone());
            person.setBirthDate(user.getBirthDate());
            person.setRegion(user.getRegion());
            person.setGender(user.getGender());
            person.setRole(user.getRole());
            userRepository.save(person);
            // Xóa cache danh sách users (để lần gọi sau lấy từ DB)
            redisTemplate.delete(USER_CACHE_KEY);
            String userCacheKey = "user:" + id;
            PersonInfoResponse personResponse = personMapper.toDTO(person);
            redisTemplate.opsForValue().set(userCacheKey, personResponse, Duration.ofHours(1));
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    @Override
    public PersonInfoResponse getUserById(Long id) {
        String cacheKey = "user:" + id;
        PersonInfoResponse person =  (PersonInfoResponse) redisTemplate.opsForValue().get(cacheKey);

        if (person != null) {
            return person;
        }
        Person person1 = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + id));

        // 🟢 Lưu lại vào cache để lần sau lấy nhanh hơn
        redisTemplate.opsForValue().set(cacheKey, person1, Duration.ofHours(1));
        // 🎯 Chuyển đổi sang MovieResponse để trả về
        return personMapper.toDTO(person1);
    }
    @Override
    public boolean addUser(UserRequest user) {
        try {
            Person person = personMapper.toEntity(user);
            userRepository.save(person);
            redisTemplate.delete(USER_CACHE_KEY);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Page<PersonInfoResponse> paginateUser(List<PersonInfoResponse> filteredMovies, int page, int recordsPerPage) {
        int start = (page - 1) * recordsPerPage;
        int end = Math.min(start + recordsPerPage, filteredMovies.size());
        return new PageImpl<>(filteredMovies.subList(start, end), PageRequest.of(page - 1, recordsPerPage), filteredMovies.size());
    }

    private List<PersonInfoResponse> filterUsersByKeyword(List<PersonInfoResponse> cachedUsers, String keyword) {
        return cachedUsers.stream()
                .filter(movie -> movie.getUserName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    private List<PersonInfoResponse> getUsersFromCache() {
        List<PersonInfoResponse> cachedUsers = (List<PersonInfoResponse>) redisTemplate.opsForValue().get(USER_CACHE_KEY);
        return cachedUsers != null ? cachedUsers : List.of();
    }
}
