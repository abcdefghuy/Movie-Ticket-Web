package com.example.movieticketWeb.restController.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javassist.NotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.movieticketWeb.dto.request.RoomRequest;
import com.example.movieticketWeb.dto.response.ApiResponse;
import com.example.movieticketWeb.dto.response.RoomResponse;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.service.IRoomService;

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
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, roomService.getRoomById(id)));
    }

    @PostMapping()
    public ResponseEntity<?> addRoom(@RequestBody RoomRequest room) {
        boolean isAdded = roomService.addRoom(room);
        if (isAdded) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.CREATED));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody RoomRequest room) {
        boolean isUpdated = roomService.updateRoom(room, id);
        if (isUpdated) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.UPDATED));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
        boolean isDeleted = roomService.deleteRoomById(id, deleteCinemaId);
        if (isDeleted) {
            return ResponseEntity.ok(ApiResponse.success(ErrorCode.DELETED));
        }
        return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchRoomsByScreenType(
            @RequestParam(name = "screenType") String screenType, @RequestParam(name = "cinemaId") Long cinemaId) {
        List<RoomResponse> rooms = null;
        if (screenType == null || screenType.isEmpty() || screenType.equals("All")) {
            rooms = roomService.getRoomByCinemaId(cinemaId);
        } else {
            rooms = roomService.searchRoomsByScreenType(screenType, cinemaId);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("rooms", rooms);
        response.put("noOfRecords", rooms.size());
        return ResponseEntity.ok(ApiResponse.success(ErrorCode.SUCCESS, response));
    }
}
