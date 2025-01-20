package dnk.casino.blackjack.Blackjack.Carta;

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