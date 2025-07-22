package com.example.movieticketWeb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.movieticketWeb.entity.Room;

@Repository
public interface IRoomRepository extends JpaRepository<Room, Long> {
    @Query("select r from Room r where r.cinema.cinemaId = :cinemaId")
    List<Room> findByCinemaId(Long cinemaId);

    @Query("select r from Room r where r.cinema.cinemaId = :cinemaId and r.screenType = :screenType")
    List<Room> searchRoomByKeyWordAndCinemaId(String screenType, Long cinemaId);
}
