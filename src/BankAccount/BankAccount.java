package BankAccount;

import UserBankAccount.Company;
import UserBankAccount.Individual;
import UserBankAccount.User;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class BankAccount implements Comparable<BankAccount>{
  
    private final User titular; // presupunem ca exista o clasa User
    private final String IBAN;
    private final String moneda;
    private final String dataDeschiderii;
    private double balanta;
    private boolean activ;

    public BankAccount(User titular) {
        if (titular == null) {
            throw new IllegalArgumentException("Titularul nu poate fi null.");
        }
        this.titular = titular;
        this.dataDeschiderii = currentDate() ;
        this.IBAN = generateRandomIBAN();
        this.moneda = "RON";
        this.balanta = 0.0;
        this.activ= false;
    }

    public BankAccount(User titular, String IBAN, String moneda, String dataDeschiderii, double balanta, boolean active) {
       
        this.titular = titular;
        this.dataDeschiderii = dataDeschiderii ;
        this.IBAN = IBAN;
        this.moneda = moneda;
        this.balanta = balanta;
        this.activ = active;
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
   
    public String getIban() { return IBAN; }
    public String getDataDeschidere() { return dataDeschiderii; }
    public double getBalanta() { return balanta; }
    public String getMoneda() { return moneda; }
    public boolean isActiv() { return activ; }
    public User getUser() { return titular; }
    public String getTableName() {
        if (titular instanceof Individual) {
            if(this.getClass() == DepositAccount.class) {
                return "cont_personal_depozit";
            } else if (this.getClass() == DebitAccount.class) {
                return "cont_personal_debit";
            }   
        }
        if (titular instanceof Company) {
            if(this.getClass() == DepositAccount.class) {
                return "cont_firma_depozit";
            } else if (this.getClass() == DebitAccount.class) {
                return "cont_firma_debit";
            }   
        }   
        return ""; 
    }
   

    // Settere
    public void setBalanta(double balanta) {
        if (balanta < 0) {
            throw new IllegalArgumentException("Soldul nu poate fi negativ.");
        }
        this.balanta = balanta;
    }
    public void activate(boolean activ) {
        this.activ = activ;
    }


    @Override
    public String toString() {
        return "BankAccount{" +
                "titular=" + titular +
                ", IBAN='" + IBAN + '\'' +
                ", moneda='" + moneda + '\'' +
                ", dataDeschiderii='" + dataDeschiderii + '\'' +
                ", balanta=" + balanta +
                ", activ=" + activ +
                '}';
    }

    @Override
    public int compareTo(BankAccount bankAccount){
        return this.dataDeschiderii.compareTo(bankAccount.getDataDeschidere());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((IBAN == null) ? 0 : IBAN.hashCode());
        result = prime * result + ((moneda == null) ? 0 : moneda.hashCode());
        result = prime * result + ((dataDeschiderii == null) ? 0 : dataDeschiderii.hashCode());
        long temp;
        temp = Double.doubleToLongBits(balanta);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (activ ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BankAccount other = (BankAccount) obj;
        if (IBAN == null) {
            if (other.IBAN != null)
                return false;
        } else if (!IBAN.equals(other.IBAN))
            return false;
        if (moneda == null) {
            if (other.moneda != null)
                return false;
        } else if (!moneda.equals(other.moneda))
            return false;
        if (dataDeschiderii == null) {
            if (other.dataDeschiderii != null)
                return false;
        } else if (!dataDeschiderii.equals(other.dataDeschiderii))
            return false;
        if (Double.doubleToLongBits(balanta) != Double.doubleToLongBits(other.balanta))
            return false;
        if (activ != other.activ)
            return false;
        return true;
    }
        
}
