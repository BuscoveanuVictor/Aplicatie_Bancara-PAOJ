import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


import BankAccount.BankAccount;
import AppAccount.AppAccount;
import AppAccount.AppAccountUtils;
import BankAccount.BankAccountUtils;
import UserBankAccount.Company;
import UserBankAccount.Individual;
import UserBankAccount.User;


public class Main {

    static Scanner SCANNER = new Scanner(System.in);
    static List<BankAccount> bankAccounts = new ArrayList<>();


    private enum SERVICES {
        INREGISTRARE(-1, "Inregistrare cont aplicatie"),
        AUTENTIFICARE(-2, "Autentificare cont aplicatie"),
        EXIT(0, "Iesire..."),

        AFISEAZA_BALANTA(1, "Afiseaza balanta cont"),
        RETRAGERE_BANI(2, "Retragere bani"),
        DEPUNERE_BANI(3, "Depunere bani"),
        AFISEAZA_CONTURI(4, "Afiseaza conturi bancare"),
        AFISEAZA_DETALII_CONT(5, "Afiseaza detalii cont"),
        CREAZA_CONT_BANCAR(6, "Creaza-ti cont bancar"),
        AFISEAZA_TRANZACTII(7, "Afiseaza tranzactii"),
        TRANSFERA_BANI(9, "Transfera bani"),
        BLOCHEAZA_CONT(8, "Blocheaza cont");


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

    public static AppAccount citireDateContAplicatie() {
        String email;
        String username;
        String password;

        System.out.println("Introduceti email-ul: ");
        email = SCANNER.next();
        System.out.println("Introduceti username-ul: ");
        username = SCANNER.next();
        System.out.println("Introduceti parola: ");
        password = SCANNER.next();
        return new AppAccount(email, username, password);
    }

    public static AppAccount enterApplication(SERVICES service) throws Exception {

        AppAccount appAccount;
        switch (service) {
            case SERVICES.INREGISTRARE -> {
                System.out.println(service);
                appAccount = citireDateContAplicatie();

                AppAccountUtils.register(appAccount);
                System.out.println("Inregistrare reusita");
                System.out.println("Bine ai venit, " + appAccount.getUsername() + "!");
                return appAccount;
            }
            case SERVICES.AUTENTIFICARE -> {
                System.out.println(service);
                appAccount = citireDateContAplicatie();

                AppAccountUtils.login(appAccount);
                System.out.println("Logare reusita");
                System.out.println("Bine ai revenit, " + appAccount.getUsername() + "!");
                return appAccount;
            }
        }

        return null;
    }


    public static Individual citesteIndividualAccount(AppAccount appAccount) {
        SCANNER.next();

        System.out.println("Introduceti numele complet: ");
        String nume = SCANNER.nextLine();

        System.out.println("Introduceti cnp-ul: ");
        String cnp = SCANNER.nextLine();

        return new Individual(appAccount, nume, cnp);
    }


    public static Company citesteCompanyAccount(AppAccount appAccount) {
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


    public static void creareaContBancar(AppAccount appAccount){
        System.out.println(
        """
            Alegeti tipul contului
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
                    userAccount = citesteIndividualAccount(appAccount);
                }
                case 2 -> {
                    userAccount = citesteCompanyAccount(appAccount);
                }
                default -> {
                    System.out.println("Optiune invalida. Alegeti 1 sau 2.");
                }
            }
        } while (userAccount == null);

        List<BankAccount>  bankAccounts =  new ArrayList<>();
        try {
            BankAccountUtils.createNewBankAccount(appAccount, userAccount);
            bankAccounts = BankAccountUtils.getAllAccounts(appAccount);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public List<BankAccount> getBankAccounts(AppAccount appAccount) {
        List<BankAccount> bankAccounts;
        try{
            bankAccounts = BankAccountUtils.getAllAccounts(appAccount);
        }catch (Exception e){
            bankAccounts = new ArrayList<>();
            System.out.println(e.getMessage());
        }
        return bankAccounts;
    }

    public static void displayBankAccounts(AppAccount appAccount) {
        for(int i = 1; i <= bankAccounts.size(); i++){
            System.out.println(i + "." + bankAccounts.get(i-1).getIban());
        }
    }

    private static BankAccount chooseAccount() {
        int index;
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
        """;


        if(bankAccounts.isEmpty()){
            System.out.println("Nu aveti conturi bancare!");
            creareaContBancar(appAccount);
            bankAccounts = BankAccountUtils.getAllAccounts(appAccount);
        }


        int optiune;
        do{
            System.out.println(banner);
            optiune = SCANNER.nextInt();
            SERVICES service = SERVICES.getService(optiune);

            BankAccount bankAccount=null;
            if(service != SERVICES.AFISEAZA_CONTURI) {
                displayBankAccounts(appAccount);
                bankAccount = chooseAccount();
            }

            switch (service) {
                case SERVICES.RETRAGERE_BANI -> {
                    System.out.println(SERVICES.RETRAGERE_BANI);
                    System.out.println("Introduceti suma de bani pe care doriti sa o retrageti: ");
                    double suma = SCANNER.nextDouble();

                    BankAccountUtils.withdraw(bankAccount, suma);
                    System.out.println("Retragere reusita!");
                    System.out.println("Noua balanta : " + bankAccount.getBalanta());

                }
                case SERVICES.DEPUNERE_BANI -> {
                    System.out.println(SERVICES.INREGISTRARE);
                    System.out.println("Introduceti suma de bani pe care doriti sa o depuneti: ");
                    double suma = SCANNER.nextDouble();

                    BankAccountUtils.deposit(bankAccount, suma);
                    System.out.println("Noua balanta : " + bankAccount.getBalanta());
                    System.out.println(bankAccount);
                }

                case SERVICES.AFISEAZA_BALANTA -> {
                    System.out.println(SERVICES.AFISEAZA_BALANTA);
                    System.out.println("Balanta contului tau este: " + bankAccount.getBalanta());
                }

                case SERVICES.AFISEAZA_DETALII_CONT -> {
                    System.out.println(SERVICES.AFISEAZA_DETALII_CONT);
                    System.out.println(bankAccount);
                }

                case SERVICES.AFISEAZA_CONTURI -> {
                    System.out.println(SERVICES.AFISEAZA_CONTURI);
                    displayBankAccounts(appAccount);
                }

                case SERVICES.TRANSFERA_BANI -> {
                    displayBankAccounts(appAccount);
                    BankAccount bankAccount2 = chooseAccount();
                    double suma;
                    System.out.println("Introduceti suma de bani pe care doriti sa o transferati:");
                    suma = SCANNER.nextDouble();

                    BankAccountUtils.transfer(bankAccount,bankAccount2,suma);
                    System.out.println("Transfer reusit!");
                }

            }
        }while(optiune != -1);
    }

    static AppAccount authentification(){

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
            optiune = SCANNER.nextInt();

            SERVICES service = SERVICES.getService(-optiune);
            if(service == SERVICES.EXIT){
                System.out.println(SERVICES.EXIT);
                System.exit(0);
            }
            else {
                try {
                    appAccount = enterApplication(service);
                }catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }while(appAccount == null);

        return appAccount;
    }

    public static void main(String[] args) {

        System.out.println(
        """
            Bine ai venit in aplicatia bancara!
            Pentru accesarea aplicatiei alegeti din urmatoarele optiuni:
        """
        );

        AppAccount appAccount = authentification();
        try {
            bankAccounts = BankAccountUtils.getAllAccounts(appAccount);
        }
        catch (Exception e) {
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
