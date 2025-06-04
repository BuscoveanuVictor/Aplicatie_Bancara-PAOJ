package UserBankAccount;

import java.util.Scanner;

import AppAccount.AppAccount;

public class UserService {
    private static final Scanner SCANNER = new Scanner(System.in);
    
    public Individual createIndividualUser(AppAccount appAccount) {
        String nume, cnp;
        System.out.println("Introduceti numele complet: ");
        nume = SCANNER.nextLine();
        System.out.println("Introduceti cnp-ul: ");
        cnp = SCANNER.nextLine();

        Individual individual = new Individual(appAccount, nume, cnp);
   
        System.out.println(individual);
        return individual;
    }

    public Company createCompanyUser(AppAccount appAccount){
        String numeFirma, CUI, numarInregistrare;
        System.out.println("Introduceti numele firmei: ");
        numeFirma = SCANNER.nextLine();
        System.out.println("Introduceti codul unic de inregistrare: ");
        CUI = SCANNER.nextLine();
        System.out.println("Introduceti numarul de inregistrare: ");
        numarInregistrare = SCANNER.nextLine();

        return new Company(appAccount, numeFirma, CUI, numarInregistrare);
    }
}
