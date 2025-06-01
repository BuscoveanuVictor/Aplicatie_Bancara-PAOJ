package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class DataBase implements IDataBase {

    // private static final Logger logger = Logger.getLogger(PgDataBase.class.getName());

    private static DataBase instance;
    private Connection connection;

    private final String URL = "jdbc:postgresql://localhost:5432/postgres";
    
    // pt Docker 
    //private final String URL = "jdbc:postgresql://db:5432/postgres";

    private final String PASSWORD = "mugly11";
    private final String USERNAME = "postgres";


    static {
        instance =  getInstance();

        String query = 
        """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                email VARCHAR(255) NOT NULL UNIQUE,
                username VARCHAR(100) NOT NULL UNIQUE,
                password VARCHAR(255) NOT NULL,
                login BOOLEAN DEFAULT FALSE,
                name VARCHAR(255),
                register_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                admin BOOLEAN DEFAULT FALSE
            );

            CREATE TABLE IF NOT EXISTS cont_personal (
                id SERIAL PRIMARY KEY,
                nume_titular VARCHAR(255) NOT NULL,
                iban VARCHAR(34) NOT NULL UNIQUE,
                moneda VARCHAR(10) NOT NULL,
                sold NUMERIC(15,2) NOT NULL DEFAULT 0,
                data_deschidere DATE NOT NULL,
                activ BOOLEAN DEFAULT TRUE,
                app_account_id INTEGER NOT NULL,
                cnp VARCHAR(13) NOT NULL UNIQUE,

                CONSTRAINT fk_app_account
                    FOREIGN KEY (app_account_id)
                    REFERENCES users(id)
                    ON DELETE CASCADE
            );
        """;

        try {
            instance.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private DataBase() {
        try {
            connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    };

    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    @Override
    public void connect() throws SQLException {
        if (connection == null)connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    @Override
    public List<Map<String, Object>> executeQuery(String query) throws SQLException {
        connect();

        Statement statement = connection.createStatement(); 
        boolean hasResultSet = statement.execute(query);

        disconnect();

        // Operatii asupra rezultatului
        if (hasResultSet) {
            ResultSet rs = statement.getResultSet(); // efectiv rezultatul (liniile)    
            ResultSetMetaData meta = rs.getMetaData(); // ce coloane sunt în ResultSet  
            int columnCount = meta.getColumnCount(); // nr de coloane                   

            List<Map<String, Object>> rows = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = meta.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                rows.add(row);
            }
            return rows;
        }
        else {
            int updateCount = statement.getUpdateCount();
            //System.out.println("Rows affected: " + updateCount);
            return new ArrayList<>();
        }
    }
}
