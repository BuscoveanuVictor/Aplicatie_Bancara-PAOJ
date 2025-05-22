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

public class DataBase implements IDataBase {

    // private static final Logger logger = Logger.getLogger(PgDataBase.class.getName());

    private static DataBase instance;
    private Connection connection = null;

    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    @Override
    public void connect() throws SQLException {

        String env = System.getenv("DB_ENV");

        String hostname = "localhost";
        String port = "5432";
        String username = "postgres";
        String password = "mugly11";
        String database = "postgres";

        if(env != null){
            hostname = System.getenv("DB_HOST");
            database = System.getenv("DB_DATABASE");
            port = System.getenv("DB_PORT");
            username = System.getenv("DB_USER");
            password = System.getenv("DB_PASSWORD");
        }

        String URL = String.format("jdbc:postgresql://%s:%s/%s", hostname, port, database);

        if (connection == null)connection = DriverManager.getConnection(URL, username, password);
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
