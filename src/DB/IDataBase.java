package DB;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public interface IDataBase {
    public void connect() throws SQLException;
    public void disconnect() throws SQLException;
    public boolean isConnected();
    public List<Map<String, Object>> executeQuery(String query) throws SQLException;
}
