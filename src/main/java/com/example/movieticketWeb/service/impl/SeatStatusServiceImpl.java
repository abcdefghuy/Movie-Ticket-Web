package com.example.movieticketWeb.service.impl;

import com.example.movieticketWeb.dto.response.MovieScreenResponse;
import com.example.movieticketWeb.dto.response.SeatStatusResponse;
import com.example.movieticketWeb.entity.MovieScreenings;
import com.example.movieticketWeb.entity.Seat;
import com.example.movieticketWeb.entity.SeatStatus;
import com.example.movieticketWeb.mapper.SeatStatusMapper;
import com.example.movieticketWeb.repository.IMovieScreenRepository;
import com.example.movieticketWeb.repository.ISeatStatusRepository;
import com.example.movieticketWeb.service.IMovieScreenService;
import com.example.movieticketWeb.service.ISeatService;
import com.example.movieticketWeb.service.ISeatStatusService;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SeatStatusServiceImpl implements ISeatStatusService {
    private final ISeatStatusRepository seatStatusRepository;
    private final SeatStatusMapper seatStatusMapper ;
    private final ISeatService seatService;
    private final IMovieScreenService movieScreeningService;

    public SeatStatusServiceImpl(ISeatStatusRepository seatStatusRepository, SeatStatusMapper seatStatusMapper, ISeatService seatService, IMovieScreenService movieScreeningService) {
        this.seatStatusRepository = seatStatusRepository;
        this.seatStatusMapper = seatStatusMapper;
        this.seatService = seatService;
        this.movieScreeningService = movieScreeningService;
    }

    @Override
    public List<SeatStatusResponse> getSeatStatusesByScreeningAndRoom(int msID, int roomID) {
        List<SeatStatus> seatStatuses = seatStatusRepository.findByScreeningAndRoom(msID, roomID);
        return seatStatuses.stream()
                .map(seatStatusMapper::toDTO)
                .collect(Collectors.toList());
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
    public MovieScreenResponse getScreeningInfo(List<SeatStatusResponse> seatStatusResponses, long msID) throws NotFoundException {
       MovieScreenResponse movieScreenResponse = movieScreeningService.getMovieScreenById(msID);
       movieScreenResponse.setAvailableSeats(seatStatusResponses.stream().filter(seatStatusResponse -> !seatStatusResponse.isStatus()).count());
       return movieScreenResponse;
    }

    @Override
    public Map<String, List<SeatStatusResponse>> groupSeatsByRow(List<SeatStatusResponse> seatStatusResponses) {
        return seatStatusResponses.stream()
                .collect(Collectors.groupingBy(seat -> seat.getSeatNumber().substring(0, 1)));
    }
}
