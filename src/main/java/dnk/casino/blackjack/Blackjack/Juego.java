package dnk.casino.blackjack.Blackjack;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import dnk.casino.blackjack.Blackjack.Carta.Carta;
import dnk.casino.blackjack.Blackjack.Carta.Palo;
import dnk.casino.blackjack.Blackjack.Carta.Valor;
import dnk.casino.blackjack.Users.Usuario;

@Document(collection = "blackjack")
public class Juego {
    @Id
    private String id;
    private IA ia;
    private Usuario jugador;
    private Mano manoJugador;
    private boolean activo;

    public Juego(Usuario jugador) {
        this.ia = new IA();
        this.jugador = jugador;
        this.manoJugador = new Mano();
        this.activo = true;
    }

    public IA getIa() {
        return ia;
    }

    public void setIa(IA ia) {
        this.ia = ia;
    }

    public Usuario getJugador() {
        return jugador;
    }

    public void setJugador(Usuario jugador) {
        this.jugador = jugador;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Mano getManoJugador() {
        return manoJugador;
    }

    public void setManoJugador(Mano mano) {
        this.manoJugador = mano;
    }

    public Mano getManoIA() {
        return ia.getMano();
    }

    public int getValorJugador() {
        return manoJugador.getValorTotal();
    }

    public int getValorIA() {
        return ia.getValorTotal();
    }

    public void agregarCartaJugador(Carta carta) {
        manoJugador.agregarCarta(carta);
    }

    public void iniciarJuego() {
        pedirCarta();
        pedirCarta();
        pedirCartaIA();
        pedirCartaIA();
        if (getValorJugador() == 21 || getValorIA() == 21) {
            determinarGanador();
        }
    }

    public Palo randomPalo() {
        return Palo.values()[(int) (Math.random() * Palo.values().length)];
    }

    public Valor randomValor() {
        return Valor.values()[(int) (Math.random() * Valor.values().length)];
    }

    public void pedirCarta() {
        Carta carta = new Carta(randomPalo(), randomValor());
        agregarCartaJugador(carta);
    }

    public void pedirCartaIA() {
        Carta carta = new Carta(randomPalo(), randomValor());
        ia.agregarCarta(carta);
    }

    // Si el jugador gana = 1 | Si la IA gana = 2 | Empate = 0
    public int determinarGanador() {
        setActivo(false);
        if (getValorJugador() > 21) {
            return 2;
        } else if (getValorIA() > 21) {
            return 1;
        } else if (getManoJugador().getCartas().size() > 2 && getManoIA().getCartas().size() == 2
                && getValorIA() == 21) {
            return 2;
        } else if (getManoJugador().getCartas().size() == 2 && getManoIA().getCartas().size() == 2
                && getValorJugador() == 21 && getValorIA() == 21) {
            return 0;
        } else if (getManoJugador().getCartas().size() == 2 && getManoIA().getCartas().size() > 2
                && getValorJugador() == 21) {
            return 1;
        } else if (getValorJugador() > getValorIA()) {
            return 1;
        } else if (getValorJugador() < getValorIA()) {
            return 2;
        } else {
            return 0;
        }
    }
}