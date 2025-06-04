package AppAccount;

import DB.DataBase;
import UserBankAccount.Company;
import UserBankAccount.Individual;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AppAccountService {

    protected  DataBase db = DataBase.getInstance();
    static AppAccountService instance;
    static Scanner SCANNER = new Scanner(System.in);

    public static AppAccountService getInstance() {
        if (instance == null) {
            instance = new AppAccountService();
        }
        return instance;
    }

    public AppAccount readAppAccount(){
        String email, username, password;
        System.out.println("Introduceti email-ul:");
        email = SCANNER.nextLine();
        System.out.println("Introduceti username-ul:");
        username = SCANNER.nextLine();
        System.out.println("Introduceti parola:");
        password = SCANNER.nextLine();

        return new AppAccount(email, username, password);
    }

    private static Individual citesteIndividualAccount(AppAccount appAccount) {

        SCANNER.nextLine();

        System.out.println("Introduceti numele complet: ");
        String nume = SCANNER.nextLine();

        System.out.println("Introduceti cnp-ul: ");
        String cnp = SCANNER.nextLine();

        return new Individual(appAccount, nume, cnp);
    }


    private static Company citesteCompanyAccount(AppAccount appAccount) {
        System.out.println("Introduceti numele firmei: ");
        SCANNER.next();
        String numeFirma = SCANNER.nextLine();

        System.out.println("Introduceti codul unic de inregistrare: ");
        SCANNER.next();
        String CUI = SCANNER.nextLine();

        System.out.println("Introduceti numarInregistrare: ");
        SCANNER.next();
        String numarInregistrare = SCANNER.nextLine();

        return new Company(appAccount, numeFirma, CUI, numarInregistrare);
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
        if(id == -1) {
            throw new Exception("Utilizatorul nu exista sau datele sunt incorecte.");
        }
        String query = String.format("UPDATE users SET login = 'true' WHERE id = '%s';", id.toString());
        db.executeQuery(query);
    }

    public void register(AppAccount user) throws Exception {
        Integer id = getId(user);

        if (id != -1) {
            throw new Exception("Utilizatorul deja exista.");
        }
        String query = String.format
            (
        "INSERT INTO users (id, email, username, password, login) VALUES ('%d','%s', '%s', '%s', true);",
                id,user.getEmail(), user.getUsername(), user.getPassword(), user.getRole()
            );      
        db.executeQuery(query);
    }



 

}
