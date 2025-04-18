import java.util.List;
import java.util.Scanner;


import BankAccount.BankAccount;
import AppAccount.AppAccount;
import AppAccount.AppAccountUtils;
import BankAccount.BankAccountUtils;


public class Main {

    static Scanner SCANNER = new Scanner(System.in);

    private enum SERVICES {
        INREGISTRARE(-1, "Inregistrare cont aplicatie"),
        AUTENTIFICARE(-2, "Autentificare cont aplicatie"),
        EXIT(0, "Iesire..."),

        RETRAGERE_BANI(1, "Retragere bani"),
        DEPUNERE_BANI(2, "Depunere bani"),
        CREAZA_CONT_BANCAR(3, "Creaza-ti cont bancar"),
        AFISEAZA_DETALII_CONT(4, "Afiseaza detalii cont"),
        AFISEAZA_BALANTA(5, "Afiseaza balanta cont"),
        AFISEAZA_TRANZACTII(6, "Afiseaza tranzactii"),
        BLOCHEAZA_CONT(7, "Blocheaza cont"),
        AFISEAZA_CONTURI(8, "Afiseaza conturi bancare");


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
        public String getBanner() {
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

    public static AppAccount autentificare(int option) throws Exception {

        AppAccount appAccount;

        SERVICES service = SERVICES.getService(-option);
        switch (service) {
            case SERVICES.INREGISTRARE -> {
                System.out.println(service.getBanner());
                appAccount = citireDateContAplicatie();

                AppAccountUtils.register(appAccount);
                System.out.println("Inregistrare reusita");
                System.out.println("Bine ai venit, " + appAccount.getUsername() + "!");
                return appAccount;
            }
            case SERVICES.AUTENTIFICARE -> {
                System.out.println(service.getBanner());
                appAccount = citireDateContAplicatie();

                AppAccountUtils.login(appAccount);
                System.out.println("Logare reusita");
                System.out.println("Bine ai revenit, " + appAccount.getUsername() + "!");
                return appAccount;
            }
            case SERVICES.EXIT -> {
                System.out.println(SERVICES.EXIT.getBanner());
                System.exit(0);
            }
            default -> {
                System.out.println("Optiune invalida. Te rugam sa alegi din nou.");
            }
        }

        return null;
    }

    public static void servicii(BankAccount bankAccount, int option) throws Exception {

        SERVICES serviciu = SERVICES.getService(option);
        switch (serviciu) {
            // case SERVICES.RETRAGERE_BANI -> {
            //     System.out.println(serviciu.getBanner());
            //     System.out.println("Selectati contul din care doriti sa retrageti bani: ");
            //     //BankAccountServices.getAllAccounts(appAccount);
            // }
            // case SERVICES.DEPUNERE_BANI -> {
            //     System.out.println(SERVICES.INREGISTRARE.getBanner());

            //     if(bankAccount!=null){
            //         BankAccountUtils.deposit(bankAccount, 100);
            //         System.out.println("Depunere reusita!");
            //         System.out.println(bankAccount);
            //     }
            // }
            // case SERVICES.CREAZA_CONT_BANCAR -> {
            //     System.out.println(SERVICES.CREAZA_CONT_BANCAR.getBanner());
            //     try {
            //         bankAccount = BankAccountUtils.createNewBankAccount(appAccount);
            //         if(bankAccount != null){
            //             System.out.println("Cont bancar creat cu succes!");
            //             System.out.println("Contul tau bancar este: " + bankAccount);
            //         }
            //         else System.out.println("Contul nu a fost creat!");

            //     } catch (Exception e) {
            //         System.out.println(e);
            //     }
            // }

            // case SERVICES.AFISEAZA_CONTURI -> {
            //     System.out.println(SERVICES.AFISEAZA_CONTURI.getBanner());
            //     //BankAccountServices.getAllAccounts(appAccount);
            // }

            case SERVICES.AFISEAZA_BALANTA -> {
                System.out.println(SERVICES.AFISEAZA_BALANTA.getBanner());
                
                System.out.println("Balanta contului tau este: " + bankAccount.getBalanta());
                
                
            }

            case SERVICES.AFISEAZA_DETALII_CONT -> {
                System.out.println(SERVICES.AFISEAZA_DETALII_CONT.getBanner());
                if(bankAccount != null){
                    System.out.println("Detalii cont: " + bankAccount);
                }
                else System.out.println("Contul nu a fost creat!");
            }
            case SERVICES.BLOCHEAZA_CONT -> {
                System.out.println(SERVICES.BLOCHEAZA_CONT.getBanner());
                if(bankAccount != null){
                    //bankAccount.blockAccount();
                    System.out.println("Contul a fost blocat!");
                }
                else System.out.println("Contul nu a fost creat!");
            }

            case SERVICES.EXIT -> {

                System.out.println(SERVICES.EXIT.getBanner());
                System.exit(0);
            }
            default -> System.out.println("Optiune invalida. Te rugam sa alegi din nou.");
        }

    }

    public static void main(String[] args) {
       

        System.out.println(
        """
            Bine ai venit in aplicatia bancara!
            Pentru accesarea aplicatiei alegeti din urmatoarele optiuni:
        """
        );


        // Etapa 1 : Autentificare in aplicatie

        String bannerAuthentification =
        """ 
            1. Inregistrare cont aplicatie 
            2. Autentificare cont aplicatie 
            0. Iesire 
        """;

        AppAccount appAccount = null;
        BankAccount bankAccount = null;

        int optiune;
        do {
            System.out.println(bannerAuthentification);
            optiune = SCANNER.nextInt();
            try {
                appAccount = autentificare(optiune);
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }while(appAccount == null);

        // Etapa 2 : Serviciile aplicatiei
        
        String bannerServices =
        """
            1. Retragere bani
            2. Depunere bani
            3. Creaza-ti cont bancar
            5. Afiseaza balanta cont
            0. Iesire
        """;

        // Facem rost de conturile bancare ale utilizatorului
        List<BankAccount> bankAccounts = null;
        try {
            bankAccounts = BankAccountUtils.getAllAccounts(appAccount);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // In caz ca nu utilizatorul nu are  conturi bancare, trebuie sa creeze unul
        if(bankAccounts == null || bankAccounts.isEmpty()){
            System.out.println("Nu aveti conturi bancare!");
            System.out.println("Va rugam sa creati un cont bancar!");
            bankAccount = BankAccountUtils.createNewBankAccount(appAccount);
        }

        // Aplicarea serviciilor asupra unui cont ales de utilizator
        int optiuneServiciu;
        do {
            System.out.println(bannerServices);
            optiuneServiciu = SCANNER.nextInt();

            System.out.println("Alegeti contul bancar: ");
            for (int i = 0; i < bankAccounts.size(); i++) {
                System.out.println((i + 1) + ". " + bankAccounts.get(i).getIban());
            }
            int contSelectat = SCANNER.nextInt();
            if (contSelectat < 1 || contSelectat > bankAccounts.size()) {
                System.out.println("Cont invalid. Te rugam sa alegi din nou.");
                continue;
            }
            bankAccount = bankAccounts.get(contSelectat - 1);

            try {
                servicii(bankAccount, optiuneServiciu);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } while (optiuneServiciu != 0);

        //servicii(appAccount);
    }


}
