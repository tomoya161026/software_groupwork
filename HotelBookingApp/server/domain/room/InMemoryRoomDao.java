package server.domain.room;

import java.util.ArrayList;
import java.util.List;

public class InMemoryRoomDao implements RoomDao {
    private final List<Room> rooms = new ArrayList<>();

    public InMemoryRoomDao() {
        // ホテルの部屋を初期化
        for (int i = 1; i <= 5; i++) {
            rooms.add(new Room("10" + i));
            rooms.add(new Room("20" + i));
        }
    }
    @Override
    public List<Room> findAll() {
        return new ArrayList<>(rooms);
    }
}