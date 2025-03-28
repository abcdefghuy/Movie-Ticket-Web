package com.example.movieticketWeb.repository;

import com.example.movieticketWeb.dto.response.RoomResponse;
import com.example.movieticketWeb.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRoomRepository extends JpaRepository<Room, Long> {
    @Query("select r from Room r where r.cinema.cinemaId = :cinemaId")
    List<Room> findByCinemaId(Long cinemaId);
    @Query("select r from Room r where r.cinema.cinemaId = :cinemaId and r.screenType = :screenType")
    List<Room> searchRoomByKeyWordAndCinemaId(String screenType, Long cinemaId);
}
