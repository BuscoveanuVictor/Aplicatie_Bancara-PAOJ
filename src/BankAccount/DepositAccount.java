package BankAccount;

import UserBankAccount.User;

public class DepositAccount extends BankAccount {
    private final double dobandaAnuala; 
    private final int perioadaLuni; 
    private final double sumaDepusa;

    public DepositAccount(User titular, String IBAN, String moneda, String dataDeschiderii, double balanta, boolean active, double dobandaAnuala, int perioadaLuni, double sumaDepusa) {
        super(titular, IBAN, moneda, dataDeschiderii, balanta, active);
        this.dobandaAnuala = dobandaAnuala;
        this.perioadaLuni = perioadaLuni;
        this.sumaDepusa = sumaDepusa;
    }
}
