package ba.unsa.etf.rpr.zadaca2;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KorisnikController {
    public TextField fldIme;
    public TextField fldPrezime;
    public TextField fldEmail;
    public TextField fldUsername;
    public ListView<Korisnik> listKorisnici;
    public PasswordField fldPassword;
    public PasswordField fldPasswordRepeat;
    public Slider sliderGodinaRodjenja;
    public CheckBox cbAdmin;

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

            if (model.getTrenutniKorisnik() != null) model.getTrenutniKorisnik().setPassword(newIme);
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
            if (model.getTrenutniKorisnik() != null) correctPassword = model.getTrenutniKorisnik().checkPassword();
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
        Pattern pattern = Pattern.compile("[a-z|A-Z|\\-| |č|ć|ž|đ|š]");
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

    public void generisiAction(ActionEvent actionEvent) {
        if (fldIme.getText().length() != 0) {
            String username = fldIme.getText().charAt(0) + fldPrezime.getText();
            username = username.toLowerCase();
            username = username.replace('č', 'c');
            username = username.replace('ć', 'c');
            username = username.replace('ž', 'z');
            username = username.replace('đ', 'd');
            username = username.replace('š', 's');
            fldUsername.setText(username);
        }

        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!#$%&/()=?,.-_";
        String values = upperCase + lowerCase + numbers;

        if (cbAdmin.isSelected()) values += specialChars;

        Integer passwordLength = 8;

        Random rnd = new Random();

        char[] password = new char[passwordLength];
        List<Integer> indeksi = IntStream.range(0, passwordLength).boxed().collect(Collectors.toList());

        int pozicijaIndeksi = rnd.nextInt(passwordLength--);
        Integer indeks = indeksi.get(pozicijaIndeksi);
        password[indeks] = upperCase.charAt(rnd.nextInt(upperCase.length()));
        indeksi.remove(pozicijaIndeksi);

        pozicijaIndeksi = rnd.nextInt(passwordLength--);
        indeks = indeksi.get(pozicijaIndeksi);
        password[indeks] = lowerCase.charAt(rnd.nextInt(lowerCase.length()));
        indeksi.remove(pozicijaIndeksi);

        pozicijaIndeksi = rnd.nextInt(passwordLength--);
        indeks = indeksi.get(pozicijaIndeksi);
        password[indeks] = numbers.charAt(rnd.nextInt(numbers.length()));
        indeksi.remove(pozicijaIndeksi);

        if (cbAdmin.isSelected()) {
            pozicijaIndeksi = rnd.nextInt(passwordLength--);
            indeks = indeksi.get(pozicijaIndeksi);
            password[indeks] = specialChars.charAt(rnd.nextInt(specialChars.length()));
            indeksi.remove(pozicijaIndeksi);
        }

        int n = passwordLength;

        for (int i = 0; i < n; i++) {
            pozicijaIndeksi = rnd.nextInt(passwordLength--);
            indeks = indeksi.get(pozicijaIndeksi);
            password[indeks] = values.charAt(rnd.nextInt(values.length()));
            indeksi.remove(pozicijaIndeksi);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Password generated");
        alert.setHeaderText(null);
        alert.setContentText(String.format("Vaš password je: %s", String.valueOf(password)));

        alert.showAndWait();

        fldPassword.setText(String.valueOf(password));
        fldPasswordRepeat.setText(String.valueOf(password));
    }

    public void adminAction(ActionEvent actionEvent) {
        if (model.getTrenutniKorisnik() != null) {
            if (cbAdmin.isSelected() && model.getTrenutniKorisnik().getClass() == Korisnik.class) {
                Administrator admin = new Administrator(model.getTrenutniKorisnik());
                ObservableList<Korisnik> korisniks = model.getKorisnici();
                korisniks.set(korisniks.indexOf(model.getTrenutniKorisnik()), admin);
                model.setKorisnici(korisniks);
                model.setTrenutniKorisnik(admin);
            } else if (model.getTrenutniKorisnik().getClass() == Administrator.class && !cbAdmin.isSelected()) {
                Korisnik admin = new Administrator(model.getTrenutniKorisnik());
                Korisnik korisnik = new Korisnik(admin.getIme(), admin.getPrezime(), admin.getEmail(), admin.getUsername(), admin.getPassword());
                ObservableList<Korisnik> korisniks = model.getKorisnici();
                korisniks.set(korisniks.indexOf(model.getTrenutniKorisnik()), korisnik);
                model.setKorisnici(korisniks);
                model.setTrenutniKorisnik(korisnik);
            }
        }
    }
}
