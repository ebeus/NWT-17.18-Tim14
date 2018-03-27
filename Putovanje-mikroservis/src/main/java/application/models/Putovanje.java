package application.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name="putovanje")
public class Putovanje {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column(name="naziv")
	private String naziv;
	
	@Column(name="start_time")
	private long start_time;
	
	@Column(name="end_time", nullable=true)
	private long end_time;
	
	@Column(name="idKorisnika")
	private long idKorisnika;
	
	@JsonIgnore
	@OneToMany(fetch=FetchType.EAGER, mappedBy="putovanje")
	private List<Lokacija> listaLokacija;
	
	protected Putovanje() {};
	
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

	public void setListaLokacija(List<Lokacija> listaLokacija) {
		this.listaLokacija = listaLokacija;
	}
	
	public void addLocation(Lokacija lokacija) {
		this.listaLokacija.add(lokacija);
	}
	
	
	
}
