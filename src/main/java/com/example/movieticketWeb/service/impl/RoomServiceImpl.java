package com.example.movieticketWeb.service.impl;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javassist.NotFoundException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.movieticketWeb.dto.request.RoomRequest;
import com.example.movieticketWeb.dto.response.RoomResponse;
import com.example.movieticketWeb.entity.Cinema;
import com.example.movieticketWeb.entity.Room;
import com.example.movieticketWeb.mapper.RoomMapper;
import com.example.movieticketWeb.repository.ICinemaRepository;
import com.example.movieticketWeb.repository.IRoomRepository;
import com.example.movieticketWeb.service.IRoomService;
import com.example.movieticketWeb.service.ISeatService;

@Service
public class RoomServiceImpl implements IRoomService {
    private final IRoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final ICinemaRepository cinemaRepository;
    private final ISeatService seatService;
    private final RedisTemplate<String, Object> redisTemplate;

    public RoomServiceImpl(
            IRoomRepository roomRepository,
            RedisTemplate<String, Object> redisTemplate,
            ICinemaRepository cinemaRepository,
            ISeatService seatService) {
        this.roomRepository = roomRepository;
        this.redisTemplate = redisTemplate;
        this.seatService = seatService;
        this.roomMapper = new RoomMapper();
        this.cinemaRepository = cinemaRepository;
    }

    @Override
    public List<RoomResponse> getRoomByCinemaId(Long cinemaId) {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        String cacheKey = "roomsByCinema:" + cinemaId;
        // 🔥 Kiểm tra xem danh sách phòng đã có trong cache chưa
        List<RoomResponse> cachedRooms =
                (List<RoomResponse>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedRooms == null) {
            List<RoomResponse> rooms = roomRepository.findByCinemaId(cinemaId).stream()
                    .map(roomMapper::toDTO)
                    .collect(Collectors.toList());
            redisTemplate.opsForValue().set(cacheKey, rooms, Duration.ofHours(1));
            return rooms;
        }
        // ✅ Trả về danh sách phòng từ cache
        return cachedRooms;
    }

    @Override
    public RoomResponse getRoomById(Long id) throws NotFoundException {
        String cacheKey = "room:" + id;

        // 🔥 Kiểm tra xem phòng có trong cache không
        RoomResponse cachedRoom = (RoomResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cachedRoom != null) {
            return cachedRoom;
        }

        Room room = roomRepository.findById(id).orElse(null);
        if (room == null) {
            throw new NotFoundException("Không tìm thấy phòng với ID: " + id);
        }

        RoomResponse roomResponse = roomMapper.toDTO(room);

        // 🏦 Lưu vào Redis (lưu DTO, không lưu Entity)
        redisTemplate.opsForValue().set(cacheKey, roomResponse, Duration.ofHours(1));

        return roomResponse;
    }

    @Override
    @Transactional
    public boolean addRoom(RoomRequest roomRequest) {
        try {
            Cinema cinema = cinemaRepository
                    .findById(roomRequest.getCinemaId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Cinema not found with ID: " + roomRequest.getCinemaId()));
            Room savedRoom = roomRepository.save(roomMapper.toEntity(roomRequest, cinema));
            if (savedRoom != null) {
                seatService.addSeatsForRoom(savedRoom);
            }
            redisTemplate.delete("roomsByCinema:" + roomRequest.getCinemaId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateRoom(RoomRequest roomRequest, Long roomId) {
        try {
            Optional<Room> roomOptional = roomRepository.findById(roomId);
            if (roomOptional.isPresent()) {
                Room room = roomOptional.get();
                // 🔄 Cập nhật thông tin Room từ request
                room.setRoomName(roomRequest.getRoomName());
                room.setChairNumber(roomRequest.getChairNumber());
                room.setScreenType(roomRequest.getScreenType());
                room.setStatus(roomRequest.isStatus());

                // 💾 Lưu vào database
                roomRepository.save(room);

                // 🔥 Xóa dữ liệu cũ trong Redis
                redisTemplate.delete("room:" + roomId);
                redisTemplate.delete("roomsByCinema:" + room.getCinema().getCinemaId());

                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteRoomById(Long roomId, Long cinemaId) {
        try {
            roomRepository.deleteById(roomId);
            redisTemplate.delete("room:" + roomId);
            redisTemplate.delete("roomsByCinema:" + cinemaId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<RoomResponse> searchRoomsByScreenType(String screenType, Long cinemaId) {
        String cacheKey = "roomsByCinema:" + cinemaId;
        // 🔥 Kiểm tra xem danh sách phòng đã có trong cache chưa
        List<RoomResponse> cachedRooms =
                (List<RoomResponse>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedRooms != null && !cachedRooms.isEmpty()) {
            // Nếu có dữ liệu trong Redis, lọc theo từ khóa và phân trang
            List<RoomResponse> filteredMovies = filterCinemaByKeyword(cachedRooms, screenType);
            return filteredMovies;
        } else {
            List<RoomResponse> rooms = roomRepository.searchRoomByKeyWordAndCinemaId(screenType, cinemaId).stream()
                    .map(roomMapper::toDTO)
                    .collect(Collectors.toList());
            redisTemplate.opsForValue().set(cacheKey, rooms, Duration.ofHours(1));
            return rooms;
        }
    }

    @Override
    public Map<String, Object> getRoomsWithCinemaName(Long cinemaId) {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
        String cacheKey = "roomsByCinema:" + cinemaId;
        // 🔥 Kiểm tra xem danh sách phòng đã có trong cache chưa
        List<RoomResponse> cachedRooms =
                (List<RoomResponse>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedRooms != null && !cachedRooms.isEmpty()) {
            // Nếu có dữ liệu trong Redis, trả về luôn
            Map<String, Object> response = new HashMap<>();
            response.put("rooms", cachedRooms);
            response.put("noOfRecords", cachedRooms.size());
            response.put("cinemaName", cachedRooms.getFirst().getCinemaName());
            return response;
        }
        List<RoomResponse> rooms = roomRepository.findByCinemaId(cinemaId).stream()
                .map(roomMapper::toDTO)
                .collect(Collectors.toList());
        String cinemaName = rooms.stream()
                .findFirst()
                .map(RoomResponse::getCinemaName)
                .orElseGet(() -> cinemaRepository
                        .findById(cinemaId)
                        .map(Cinema::getCinemaName)
                        .orElse("Tên rạp chưa xác định"));
        redisTemplate.opsForValue().set(cacheKey, rooms, Duration.ofHours(1));
        Map<String, Object> response = new HashMap<>();
        response.put("rooms", rooms);
        response.put("noOfRecords", rooms.size());
        response.put("cinemaName", cinemaName);

        return response;
    }

    private List<RoomResponse> filterCinemaByKeyword(List<RoomResponse> rooms, String keyword) {
        return rooms.stream()
                .filter(room -> room.getScreenType().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
}
