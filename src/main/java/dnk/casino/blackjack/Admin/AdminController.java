package dnk.casino.blackjack.Admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import dnk.casino.blackjack.Skins.Skin;
import dnk.casino.blackjack.Skins.SkinRepository;
import dnk.casino.blackjack.Skins.SkinService;
import dnk.casino.blackjack.Users.Rol;
import dnk.casino.blackjack.Users.Usuario;
import dnk.casino.blackjack.Users.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/admin/api")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SkinService skinService;

    @Autowired
    private SkinRepository skinRepository;

    @PostMapping("/users")
    public List<Usuario> getAllUsers() {
        return usuarioService.getAllUsers();
    }

    @PutMapping("/users/{id}")
    public Usuario updateUser(@PathVariable String id, @RequestBody UsuarioRequest request) {
        Usuario updatedUser = new Usuario();
        updatedUser.setRol(request.getRol());
        updatedUser.setSkins(Stream.of(request.getSkins())
                .map(skinId -> {
                    return skinRepository.findById(skinId).get().getId();
                })
                .collect(Collectors.toSet()));
        updatedUser.setCoins(request.getCoins());
        return usuarioService.updateUser(id, updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        if (usuarioService.deleteUser(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping(value = "/skins/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Skin> createSkin(@RequestBody Skin skin) {
        System.out.println(skin);
        Skin newSkin = skinService.createSkin(skin);
        System.out.println(newSkin);
        return ResponseEntity.ok(newSkin);
    }

    @PutMapping("/skins/{id}")
    public Skin updateSkin(@PathVariable String id, @RequestBody SkinRequest request) {
        Skin updatedSkin = new Skin();
        updatedSkin.setName(request.getNombre());
        updatedSkin.setPrecio(request.getPrecio());
        updatedSkin.setDescription(request.getDescription());
        updatedSkin.setReels(request.getReels());
        updatedSkin.setVendible(request.isVendible());
        return skinService.updateSkin(id, updatedSkin);
    }

    @DeleteMapping("/skins/{id}")
    public ResponseEntity<Void> deleteSkin(@PathVariable String id) {
        if (skinService.deleteSkin(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public static class UsuarioRequest {
        @JsonProperty("rol")
        private Rol rol;
        @JsonProperty("coins")
        private int coins;
        @JsonProperty("skinsId")
        private String[] skinsId;

        public Rol getRol() {
            return rol;
        }

        public void setRol(Rol rol) {
            this.rol = rol;
        }

        public int getCoins() {
            return coins;
        }

        public void setCoins(int coins) {
            this.coins = coins;
        }

        public String[] getSkins() {
            return skinsId;
        }

        public void setSkins(String[] skinsId) {
            this.skinsId = skinsId;
        }
    }

    public static class SkinRequest {
        @JsonProperty("nombre")
        private String nombre;
        @JsonProperty("precio")
        private int precio;
        @JsonProperty("description")
        private String description;
        @JsonProperty("reels")
        private String[] reels;
        @JsonProperty("vendible")
        private boolean vendible;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public int getPrecio() {
            return precio;
        }

        public void setPrecio(int precio) {
            this.precio = precio;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String[] getReels() {
            return reels;
        }

        public void setReels(String[] reels) {
            this.reels = reels;
        }

        public boolean isVendible() {
            return vendible;
        }

        public void setVendible(boolean vendible) {
            this.vendible = vendible;
        }
    }
}