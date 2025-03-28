package com.example.movieticketWeb.repository;

import com.example.movieticketWeb.dto.response.MovieScreenResponse;
import com.example.movieticketWeb.entity.MovieScreenings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IMovieScreenRepository extends JpaRepository<MovieScreenings, Long> {
    @Query("select ms from MovieScreenings ms where ms.room.roomId = :roomId")
    Page<MovieScreenings> getMovieScreenByRoomId(Long roomId, PageRequest of) ;
    @Query("select ms from MovieScreenings ms where ms.room.roomId = :roomId and ms.screeningDate = :keyword")
    List<MovieScreenings> searchMovieScreeningsByScreenDate(Long roomId, Date keyword);
}
