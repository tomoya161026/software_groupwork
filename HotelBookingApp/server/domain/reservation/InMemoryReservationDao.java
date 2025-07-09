package server.domain.reservation;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryReservationDao implements ReservationDao {
    private final Map<String, Reservation> db = new ConcurrentHashMap<>();
    // ★★★ 永続化のための設定を追加 ★★★
    private static final Path FILE_PATH = Paths.get("reservations.jsonl");
    private static final Gson gson = new Gson();

    public InMemoryReservationDao() {
        // ★★★ サーバー起動時にファイルからデータを読み込む ★★★
        loadFromFile();
    }

    @Override
    public synchronized void save(Reservation reservation) {
        db.put(reservation.getReservationNumber(), reservation);
        // ★★★ データ変更時にファイルへ書き込む ★★★
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
        // ★★★ データ変更時にファイルへ書き込む ★★★
        persistToFile();
    }

    // ★★★ ファイルからデータを読み込むメソッド ★★★
    private void loadFromFile() {
        if (!Files.exists(FILE_PATH)) {
            System.out.println("データファイルが見つかりません。新しいファイルを作成します。");
            return;
        }
        try (Stream<String> lines = Files.lines(FILE_PATH)) {
            lines.forEach(line -> {
                try {
                    Reservation reservation = gson.fromJson(line, Reservation.class);
                    db.put(reservation.getReservationNumber(), reservation);
                } catch (JsonSyntaxException e) {
                    System.err.println("JSONの解析に失敗しました: " + line);
                }
            });
            System.out.println(db.size() + "件の予約データをファイルから読み込みました。");
        } catch (IOException e) {
            System.err.println("予約データの読み込みに失敗しました: " + e.getMessage());
        }
    }

    // ★★★ メモリ上の全データをファイルに書き出すメソッド ★★★
    private void persistToFile() {
        try {
            List<String> jsonLines = db.values().stream()
                    .map(gson::toJson)
                    .collect(Collectors.toList());
            Files.write(FILE_PATH, jsonLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("予約データの書き込みに失敗しました: " + e.getMessage());
        }
    }
}