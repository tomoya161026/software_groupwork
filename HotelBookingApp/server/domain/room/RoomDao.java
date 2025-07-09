package server.domain.room;

import java.util.List;

public interface RoomDao {
    List<Room> findAll();
}