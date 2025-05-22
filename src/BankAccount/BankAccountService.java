package BankAccount;
import AppAccount.AppAccount;
import AppAccount.AppAccountService;
import DB.DataBase;
import UserBankAccount.Company;
import UserBankAccount.Individual;
import UserBankAccount.User;
import java.util.*;

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

    public BankAccount createNewBankAccount (AppAccount appAccount, User userAccount) throws Exception {
        BankAccount bankAccount = new BankAccount(userAccount);      
        //System.out.println(bankAccount);
        saveBankAccount(appAccount,bankAccount);
        System.out.println("Cont bancar creat cu succes!");
        System.out.println("Contul tau bancar este: " + bankAccount.getIban());       
        
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
                            appAccount.getId() 
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
                            appAccount.getId()
                        );
            }
            default -> throw new Exception("Eroare la crearea contului: " + bankAccount.getUser());
        }
        
        //System.out.println("Query-ul este: " + query);

        db.executeQuery(query);
    }

    public List<BankAccount> getAllAccounts(AppAccount appAccount) throws Exception {
        // ATENTIE : object = String (datele intoarse de la DB sunt de tip String)
        // Map <String,Obj> =  nume coloana si valoare
        List<Map<String, Object>> result =
        db.executeQuery(
                String.format(
                        """     
                            SELECT * FROM cont_personal
                            WHERE app_account_id=%d
                        """,
                        appAccount.getId()
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

    private void saveChanges(BankAccount bankAccount) throws Exception {
        DataBase db = DataBase.getInstance();
        db.executeQuery(
            String.format(
            """
                UPDATE cont_personal 
                SET 
                    sold=%f, 
                    activ=%s 
                WHERE 
                    app_)account_id=%d
                """,
                bankAccount.getBalanta(),
                bankAccount.isActiv(),
                bankAccount.getId()
        )
        );
    }

    public void showBalance(BankAccount bankAccount) {   
        System.out.println("Balanta contului este de : " + bankAccount.getBalanta() + " " + bankAccount.getMoneda());
    }

    public void deposit(BankAccount bankAccount, double suma) throws Exception {
        bankAccount.setBalanta(bankAccount.getBalanta() + suma);
        saveChanges(bankAccount);
    }

    public void withdraw(BankAccount bankAccount, double suma) throws Exception {
        bankAccount.setBalanta(bankAccount.getBalanta() - suma);
        saveChanges(bankAccount);
    }

    public void transfer(BankAccount from, BankAccount to, double suma) throws Exception {
        withdraw(from, suma);
        deposit(to, suma);
        saveChanges(from);
        saveChanges(to);
    }

}
