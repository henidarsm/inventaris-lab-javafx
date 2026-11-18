package inventarislab.exception;

/**
 * Custom exception ketika login gagal atau pengguna tidak punya hak akses.
 */
public class AutentikasiException extends Exception {

    private static final long serialVersionUID = 1L;

    public AutentikasiException(String pesan) {
        super(pesan);
    }
}
