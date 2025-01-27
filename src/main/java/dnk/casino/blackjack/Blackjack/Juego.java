package dnk.casino.blackjack.Blackjack;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import dnk.casino.blackjack.Blackjack.Carta.Palo;
import dnk.casino.blackjack.Blackjack.Carta.Valor;

@Document(collection = "blackjack")
public class Juego {
    @Id
    @JsonProperty
    private String id;
    @JsonProperty
    private int apuesta;
    @JsonProperty
    private IA ia;
    @JsonProperty
    private String idJugador;
    @JsonProperty
    private Mano manoJugador;
    @JsonProperty
    private boolean activo;

    public Juego(String idJugador, int apuesta) {
        this.apuesta = apuesta;
        this.ia = new IA();
        this.idJugador = idJugador;
        this.manoJugador = new Mano();
        this.activo = true;
    }

    public int getApuesta() {
        return apuesta;
    }

    public void setApuesta(int apuesta) {
        this.apuesta = apuesta;
    }

    public IA getIa() {
        return ia;
    }

    public void setIa(IA ia) {
        this.ia = ia;
    }

    public String getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(String idJugador) {
        this.idJugador = idJugador;
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
        if (getValorJugador() >= 21) {
            plantarse();
        }
    }

    public void pedirCartaIA() {
        Carta carta = new Carta(randomPalo(), randomValor());
        ia.agregarCarta(carta);
    }

    public int plantarse() {
        if (getValorJugador() <= 21) {
            while (getValorIA() < 17) {
                pedirCartaIA();
            }
        }
        return determinarGanador();
    }

    // Si el jugador gana = 1 | Si la IA gana = 2 | Empate = 0
    public int determinarGanador() {
        setActivo(false);
        if (getValorJugador() > 21) {
            return 2; // El jugador se pasó, gana la IA
        } else if (getValorIA() > 21) {
            return 1; // La IA se pasó, gana el jugador
        } else if (getValorIA() == 21 && getManoIA().getCartas().size() == 2) {
            return 2; // La IA tiene un blackjack, gana
        } else if (getValorJugador() == 21 && getManoJugador().getCartas().size() == 2) {
            if (getValorIA() == 21 && getManoIA().getCartas().size() == 2) {
                return 0; // Ambos tienen un blackjack, empate
            } else {
                return 1; // El jugador tiene un blackjack, gana
            }
        } else {
            if (getValorJugador() > getValorIA()) {
                return 1; // El jugador tiene un valor mayor, gana
            } else if (getValorJugador() < getValorIA()) {
                return 2; // La IA tiene un valor mayor, gana
            } else {
                return 0; // Empate
            }
        }
    }
}