package com.example.movieticketWeb.restController.admin;

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
    public ResponseEntity<?> getCouponById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, couponService.getCouponById(id)));
    }

    @PostMapping()
    public ResponseEntity<?> createCoupon(@RequestBody CouponRequest couponRequest) {
        boolean isAdded = couponService.addCoupon(couponRequest);
        if (isAdded) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.CREATED));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCoupon(@PathVariable Long id, @RequestBody CouponRequest couponRequest) {
        boolean isUpdated = couponService.updateCoupon(id, couponRequest);
        if (isUpdated) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.UPDATED));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long id) {
        boolean isDeleted = couponService.deleteCoupon(id);
        if (isDeleted) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.DELETED));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
