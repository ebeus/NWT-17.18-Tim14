package application.Utils;

import application.Models.Korisnik;
import application.Models.KorisnikReturn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ConvertUsers {

    public static KorisnikReturn ToKorisnikReturn(Korisnik korisnik) {
        return new KorisnikReturn(korisnik);
    }

    public static Optional<KorisnikReturn> ToKorisnikReturnOpt(Optional<Korisnik> korisnik) {
        KorisnikReturn korisnikReturn = ToKorisnikReturn(korisnik.get());
        Optional<KorisnikReturn> korisnikReturnOptional = Optional.of(korisnikReturn);
        return korisnikReturnOptional;
    }


    public static Collection<KorisnikReturn> ToKorisniciReturn(Collection<Korisnik> korisnici) {
        ArrayList<KorisnikReturn> korisnikReturns = new ArrayList<>();

        for(Korisnik k:korisnici) {
            korisnikReturns.add(ToKorisnikReturn(k));
        }
        return  korisnikReturns;
    }

    public static Iterable<KorisnikReturn> ToKorisniciReturn(Iterable<Korisnik> korisnici) {
        ArrayList<KorisnikReturn> korisnikReturns = new ArrayList<>();

        for(Korisnik k:korisnici) {
            korisnikReturns.add(ToKorisnikReturn(k));
        }
        return  korisnikReturns;
    }
}
