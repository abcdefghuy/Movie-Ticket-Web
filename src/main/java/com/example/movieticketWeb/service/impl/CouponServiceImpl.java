package com.example.movieticketWeb.service.impl;

import com.example.movieticketWeb.dto.request.CouponRequest;
import com.example.movieticketWeb.dto.response.CouponResponse;
import com.example.movieticketWeb.dto.response.PersonInfoResponse;
import com.example.movieticketWeb.entity.Coupon;
import com.example.movieticketWeb.mapper.CouponMapper;
import com.example.movieticketWeb.repository.ICouponRepository;
import com.example.movieticketWeb.service.ICouponService;
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
public class CouponServiceImpl implements ICouponService {
    private final ICouponRepository couponRepository;
    private final CouponMapper couponMapper;
    private static final String COUPON_CACHE_KEY = "allCoupons";
    private final RedisTemplate<String, Object> redisTemplate;
    public CouponServiceImpl(ICouponRepository couponRepository, CouponMapper couponMapper, RedisTemplate<String, Object> redisTemplate) {
        this.couponRepository = couponRepository;
        this.couponMapper = couponMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Page<CouponResponse> getCoupons(int page, int soluong) {
        redisTemplate.delete(COUPON_CACHE_KEY);
        List<CouponResponse> cachedCoupons = getCouponsFromCache();
        if (!cachedCoupons.isEmpty()) {
            return paginateCoupon(cachedCoupons, page, soluong);
        }
// Nếu cache không có, lấy từ database và lưu vào cache
        List<CouponResponse> coupons = couponRepository.findAll().stream()
                .map(couponMapper::toDTO)
                .collect(Collectors.toList());
        redisTemplate.opsForValue().set(COUPON_CACHE_KEY, coupons, Duration.ofMinutes(30));
        return paginateCoupon(coupons, page, soluong);
    }

    @Override
    public CouponResponse getCouponById(Long id) {
        String cacheKey = "coupon:" + id;
        CouponResponse couponResponse =  (CouponResponse) redisTemplate.opsForValue().get(cacheKey);
        if (couponResponse != null) {
            return couponResponse;
        }
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + id));
        redisTemplate.opsForValue().set(cacheKey, coupon, Duration.ofHours(1));
        return couponMapper.toDTO(coupon);
    }

    @Override
    public boolean addCoupon(CouponRequest couponRequest) {
        try{
            Coupon coupon = couponMapper.toEntity(couponRequest);
            couponRepository.save(coupon);
            redisTemplate.delete(COUPON_CACHE_KEY);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateCoupon(Long id, CouponRequest couponRequest) {
        try {
            Coupon coupon = couponRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + id));
            coupon.setCouponName(couponRequest.getCouponName());
            coupon.setCouponType(couponRequest.getCouponType());
            coupon.setCouponValue(couponRequest.getCouponValue());
            coupon.setEndDate(couponRequest.getEndDate());
            coupon.setStartDate(couponRequest.getStartDate());
            couponRepository.save(coupon);
            redisTemplate.delete(COUPON_CACHE_KEY);
            CouponResponse couponResponse = couponMapper.toDTO(coupon);
            redisTemplate.opsForValue().set("coupon:" + id, couponResponse, Duration.ofHours(1));
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteCoupon(Long id) {
        try {
            couponRepository.deleteById(id);
            redisTemplate.delete(COUPON_CACHE_KEY);
            redisTemplate.delete("coupon:" + id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Page<CouponResponse> paginateCoupon(List<CouponResponse> cachedCoupons, int page, int soluong) {
        int start = (page - 1) * soluong;
        int end = Math.min(start + soluong, cachedCoupons.size());
        return new PageImpl<>(cachedCoupons.subList(start, end), PageRequest.of(page - 1, soluong), cachedCoupons.size());
    }

    private List<CouponResponse> getCouponsFromCache() {
        List<CouponResponse> cachedCoupons = (List<CouponResponse>) redisTemplate.opsForValue().get(COUPON_CACHE_KEY);
        return cachedCoupons != null ? cachedCoupons : List.of();
    }
}
