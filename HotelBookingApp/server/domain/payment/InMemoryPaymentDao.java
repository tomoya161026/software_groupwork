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
    // ★★★ 永続化のための設定を追加 ★★★
    private static final Path FILE_PATH = Paths.get("payments.jsonl");
    private static final Gson gson = new Gson();

    public InMemoryPaymentDao() {
        // ★★★ サーバー起動時にファイルからデータを読み込む ★★★
        loadFromFile();
    }

    @Override
    public synchronized void save(Payment payment) {
        db.put(payment.getReservationNumber(), payment);
        // ★★★ データ変更時にファイルへ書き込む ★★★
        persistToFile();
    }

    @Override
    public Payment findByReservationNumber(String reservationNumber) {
        return db.get(reservationNumber);
    }

    // ★★★ ファイルからデータを読み込むメソッド ★★★
    private void loadFromFile() {
        if (!Files.exists(FILE_PATH)) {
            System.out.println("支払いデータファイルが見つかりません。新しいファイルを作成します。");
            return;
        }
        try (Stream<String> lines = Files.lines(FILE_PATH)) {
            lines.forEach(line -> {
                try {
                    Payment payment = gson.fromJson(line, Payment.class);
                    db.put(payment.getReservationNumber(), payment);
                } catch (JsonSyntaxException e) {
                    System.err.println("JSONの解析に失敗しました: " + line);
                }
            });
            System.out.println(db.size() + "件の支払いデータをファイルから読み込みました。");
        } catch (IOException e) {
            System.err.println("支払いデータの読み込みに失敗しました: " + e.getMessage());
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
            System.err.println("支払いデータの書き込みに失敗しました: " + e.getMessage());
        }
    }
}