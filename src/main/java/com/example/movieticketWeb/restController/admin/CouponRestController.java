package com.example.movieticketWeb.restController.admin;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.movieticketWeb.dto.request.CouponRequest;
import com.example.movieticketWeb.dto.response.ApiResponse;
import com.example.movieticketWeb.dto.response.CouponResponse;
import com.example.movieticketWeb.dto.response.PagedResponse;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.service.ICouponService;

@RestController
@RequestMapping("/admin/coupons")
public class CouponRestController {
    private final ICouponService couponService;

    public CouponRestController(ICouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllCoupons(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int soluong) {
        Page<CouponResponse> coupons = couponService.getCoupons(page, soluong);

        PagedResponse<CouponResponse> response = new PagedResponse<>(
                coupons.getContent(),
                coupons.getNumber(),
                coupons.getSize(),
                coupons.getTotalElements(),
                coupons.getTotalPages(),
                coupons.isLast());
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(couponService.getCouponById(id));
    }

    @PostMapping()
    public ResponseEntity<?> createCoupon(@RequestBody CouponRequest couponRequest) {
        boolean isAdded = couponService.addCoupon(couponRequest);
        if (isAdded) {
            return ResponseEntity.ok(Map.of("message", "Add coupon successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Add coupon failed"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCoupon(@PathVariable Long id, @RequestBody CouponRequest couponRequest) {
        boolean isUpdated = couponService.updateCoupon(id, couponRequest);
        if (isUpdated) {
            return ResponseEntity.ok(Map.of("message", "Update coupon successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Update coupon failed"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long id) {
        boolean isDeleted = couponService.deleteCoupon(id);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("message", "Delete coupon successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Delete coupon failed"));
    }
}
