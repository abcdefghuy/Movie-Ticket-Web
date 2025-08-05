package com.example.movieticketWeb.restController.admin;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.movieticketWeb.dto.response.ApiResponse;
import com.example.movieticketWeb.dto.response.MovieScreenResponse;
import com.example.movieticketWeb.dto.response.SeatStatusResponse;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.service.IMovieScreenService;
import com.example.movieticketWeb.service.ISeatStatusService;

@RestController
@RequestMapping("/admin/seat")
public class SeatRestController {
    private final ISeatStatusService seatStatusService;
    private final IMovieScreenService movieScreenService;

    public SeatRestController(ISeatStatusService seatStatusService, IMovieScreenService movieScreenService) {
        this.seatStatusService = seatStatusService;
        this.movieScreenService = movieScreenService;
    }

    @GetMapping
    public ResponseEntity<?> viewSeatRoom(@RequestParam int msID, @RequestParam int roomID) {
        List<SeatStatusResponse> seatStatusResponses =
                seatStatusService.getSeatStatusesByScreeningAndRoom(msID, roomID);
        MovieScreenResponse screeningResponse = movieScreenService.getScreeningInfo(seatStatusResponses, msID);
        Map<String, List<SeatStatusResponse>> seatStatusesGroupedByRow =
                seatStatusService.groupSeatsByRow(seatStatusResponses);
        return ResponseEntity.ok(ApiResponse.success(
                ErrorCode.SUCCESS,
                Map.of(
                        "screen", screeningResponse,
                        "seat", seatStatusesGroupedByRow)));
    }
}
