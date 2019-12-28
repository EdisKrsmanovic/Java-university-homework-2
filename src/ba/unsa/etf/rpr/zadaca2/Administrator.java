package ba.unsa.etf.rpr.zadaca2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Administrator extends Korisnik {
    public Administrator(String ime, String prezime, String email, String username, String password) {
        super(ime, prezime, email, username, password);
    }

    public Administrator(Korisnik korisnik) {
        super(korisnik.getIme(), korisnik.getPrezime(), korisnik.getEmail(), korisnik.getUsername(), korisnik.getPassword());
    }

    @Override
    public boolean checkPassword() {
        boolean isValid = super.checkPassword();

        String regex = "[A-Z|a-z|0-9]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matches = pattern.matcher(getPassword());

        StringBuilder pw = new StringBuilder();

        while(matches.find()) pw.append(matches.group());
        return isValid && !pw.toString().equals(getPassword());
    }
}
