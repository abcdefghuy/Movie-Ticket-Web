package com.example.movieticketWeb.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/coupons")
public class CouponController {
    @GetMapping("/coupon-list")
    public ModelAndView couponList(){
        return new ModelAndView("admin/coupon/coupon-list");
    }
}
