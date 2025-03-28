package com.example.movieticketWeb.service.impl;

import com.example.movieticketWeb.entity.Room;
import com.example.movieticketWeb.entity.Seat;
import com.example.movieticketWeb.repository.SeatRepository;
import com.example.movieticketWeb.service.ISeatService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatServiceImpl implements ISeatService {
    private final SeatRepository seatRepository;

    public SeatServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    public void addSeatsForRoom(Room savedRoom) {
        char[] rows = {'A', 'B', 'C', 'D', 'E'};
        int seatsPerRow = 14;
        int seatCounter = 0;
        List<Seat> seats = new ArrayList<>();
        for (char row : rows) {
            for (int i = 1; i <= seatsPerRow && seatCounter < savedRoom.getChairNumber(); i++) {
                Seat seat = new Seat();
                seat.setSeatNumber(String.valueOf(row) + i); // Tạo số ghế nhanh hơn
                seat.setRoom(savedRoom);
                seat.setCouple(row == 'D' || row == 'E');
                seats.add(seat);
                seatCounter++;
            }
        }
        // Lưu tất cả ghế vào database trong một lần
        seatRepository.saveAll(seats);
    }

    @Override
    public List<Seat> getSeatsByRoomId(int roomId) {
        return seatRepository.getSeatsByRoomId(roomId);
    }
}
