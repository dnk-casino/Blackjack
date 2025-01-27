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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.annotation.JsonProperty;

import dnk.casino.Users.JwtTokenUtil;
import dnk.casino.Users.Usuario;
import dnk.casino.Users.UsuarioService;
import dnk.casino.blackjack.Blackjack.Juego;
import dnk.casino.blackjack.Blackjack.JuegoService;
import dnk.casino.blackjack.Blackjack.Ranking;

@Controller
public class BlackjackController {

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
        List<Usuario> usuarios = usuarioService.getTop5BJWinners();
        // Crea un objeto que contenga el ranking
        return ResponseEntity.ok(new Ranking(usuarios, usuarioService));
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

    // Blackjack

    @Autowired
    private JuegoService juegoService;

    @PostMapping(value = "/crear-juego/{apuesta}")
    public ResponseEntity<?> crearJuego(@PathVariable int apuesta, @RequestHeader("Authorization") String token) {
        Optional<String> usernameOpt = JwtTokenUtil.extractUsernameFromToken(token);
        if (usernameOpt.isPresent()) {
            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(usernameOpt.get());
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                if (usuario.getCoins() >= apuesta) {
                    usuarioService.pagar(usuario.getId(), apuesta);
                    Juego juego = juegoService.crearJuego(usuario.getId(), apuesta);
                    if (!juego.isActivo()) {
                        switch (juego.determinarGanador()) {
                            case 0 -> {
                                usuarioService.cobrar(usuario.getId(), juego.getApuesta());
                            }
                            case 1 -> {
                                usuarioService.cobrar(usuario.getId(), (juego.getApuesta() * 2));
                                usuarioService.bjvictoria(usuario.getId());
                            }
                            default -> {
                            }
                        }
                    }
                    return ResponseEntity.ok(juego);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No tienes suficientes monedas");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tienes que iniciar sesión");
        }
    }

    @PostMapping("/juego/{id}")
    public ResponseEntity<?> getJuego(@PathVariable String id, @RequestHeader("Authorization") String token) {
        Optional<String> usernameOpt = JwtTokenUtil.extractUsernameFromToken(token);
        if (usernameOpt.isPresent()) {
            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(usernameOpt.get());
            if (usuarioOpt.isPresent()) {
                Optional<Juego> juegoOpt = juegoService.findById(id);
                if (juegoOpt.isPresent()) {
                    return ResponseEntity.ok(juegoOpt.get());
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Partida no encontrada");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no econtrado");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tienes que iniciar sesión");
        }
    }

    @PostMapping("/juegoActivo")
    public ResponseEntity<?> getJuegoActivo(@RequestHeader("Authorization") String token) {
        Optional<String> usernameOpt = JwtTokenUtil.extractUsernameFromToken(token);
        if (usernameOpt.isPresent()) {
            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(usernameOpt.get());
            if (usuarioOpt.isPresent()) {
                Optional<Juego> juegoOpt = juegoService.findLastActiveJuegoByJugador(usuarioOpt.get().getId());
                if (juegoOpt.isPresent()) {
                    return ResponseEntity.ok(juegoOpt.get());
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Partida no encontrada");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no econtrado");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tienes que iniciar sesión");
        }
    }

    @PostMapping("/pedir-carta/{id}")
    public ResponseEntity<?> pedirCarta(@PathVariable String id, @RequestHeader("Authorization") String token) {
        Optional<String> usernameOpt = JwtTokenUtil.extractUsernameFromToken(token);
        if (usernameOpt.isPresent()) {
            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(usernameOpt.get());
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                Optional<Juego> juegoOpt = juegoService.findById(id);
                if (juegoOpt.isPresent()) {
                    if (juegoOpt.get().isActivo()) {
                        Juego juego = juegoService.pedirCarta(id);
                        if (!juego.isActivo()) {
                            switch (juego.determinarGanador()) {
                                case 0 -> {
                                    usuarioService.cobrar(usuario.getId(), juego.getApuesta());
                                }
                                case 1 -> {
                                    usuarioService.cobrar(usuario.getId(), (juego.getApuesta() * 2));
                                    usuarioService.bjvictoria(usuario.getId());
                                }
                                default -> {
                                }
                            }
                        } else {
                            return ResponseEntity.ok(juego);
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La partida ya ha terminado");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Partida no encontrada");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no econtrado");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tienes que iniciar sesión");
        }
    }

    @PostMapping(value = "/plantarse/{id}")
    public ResponseEntity<?> plantarse(@PathVariable String id, @RequestHeader("Authorization") String token) {
        Optional<String> usernameOpt = JwtTokenUtil.extractUsernameFromToken(token);
        if (usernameOpt.isPresent()) {
            Optional<Usuario> usuarioOpt = usuarioService.findByUsername(usernameOpt.get());
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                Optional<Juego> juegoOpt = juegoService.findById(id);
                if (juegoOpt.isPresent()) {
                    if (juegoOpt.get().isActivo()) {
                        Object[] result = juegoService.plantarse(id);
                        switch ((int) result[1]) {
                            case 0 -> {
                                usuarioService.cobrar(usuario.getId(), juegoOpt.get().getApuesta());
                            }
                            case 1 -> {
                                usuarioService.cobrar(usuario.getId(), (juegoOpt.get().getApuesta() * 2));
                                usuarioService.bjvictoria(usuario.getId());
                            }
                            default -> {
                            }
                        }
                        return ResponseEntity.ok(result);
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La partida ya ha terminado");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Partida no encontrada");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no econtrado");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tienes que iniciar sesión");
        }
    }
}