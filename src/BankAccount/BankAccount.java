package BankAccount;

import AppAccount.AppAccountUtils;
import UserBankAccount.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class BankAccount implements Comparable<BankAccount>{

    private final User userAccount;

    private final Integer id;
    private final String IBAN;
    private final String moneda;
    private final String dataDeschiderii;
    private double balanta;
    private boolean active;

    public BankAccount(User userAccount) {

        this.userAccount = userAccount;

        this.id = -1;
        this.dataDeschiderii = currentDate() ;
        this.IBAN = generateRandomIBAN();
        this.moneda = "RON";
        this.balanta = 0.0;
        this.active = true;
    }

    public BankAccount(User userAccount, Integer id, String IBAN, String moneda, String dataDeschiderii, double balanta, boolean active) {

        this.id =  id;
        this.userAccount = userAccount;
        this.dataDeschiderii = dataDeschiderii ;
        this.IBAN = IBAN;
        this.moneda = moneda;
        this.balanta = balanta;
        this.active = active;
    }

    private static String currentDate(){
        LocalDate dataCurenta = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dataCurenta.format(formatter);
    }

    private static String generateRandomIBAN() {
        String countryCode = "RO";
        String checkDigits = String.format("%02d", new Random().nextInt(100));
        String bankCode = generateRandomLetters(4).toUpperCase(); // cod bancă fictiv
        String accountNumber = generateRandomDigits(16);

        return countryCode + checkDigits + bankCode + accountNumber;
    }

    private static String generateRandomLetters(int length) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rand = new Random();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            result.append(letters.charAt(rand.nextInt(letters.length())));
        }

        return result.toString();
    }

    private static String generateRandomDigits(int length) {
        Random rand = new Random();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            result.append(rand.nextInt(10));
        }

        return result.toString();
    }


    // Gettere

    public Integer getId() {return this.id;}
    public String getIban() { return IBAN; }
    public String getDataDeschidere() { return dataDeschiderii; }
    public double getBalanta() { return balanta; }
    public String getMoneda() { return moneda; }
    public boolean isActiv() { return active; }
    public User getUser() { return userAccount; }

    // Settere
    public void setBalanta(double balanta) {
        if (balanta < 0) {
            throw new IllegalArgumentException("Soldul nu poate fi negativ.");
        }
        this.balanta = balanta;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "userAccount=" + userAccount +
                ", IBAN='" + IBAN + '\'' +
                ", moneda='" + moneda + '\'' +
                ", dataDeschiderii='" + dataDeschiderii + '\'' +
                ", balanta=" + balanta +
                ", active=" + active +
                '}';
    }

    @Override
    public int compareTo(BankAccount bankAccount){
        return this.dataDeschiderii.compareTo(bankAccount.getDataDeschidere());
    }
    
}
