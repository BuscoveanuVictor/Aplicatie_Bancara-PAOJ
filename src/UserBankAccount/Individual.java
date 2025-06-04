package UserBankAccount;

import AppAccount.AppAccount;

public class Individual extends User {
    private final String nume_titular;
    private final String cnp;

    public Individual(AppAccount appAccount, String nume_titular, String cnp) {
        super(appAccount);
        if (nume_titular == null || nume_titular.isEmpty()) {
            throw new IllegalArgumentException("Numele titularului este obligatoriu.");
        }
        if (cnp == null || cnp.isEmpty()) {
            throw new IllegalArgumentException("CNP-ul este obligatoriu.");
        }
        this.nume_titular = nume_titular;
        this.cnp = cnp;
    }
    @Override
    public String toString() {
        return "Individual{" +
                "nume_titular='" + nume_titular + '\'' +
                ", cnp='" + cnp + '\'' +
                ", appAccount=" + getAppAccount() +
                '}';
    }

    public String getCnp() {
        return cnp;
    }

    public String getNume() {
        return nume_titular;
    }
}
