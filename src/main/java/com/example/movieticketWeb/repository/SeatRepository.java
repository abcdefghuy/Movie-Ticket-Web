package com.example.movieticketWeb.repository;

import com.example.movieticketWeb.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query("SELECT s FROM Seat s WHERE s.room.roomId = :roomId")
    List<Seat> getSeatsByRoomId(@Param("roomId") int roomId);
}
