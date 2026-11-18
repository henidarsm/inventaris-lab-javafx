package inventarislab.model;

public enum Kondisi {
    BAIK("Baik"),
    RUSAK_RINGAN("Rusak Ringan"),
    RUSAK_BERAT("Rusak Berat");

    private final String label;

    Kondisi(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
