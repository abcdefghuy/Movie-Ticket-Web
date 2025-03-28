package com.example.movieticketWeb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieScreenRequest {
    private Long movieId;
    private Long roomId;
    private Date startTime;
    private Date endTime;
    private Date screenDate;
    private boolean status;
}
