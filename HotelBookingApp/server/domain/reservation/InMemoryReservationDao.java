package server.domain.reservation;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryReservationDao implements ReservationDao {
    private final Map<String, Reservation> db = new ConcurrentHashMap<>();
    private static final Path FILE_PATH = Paths.get("reservations.jsonl");
    private static final Gson gson = new Gson();

    public InMemoryReservationDao() {
        loadFromFile();
    }

    @Override
    public synchronized void save(Reservation reservation) {
        db.put(reservation.getReservationNumber(), reservation);
        persistToFile();
    }

    @Override
    public Reservation findByNumber(String reservationNumber) {
        return db.get(reservationNumber);
    }

    @Override
    public List<Reservation> findAll() {
        return new ArrayList<>(db.values());
    }

    @Override
    public synchronized void delete(String reservationNumber) {
        db.remove(reservationNumber);
        persistToFile();
    }
    
    private void loadFromFile() {
        if (!Files.exists(FILE_PATH)) {
            System.out.println("[LOG] Reservation data file not found. A new one will be created.");
            return;
        }
        try (Stream<String> lines = Files.lines(FILE_PATH)) {
            lines.forEach(line -> {
                try {
                    Reservation reservation = gson.fromJson(line, Reservation.class);
                    db.put(reservation.getReservationNumber(), reservation);
                } catch (JsonSyntaxException e) {
                    System.err.println("Failed to parse JSON: " + line);
                }
            });
            System.out.println("[LOG] Loaded " + db.size() + " reservations from file.");
        } catch (IOException e) {
            System.err.println("Failed to load reservation data: " + e.getMessage());
        }
    }
    
    private void persistToFile() {
        try {
            List<String> jsonLines = db.values().stream()
                    .map(gson::toJson)
                    .collect(Collectors.toList());
            Files.write(FILE_PATH, jsonLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("[LOG] Reservation data persisted to file. Total records: " + jsonLines.size());
        } catch (IOException e) {
            System.err.println("Failed to persist reservation data: " + e.getMessage());
        }
    }
}