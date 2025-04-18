package BankAccount;
import AppAccount.AppAccount;
import AppAccount.AppAccountUtils;

import java.util.*;

import DB.DataBase;
import UserBankAccount.Company;
import UserBankAccount.Individual;
import UserBankAccount.User;
import java.math.RoundingMode;

public class BankAccountUtils {

    private static final Scanner SCANNER = new Scanner(System.in);

    private static Individual citesteIndividualAccount(AppAccount appAccount) {
        System.out.println("Introduceti numele complet: ");
        String nume = SCANNER.next();
        System.out.println("Introduceti cnp-ul: ");
        String cnp = SCANNER.next();

        return new Individual(appAccount, nume, cnp);
    }

    private static Company citesteCompanyAccount(AppAccount appAccount) {
        System.out.println("Introduceti numele firmei: ");
        String numeFirma = SCANNER.next();
        System.out.println("Introduceti codul unic de inregistrare: ");
        String CUI = SCANNER.next();
        System.out.println("Introduceti numarInregistrare: ");
        String numarInregistrare = SCANNER.next();

        return new Company(appAccount, numeFirma, CUI, numarInregistrare);
    }

   

    public static BankAccount createNewBankAccount(AppAccount appAccount) {

        System.out.println("Alegeti tipul contului: ");
        System.out.println("1. Cont pe persoana fizica");
        System.out.println("2. Cont pe firma");
        System.out.println("0. Anulare");

        int optiune;
        User userAccount = null;
        do { 
            optiune = SCANNER.nextInt();
            switch (optiune) {
                case 1 -> {
                    userAccount = citesteIndividualAccount(appAccount); 
                    optiune = 0;
                }
                case 2 -> {
                    userAccount = citesteCompanyAccount(appAccount); 
                    optiune = 0;
                }
                default -> {
                    System.out.println("Optiune invalida. Alegeti 1 sau 2.");
                }
            }
        } while (optiune != 0);

        BankAccount bankAccount = new BankAccount(userAccount);

        try {
            saveBankAccount(bankAccount);
            System.out.println("Contul a fost creat cu succes!");
        } catch (Exception e) {
            System.out.println("Eroare la crearea contului: " + e.getMessage());
        }
        
        return bankAccount;
    }


    private static void saveBankAccount(BankAccount bankAccount) throws Exception{
        DataBase db = DataBase.getInstance();

        String query;
        switch (bankAccount.getUser()) {
            case Individual individual -> {
                query = String.format(
                        "INSERT INTO cont_personal " +
                        "(nume_titular, cnp, iban, moneda, sold, data_deschidere, activ) " +
                        "VALUES (%s, %s, %s, %s, %s, %s, %s)",
                        individual.getNume(),individual.getCnp(), bankAccount.getIban(), bankAccount.getMoneda(),
                        bankAccount.getBalanta(), bankAccount.getDataDeschidere(), bankAccount.isActiv());
            }
            case Company company -> {
                query = String.format(
                        "INSERT INTO cont_firma " +
                        "(denumire_firma, CUI, numar_inregistrare, iban, moneda, sold, data_deschidere, activ) " +
                        "VALUES (%s, %s, %s, %s, %s, %s, %s, %s)",
                        company.getDenumire(), company.getCUI(), company.getNumarInregistrare(), bankAccount.getIban(),
                        bankAccount.getMoneda(), bankAccount.getBalanta(), bankAccount.getDataDeschidere(),
                        bankAccount.isActiv()
                        );
            }
            default -> throw new Exception("Eroare la crearea contului: " + bankAccount.getUser());
        }
        
        db.executeQuery(query);
    }

    public static List<BankAccount> getAllAccounts(AppAccount appAccount) throws Exception {
        DataBase db = DataBase.getInstance();

        System.out.println(AppAccountUtils.getId(appAccount));
        String query = String.format("SELECT * FROM cont_personal WHERE app_account_id= %s",
                AppAccountUtils.getId(appAccount).toString());

        List<Map<String, Object>> result = db.executeQuery(query);

        // for (Map<String, Object> row : result){
        //     for(String key : row.keySet()){
        //         System.out.print(key.getClass());
        //     }
        // }
        
        List<BankAccount> bankAccounts = new ArrayList<>();
        for (Map<String, Object> row : result) {
            Individual individual = new Individual(appAccount, row.get("nume_titular").toString(), row.get("cnp").toString());
            bankAccounts.add(
                new BankAccount(
                        individual, row.get("iban").toString(), row.get("moneda").toString(),
                        row.get("data_deschidere").toString(),  Double.parseDouble(row.get("sold").toString()),
                        Boolean.parseBoolean(row.get("activ").toString())
                )
            );
        }

        return bankAccounts;
    }  

    public static void showBalance(BankAccount bankAccount) {   
        System.out.println("Balanta contului este de : " + bankAccount.getBalanta() + " " + bankAccount.getMoneda());
    }

//    public static void deposit(BankAccount bankAccount, double suma) {
//        bankAccount.setBalanta(bankAccount.getBalanta() + suma);
//    }

//    public static void withdraw(BankAccount bankAccount, double suma) {
//        bankAccount.setBalanta(bankAccount.getBalanta() - suma);
//    }

}
