package com.example.movieticketWeb.service.impl;

import com.example.movieticketWeb.dto.request.PopCornRequest;
import com.example.movieticketWeb.dto.response.CouponResponse;
import com.example.movieticketWeb.dto.response.PopcornResponse;
import com.example.movieticketWeb.entity.Coupon;
import com.example.movieticketWeb.entity.PopCorn;
import com.example.movieticketWeb.mapper.PopcornMapper;
import com.example.movieticketWeb.repository.IPopCornRepository;
import com.example.movieticketWeb.service.IPopCornService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PopCornServiceImpl implements IPopCornService {
    private final IPopCornRepository popCornRepository;
    private final PopcornMapper popcornMapper;
    private static final String POPCORN_CACHE_KEY = "allPopCorns";
    private final RedisTemplate<String, Object> redisTemplate;

    public PopCornServiceImpl(IPopCornRepository popCornRepository, PopcornMapper popcornMapper, RedisTemplate<String, Object> redisTemplate) {
        this.popCornRepository = popCornRepository;
        this.popcornMapper = popcornMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Page<PopcornResponse> getAllPopCorn(int page, int recordPerPage) {
        List<PopcornResponse> cachedPopCorns = getPopCornsFromCache();
        if (!cachedPopCorns.isEmpty()) {
            return paginatePopCorn(cachedPopCorns, page, recordPerPage);
        }
// Nếu cache không có, lấy từ database và lưu vào cache
        List<PopcornResponse> coupons = popCornRepository.findAll().stream()
                .map(popcornMapper::toDTO)
                .collect(Collectors.toList());
        redisTemplate.opsForValue().set(POPCORN_CACHE_KEY, coupons, Duration.ofMinutes(30));
        return paginatePopCorn(coupons, page, recordPerPage);
    }

    private Page<PopcornResponse> paginatePopCorn(List<PopcornResponse> cachedPopCorns, int page, int recordPerPage) {
        int start = (page - 1) * recordPerPage;
        int end = Math.min(start + recordPerPage, cachedPopCorns.size());
        return new PageImpl<>(cachedPopCorns.subList(start, end), PageRequest.of(page - 1, recordPerPage), cachedPopCorns.size());
    }

    private List<PopcornResponse> getPopCornsFromCache() {
        List<PopcornResponse> cachedCoupons = (List<PopcornResponse>) redisTemplate.opsForValue().get(POPCORN_CACHE_KEY);
        return cachedCoupons != null ? cachedCoupons : List.of();
    }

    @Override
    public PopcornResponse getPopCornById(Long id) {
        String cacheKey = "popcorn:" + id;
        PopcornResponse popcornResponse = (PopcornResponse) redisTemplate.opsForValue().get(cacheKey);
        if (popcornResponse != null) {
            return popcornResponse;
        }
        PopCorn popCorn = popCornRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + id));
        redisTemplate.opsForValue().set(cacheKey, popCorn, Duration.ofHours(1));
        return popcornMapper.toDTO(popCorn);
    }

    @Override
    public boolean addPopCorn(PopCornRequest popCorn) {
        try {
            PopCorn popcorn = popcornMapper.toEntity(popCorn);
            popCornRepository.save(popcorn);
            redisTemplate.delete(POPCORN_CACHE_KEY);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updatePopCorn(PopCornRequest popCorn, Long id) {
        try{
            PopCorn popcorn = popCornRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + id));
            popcorn.setNamePopCorn(popCorn.getNamePopCorn());
            popcorn.setTypePopCorn(popCorn.getTypePopCorn());
            popcorn.setPrice(popCorn.getPrice());
            popcorn.setStatus(popCorn.isStatus());
            popCornRepository.save(popcorn);
            redisTemplate.delete(POPCORN_CACHE_KEY);
            PopcornResponse popcornResponse = popcornMapper.toDTO(popcorn);
            redisTemplate.opsForValue().set("popcorn:" + id, popcornResponse, Duration.ofHours(1));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deletePopCorn(long id) {
        try {
            popCornRepository.deleteById(id);
            redisTemplate.delete(POPCORN_CACHE_KEY);
            redisTemplate.delete("popcorn:" + id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
