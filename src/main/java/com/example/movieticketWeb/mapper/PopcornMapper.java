package com.example.movieticketWeb.mapper;

import com.example.movieticketWeb.dto.request.PopCornRequest;
import com.example.movieticketWeb.dto.response.PopcornResponse;
import com.example.movieticketWeb.entity.PopCorn;
import org.springframework.stereotype.Service;

@Service
public class PopcornMapper {
    public PopCorn toEntity(PopCornRequest popCornRequest) {
        return PopCorn.builder()
                .namePopCorn(popCornRequest.getNamePopCorn())
                .typePopCorn(popCornRequest.getTypePopCorn())
                .price(popCornRequest.getPrice())
                .status(popCornRequest.isStatus())
                .build();
    }
    public PopcornResponse toDTO(PopCorn popCorn) {
        return PopcornResponse.builder()
                .popcornId(popCorn.getPopcornID())
                .namePopcorn(popCorn.getNamePopCorn())
                .typePopcorn(popCorn.getTypePopCorn())
                .price(popCorn.getPrice())
                .status(popCorn.isStatus())
                .build();
    }
}
