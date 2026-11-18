package inventarislab.exception;

/**
 * Custom exception untuk kegagalan membaca/menulis media penyimpanan (file data).
 */
public class DataAksesException extends Exception {

    private static final long serialVersionUID = 1L;

    public DataAksesException(String pesan) {
        super(pesan);
    }

    public DataAksesException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}
