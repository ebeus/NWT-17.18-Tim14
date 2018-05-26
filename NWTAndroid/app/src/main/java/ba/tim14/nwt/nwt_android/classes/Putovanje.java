package ba.tim14.nwt.nwt_android.classes;

import java.util.List;

public class Putovanje {

    private long id;
    private String naziv;
    private long start_time;
    private long end_time;
    private long idKorisnika;
    private double distance;
    private List<Lokacija> listaLokacija;

    public Putovanje() {};

    public Putovanje(String naziv, long start_time, long end_time, long korisnik_id) {
        this.naziv = naziv;
        this.start_time = start_time;
        this.end_time = end_time;
        this.idKorisnika = korisnik_id;
        this.listaLokacija = null;
    }

    public Putovanje(String naziv, long start_time, long end_time, long korisnik_id, List<Lokacija> listaLokacija) {
        this.naziv = naziv;
        this.start_time = start_time;
        this.end_time = end_time;
        this.idKorisnika = korisnik_id;
        this.listaLokacija = listaLokacija;
    }

    public Putovanje(String naziv, long start_time, long korisnik_id) {
        this.naziv = naziv;
        this.start_time = start_time;
        this.idKorisnika = korisnik_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public String getNaziv() {
        return naziv;
    }
    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }
    public Long getStart_time() {
        return start_time;
    }
    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }
    public Long getEnd_time() {
        return end_time;
    }
    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }
    public Long getIdKorisnika() {
        return idKorisnika;
    }
    public void setIdKorisnika(long korisnik_id) {
        this.idKorisnika = korisnik_id;
    }

    public List<Lokacija> getListaLokacija() {
        return listaLokacija;
    }

    public double getDistance() {        return distance;    }

    public void setDistance(double distance) {        this.distance = distance;    }

    public void setListaLokacija(List<Lokacija> listaLokacija) {
        this.listaLokacija = listaLokacija;
    }

    public void addLocation(Lokacija lokacija) {
        this.listaLokacija.add(lokacija);
    }

}