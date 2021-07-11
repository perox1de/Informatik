import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Scanner;


public class PizzaKunde {

    //Statische Variablen
    static final String[][] produktNamen = {
            {"Pizza Salami", "Pizza Funghi", "Pizza Margharita", "Pizza Quattro Stagioni", "Pizza Speziale"},
            {"Wasser", "Cola", "Spezi", "Sprite", "Fanta"},
            {"Tomatensuppe", "Pizzabrot", "Pommes Frites", "Gemischter Salat", "Caprese"}
    }; //Namen der Produkte, angeordnet in 3 Kategorien(Pizzas, Getränke, Beilagen)
    static final double[][] produktPreise = {
            {8.99, 7.99, 6.99, 8.99, 9.99},
            {1.99, 2.50, 2.75, 2.25, 2.25},
            {3.99, 3.50, 2.99, 5.50, 4.99}
    }; //Preise der Produkte
    static String textEingabe = ""; //Platzhalter für eine Texteingabe
    static int zahlEingabe = -2; //Platzhalter für eine Zahleingabe
    static Scanner sc = new Scanner(System.in);
    static int kundenAnzahl = 0; //Gesamtanzahl der erstellten Kunden
    static int kundenNummer = 0; //Identifikationsnummer des bestellenden Kunden
    static DecimalFormat geldFormat = new DecimalFormat("#.##");

    static PizzaKunde[] PizzaKunden = new PizzaKunde[100]; //Liste von erstellten Kunden

    //Kundenabhängige Variablen
    double summe = 0; //Summe(Geld) der Bestellung
    double geld;  //"Konto"stand des Kunden
    int[] favorite; //Favorit des Kunden in einer bestimmten Kategorie nach dem Format { pizzafavorit, getränkfavorit, beilagenfavorit }
    String name; //Name des Kunden
    boolean ordered = false; //Ist "true", wenn der Kunde etwas bestellt hat
    int[][] produktAnzahl = new int[3][5]; //Anzahl von bestellten Produkten, nach Produktart
    int[][] letzteBestellung = new int[3][5]; //Platzhalter für letzte Bestellung des Kunden
    double letzteSumme = 0.0; //Platzhalter für letzte Summe des Kunden
    boolean istStammkunde = false; //Ist "true", wenn der Kunde schon einmal bestellt hat


    //Konstruktor für das Objekt PizzaKunde
    PizzaKunde(String name, double geld) {
        this.name = name;
        this.geld = geld;
        this.favorite = new int[]{-1, -1, -1}; //Der Kunde hat bei Erstellung keine Favoriten
        kundenAnzahl++;
    }


    //Kunden-unabhängige Methoden

    //Test anhand des eingegeben Namen, ob der Kunde schon einmal da war
    static boolean istKunde(String s) {
        for (int i = 0; i < kundenAnzahl; i++) {
            if (PizzaKunden[i].name.equals(s)) { //Wenn der Name registriert ist
                kundenNummer = i; //Die Nummer ist nun die ID des bestehenden Kunden
                return true;
            }
        }
        return false;
    }

