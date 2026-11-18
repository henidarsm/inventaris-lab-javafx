package inventarislab.model;

/** Hak akses: ADMIN boleh semua, LABORAN tidak boleh menghapus data. */
public enum Peran {
    ADMIN("Admin"),
    LABORAN("Laboran");

    private final String label;

    Peran(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
