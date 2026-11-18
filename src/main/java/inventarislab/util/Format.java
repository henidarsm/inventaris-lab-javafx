package inventarislab.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Helper format tampilan: Rupiah dan tanggal dd-MM-yyyy.
 */
public final class Format {

    public static final DateTimeFormatter POLA_TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private Format() {
    }

    public static String rupiah(double nilai) {
        DecimalFormat df = new DecimalFormat("#,##0", new DecimalFormatSymbols(new Locale("id", "ID")));
        return "Rp " + df.format(Math.round(nilai));
    }

    /** Rupiah ringkas untuk kartu ringkasan, mis. 78650000 -> "Rp 78,65 jt". */
    public static String rupiahRingkas(double nilai) {
        Locale id = new Locale("id", "ID");
        if (Math.abs(nilai) >= 1_000_000_000) {
            return String.format(id, "Rp %.2f M", nilai / 1_000_000_000);
        }
        if (Math.abs(nilai) >= 1_000_000) {
            return String.format(id, "Rp %.2f jt", nilai / 1_000_000);
        }
        return rupiah(nilai);
    }

    public static String tanggal(LocalDate tanggal) {
        return tanggal == null ? "-" : tanggal.format(POLA_TANGGAL);
    }
}
