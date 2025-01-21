package dnk.casino.blackjack.Blackjack;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JuegoRepository extends MongoRepository<Juego, String> {
    Optional<Juego> findById(String id);

    @Query(value = "{idJugador: ?0, activo: true}", sort = "{_id: -1}")
    Optional<Juego> findLastActiveJuegoByJugador(String idJugador);
}