    //Methode zur Eingabe von Zahlen
    static int intIn() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (Exception ignored) { //Abfangen von Ausnahmen; z.B. der Kunde gibt Buchstaben ein
                System.out.println("Bitte tätigen Sie eine gültige Eingabe.");
            }
        }
    }


    //Runden von der Summe und des Kontostands zum Anzeigen
    static String runden(double input) {
        geldFormat.setRoundingMode(RoundingMode.HALF_UP);
        return geldFormat.format(input);
    }


    //Methode zur unabhängigen Kopie von 2D-Arrays
    static int[][] deepCopy(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return copy;
    }


    //Regeln der kundenNummer, kundenAnzahl und Erstellung eines neuen Kunden
    static void neuerKunde() {
        System.out.println("Herzlich willkommen bei unserer Online-Pizzaria!");
        System.out.println("Wie ist ihr Name?");
        textEingabe = sc.nextLine();
        if (istKunde(textEingabe)) System.out.println("Willkommen zurück, " + PizzaKunden[kundenNummer].name + "!");
        else { //Wenn der Benutzer ein neuer Kunde ist
            kundenNummer = kundenAnzahl;  //Setze die kundenNummer ans Ende des Arrays
            PizzaKunden[kundenNummer] = new PizzaKunde(textEingabe, 0.0); //Erstellung des Kunden
            System.out.println("Willkommen " + PizzaKunden[kundenNummer].name + "!");
            System.out.println("Wie viel Geld haben Sie zur Verfügung?");
            PizzaKunden[kundenNummer].geld = intIn();
        }
    }


    //Kundenabhängige Methoden

    //Setzt bestellungsabhängige Variablen am Ende der Bestellung zurück; behält aber Namen, Favoriten etc.
    void reset() {
        summe = 0.0;
        ordered = false;
        produktAnzahl = new int[3][5];
    }


    //Methode zum Einzahlen von Geld
    void einzahlen() {
        System.out.println("Möchten Sie Geld einzahlen? [1-ja/2-nein]");
        zahlEingabe = intIn();
        if (zahlEingabe == 1) {
            System.out.println("Wie viel Geld möchten Sie einzahlen?");
            geld += intIn();
            System.out.println("Einzahlung erfolgreich!");
        } else if (zahlEingabe != 2) {
            System.out.println("Bitte tätigen Sie eine gültige Eingabe.");
        } else {
            reset(); //Zurücksetzen, da der Kunde die Bestellung nicht bezahlen kann
        }
    }


    //Ändern der Summe und der produktAnzahl anhand der Bestellung, Methode wegen sechsfacher Wiederholung
    void bestellen(int kategorie, int produktWahl) {
        summe += produktPreise[kategorie][produktWahl];  //Erhöhe Summe der Bestellung
        produktAnzahl[kategorie][produktWahl]++;  //Erhöhe Anzahl des Produkts
        ordered = true;
    }


    //Bestellung einer Pizza
    void pizzaBestellung() {

        if (favorite[0] >= 0) System.out.println("[0] Das Übliche"); //Dieser Teil wird nur ausgegeben, wenn der Kunde einen Pizza-Favoriten hat
        System.out.println("[1] Pizza Salami - 8,99€ \n[2] Pizza Funghi - 7,99€ \n[3] Pizza Margharita - 6,99€ \n[4] Pizza Quattro Stagioni - 8,99€\n[5] Pizza Speziale - 9,99€ \n[6] Auswahl abschließen\n");

        do {
            zahlEingabe = intIn() - 1; //-1 zur Korrektur der Eingabe auf den Index des Produkts

            if ((zahlEingabe>= 0 && zahlEingabe <5) || (favorite[0] != -1 && zahlEingabe == -1)) {
                if(zahlEingabe == -1) zahlEingabe=favorite[0]; //Setze die Eingabe auf den Favoriten

                System.out.println("Sie haben eine " + produktNamen[0][zahlEingabe] + " bestellt. Noch etwas?");
                bestellen(0, zahlEingabe); //0 entspricht der Kategorie "Pizzas"
            }
            else if (zahlEingabe!=5) { //Wenn der Kunde ein Produkt auswählt
                System.out.println("Bitte geben Sie eine gültige Bestellung auf.");
                }
        }
        while (zahlEingabe != 5); //Wiederhole, bis "Auswahl abschließen" gewählt wird
        System.out.println("Alles klar, ist das alles?");
        zahlEingabe = -2;
    }


    //Bestellung eines Getränks
    void drinkBestellung() {
        if (favorite[1] >= 0) System.out.println("[0] Das Übliche"); //Dieser Teil wird nur ausgegeben, wenn der Kunde einen Getränk-Favoriten hat
        System.out.println("[1] Wasser - 1,99€ \n[2] Cola - 2,50€ \n[3] Spezi - 2,75€ \n[4] Sprite - 2,25€\n[5] Fanta - 2,25€ \n[6] Auswahl abschließen\n");

        do {
            zahlEingabe = intIn() - 1; //-1 zur Korrektur der Eingabe auf den Index des Produkts

            if ((zahlEingabe>= 0 && zahlEingabe <5) || (favorite[1] != -1 && zahlEingabe == -1)) {
                if(zahlEingabe == -1) zahlEingabe=favorite[1]; //Setze die Eingabe auf den Favoriten

                if (zahlEingabe == 0) System.out.print("Sie haben ein ");
                else System.out.print("Sie haben eine ");
                System.out.println(produktNamen[1][zahlEingabe] + " bestellt. Noch etwas?");

                bestellen(1, zahlEingabe);
            }
            else if (zahlEingabe!=5) { //Wenn der Kunde ein Produkt auswählt
                System.out.println("Bitte geben Sie eine gültige Bestellung auf.");
            }
        }
        while (zahlEingabe != 5); //Wiederhole, bis "Auswahl abschließen" gewählt wird
        System.out.println("Alles klar, ist das alles?");
        zahlEingabe = -2;
    }


    //Bestellung einer Beilage
    void beilageBestellung() {
        if (favorite[2] >= 0) System.out.println("[0] Das Übliche"); //Dieser Teil wird nur ausgegeben, wenn der Kunde einen Beilagen-Favoriten hat
        System.out.println("[1] Tomatensuppe - 3,99€ \n[2] Pizzabrot - 2,50€ \n[3] Pommes Frites - 2,99€ \n[4] Gemischter Salat - 5,50€\n[5] Caprese - 4,99€ \n[6] Auswahl abschließen\n");

        do {
            zahlEingabe = intIn() - 1; //-1 zur Korrektur der Eingabe auf den Index des Produkts

            if ((zahlEingabe>= 0 && zahlEingabe <5) || (favorite[2] != -1 && zahlEingabe == -1)) {
                if(zahlEingabe == -1) zahlEingabe=favorite[1]; //Setze die Eingabe auf den Favoriten

                if (zahlEingabe == 0) System.out.println("Sie haben eine Tomatensuppe bestellt. Noch etwas? ");
                else if (zahlEingabe == 1) System.out.println("Sie haben ein Pizzabrot bestellt. Noch etwas?");
                else if (zahlEingabe == 2) System.out.println("Sie haben Pommes Frites bestellt. Noch etwas?");
                else if (zahlEingabe == 3) System.out.println("Sie haben einen gemischten Salat bestellt. Noch etwas?");
                else System.out.println("Sie haben einen Caprese bestellt. Noch etwas?");
                bestellen(2, zahlEingabe);
            }
            else if (zahlEingabe!=5) { //Wenn der Kunde ein Produkt auswählt
                System.out.println("Bitte geben Sie eine gültige Bestellung auf.");
            }
        }
        while (zahlEingabe != 5); //Wiederhole, bis "Auswahl abschließen" gewählt wird
        System.out.println("Alles klar, ist das alles?");
        zahlEingabe = -2;
    }


    //Ändern des Favoriten
    void support() {
        System.out.println("Welche Produkte möchten Sie als Favorit festlegen?");
        do{
            System.out.println("[1] Pizzas\n[2] Getränke\n[3] Beilagen & Vorspeisen\n[4] Festlegen");
            zahlEingabe = intIn();
            switch (zahlEingabe) {
                case 1 -> {
                    System.out.println("Welche Pizza ist Ihr Favorit?");
                    System.out.println("[1] Pizza Salami\n[2] Pizza Funghi\n[3] Pizza Margharita\n[4] Pizza Quattro Stagioni\n[5] Pizza Speziale");
                    favoritFestlegen(0); //Favorit festlegen für Kategorie 0 (Pizzen)
                }

                case 2 -> {
                    System.out.println("Welches Getränk ist Ihr Favorit?");
                    System.out.println("[1] Wasser\n[2] Cola\n[3] Spezi\n[4] Sprite\n[5] Fanta");
                    favoritFestlegen(1); //Favorit festlegen für Kategorie 1 (Getränke)
                }

                case 3 -> {
                    System.out.println("Welche Beilage ist Ihr Favorit?");
                    System.out.println("[1] Tomatensuppe\n[2] Pizzabrot\n[3] Pommes Frites\n[4] Gemischter Salat\n[5] Calprese");
                    favoritFestlegen(2); //Favorit festlegen für Kategorie 2 (Beilagen)
                }
                case 4 -> System.out.println("Okay! Das werden wir uns merken.");
                default -> System.out.println("Bitte tätigen Sie eine gültige Auswahl.");
            }

        }
        while (zahlEingabe != 4);
        zahlEingabe = -2; //Zurücksetzen des Platzhalters
    }


    //Methode wegen dreifacher Wiederholung
    void favoritFestlegen(int kategorie) {
        zahlEingabe = intIn() - 1; //Korrigieren auf Index
        if (zahlEingabe >= 0 && zahlEingabe <= 4) {
            favorite[kategorie] = zahlEingabe; //Favorit setzen
            System.out.println("Alles klar!");
        } else System.out.println("Das hat nicht geklappt.");
        zahlEingabe = 0;
    }

    
    //Beenden der Bestellung, Ausgeben des Belegs, Speichern der Bestellung
    void bestellungEnde() {
        System.out.println("Vielen Dank für ihre Bestellung, ihre Auswahl ist:");
        for (int x = 0; x < 3; x++) { //Erstelle einen "Beleg" aus den bestellten Produkten
            for (int y = 0; y < 5; y++) {
                if (produktAnzahl[x][y] > 0) {
                    System.out.println(produktAnzahl[x][y] + "x " + produktNamen[x][y] + " - " + (produktPreise[x][y] * produktAnzahl[x][y]) + "€");
                }
            }
        }
        System.out.println("Ihre Summe beträgt " + runden(summe) + "€. Drücken Sie Enter zum Bezahlen.");
        try {
            sc.nextLine();
        } catch (Exception ignored) {
        }
        geld -= summe;
        letzteBestellung = deepCopy(produktAnzahl); //Lagern der Bestellung
        letzteSumme = summe; //Lagern der Summe
        reset();
        istStammkunde = true;
        System.out.println("Ihr neuer Kontostand ist " + runden(geld) + "€. Vielen Dank und bis zum nächsten Mal, " + name + "!");
    }


    //Menü zur Auswahl der Bestelloptionen
    void bestellVorgang () {

        System.out.println("Was hätten Sie denn gern?");
        do{
            if (istStammkunde) System.out.println("[0] Das von letztem Mal"); //Wird nur ausgegeben, wenn der Kunde bereits einmal bestellt hat
            System.out.println("[1] Pizzas\n[2] Getränke\n[3] Beilagen/Vorspeisen\n[4] Favorit festlegen\n[5] Bezahlen\n[6] Restaurant verlassen");
            zahlEingabe = intIn(); //Eingabe einlesen

            switch (zahlEingabe) {

                case 0 -> { //Wenn der Kunde seine letzte Bestellung wiederholen will
                    if (istStammkunde) {  //Und schon einmal da war (wenn nein, default)
                        summe = letzteSumme; //Setze Summe auf letzte Summe
                        produktAnzahl = deepCopy(letzteBestellung); //Und kopiere die letzte Bestellung unabhängig
                        ordered=true;
                        System.out.println("Okay " + name + ", noch etwas dazu?"); //Kunde kann nun bezahlen oder etwas dazubestellen
                    }
                }
                //Bestellungen oder Favoriten
                case 1 -> pizzaBestellung();
                case 2 -> drinkBestellung();
                case 3 -> beilageBestellung();
                case 4 -> support();
                //Bezahlen
                case 5 -> {
                    if (geld < summe) { //Wenn der Kunde nicht genug Geld hat
                        System.out.println("Für diese Bestellung haben Sie leider nicht genug Geld zur Verfügung.");
                        einzahlen();  }
                    else if (ordered) { //Wenn der Kunde etwas bestellt hat
                        bestellungEnde(); }
                    else { //Wenn der Kunde nichts bestellt hat
                        System.out.println("Wollten Sie nicht zuerst etwas bestellen?");
                        zahlEingabe=-2;  }
                }
                //Gehen
                case 6 -> {
                    System.out.println("Schade, vielleicht beim nächsten Mal!");
                    reset();
                }

                default -> System.out.println("Bitte wählen Sie eine gültige Option.");
            }
        }
        while (zahlEingabe != 5 && zahlEingabe != 6); //Solange bis der Kunde bezahlt oder geht
    }


    public static void main(String[] args) {
        while (kundenAnzahl < 100) { //Programm läuft, bis 100 verschiedene Kunden da waren

            neuerKunde();
            PizzaKunden[kundenNummer].bestellVorgang();

            System.out.println("Warten auf nächsten Kunden...");
            try {
                sc.nextLine(); //Warten auf Enter
            } catch (Exception ignored) {}
        }
    }
}

