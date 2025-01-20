package dnk.casino.blackjack.Blackjack;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dnk.casino.blackjack.Users.Usuario;

@Service
public class JuegoService {

    @Autowired
    private JuegoRepository juegoRepository;

    public Optional<Juego> findById(String id) {
        return juegoRepository.findById(id);
    }

    public Juego crearJuego(Usuario jugador) {
        Juego juego = new Juego(jugador);
        juego.iniciarJuego();
        return juegoRepository.save(juego);
    }

    public Juego pedirCarta(String id) {
        Juego juego = findById(id).orElseThrow(() -> new RuntimeException("Juego no encontrado"));
        juego.pedirCarta();
        return juegoRepository.save(juego);
    }

    public Juego determinarGanador(String id ) {
        Juego juego = findById(id).orElseThrow(() -> new RuntimeException("Juego no encontrado"));
        juego.determinarGanador();
        return juegoRepository.save(juego);
    }
}
