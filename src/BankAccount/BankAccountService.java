package BankAccount;
import AppAccount.AppAccount;
import AppAccount.AppAccountService;
import DB.DataBase;
import UserBankAccount.Company;
import UserBankAccount.Individual;
import UserBankAccount.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class BankAccountService {

    private static final Scanner SCANNER = new Scanner(System.in);

    private final AppAccountService appAccountService = AppAccountService.getInstance();

    private final DataBase db = DataBase.getInstance();

    private static BankAccountService instance = null;

    private BankAccountService() { }

    public static BankAccountService getInstance() {
       if (instance == null) {
           instance = new BankAccountService();
       }
        return instance;
    }

    private int getId(BankAccount bankAccount) throws SQLException {
        String query = String.format(
                """
                    SELECT id FROM '%s'
                    WHERE iban = '%s';
                """,
                bankAccount.getTableName(),
                bankAccount.getIban()
        );
        List<Map<String, Object>> result = db.executeQuery(query);
        return result.isEmpty() ? -1 : Integer.parseInt(result.get(0).get("id").toString());
    }


    public BankAccount createNewBankAccount (AppAccount appAccount, User userAccount) {
        BankAccount bankAccount = new BankAccount(userAccount);      
        //System.out.println(bankAccount);
        try {
            saveBankAccount(appAccount,bankAccount);
            System.out.println("Cont bancar creat cu succes!");
            System.out.println("Contul tau bancar este: " + bankAccount.getIban());       
        } catch (Exception e) {
            System.out.println("Eroare la crearea contului bancar: " + e.getMessage());
            return null;
        }
        return bankAccount;
    }

    private void saveBankAccount(AppAccount appAccount,BankAccount bankAccount) throws Exception{

        String query;
        switch (bankAccount.getUser()) {
            case Individual individual -> {
                query = String.format(
                            """
                                INSERT INTO cont_personal (
                                    nume_titular, 
                                    cnp, 
                                    iban, 
                                    moneda, 
                                    sold, 
                                    data_deschidere, 
                                    activ, 
                                    app_account_id
                                   ) 
                                VALUES ('%s', '%s', '%s', '%s', %f, '%s', %s, %d)
                            """,
                            individual.getNume(),
                            individual.getCnp(),
                            bankAccount.getIban(),
                            bankAccount.getMoneda(),
                            bankAccount.getBalanta(),
                            bankAccount.getDataDeschidere(),
                            bankAccount.isActiv(),
                            appAccountService.getId(appAccount)
                        );
            }
            case Company company -> {
                query = String.format(
                            """
                                INSERT INTO cont_firma(
                                    denumire_firma, 
                                    CUI, 
                                    numar_inregistrare,
                                    iban, 
                                    moneda, 
                                    sold, 
                                    data_deschidere, 
                                    activ, 
                                    app_account_id
                                ) 
                                VALUES (%s, %s, %s, %s, %s, %f, %s, %s, %d)
                            """,
                            company.getDenumire(),
                            company.getCUI(),
                            company.getNumarInregistrare(),
                            bankAccount.getIban(),
                            bankAccount.getMoneda(),
                            bankAccount.getBalanta(),
                            bankAccount.getDataDeschidere(),
                            bankAccount.isActiv(),
                            appAccountService.getId(appAccount)
                        );
            }
            default -> throw new Exception("Eroare la crearea contului: " + bankAccount.getUser());
        }
        db.executeQuery(query);
    }

    public List<BankAccount> getAllAccounts(AppAccount appAccount) throws SQLException {
        // ATENTIE : object = String (datele intoarse de la DB sunt de tip String)
        // Map <String,Obj> =  nume coloana si valoare
        List<Map<String, Object>> result =
        db.executeQuery(
                String.format(
                        """     
                            SELECT * FROM cont_personal, cont_depozit
                            WHERE app_account_id=%d
                        """,
                        appAccountService.getId(appAccount)
                )
        );

        List<BankAccount> bankAccounts = new ArrayList<>();
        for (Map<String, Object> row : result) {
            bankAccounts.add(
                new BankAccount(
                        new Individual
                        (
                            appAccount,
                            row.get("nume_titular").toString(),
                            row.get("cnp").toString()
                        ),
                        row.get("iban").toString(),
                        row.get("moneda").toString(),
                        row.get("data_deschidere").toString(),
                        Double.parseDouble(row.get("sold").toString()),
                        Boolean.parseBoolean(row.get("activ").toString())
                )
            );
        }

        return bankAccounts;
    }

    private void updateAccount(BankAccount bankAccount){
        try{
            db.executeQuery(
                String.format(
                """
                    UPDATE '%s' 
                    SET 
                        sold=%f, 
                        activ=%s 
                    WHERE 
                        id=%d
                    """,
                    bankAccount.getTableName(),
                    bankAccount.getBalanta(),
                    bankAccount.isActiv(),
                    getId(bankAccount)
                )
            );
        }catch (SQLException e) {
            System.out.println("Eroare la actualizarea contului: " + e.getMessage());
        }
    }

    private void deleteBankAccount(BankAccount bankAccount) throws SQLException {
        String query = String.format(
                """
                    DELETE FROM '%s' 
                    WHERE id = %d
                """,
                bankAccount.getTableName(),
                getId(bankAccount)
        );
        db.executeQuery(query);
    }


    public void showBalance(BankAccount bankAccount) {   
        System.out.println("Balanta contului este de : " + bankAccount.getBalanta() + " " + bankAccount.getMoneda());
    }

    public void deposit(BankAccount bankAccount, double suma){
        bankAccount.setBalanta(bankAccount.getBalanta() + suma);
        updateAccount(bankAccount);
    }

    public void withdraw(BankAccount bankAccount, double suma){
        bankAccount.setBalanta(bankAccount.getBalanta() - suma);
        updateAccount(bankAccount);
    }

    public void transfer(BankAccount from, BankAccount to, double suma){
        withdraw(from, suma);
        deposit(to, suma);
        updateAccount(from);
        updateAccount(to);
    }

    public void delete(BankAccount bankAccount, List<BankAccount> bankAccounts) throws Exception{
       
        System.out.println("Alegeti contul pe care sa transferati suma curenta:");

        // Eliminam contul curent din lista
        // pentru a nu-l afisa in lista de conturi
        bankAccounts.stream()
                .filter(account -> account.getIban().equals(bankAccount.getIban()))
                .findFirst()
                .ifPresent(bankAccounts::remove);

        Iterator<BankAccount> iterator = bankAccounts.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            BankAccount account = iterator.next();
            System.out.println(++i + ". " + account.getIban());
        }

        int index = SCANNER.nextInt() - 1;

        if (index < 0 || index >= bankAccounts.size()) {
            // Adaugam din nou contul curent in lista 
            bankAccounts.add(bankAccount);
            throw new Exception("Contul selectat nu este valid!");
        }

        BankAccount transferTo = bankAccounts.get(index);
        double suma = bankAccount.getBalanta();
        transfer(bankAccount, transferTo, suma);
        System.out.println("Suma a fost transferata cu succes!");
       
        try {
            deleteBankAccount(bankAccount);
            System.out.println("Contul a fost sters cu succes!");
        } catch (SQLException e) {
            System.out.println("Eroare la stergerea contului: " + e.getMessage());
        }
    }
}
