package BankAccount;
import UserBankAccount.User;


public class DebitAccount extends BankAccount {
    public DebitAccount(User titular){
        super(titular);
    }
    public DebitAccount(User titular, String IBAN, String moneda, String dataDeschiderii, double balanta, boolean active) {
        super(titular, IBAN, moneda, dataDeschiderii, balanta, active);
    }
}
