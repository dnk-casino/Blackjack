package dnk.casino.blackjack.Blackjack;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import dnk.casino.blackjack.Blackjack.Carta.Valor;

@Component
public class Mano {
    private List<Carta> cartas;

    public Mano() {
        this.cartas = new ArrayList<>();
    }

    public void agregarCarta(Carta carta) {
        cartas.add(carta);
    }

    public int getValorTotal() {
        int valorTotal = 0;
        int numAses = 0;
        for (Carta carta : cartas) {
            if (carta.getValor().equals(Valor.AS)) {
                numAses++;
                valorTotal += 11;
            } else {
                valorTotal += carta.getValorNumerico();
            }
        }
        while (valorTotal > 21 && numAses > 0) {
            valorTotal -= 10;
            numAses--;
        }
        return valorTotal;
    }

    public List<Carta> getCartas() {
        return cartas;
    }

    public void setCartas(List<Carta> cartas) {
        this.cartas = cartas;
    }
}