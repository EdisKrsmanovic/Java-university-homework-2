package ba.unsa.etf.rpr.zadaca2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KorisnikController {
    public TextField fldIme;
    public TextField fldPrezime;
    public TextField fldEmail;
    public TextField fldUsername;
    public ListView<Korisnik> listKorisnici;
    public PasswordField fldPassword;
    public PasswordField fldPasswordRepeat;
    public Slider sliderGodinaRodjenja;

    private KorisniciModel model;

    public KorisnikController(KorisniciModel model) {
        this.model = model;
    }

    @FXML
    public void initialize() {
        listKorisnici.setItems(model.getKorisnici());
        listKorisnici.getSelectionModel().selectedItemProperty().addListener((obs, oldKorisnik, newKorisnik) -> {
            model.setTrenutniKorisnik(newKorisnik);
            listKorisnici.refresh();
        });

        model.trenutniKorisnikProperty().addListener((obs, oldKorisnik, newKorisnik) -> {
            if (oldKorisnik != null) {
                fldIme.textProperty().unbindBidirectional(oldKorisnik.imeProperty());
                fldPrezime.textProperty().unbindBidirectional(oldKorisnik.prezimeProperty());
                fldEmail.textProperty().unbindBidirectional(oldKorisnik.emailProperty());
                sliderGodinaRodjenja.valueProperty().unbindBidirectional(oldKorisnik.godinaRodjenjaProperty());
                fldUsername.textProperty().unbindBidirectional(oldKorisnik.usernameProperty());
                fldPassword.textProperty().unbindBidirectional(oldKorisnik.passwordProperty());
            }
            if (newKorisnik == null) {
                fldIme.setText("");
                fldPrezime.setText("");
                fldEmail.setText("");
                sliderGodinaRodjenja.setValue(2000);
                fldUsername.setText("");
                fldPassword.setText("");
                fldPasswordRepeat.setText("");
            } else {
                fldIme.textProperty().bindBidirectional(newKorisnik.imeProperty());
                fldPrezime.textProperty().bindBidirectional(newKorisnik.prezimeProperty());
                fldEmail.textProperty().bindBidirectional(newKorisnik.emailProperty());
                sliderGodinaRodjenja.valueProperty().bindBidirectional(newKorisnik.godinaRodjenjaProperty());
                fldUsername.textProperty().bindBidirectional(newKorisnik.usernameProperty());
                fldPassword.textProperty().bindBidirectional(newKorisnik.passwordProperty());
                fldPasswordRepeat.textProperty().setValue(fldPassword.getText());
            }
        });

        fldIme.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && checkIme(newIme) && newIme.length() >= 3) {
                fldIme.getStyleClass().removeAll("poljeNijeIspravno");
                fldIme.getStyleClass().add("poljeIspravno");
            } else {
                fldIme.getStyleClass().removeAll("poljeIspravno");
                fldIme.getStyleClass().add("poljeNijeIspravno");
            }
        });

        fldPrezime.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && checkIme(newIme) && newIme.length() >= 3) {
                fldPrezime.getStyleClass().removeAll("poljeNijeIspravno");
                fldPrezime.getStyleClass().add("poljeIspravno");
            } else {
                fldPrezime.getStyleClass().removeAll("poljeIspravno");
                fldPrezime.getStyleClass().add("poljeNijeIspravno");
            }
        });

        fldEmail.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && checkEmail(newIme)) {
                fldEmail.getStyleClass().removeAll("poljeNijeIspravno");
                fldEmail.getStyleClass().add("poljeIspravno");
            } else {
                fldEmail.getStyleClass().removeAll("poljeIspravno");
                fldEmail.getStyleClass().add("poljeNijeIspravno");
            }
        });

        fldUsername.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && newIme.length() <= 16 && checkUsername(newIme)) {
                fldUsername.getStyleClass().removeAll("poljeNijeIspravno");
                fldUsername.getStyleClass().add("poljeIspravno");
            } else {
                fldUsername.getStyleClass().removeAll("poljeIspravno");
                fldUsername.getStyleClass().add("poljeNijeIspravno");
            }
        });

        fldPassword.textProperty().addListener((obs, oldIme, newIme) -> {
            if(model.getTrenutniKorisnik() != null) model.getTrenutniKorisnik().setPassword(newIme);
            if (!newIme.isEmpty() && newIme.equals(fldPasswordRepeat.getText()) && model.getTrenutniKorisnik().checkPassword()) {
                fldPassword.getStyleClass().removeAll("poljeNijeIspravno");
                fldPassword.getStyleClass().add("poljeIspravno");
                fldPasswordRepeat.getStyleClass().removeAll("poljeNijeIspravno");
                fldPasswordRepeat.getStyleClass().add("poljeIspravno");
            } else {
                fldPassword.getStyleClass().removeAll("poljeIspravno");
                fldPassword.getStyleClass().add("poljeNijeIspravno");
                fldPasswordRepeat.getStyleClass().removeAll("poljeIspravno");
                fldPasswordRepeat.getStyleClass().add("poljeNijeIspravno");
            }
        });

        fldPasswordRepeat.textProperty().addListener((obs, oldIme, newIme) -> {
            boolean correctPassword = true;
            if(model.getTrenutniKorisnik() != null) correctPassword = model.getTrenutniKorisnik().checkPassword();
            if (!newIme.isEmpty() && newIme.equals(fldPassword.getText()) && correctPassword) {
                fldPasswordRepeat.getStyleClass().removeAll("poljeNijeIspravno");
                fldPasswordRepeat.getStyleClass().add("poljeIspravno");
                fldPassword.getStyleClass().removeAll("poljeNijeIspravno");
                fldPassword.getStyleClass().add("poljeIspravno");
            } else {
                fldPasswordRepeat.getStyleClass().removeAll("poljeIspravno");
                fldPasswordRepeat.getStyleClass().add("poljeNijeIspravno");
                fldPassword.getStyleClass().removeAll("poljeIspravno");
                fldPassword.getStyleClass().add("poljeNijeIspravno");
            }
        });
    }

    private boolean checkUsername(String newIme) {
        return newIme.matches("^[a-zA-Z_$][a-zA-Z_$0-9]*$");
    }

    private boolean checkIme(String newIme) {
        Pattern pattern = Pattern.compile("[a-z|A-Z|\\-| ]");
        Matcher matches = pattern.matcher(newIme);
        String ime = "";
        while (matches.find()) {
            ime += matches.group();
        }
        return ime.equals(newIme);
    }

    private boolean checkEmail(String email) {
        Pattern pattern = Pattern.compile("[\\s\\S][@][\\s\\S]");
        Matcher matches = pattern.matcher(email);
        return matches.find() && matches.group().length() == 3;
    }

    public void dodajAction(ActionEvent actionEvent) {
        model.getKorisnici().add(new Korisnik("", "", "", "", ""));
        listKorisnici.getSelectionModel().selectLast();
    }

    public void krajAction(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void obrisiAction(ActionEvent actionEvent) {
        model.getKorisnici().remove(model.getTrenutniKorisnik());
    }
}
