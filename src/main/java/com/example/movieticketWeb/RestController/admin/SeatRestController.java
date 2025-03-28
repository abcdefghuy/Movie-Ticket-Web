package com.example.movieticketWeb.RestController.admin;

import com.example.movieticketWeb.dto.response.MovieScreenResponse;
import com.example.movieticketWeb.dto.response.SeatStatusResponse;
import com.example.movieticketWeb.service.ISeatStatusService;
import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/seat")
public class SeatRestController {
    private final ISeatStatusService seatStatusService;

    public SeatRestController(ISeatStatusService seatStatusService) {
        this.seatStatusService = seatStatusService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> viewSeatRoom(@RequestParam int msID,
                                            @RequestParam int roomID) throws NotFoundException {
        List<SeatStatusResponse> seatStatusResponses = seatStatusService.getSeatStatusesByScreeningAndRoom(msID, roomID);
        MovieScreenResponse screeningResponse = seatStatusService.getScreeningInfo(seatStatusResponses, msID);
        Map<String, List<SeatStatusResponse>> seatStatusesGroupedByRow = seatStatusService.groupSeatsByRow(seatStatusResponses);
        return ResponseEntity.ok(Map.of(
                "screen", screeningResponse,
                "seat", seatStatusesGroupedByRow
        ));
    }
}
