package dnk.casino.blackjack.Blackjack;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JuegoService {

    @Autowired
    private JuegoRepository juegoRepository;

    public Optional<Juego> findById(String id) {
        return juegoRepository.findById(id);
    }

    public Juego crearJuego(String idJugador, int apuesta) {
        Optional<Juego> juegoOpt = juegoRepository.findByIdJugador(idJugador);
        if (juegoOpt.isPresent()) {
            if (!juegoOpt.get().isActivo()) {
                return iniciarJuego(idJugador, apuesta);
            } else {
                return null;
            }
        } else {
            return iniciarJuego(idJugador, apuesta);
        }
    }

    public Juego iniciarJuego(String idJugador, int apuesta) {
        Juego juego = new Juego(idJugador, apuesta);
        juego.iniciarJuego();
        return juegoRepository.save(juego);
    }

    public Juego pedirCarta(String id) {
        Juego juego = findById(id).orElseThrow(() -> new RuntimeException("Juego no encontrado"));
        juego.pedirCarta();
        return juegoRepository.save(juego);
    }

    public Object[] plantarse(String id) {
        Object[] result = new Object[2];
        result[0] = findById(id).orElseThrow(() -> new RuntimeException("Juego no encontrado"));
        result[1] = ((Juego) result[0]).plantarse();
        juegoRepository.save((Juego) result[0]);
        return result;
    }
}
