package AppAccount;
import DB.DataBase;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AppAccountAdminService extends AppAccountService {

    private static AppAccountAdminService instance = null;
  
    public static AppAccountAdminService getInstance() {
        if (instance == null) {
            instance = new AppAccountAdminService();
        }
        return instance;
    }

    @Override
    public AppAccount readAppAccount(){
        String email, username, password, role;
        System.out.println("Introduceti email-ul:");
        email = SCANNER.nextLine();
        System.out.println("Introduceti username-ul:");
        username = SCANNER.nextLine();
        System.out.println("Introduceti parola:");
        password = SCANNER.nextLine();
        System.out.println("Introduceti rolul:");
        role = SCANNER.nextLine();

        return new AppAccount(email, username, password, role);
    }

    // Operatia de insert/create
    @Override
    public void register(AppAccount user) throws Exception {
        int id = getId(user);
        if (id != -1) {
            throw new Exception("Utilizatorul deja exista.");
        }
        String query = String.format(
            "INSERT INTO users (email, username, password, role) VALUES ('%s', '%s', '%s', '%s');",
            user.getEmail(), user.getUsername(), user.getPassword(), user.getRole()
        );
        db.executeQuery(query);
    }

    public void delete(AppAccount user) throws Exception{
        String query = String.format("DELETE FROM users WHERE id = '%s';", getId(user));
        db.executeQuery(query);
    }

    
    public void displayAllUsers() throws SQLException {
        String query = "SELECT * FROM users;";
        List<Map<String, Object>> result = db.executeQuery(query);
        
        if (result.isEmpty()) {
            System.out.println("Nu exista utilizatori in baza de date.");
            return;
        }
        
        for (Map<String, Object> row : result) {
            System.out.println("ID: " + row.get("id") + ", Email: " + row.get("email") +
                               ", Username: " + row.get("username") + ", Role: " + row.get("role"));
        }
    }

    public AppAccount getUserAfterId(int id) throws SQLException {
        String query = "SELECT * FROM users WHERE id = " + id + ";";
        List<Map<String, Object>> result = db.executeQuery(query);
        
        if (result.isEmpty()) {
            System.out.println("Nu exista utilizatori cu acest ID in baza de date.");
            return null;
        }
        
        Map<String, Object> row = result.get(0);
        return new AppAccount(
            row.get("email").toString(),
            row.get("username").toString(),
            row.get("password").toString()
        );
    }

    
}
