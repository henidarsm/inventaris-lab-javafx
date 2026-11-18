package inventarislab.model;

public enum JenisAset {
    KOMPUTER("Komputer", "KOM"),
    JARINGAN("Perangkat Jaringan", "JAR"),
    FURNITUR("Furnitur", "FUR"),
    EMBEDDED("Perangkat Embedded", "EMB");

    private final String label;
    private final String prefixId;

    JenisAset(String label, String prefixId) {
        this.label = label;
        this.prefixId = prefixId;
    }

    public String getPrefixId() {
        return prefixId;
    }

    @Override
    public String toString() {
        return label;
    }
}
