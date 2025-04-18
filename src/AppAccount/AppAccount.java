package AppAccount;

public final class AppAccount {
    private String username;
    private String email;
    private String password;
    
    public AppAccount(String email, String username, String password) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email-ul este obligatoriu.");
        }
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username-ul este obligatoriu.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Parola este obligatorie.");
        }

        this.email = email;
        this.username = username;
        this.password = password;
    }

    
    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "ClientRecord{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
