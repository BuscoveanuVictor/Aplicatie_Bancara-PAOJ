package UserBankAccount;

import AppAccount.AppAccount;

public abstract class User {

    private final AppAccount appAccount;

    public User(AppAccount appAccount) {
        this.appAccount = appAccount;
    }

    

}

