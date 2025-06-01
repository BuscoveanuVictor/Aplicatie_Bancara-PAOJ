package DB;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CSVLogger {
    private static final String CSV_FILE = "logs/tranzactii.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        try {
            File dir = new File("logs");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(CSV_FILE);
            if (!file.exists()) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("Timestamp,Operatie,IBAN,Suma,Detalii\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logTranzactie(String operatie, String iban, double suma, String detalii) {
        try (FileWriter writer = new FileWriter(CSV_FILE, true)) {
            String timestamp = LocalDateTime.now().format(formatter);
            String line = String.format("%s,%s,%s,%.2f,%s\n", 
                timestamp, operatie, iban, suma, detalii);
            writer.write(line);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 