package AppAccount;

import DB.DataBase;


public class AppAccountService {

    private final DataBase db = DataBase.getInstance();

    static AppAccountService instance;
    public static AppAccountService getInstance() {
        if (instance == null) {
            instance = new AppAccountService();
        }
        return instance;
    }

    private AppAccountService() { }

    public void login(AppAccount user) throws Exception{
        Integer id = user.getId();
        String query = String.format("UPDATE users SET login = 'true' WHERE id = '%s';", id.toString());
        db.executeQuery(query);
    }

    public void register(AppAccount user) throws Exception {
        Integer id = user.getId();

        String query = String.format
            (
        "INSERT INTO AppAccounts (id, email, username, password,true) VALUES ('%d','%s', '%s', '%s', true);",
                id,user.getEmail(), user.getUsername(), user.getPassword()
            );      
        db.executeQuery(query);
    }

}
