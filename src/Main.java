import AppAccount.AppAccount;
import AppAccount.AppAccountService;
import BankAccount.BankAccount;
import BankAccount.BankAccountService;
import DB.CSVLogger;
import UserBankAccount.Company;
import UserBankAccount.Individual;
import UserBankAccount.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner SCANNER = new Scanner(System.in);
    static List<BankAccount> bankAccounts = new ArrayList<>();
    static AppAccountService appAccountService = AppAccountService.getInstance();
    static BankAccountService bankAccountService = BankAccountService.getInstance();


    private enum SERVICES {
        INREGISTRARE            (-1, "Inregistrare cont aplicatie"),
        AUTENTIFICARE           (-2, "Autentificare cont aplicatie"),
        EXIT                    (0, "Iesire..."),

        AFISEAZA_BALANTA        (1, "Afiseaza balanta cont"),
        RETRAGERE_BANI          (2, "Retragere bani"),
        DEPUNERE_BANI           (3, "Depunere bani"),
        AFISEAZA_CONTURI        (4, "Afiseaza conturi bancare"),
        AFISEAZA_DETALII_CONT   (5, "Afiseaza detalii cont"),
        CREAZA_CONT_BANCAR      (6, "Creaza-ti cont bancar"),
        AFISEAZA_TRANZACTII     (7, "Afiseaza tranzactii"),
        TRANSFERA_BANI          (9, "Transfera bani"),
        BLOCHEAZA_CONT          (8, "Blocheaza cont"),
        STERGE_CONT             (10, "Sterge cont bancar");


        private final int code;
        private final String banner;
    
        SERVICES(int code, String banner) {
            this.code = code;
            this.banner = banner;
        }
    
        public static SERVICES getService(int code) {
            for (SERVICES service : SERVICES.values()) {
                if (service.code == code) {
                    return service;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return banner;
        }
        
    }

    static AppAccount entryPointApp(){

        String banner =
                """ 
                    1. Inregistrare cont aplicatie 
                    2. Autentificare cont aplicatie 
                    0. Iesire 
                """;

        AppAccount appAccount = null;

        int optiune;
        do {
            System.out.println(banner);
            optiune = Integer.parseInt(SCANNER.nextLine());

            SERVICES service = SERVICES.getService(-optiune);
            if(service == SERVICES.EXIT){
                System.out.println(SERVICES.EXIT);
                System.exit(0);
            }
            else {
                try {
                    appAccount = auth(service);
                }catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }while(appAccount == null);

        return appAccount;
    }

    public static AppAccount auth(SERVICES service) throws Exception {

        System.out.println(service);
        AppAccount appAccount = appAccountService.readAppAccount();
        
        switch (service) {    
            case INREGISTRARE -> {
                appAccountService.register(appAccount);
                System.out.println("Inregistrare reusita");
                System.out.println("Bine ai venit, " + appAccount.getUsername() + "!");
                return appAccount;
            }
            case AUTENTIFICARE -> {
                appAccountService.login(appAccount);
                System.out.println("Logare reusita");
                System.out.println("Bine ai revenit, " + appAccount.getUsername() + "!");
                return appAccount;
            }
        }

        return null;
    }

    public static void serviciiConturiBancare(AppAccount appAccount)
            throws Exception {

        String banner =
        """ 
            1. Afiseaza balanta cont
            2. Retragere bani
            3. Depunere bani
            4. Afiseaza conturi bancar
            5. Afiseaza detalii cont
            6. Creaza-ti cont bancar
            7. Afiseaza tranzactii
            8. Blocheaza cont
            9. Transfera bani
            10. Sterge cont
            -1. Iesire
        """;


        if(bankAccounts.isEmpty()){
            System.out.println("Nu aveti conturi bancare!");
            bankAccountService.createNewBankAccount(appAccount);
            CSVLogger.logOperatie("Creare cont bancar nou");
        }


        int optiune;
        BankAccount bankAccount;
        do{
            System.out.println(banner);
            optiune = SCANNER.nextInt();
            SERVICES service = SERVICES.getService(optiune);

            switch (service) {
                case RETRAGERE_BANI -> {
                    System.out.println(SERVICES.RETRAGERE_BANI);
                    System.out.println("Introduceti suma de bani pe care doriti sa o retrageti: ");
                    double suma = SCANNER.nextDouble();

                    bankAccount = bankAccountService.chooseAccount(appAccount);
                    bankAccountService.withdraw(bankAccount, suma);
                    CSVLogger.logOperatie("Retragere suma " + suma + " RON");
                    System.out.println("Retragere reusita!");
                    System.out.println("Noua balanta : " + bankAccount.getBalanta());
                }
                case DEPUNERE_BANI -> {
                    System.out.println(SERVICES.DEPUNERE_BANI);
                    System.out.println("Introduceti suma de bani pe care doriti sa o depuneti: ");
                    double suma = SCANNER.nextDouble();

                    bankAccount = bankAccountService.chooseAccount(appAccount);
                    bankAccountService.deposit(bankAccount, suma);
                    CSVLogger.logOperatie("Depunere suma " + suma + " RON");
                    System.out.println("Noua balanta : " + bankAccount.getBalanta());
                    System.out.println(bankAccount);
                }

                case AFISEAZA_BALANTA -> {
                    System.out.println(SERVICES.AFISEAZA_BALANTA);
                    bankAccount = bankAccountService.chooseAccount(appAccount);
                    System.out.println("Balanta contului tau este: " + bankAccount.getBalanta());
                    CSVLogger.logOperatie("Afisare balanta cont");
                }

                case AFISEAZA_DETALII_CONT -> {
                    System.out.println(SERVICES.AFISEAZA_DETALII_CONT);
                    bankAccount = bankAccountService.chooseAccount(appAccount);
                    System.out.println(bankAccount);
                    CSVLogger.logOperatie("Afisare detalii cont");
                }

                case AFISEAZA_CONTURI -> {
                    System.out.println(SERVICES.AFISEAZA_CONTURI);
                    bankAccountService.getAllAccounts(appAccount);
                    CSVLogger.logOperatie("Afisare lista conturi");
                }

                case CREAZA_CONT_BANCAR -> {
                    System.out.println(SERVICES.CREAZA_CONT_BANCAR);
                    bankAccountService.createNewBankAccount(appAccount);
                    CSVLogger.logOperatie("Creare cont bancar nou");
                }

                case BLOCHEAZA_CONT -> {
                    System.out.println(SERVICES.BLOCHEAZA_CONT);
                    bankAccount = bankAccountService.chooseAccount(appAccount);
                    bankAccount.activate(false);
                    CSVLogger.logOperatie("Blocare cont bancar");
                }

                case STERGE_CONT -> {
                    System.out.println(SERVICES.STERGE_CONT);
                    bankAccount = bankAccountService.chooseAccount(appAccount);
                    bankAccountService.delete(bankAccount, bankAccounts);
                    CSVLogger.logOperatie("Stergere cont bancar");
                }
            }
        }while(optiune != -1);
    }

    public static void main(String[] args) {

        System.out.println(
        """
            Bine ai venit in aplicatia bancara!
            Pentru accesarea aplicatiei alegeti din urmatoarele optiuni:
        """
        );
    
        AppAccount appAccount = entryPointApp();
        try {
            bankAccounts = bankAccountService.getAllAccounts(appAccount);
        }
        catch (Exception e) {
            System.out.println("Eroare la obtinerea conturilor bancare");
            System.out.println(e.getMessage());
            bankAccounts = new ArrayList<>();
        }  

        try {
            serviciiConturiBancare(appAccount);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
