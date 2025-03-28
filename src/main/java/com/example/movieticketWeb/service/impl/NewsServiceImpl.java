package com.example.movieticketWeb.service.impl;

import com.example.movieticketWeb.dto.request.NewsOrDiscountRequest;
import com.example.movieticketWeb.dto.response.NewsOrDiscountResponse;
import com.example.movieticketWeb.dto.response.PopcornResponse;
import com.example.movieticketWeb.entity.Movie;
import com.example.movieticketWeb.entity.NewsOrDiscount;
import com.example.movieticketWeb.mapper.NewsMapper;
import com.example.movieticketWeb.repository.INewsRepository;
import com.example.movieticketWeb.service.INewsService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements INewsService {
    private final INewsRepository newsRepository;
    private final NewsMapper newsMapper ;
    private static final String NEWS_CACHE_KEY = "NEWS_CACHE_KEY";
    private final RedisTemplate<String, Object> redisTemplate;
    public NewsServiceImpl(INewsRepository newsRepository, NewsMapper newsMapper, RedisTemplate<String, Object> redisTemplate) {
        this.newsRepository = newsRepository;
        this.newsMapper = newsMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean addNews(NewsOrDiscountRequest newsRequest) {
        try {
            NewsOrDiscount newsOrDiscount = newsMapper.toEntity(newsRequest);
            newsRepository.save(newsOrDiscount);
            redisTemplate.delete(NEWS_CACHE_KEY);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateNews(Long id, NewsOrDiscountRequest newsRequest) {
       try {
           NewsOrDiscount newsOrDiscount = newsRepository.findById(id)
                   .orElseThrow(() -> new EntityNotFoundException("News not found with ID: " + id));
           newsOrDiscount.setAuthor(newsRequest.getAuthor());
           newsOrDiscount.setDetail(newsRequest.getDetail());
           newsOrDiscount.setImages(newsRequest.getImages());
           newsOrDiscount.setTitle(newsRequest.getTitle());
           newsOrDiscount.setDescription(newsRequest.getDescription());
           newsOrDiscount.setDate(newsRequest.getDate());
           newsRepository.save(newsOrDiscount);
           NewsOrDiscountResponse newsOrDiscountResponse = newsMapper.toDTO(newsOrDiscount);
           redisTemplate.opsForValue().set("news:" + id, newsOrDiscountResponse, Duration.ofHours(1));
           return true;
        } catch (Exception e) {
            return false;
       }
    }

    @Override
    public boolean deleteNews(Long id) {
        try {
            newsRepository.deleteById(id);
            redisTemplate.delete(NEWS_CACHE_KEY);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Page<NewsOrDiscountResponse> getAllNews(int page, int recordPerPage) {
        List<NewsOrDiscountResponse> cachedNews = getNewsFromCache();
        if (!cachedNews.isEmpty()) {
            return paginateNews(cachedNews, page, recordPerPage);
        }
// Nếu cache không có, lấy từ database và lưu vào cache
        List<NewsOrDiscountResponse> news = newsRepository.findAll().stream()
                .map(newsMapper::toDTO)
                .collect(Collectors.toList());
        redisTemplate.opsForValue().set(NEWS_CACHE_KEY,news, Duration.ofMinutes(30));
        return paginateNews(news, page, recordPerPage);
    }

    private Page<NewsOrDiscountResponse> paginateNews(List<NewsOrDiscountResponse> cachedPopCorns, int page, int recordPerPage) {
        int start = (page - 1) * recordPerPage;
        int end = Math.min(start + recordPerPage, cachedPopCorns.size());
        return new PageImpl<>(cachedPopCorns.subList(start, end), PageRequest.of(page - 1, recordPerPage), cachedPopCorns.size());
    }

    private List<NewsOrDiscountResponse> getNewsFromCache() {
        List<NewsOrDiscountResponse> cachedNews = (List<NewsOrDiscountResponse>) redisTemplate.opsForValue().get(NEWS_CACHE_KEY);
        return cachedNews == null ? new ArrayList<>() : cachedNews;
    }

    @Override
    public NewsOrDiscountResponse getNewsById(Long id) {
        String cacheKey = "news:" + id;
        NewsOrDiscountResponse cachedMovie = (NewsOrDiscountResponse) redisTemplate.opsForValue().get(cacheKey);

        if (cachedMovie == null) {
            NewsOrDiscount newsOrDiscount = newsRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("News not found with ID: " + id));

            // Chuyển đổi sang DTO trước khi lưu vào Redis
            cachedMovie = newsMapper.toDTO(newsOrDiscount);
            redisTemplate.opsForValue().set(cacheKey, cachedMovie, Duration.ofHours(1));
        }
        return cachedMovie;
    }
}
