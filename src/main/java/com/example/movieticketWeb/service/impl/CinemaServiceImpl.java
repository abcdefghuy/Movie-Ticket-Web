package com.example.movieticketWeb.service.impl;

import com.example.movieticketWeb.dto.request.CinemaRequest;
import com.example.movieticketWeb.dto.response.CinemaResponse;
import com.example.movieticketWeb.dto.response.PersonInfoResponse;
import com.example.movieticketWeb.entity.Cinema;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.mapper.CinemaMapper;
import com.example.movieticketWeb.repository.ICinemaRepository;
import com.example.movieticketWeb.service.ICinemaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CinemaServiceImpl implements ICinemaService {
    private final ICinemaRepository cinemaRepository;
    private final CinemaMapper cinemaMapper;
    private static final String CINEMA_CACHE_KEY = "allCinemas";
    private final RedisTemplate<String, Object> redisTemplate;

    public CinemaServiceImpl(ICinemaRepository cinemaRepository, CinemaMapper cinemaMapper, RedisTemplate<String, Object> redisTemplate) {
        this.cinemaRepository = cinemaRepository;
        this.cinemaMapper = cinemaMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean addCinema(CinemaRequest cinemaRequest) {
        try {
            cinemaRepository.save(cinemaMapper.toEntity(cinemaRequest));
            redisTemplate.delete(CINEMA_CACHE_KEY);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateCinema(Long id, CinemaRequest cinemaRequest) {
        try {
            cinemaRepository.findById(id).ifPresent(cinema -> {
                cinema.setCinemaName(cinemaRequest.getCinemaName());
                cinema.setAddress(cinemaRequest.getAddress());
                cinema.setLocation(cinemaRequest.getLocation());
                cinema.setStatus(cinemaRequest.isStatus());
                cinema.setRoomCount(cinemaRequest.getRoomCount());
                cinemaRepository.save(cinema);
                List<CinemaResponse> cachedMovies = getCinemaFromCache();
                if (cachedMovies != null) {
                    cachedMovies = cachedMovies.stream()
                            .map(m -> m.getCinemaId() == id.intValue() ? cinemaMapper.toDTO(cinema) : m).collect(Collectors.toList());
                    redisTemplate.opsForValue().set(CINEMA_CACHE_KEY, cachedMovies, Duration.ofHours(1));
                }
                String userCacheKey = "cinema:" + id;
                redisTemplate.opsForValue().set(userCacheKey, cinema, Duration.ofHours(1));
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public CinemaResponse getCinemaById(Long id) {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        String cacheKey = "cinema:" + id;
        CinemaResponse cinema =  (CinemaResponse) redisTemplate.opsForValue().get(cacheKey);

        if (cinema != null) {
            return cinema;
        }
        Cinema cinema1 = cinemaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cinema not found with ID: " + id));

        redisTemplate.opsForValue().set(cacheKey, cinema1, Duration.ofHours(1));
        return cinemaMapper.toDTO(cinema1);
    }

    @Override
    public Page<CinemaResponse> getCinemas(int page, int soluong) {
        List<CinemaResponse> cachedUsers = getCinemaFromCache();
        if (!cachedUsers.isEmpty()) {
            return paginateUser(cachedUsers, page, soluong);
        }
        List<CinemaResponse> users = cinemaRepository.findAll().stream()
                .map(cinemaMapper::toDTO)
                .collect(Collectors.toList());
        redisTemplate.opsForValue().set(CINEMA_CACHE_KEY, users, Duration.ofMinutes(30));
        return paginateUser(users, page, soluong);
    }

    @Override
    public boolean deleteCinema(Long id) {
        try {
            cinemaRepository.deleteById(id);
            redisTemplate.delete(CINEMA_CACHE_KEY);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Page<CinemaResponse> searchCinema(int page, int recordsPerPage, String keyword) {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        List<CinemaResponse> cachedUsers = getCinemaFromCache();
        if (!cachedUsers.isEmpty()) {
            // Nếu có dữ liệu trong Redis, lọc theo từ khóa và phân trang
            List<CinemaResponse> filteredMovies = filterCinemaByKeyword(cachedUsers, keyword);
            return paginateUser(filteredMovies, page, recordsPerPage);
        }
        // Nếu không có trong Redis, lấy từ Database
        Page<Cinema> userPage = cinemaRepository.searchUserByKeyword(keyword, PageRequest.of(page - 1, recordsPerPage));

        // Chuyển đổi từ Entity -> DTO và lưu vào cache
        List<CinemaResponse> movieResponses = userPage.getContent().stream().map(cinemaMapper::toDTO).toList();
        return new PageImpl<>(movieResponses, PageRequest.of(page - 1, recordsPerPage), userPage.getTotalElements());
    }

    private List<CinemaResponse> filterCinemaByKeyword(List<CinemaResponse> cachedUsers, String keyword) {
        return cachedUsers.stream()
                .filter(m -> m.getLocation().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    private Page<CinemaResponse> paginateUser(List<CinemaResponse> cachedUsers, int page, int recordsPerPage) {
        int start = (page - 1) * recordsPerPage;
        int end = Math.min(start + recordsPerPage, cachedUsers.size());
        return new PageImpl<>(cachedUsers.subList(start, end), PageRequest.of(page - 1, recordsPerPage), cachedUsers.size());
    }

    private List<CinemaResponse> getCinemaFromCache() {
        List<CinemaResponse> cachedUsers = (List<CinemaResponse>) redisTemplate.opsForValue().get(CINEMA_CACHE_KEY);
        return cachedUsers != null ? cachedUsers : List.of();
    }
}
