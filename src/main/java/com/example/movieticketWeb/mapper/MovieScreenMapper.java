package com.example.movieticketWeb.mapper;

import com.example.movieticketWeb.dto.request.MovieScreenRequest;
import com.example.movieticketWeb.dto.response.MovieResponse;
import com.example.movieticketWeb.dto.response.MovieScreenResponse;
import com.example.movieticketWeb.entity.Movie;
import com.example.movieticketWeb.entity.MovieScreenings;
import com.example.movieticketWeb.entity.Room;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class MovieScreenMapper {
    public MovieScreenResponse toDTO(MovieScreenings movieScreenings){
        return MovieScreenResponse.builder()
                .movieScreenId(movieScreenings.getMsID())
                .movieName(movieScreenings.getMovie().getMovieName())
                .roomName(movieScreenings.getRoom().getRoomName())
                .movieId(movieScreenings.getMovie().getMovieID())
                .roomId(movieScreenings.getRoom().getRoomId())
                .startTime(movieScreenings.getStartHour())
                .endTime(movieScreenings.getEndHour())
                .screeningDate(movieScreenings.getScreeningDate())
                .status(movieScreenings.isStatus())
                .cinemaName(movieScreenings.getRoom().getCinema().getCinemaName())
                .build();
    }
    public MovieScreenings toEntity(MovieScreenRequest movieScreenings, Movie movie, Room room){
        return MovieScreenings.builder()
                .movie(movie)
                .room(room)
                .startHour(movieScreenings.getStartTime())
                .endHour(movieScreenings.getEndTime())
                .screeningDate(movieScreenings.getScreenDate())
                .status(movieScreenings.isStatus())
                .build();
    }
}
