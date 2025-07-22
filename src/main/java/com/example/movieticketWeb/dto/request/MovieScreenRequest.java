package com.example.movieticketWeb.dto.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
