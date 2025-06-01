package AppAccount;

import DB.DataBase;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AppAccountService {

    private final DataBase db = DataBase.getInstance();
    static AppAccountService instance;

    private AppAccountService() { }

    public static AppAccountService getInstance() {
        if (instance == null) {
            instance = new AppAccountService();
        }
        return instance;
    }

    public int getId(AppAccount user) throws SQLException{
        String query = String.format
            (
                """
                    SELECT id FROM users 
                    WHERE email = '%s' AND username = '%s' AND password = '%s';
                """,
                user.getEmail(), user.getUsername(), user.getPassword()
            );
        List<Map<String, Object>> result = db.executeQuery(query);
        
        return result.isEmpty() ? -1 :  Integer.parseInt(result.get(0).get("id").toString());
    }

    public void login(AppAccount user) throws Exception{
        Integer id = getId(user);
        String query = String.format("UPDATE users SET login = 'true' WHERE id = '%s';", id.toString());
        db.executeQuery(query);
    }

    public void register(AppAccount user) throws Exception {
        Integer id = getId(user);

        String query = String.format
            (
        "INSERT INTO users (id, email, username, password, login) VALUES ('%d','%s', '%s', '%s', true);",
                id,user.getEmail(), user.getUsername(), user.getPassword()
            );      
        db.executeQuery(query);
    }

    public void delete(AppAccount user) throws Exception{
        String query = String.format("DELETE FROM users WHERE id = '%s';", getId(user));
        db.executeQuery(query);
    }

}
