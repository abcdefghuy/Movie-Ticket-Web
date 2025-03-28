package com.example.movieticketWeb.mapper;

import com.example.movieticketWeb.dto.request.NewsOrDiscountRequest;
import com.example.movieticketWeb.dto.response.NewsOrDiscountResponse;
import com.example.movieticketWeb.entity.NewsOrDiscount;
import org.springframework.stereotype.Service;

@Service
public class NewsMapper {
    public NewsOrDiscount toEntity(NewsOrDiscountRequest newsRequest) {
        return NewsOrDiscount.builder()
                .title(newsRequest.getTitle())
                .description(newsRequest.getDescription())
                .detail(newsRequest.getDetail())
                .images(newsRequest.getImages())
                .author(newsRequest.getAuthor())
                .date(newsRequest.getDate())
                .build();
    }
    public NewsOrDiscountResponse toDTO(NewsOrDiscount news) {
        return NewsOrDiscountResponse.builder()
                .newsID(news.getNewsID())
                .title(news.getTitle())
                .description(news.getDescription())
                .detail(news.getDetail())
                .images(news.getImages())
                .author(news.getAuthor())
                .date(news.getDate())
                .build();
    }
}
