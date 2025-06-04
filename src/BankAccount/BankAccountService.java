package BankAccount;
import AppAccount.AppAccount;
import AppAccount.AppAccountService;
import DB.DataBase;
import UserBankAccount.Company;
import UserBankAccount.Individual;
import UserBankAccount.User;
import UserBankAccount.UserService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class BankAccountService {

    private static final Scanner SCANNER = new Scanner(System.in);

    private final AppAccountService appAccountService = AppAccountService.getInstance();
    private final UserService userService = new UserService();

    private final DataBase db = DataBase.getInstance();

    private static BankAccountService instance = null;

    private BankAccountService() { }

    public static BankAccountService getInstance() {
       if (instance == null) {
           instance = new BankAccountService();
       }
        return instance;
    }

    public void displayBankAccounts(AppAccount appAccount){
        List<BankAccount> bankAccounts = getAllAccounts(appAccount);
        Collections.sort(bankAccounts);
        
        for(int i = 1; i <= bankAccounts.size(); i++){
            BankAccount bankAccount =  bankAccounts.get(i-1);
            System.out.println(
                """
                    %d. IBAN: %s 
                    Titular: %s 
                    Tip: %s
                    Data deschidere: %s
                """.formatted(
                    i, 
                    bankAccount.getIban(),
                    (bankAccount.getUser() instanceof Individual individual) ? 
                    individual.getNume() : ((Company) bankAccount.getUser()).getDenumire(),
                    bankAccount.getClass().getSimpleName(),
                    bankAccount.getDataDeschidere()
                )
            );
        }
    }

    public BankAccount chooseAccount(AppAccount appAccount) {

        displayBankAccounts(appAccount);

        int index;
        List<BankAccount> bankAccounts = getAllAccounts(appAccount);
        System.out.println("Alege contul:");
        do{
            index = SCANNER.nextInt();
            if(index<1 || index>bankAccounts.size()){
                System.out.println("Cont bancar invalid");
                index = -1;
            }
        }while(index == -1);

        // Intrucat lista de conturi bancare incepe de la 0
        index--;
        BankAccount bankAccount = bankAccounts.get(index);
        return bankAccount;
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


    public BankAccount createNewBankAccount (AppAccount appAccount) {
        System.out.println(
            """
                Alegeti tipul de beneficiar al contului bancar:
                1. Cont pe persoana fizica
                2. Cont pe firma
                0. Anulare
            """
        );

        int optiune;
        User userAccount = null;
        do {
            optiune = SCANNER.nextInt();
            switch (optiune) {
                case 1 -> {
                    userAccount = userService.createIndividualUser(appAccount);
                }
                case 2 -> {
                    userAccount = userService.createCompanyUser(appAccount);
                }
                default -> {
                    System.out.println("Optiune invalida. Alegeti 1 sau 2.");
                }
            }
        } while (userAccount == null);

        System.out.println(
            """
                Alegeti tipul contului bancar:
                1. Cont de debit
                2. Cont de depozit
                0. Anulare
            """
        );

        BankAccount bankAccount = null;
        do {
            optiune = SCANNER.nextInt();
            switch (optiune) {
                case 1 -> {
                    bankAccount = new DebitAccount(userAccount);
                }
                case 2 -> {
                    bankAccount = new DepositAccount(userAccount);
                }
                default -> {
                    System.out.println("Optiune invalida. Alegeti 1 sau 2.");
                }
            }
        } while (bankAccount == null);

     

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

        String query = null;
        User user = bankAccount.getUser();
        System.out.println(user);
        if (user == null) {
            throw new Exception("Utilizatorul nu este valid.");
        }

        if (user instanceof Individual individual && bankAccount instanceof DepositAccount) {
            query = String.format(
                """
                    INSERT INTO cont_personal_depozit (
                        nume_titular, 
                        cnp, 
                        iban, 
                        moneda, 
                        sold, 
                        data_deschidere, 
                        activ, 
                        app_account_id,
                        dobanda_anuala,
                        perioada_luni,
                        suma_depusa
                    ) 
                    VALUES ('%s', '%s', '%s', '%s', %f, '%s', %s, %d, %f, %d, %f)
                """,
                individual.getNume(),
                individual.getCnp(),
                bankAccount.getIban(),
                bankAccount.getMoneda(),
                bankAccount.getBalanta(),
                bankAccount.getDataDeschidere(),
                bankAccount.isActiv(),
                appAccountService.getId(appAccount),
                ((DepositAccount)bankAccount).getDobandaAnuala(),
                ((DepositAccount)bankAccount).getPerioadaLuni(),
                ((DepositAccount)bankAccount).getSumaDepusa()
            );
        } 

        if (user instanceof Individual individual && bankAccount instanceof DebitAccount) {
            query = String.format(
                """
                    INSERT INTO cont_personal_debit (
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

        if (user instanceof Company company && bankAccount instanceof DepositAccount) {
            query = String.format(
                """
                    INSERT INTO cont_firma_depozit (
                        denumire_firma, 
                        CUI,
                        numar_inregistrare,
                        iban, 
                        moneda, 
                        sold, 
                        data_deschidere, 
                        activ, 
                        app_account_id,
                        dobanda_anuala,
                        perioada_luni,
                        suma_depusa
                    ) 
                    VALUES ('%s', '%s', '%s', '%s', '%s', %f, '%s', %s, %d, %f, %d, %f)
                """,
                company.getDenumire(),
                company.getCUI(),
                company.getNumarInregistrare(),
                bankAccount.getIban(),
                bankAccount.getMoneda(),
                bankAccount.getBalanta(),
                bankAccount.getDataDeschidere(),
                bankAccount.isActiv(),
                appAccountService.getId(appAccount),
                ((DepositAccount)bankAccount).getDobandaAnuala(),
                ((DepositAccount)bankAccount).getPerioadaLuni(),
                ((DepositAccount)bankAccount).getSumaDepusa()
            );
        }

        if( user instanceof Company company && bankAccount instanceof DebitAccount) {
            query = String.format(
                """
                    INSERT INTO cont_firma_debit (
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
                    VALUES ('%s', '%s', '%s', '%s', '%s', %f, '%s', %s, %d)
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

        try {
            db.executeQuery(query);
            System.out.println("Contul bancar a fost salvat cu succes!");
        } catch (SQLException e) {
            throw new Exception("Eroare la salvarea contului bancar: " + e.getMessage());
        }
    }

    public List<BankAccount> getAllAccounts(AppAccount appAccount) {
        // ATENTIE : object = String (datele intoarse de la DB sunt de tip String)
        // Map <String,Obj> =  nume coloana si valoare
        List<List<Map<String, Object>>> result = new ArrayList<>();
        try {
            result.add(db.executeQuery(
                    String.format(
                            """
                                SELECT * FROM cont_personal_debit
                                WHERE app_account_id = %d;
                            """,
                            appAccountService.getId(appAccount)
                    )
                )
            );
            result.add(db.executeQuery(
                    String.format(
                            """
                                SELECT * FROM cont_personal_depozit
                                WHERE app_account_id = %d;
                            """,
                            appAccountService.getId(appAccount)
                    )
                )
            );
            result.add(db.executeQuery(
                    String.format(
                            """
                                SELECT * FROM cont_firma_debit
                                WHERE app_account_id = %d;
                            """,
                            appAccountService.getId(appAccount)
                    )
                )
            );
            result.add(db.executeQuery(
                    String.format(
                            """
                                SELECT * FROM cont_firma_depozit
                                WHERE app_account_id = %d;
                            """,
                            appAccountService.getId(appAccount)
                    )
                )
            );

        } catch (SQLException e) {
            System.out.println("Eroare la obtinerea conturilor bancare: " + e.getMessage());
            
        }

        List<BankAccount> bankAccounts = new ArrayList<>(); // Lista de conturi bancare
        for(int i = 0; i < result.size(); i++) {
            List<Map<String, Object>> list = result.get(i);
            if(list.isEmpty()) {
                continue; // Daca lista este goala, trecem la urmatoarea
            }
            
            if(i == 0) {
                // Conturi personale de debit
               bankAccounts.add( new DebitAccount(
                        new Individual
                        (
                            appAccount,
                            list.get(0).get("nume_titular").toString(),
                            list.get(0).get("cnp").toString()
                        ),
                        list.get(0).get("iban").toString(),
                        list.get(0).get("moneda").toString(),
                        list.get(0).get("data_deschidere").toString(),
                        Double.parseDouble(list.get(0).get("sold").toString()),
                        Boolean.parseBoolean(list.get(0).get("activ").toString())
                    )
                );
            } else if (i == 1) {
                bankAccounts.add( new DepositAccount(
                        new Individual
                        (
                            appAccount,
                            list.get(0).get("nume_titular").toString(),
                            list.get(0).get("cnp").toString()
                        ),
                        list.get(0).get("iban").toString(),
                        list.get(0).get("moneda").toString(),
                        list.get(0).get("data_deschidere").toString(),
                        Double.parseDouble(list.get(0).get("sold").toString()),
                        Boolean.parseBoolean(list.get(0).get("activ").toString()),
                        Double.parseDouble(list.get(0).get("dobanda_anuala").toString()),
                        Integer.parseInt(list.get(0).get("perioada_luni").toString()),
                        Double.parseDouble(list.get(0).get("suma_depusa").toString())
                    )
                );
            } else if (i == 2) {
                // Conturi de debit pe firma
                bankAccounts.add( new DebitAccount(
                        new Company
                        (
                            appAccount,
                            list.get(0).get("denumire_firma").toString(),
                            list.get(0).get("CUI").toString(),
                            list.get(0).get("numar_inregistrare").toString()
                        ),
                        list.get(0).get("iban").toString(),
                        list.get(0).get("moneda").toString(),
                        list.get(0).get("data_deschidere").toString(),
                        Double.parseDouble(list.get(0).get("sold").toString()),
                        Boolean.parseBoolean(list.get(0).get("activ").toString())
                    )
                );

            } else if (i == 3) {
                // Conturi de depozit pe firma
                bankAccounts.add( new DepositAccount(
                        new Company
                        (
                            appAccount,
                            list.get(0).get("denumire_firma").toString(),
                            list.get(0).get("CUI").toString(),
                            list.get(0).get("numar_inregistrare").toString()
                        ),
                        list.get(0).get("iban").toString(),
                        list.get(0).get("moneda").toString(),
                        list.get(0).get("data_deschidere").toString(),
                        Double.parseDouble(list.get(0).get("sold").toString()),
                        Boolean.parseBoolean(list.get(0).get("activ").toString()),
                        Double.parseDouble(list.get(0).get("dobanda_anuala").toString()),
                        Integer.parseInt(list.get(0).get("perioada_luni").toString()),
                        Double.parseDouble(list.get(0).get("suma_depusa").toString())
                    )
                );
            }
            System.out.println("Conturi bancare gasite: " + list.size());
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
