package dnk.casino.blackjack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import dnk.casino.blackjack.Juego.Juego;

@RestController
public class BlackjackController {
    @Autowired
    private Juego juego;

    @GetMapping("/iniciar-juego")
    public String iniciarJuego() {
        juego.iniciarJuego();
        return "Juego iniciado";
    }

    @PostMapping("/pedir-carta")
    public String pedirCarta() {
        juego.pedirCarta();
        return "Carta pedida";
    }

    @PostMapping("/pedir-carta-ia")
    public String pedirCartaIA() {
        juego.pedirCartaIA();
        return "Carta pedida para la IA";
    }

    @GetMapping("/determinar-ganador")
    public String determinarGanador() {
        juego.determinarGanador();
        return "Ganador determinado";
    }
}