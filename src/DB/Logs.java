package DB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Logs extends Logger{

    private static Logs instance;
    private ArrayList<FileHandler> fh;

    private void createFileHandler(String name) throws IOException {
        fh.add(new FileHandler(name));
    }

    public Logs(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    public static Logs getLogger(String name){
        if (instance == null) {
            instance = new Logs(name, null);
        }
        return instance;
    }

    public void info(String message) {
        try {
            createFileHandler("info.txt");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
