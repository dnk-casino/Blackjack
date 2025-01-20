package dnk.casino.blackjack.Juego;

import org.springframework.stereotype.Component;

@Component
public class Jugador {
    private Mano mano;
    private int saldo;

    public Jugador() {
        this.mano = new Mano();
        this.saldo = 100;
    }

    public void agregarCarta(Carta carta) {
        mano.agregarCarta(carta);
    }

    public int getValorTotal() {
        return mano.getValorTotal();
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }
}