package server.domain.payment;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryPaymentDao implements PaymentDao {
    private final Map<String, Payment> db = new ConcurrentHashMap<>();
    // --- Persistence settings ---
    private static final Path FILE_PATH = Paths.get("payments.jsonl");
    private static final Gson gson = new Gson();

    public InMemoryPaymentDao() {
        loadFromFile();
    }

    @Override
    public synchronized void save(Payment payment) {
        db.put(payment.getReservationNumber(), payment);
        persistToFile();
    }

    @Override
    public Payment findByReservationNumber(String reservationNumber) {
        return db.get(reservationNumber);
    }

    private void loadFromFile() {
        if (!Files.exists(FILE_PATH)) {
            System.out.println("[LOG] Payment data file not found. A new one will be created.");
            return;
        }
        try (Stream<String> lines = Files.lines(FILE_PATH)) {
            lines.forEach(line -> {
                try {
                    Payment payment = gson.fromJson(line, Payment.class);
                    db.put(payment.getReservationNumber(), payment);
                } catch (JsonSyntaxException e) {
                    System.err.println("Failed to parse JSON from payments file: " + line);
                }
            });
            System.out.println("[LOG] Loaded " + db.size() + " payment records from file.");
        } catch (IOException e) {
            System.err.println("Failed to load payment data: " + e.getMessage());
        }
    }

    private void persistToFile() {
        try {
            List<String> jsonLines = db.values().stream()
                    .map(gson::toJson)
                    .collect(Collectors.toList());
            Files.write(FILE_PATH, jsonLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to persist payment data: " + e.getMessage());
        }
    }
}