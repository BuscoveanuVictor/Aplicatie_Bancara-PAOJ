package BankAccount;

import UserBankAccount.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BankAccount {

    private final User userAccount;

    private final String IBAN;
    private final String moneda;
    private final String dataDeschiderii;
    private double balanta;
    private boolean active;

    public BankAccount(User userAccount) {
      
        this.userAccount = userAccount;

        LocalDate dataCurenta = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        this.IBAN = "RO00BTLSALUUUT";
        this.moneda = "RON";
        this.dataDeschiderii = dataCurenta.format(formatter);
        this.balanta = 0.0;
        this.active = true;
    }

    public BankAccount(User userAccount, String IBAN, String moneda, String dataDeschiderii, double balanta, boolean active) {

        this.userAccount = userAccount;

        this.IBAN = IBAN;
        this.moneda = moneda;
        this.dataDeschiderii = dataDeschiderii;
        this.balanta = balanta;
        this.active = active;
    }

    // Gettere
    public String getIban() { return IBAN; }
    public String getDataDeschidere() { return dataDeschiderii; }
    public double getBalanta() { return balanta; }
    public String getMoneda() { return moneda; }
    public boolean isActiv() { return active; }
    public User getUser() { return userAccount; }




    @Override
    public String toString() {
       
        return "BankAccount{" +
                "IBAN='" + IBAN + '\'' +
                ", dataDeschiderii='" + dataDeschiderii + '\'' +
                ", balanta=" + balanta +
                '}';
    }


}
