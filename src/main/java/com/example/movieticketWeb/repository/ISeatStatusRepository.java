package com.example.movieticketWeb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.movieticketWeb.entity.SeatStatus;

@Repository
public interface ISeatStatusRepository extends JpaRepository<SeatStatus, Integer> {
    @Query("SELECT s FROM SeatStatus s WHERE s.screening.msID = :msID AND s.seat.room.roomId = :roomID")
    List<SeatStatus> findByScreeningAndRoom(int msID, int roomID);
}
