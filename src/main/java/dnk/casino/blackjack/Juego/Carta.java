package dnk.casino.blackjack.Juego;

public class Carta {
    private String palo;
    private String valor;

    public Carta(String palo, String valor) {
        this.palo = palo;
        this.valor = valor;
    }

    public String getPalo() {
        return palo;
    }

    public String getValor() {
        return valor;
    }

    public int getValorNumerico(boolean esAs11) {
        switch (valor) {
            case "As":
                if (esAs11) {
                    return 11;
                } else {
                    return 1;
                }
            case "2":
                return 2;
            case "3":
                return 3;
            case "4":
                return 4;
            case "5":
                return 5;
            case "6":
                return 6;
            case "7":
                return 7;
            case "8":
                return 8;
            case "9":
                return 9;
            case "10":
                return 10;
            case "J":
                return 10;
            case "Q":
                return 10;
            case "K":
                return 10;
            default:
                return 0;
        }
    }
}