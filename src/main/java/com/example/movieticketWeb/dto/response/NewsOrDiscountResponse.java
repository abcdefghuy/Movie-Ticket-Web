package com.example.movieticketWeb.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsOrDiscountResponse {
    private int newsID;
    private String title;
    private String description;
    private String detail;
    private String images;
    private String author;
    private Date date;
}
