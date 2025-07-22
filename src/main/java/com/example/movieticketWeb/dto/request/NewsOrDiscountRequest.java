package com.example.movieticketWeb.dto.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
