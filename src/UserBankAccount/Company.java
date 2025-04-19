package UserBankAccount;
import AppAccount.AppAccount;

public class Company extends User {
    private String denumire;
    private String CUI;
    private String numarInregistrare;

    // Constructor principal cu datele esențiale
    public Company(AppAccount appAccount, String denumire, String CUI, String numarInregistrare) {
        super(appAccount);
        
        if (denumire == null || denumire.isEmpty()) {
            throw new IllegalArgumentException("Denumirea este obligatorie.");
        }
        if (CUI == null || CUI.isEmpty()) {
            throw new IllegalArgumentException("CUI-ul este obligatoriu.");
        }
        if (numarInregistrare == null || numarInregistrare.isEmpty()) {
            throw new IllegalArgumentException("Numărul de înregistrare este obligatoriu.");
        }
      
        this.denumire = denumire;
        this.CUI = CUI;
        this.numarInregistrare = numarInregistrare;
    }

    public String getDenumire() {
        return denumire;
    }

    public String getCUI() {
        return CUI;
    }

    public String getNumarInregistrare() {
        return numarInregistrare;
    }

    @Override
    public String toString() {
        return "CompanyAccount{" +
                "denumire='" + denumire + '\'' +
                ", CUI='" + CUI + '\'' +
                ", numarInregistrare='" + numarInregistrare + '\'' +
                '}';
    }
    

}