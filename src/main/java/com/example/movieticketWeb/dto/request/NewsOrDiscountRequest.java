package com.example.movieticketWeb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsOrDiscountRequest {
    private String title;
    private String description;
    private String detail;
    private String images;
    private String author;
    private Date date;
}
