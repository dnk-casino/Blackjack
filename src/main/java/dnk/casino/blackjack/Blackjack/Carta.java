package dnk.casino.blackjack.Blackjack;

public class Carta {
    private final Palo palo;
    private final Valor valor;

    public Carta(Palo palo, Valor valor) {
        this.palo = palo;
        this.valor = valor;
    }

    public Palo getPalo() {
        return palo;
    }

    public Valor getValor() {
        return valor;
    }

    public String getPaloIcono() {
        return palo.getIcono();
    }

    public int getValorNumerico() {
        return valor.getValor();
    }

    public enum Palo {
        CORAZONES("Corazones", "♥️"),
        DIAMANTES("Diamantes", "♦️"),
        PICAS("Picas", "♠️"),
        TREBOLES("Tréboles", "♣️");
    
        private final String nombre;
        private final String icono;
    
        Palo(String nombre, String icono) {
            this.nombre = nombre;
            this.icono = icono;
        }
    
        public String getNombre() {
            return nombre;
        }
    
        public String getIcono() {
            return icono;
        }
    
        @Override
        public String toString() {
            return icono;
        }
    }

    public enum Valor {
        AS("As", 11),
        DOS("2", 2),
        TRES("3", 3),
        CUATRO("4", 4),
        CINCO("5", 5),
        SEIS("6", 6),
        SIETE("7", 7),
        OCHO("8", 8),
        NUEVE("9", 9),
        DIEZ("10", 10),
        JOTA("J", 10),
        REINA("Q", 10),
        REY("K", 10);
    
        private final String nombre;
        private final int valor;
    
        Valor(String nombre, int valor) {
            this.nombre = nombre;
            this.valor = valor;
        }
    
        public String getNombre() {
            return nombre;
        }
    
        public int getValor() {
            return valor;
        }
    
        @Override
        public String toString() {
            return nombre;
        }
    }
}