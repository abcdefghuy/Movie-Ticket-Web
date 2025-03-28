package com.example.movieticketWeb.RestController.admin;

import com.example.movieticketWeb.dto.request.MovieScreenRequest;
import com.example.movieticketWeb.entity.MovieScreenings;
import com.example.movieticketWeb.service.IMovieScreenService;
import javassist.NotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/admin/movie-screens")
public class MovieScreenRestController {
    private final IMovieScreenService movieScreenService;

    public MovieScreenRestController(IMovieScreenService movieScreenService) {
        this.movieScreenService = movieScreenService;
    }

    @GetMapping()
    public ResponseEntity<Map<String,Object>> getMovieScreen(@RequestParam(name = "roomId") Long roomId,
                                                             @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int soluong){
        return ResponseEntity.ok(movieScreenService.getMovieScreens(roomId,page,soluong));
    }
    @PostMapping()
    public ResponseEntity<?> addMovieScreen(@RequestBody MovieScreenRequest movieScreenRequest){
       boolean isAdded = movieScreenService.addMovieScreen(movieScreenRequest);
       if(isAdded){
           return ResponseEntity.ok(Map.of("message","Add movie screen successfully"));
       }
       return ResponseEntity.badRequest().body(Map.of("error","Add movie screen failed"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovieScreen(@PathVariable Long id, @RequestBody MovieScreenRequest movieScreenRequest){
        boolean isUpdated = movieScreenService.updateMovieScreen(id,movieScreenRequest);
        if(isUpdated){
            return ResponseEntity.ok(Map.of("message","Update movie screen successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error","Update movie screen failed"));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovieScreen(@PathVariable Long id){
        boolean isDeleted = movieScreenService.deleteMovieScreen(id);
        if(isDeleted){
            return ResponseEntity.ok(Map.of("message","Delete movie screen successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error","Delete movie screen failed"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieScreenById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(movieScreenService.getMovieScreenById(id));
    }
    @GetMapping("/search")
    public ResponseEntity<Map<String,Object>> searchMovieScreen(@RequestParam(name = "roomId") Long roomId,
                                                                @RequestParam(name ="page", defaultValue = "1") int page,
                                                                @RequestParam(name="soluong", defaultValue = "5") int soluong,
                                                                @RequestParam(name = "keyword", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")  Date keyword){
        if(keyword == null){
            return ResponseEntity.ok(movieScreenService.getMovieScreens(roomId,page,soluong));
        }
        return ResponseEntity.ok(movieScreenService.searchMovieScreen(roomId,page,soluong,keyword));
    }
}
