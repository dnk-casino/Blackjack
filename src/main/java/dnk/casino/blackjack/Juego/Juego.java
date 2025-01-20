package dnk.casino.blackjack.Juego;

import org.springframework.stereotype.Service;

@Service
public class Juego {
    private Jugador jugador;
    private IA ia;

    public Juego() {
    }

    public Juego(Jugador jugador, IA ia) {
        this.jugador = jugador;
        this.ia = ia;
    }

    public void iniciarJuego() {
        // Iniciar el juego
        jugador.setSaldo(100);
        ia = new IA();
    }

    public void pedirCarta() {
        // Pedir carta para el jugador
        Carta carta = new Carta("Corazones", "5");
        jugador.agregarCarta(carta);
    }

    public void pedirCartaIA() {
        // Pedir carta para la IA
        Carta carta = new Carta("Picas", "8");
        ia.agregarCarta(carta);
    }

    public void determinarGanador() {
        // Determinar el ganador
        if (jugador.getValorTotal() > 21) {
            System.out.println("La IA gana");
        } else if (ia.getValorTotal() > 21) {
            System.out.println("El jugador gana");
        } else if (jugador.getValorTotal() > ia.getValorTotal()) {
            System.out.println("El jugador gana");
        } else if (jugador.getValorTotal() < ia.getValorTotal()) {
            System.out.println("La IA gana");
        } else {
            System.out.println("Empate");
        }
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }

    public IA getIa() {
        return ia;
    }

    public void setIa(IA ia) {
        this.ia = ia;
    }
}