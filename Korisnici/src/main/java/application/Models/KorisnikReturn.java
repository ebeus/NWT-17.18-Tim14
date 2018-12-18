package application.Models;

public class KorisnikReturn {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private GrupaKorisnika grupaKorisnika;
    private TipKorisnika tipKorisnika;

    public KorisnikReturn(Korisnik korisnik) {
        this.id = korisnik.getId();
        this.fullName = korisnik.getFirstName() + " " + korisnik.getLastName();
        this.username = korisnik.getUserName();
        this.email = korisnik.getEmail();
        this.grupaKorisnika = korisnik.getUserGroup();
        this.tipKorisnika = korisnik.getUserType();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GrupaKorisnika getGrupaKorisnika() {
        return grupaKorisnika;
    }

    public void setGrupaKorisnika(GrupaKorisnika grupaKorisnika) {
        this.grupaKorisnika = grupaKorisnika;
    }

    public TipKorisnika getTipKorisnika() {
        return tipKorisnika;
    }

    public void setTipKorisnika(TipKorisnika tipKorisnika) {
        this.tipKorisnika = tipKorisnika;
    }
}
