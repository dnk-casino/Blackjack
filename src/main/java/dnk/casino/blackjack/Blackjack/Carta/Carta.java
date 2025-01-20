package dnk.casino.blackjack.Blackjack.Carta;

public class Carta {
    private final Palo palo;
    private final Valor valor;

    public Carta(Palo palo, Valor valor) {
        this.palo = palo;
        this.valor = valor;
    }

    public Palo getPalo() {
        return palo;
    }

    public Valor getValor() {
        return valor;
    }

    public String getPaloIcono() {
        return palo.getIcono();
    }

    public int getValorNumerico() {
        return valor.getValor();
    }
}