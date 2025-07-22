package com.example.movieticketWeb.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.movieticketWeb.dto.response.SeatStatusResponse;
import com.example.movieticketWeb.entity.MovieScreenings;
import com.example.movieticketWeb.entity.Seat;
import com.example.movieticketWeb.entity.SeatStatus;
import com.example.movieticketWeb.mapper.SeatStatusMapper;
import com.example.movieticketWeb.repository.ISeatStatusRepository;
import com.example.movieticketWeb.service.ISeatService;
import com.example.movieticketWeb.service.ISeatStatusService;

@Service
public class SeatStatusServiceImpl implements ISeatStatusService {
    private final ISeatStatusRepository seatStatusRepository;
    private final SeatStatusMapper seatStatusMapper;
    private final ISeatService seatService;

    public SeatStatusServiceImpl(
            ISeatStatusRepository seatStatusRepository, SeatStatusMapper seatStatusMapper, ISeatService seatService) {
        this.seatStatusRepository = seatStatusRepository;
        this.seatStatusMapper = seatStatusMapper;
        this.seatService = seatService;
    }

    @Override
    public List<SeatStatusResponse> getSeatStatusesByScreeningAndRoom(int msID, int roomID) {
        List<SeatStatus> seatStatuses = seatStatusRepository.findByScreeningAndRoom(msID, roomID);
        return seatStatuses.stream().map(seatStatusMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public void addSeatStatusForScreening(MovieScreenings movieScreening) {
        List<Seat> seats = seatService.getSeatsByRoomId(movieScreening.getRoom().getRoomId());
        List<SeatStatus> seatStatuses = new ArrayList<>();
        for (Seat seat : seats) {
            SeatStatus seatStatus = new SeatStatus();
            seatStatus.setSeat(seat);
            seatStatus.setScreening(movieScreening);
            seatStatus.setStatus(false); // Ghế trống mặc định
            seatStatuses.add(seatStatus);
        }
        seatStatusRepository.saveAll(seatStatuses); // Lưu một lần để tối ưu hiệu suất
    }

    @Override
    public Map<String, List<SeatStatusResponse>> groupSeatsByRow(List<SeatStatusResponse> seatStatusResponses) {
        return seatStatusResponses.stream()
                .collect(Collectors.groupingBy(seat -> seat.getSeatNumber().substring(0, 1)));
    }
}
