package dnk.casino.blackjack;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.annotation.JsonProperty;

import dnk.casino.blackjack.Blackjack.Juego;
import dnk.casino.blackjack.Blackjack.Ranking;
import dnk.casino.blackjack.Users.JwtTokenUtil;
import dnk.casino.blackjack.Users.Usuario;
import dnk.casino.blackjack.Users.UsuarioService;

@Controller
public class BlackjackController {
    @Autowired
    private Juego juego;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/tienda")
    public String tienda() {
        return "tienda";
    }

    @GetMapping("/restablecer-contrasena")
    public String restablecerContrasena(Model model, @RequestParam(required = false) String token) {
        model.addAttribute("token", token);
        return "restablecer-contrasena";
    }

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

    @PostMapping(value = "/coins", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Integer> getCoins(@RequestHeader("Authorization") String token) {
        Optional<String> usernameOpt = JwtTokenUtil.extractUsernameFromToken(token);
        if (usernameOpt.isPresent()) {
            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(usernameOpt.get());
            if (usuarioOpt.isPresent()) {
                return ResponseEntity.ok(usuarioOpt.get().getCoins());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/coins")
    @ResponseBody
    public ResponseEntity<Integer> updateCoins(@RequestHeader("Authorization") String token,
            @RequestBody CoinUpdateRequest request) {
        Optional<String> usernameOpt = JwtTokenUtil.extractUsernameFromToken(token);
        if (usernameOpt.isPresent()) {
            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(usernameOpt.get());
            if (usuarioOpt.isPresent()) {
                int newCoins = usuarioOpt.get().getCoins() + request.getDelta();
                if (newCoins >= 0) {
                    usuarioOpt.get().setCoins(newCoins);
                    usuarioService.updateUser(usuarioOpt.get().getId(), usuarioOpt.get());
                    return ResponseEntity.ok(newCoins);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping(value = "/ranking", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getRanking() {
        // Obtén los 5 jugadores con más victorias
        List<Usuario> usuarios = usuarioService.getTop5Winners();
        // Crea un objeto que contenga el ranking
        return ResponseEntity.ok(new Ranking(usuarios, usuarioService));
    }

    public static class PlayRequest {
        @JsonProperty("skin")
        private String skin;
        @JsonProperty("cost")
        private int cost;

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public String getSkin() {
            return skin;
        }

        public void setSkin(String skin) {
            this.skin = skin;
        }
    }

    public static class CoinUpdateRequest {
        @JsonProperty("delta")
        private int delta;

        public int getDelta() {
            return delta;
        }

        public void setDelta(int delta) {
            this.delta = delta;
        }
    }
}