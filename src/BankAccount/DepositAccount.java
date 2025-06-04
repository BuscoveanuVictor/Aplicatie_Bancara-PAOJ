package BankAccount;

import UserBankAccount.User;

public class DepositAccount extends BankAccount {
    private final double dobandaAnuala; 
    private final int perioadaLuni; 
    private final double sumaDepusa;

    public DepositAccount(User titular) {
        super(titular);
        this.dobandaAnuala = 0.0;
        this.perioadaLuni = 0;
        this.sumaDepusa = 0.0;
    }

    public DepositAccount(User titular, String IBAN, String moneda, String dataDeschiderii, double balanta, boolean active, double dobandaAnuala, int perioadaLuni, double sumaDepusa) {
        super(titular, IBAN, moneda, dataDeschiderii, balanta, active);
        this.dobandaAnuala = dobandaAnuala;
        this.perioadaLuni = perioadaLuni;
        this.sumaDepusa = sumaDepusa;
    }

    public double getDobandaAnuala() {
        return dobandaAnuala;
    }

    public int getPerioadaLuni() {
        return perioadaLuni;
    }

    public double getSumaDepusa() {
        return sumaDepusa;
    }
}
