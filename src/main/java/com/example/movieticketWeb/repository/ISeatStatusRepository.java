package com.example.movieticketWeb.repository;

import com.example.movieticketWeb.entity.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISeatStatusRepository extends JpaRepository<SeatStatus, Integer> {
    @Query("SELECT s FROM SeatStatus s WHERE s.screening.msID = :msID AND s.seat.room.roomId = :roomID")
    List<SeatStatus> findByScreeningAndRoom(int msID, int roomID);
}
