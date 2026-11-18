package inventarislab.exception;

/**
 * Custom exception untuk input/data yang tidak memenuhi aturan bisnis
 * (mis. harga negatif, nama kosong, kode ruangan salah format).
 */
public class ValidasiException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String namaField;

    public ValidasiException(String pesan) {
        this(null, pesan);
    }

    public ValidasiException(String namaField, String pesan) {
        super(pesan);
        this.namaField = namaField;
    }

    /** Nama field yang bermasalah (boleh null), dipakai view untuk memberi fokus. */
    public String getNamaField() {
        return namaField;
    }
}
