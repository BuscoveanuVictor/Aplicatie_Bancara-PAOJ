package AppAccount;

import DB.DataBase;

import java.util.List;
import java.util.Map;


public class AppAccountUtils {

    private final static DataBase db = DataBase.getInstance();

    public static Integer getId(AppAccount appAccount) throws Exception {

        String str = "SELECT id FROM users WHERE email = '%s' AND username = '%s' AND password = '%s';";
        String query = String.format(str, appAccount.getEmail(),appAccount.getUsername(),appAccount.getPassword());

        List<Map<String, Object>> result = db.executeQuery(query);

        return result.isEmpty() ? null : (Integer)result.getFirst().get("id");
    }

    public static void login(AppAccount user) throws Exception{
        Integer id = getId(user);
        if(id == null) throw new Exception("Contul nu exista!");
    
        String query = String.format("UPDATE users SET login = 'true' WHERE id = '%s';", id.toString());
        db.executeQuery(query);
    }

    public static void register(AppAccount user) throws Exception {

        Integer id = getId(user);
        if(id != null) throw new Exception("Exista deja un cont cu datele introduse!");

        String query = String.format(
            "INSERT INTO users (email, username, password, login) VALUES ('%s', '%s', '%s', true);",
                    user.getEmail(), user.getUsername(), user.getPassword()
                );
        db.executeQuery(query);

    }

}
