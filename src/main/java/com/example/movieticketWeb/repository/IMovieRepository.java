package com.example.movieticketWeb.repository;

import com.example.movieticketWeb.dto.response.MovieResponse;
import com.example.movieticketWeb.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMovieRepository extends JpaRepository<Movie, Long> {
    @Query("SELECT COUNT(m) FROM Movie m")
    int countMovies();

    // Tìm kiếm phim theo từ khóa (phân trang)
    @Query("SELECT m FROM Movie m WHERE LOWER(m.movieName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Movie> searchMoviesByKeyword(String keyword, Pageable pageable);

    // Tìm kiếm phim theo từ khóa và danh mục (phân trang)
    @Query("""
        SELECT m FROM Movie m
        WHERE LOWER(m.movieName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        AND (:categories IS NULL OR m.category IN :categories)
    """)
    Page<Movie> searchMoviesByKeywordAndCategories(@Param("keyword") String keyword,
                                                   @Param("categories") List<String> categories,
                                                   Pageable pageable);
    // Đếm số kết quả tìm kiếm theo từ khóa
    @Query("SELECT COUNT(m) FROM Movie m WHERE LOWER(m.movieName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    int countMoviesByKeyword(String keyword);

    // Đếm số kết quả tìm kiếm theo từ khóa và danh mục
    @Query("SELECT COUNT(m) FROM Movie m WHERE LOWER(m.movieName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND m.category IN :categories")
    int countMoviesByKeywordAndCategories(String keyword, List<String> categories);
}
