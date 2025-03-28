package com.example.movieticketWeb.RestController.admin;

import com.example.movieticketWeb.dto.request.RoomRequest;
import com.example.movieticketWeb.dto.response.RoomResponse;
import com.example.movieticketWeb.entity.Room;
import com.example.movieticketWeb.service.IRoomService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/rooms")
public class RoomRestController {
    private final IRoomService roomService;
    private Long deleteCinemaId;
    public RoomRestController(IRoomService roomService) {
        this.roomService = roomService;
    }
    @GetMapping()
    public ResponseEntity<Map<String, Object>> getRoomByCinemaId(@RequestParam(name = "cinemaId") Long cinemaId) {
        deleteCinemaId = cinemaId;
        return ResponseEntity.ok(roomService.getRoomsWithCinemaName(cinemaId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable Long id) throws NotFoundException {
        RoomResponse room = roomService.getRoomById(id);
        return ResponseEntity.ok(room);
    }
    @PostMapping()
    public ResponseEntity<?> addRoom(@RequestBody RoomRequest room) {
        boolean isAdded = roomService.addRoom(room);
        if (isAdded) {
            return ResponseEntity.ok(Map.of("message", "Add room successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Add room failed"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody RoomRequest room) {
        boolean isUpdated = roomService.updateRoom(room, id);
        if (isUpdated) {
            return ResponseEntity.ok(Map.of("message", "Update room successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Update room failed"));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
        boolean isDeleted = roomService.deleteRoomById(id,deleteCinemaId);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("message", "Delete room successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Delete room failed"));
    }
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchRoomsByScreenType(@RequestParam(name = "screenType") String screenType,
                                                                       @RequestParam(name = "cinemaId") Long cinemaId) {
        List<RoomResponse> rooms=null;
        if(screenType == null || screenType.isEmpty() || screenType.equals("All")) {
            rooms = roomService.getRoomByCinemaId(cinemaId);
        }
        else{
             rooms = roomService.searchRoomsByScreenType(screenType,cinemaId);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("rooms", rooms);
        response.put("noOfRecords", rooms.size());
        return ResponseEntity.ok(response);
    }
}
