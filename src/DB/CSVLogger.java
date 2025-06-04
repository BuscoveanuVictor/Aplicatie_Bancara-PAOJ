package DB;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CSVLogger {
    private static final String CSV_FILE = "logs/operatii.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    static {
        createLogFile();
    }

    private static void createLogFile() {
        try {
            File dir = new File("logs");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(CSV_FILE);
            if (!file.exists()) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("Data si Ora,Operatie\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Eroare la crearea fisierului de log: " + e.getMessage());
        }
    }

    public static void logOperatie(String operatie) {
        try {
            File file = new File(CSV_FILE);
            if (!file.exists()) {
                createLogFile();
            }

            try (FileWriter writer = new FileWriter(file, true)) {
                String timestamp = LocalDateTime.now().format(formatter);
                String line = String.format("%s,%s\n", timestamp, operatie);
                writer.write(line);
                writer.flush();
            }
        } catch (IOException e) {
            System.out.println("Eroare la logarea operatiei: " + e.getMessage());
        }
    }
} 