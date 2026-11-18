package inventarislab.util;

import inventarislab.exception.ValidasiException;

/**
 * Kumpulan aturan validasi yang dipakai ulang oleh setter kelas model.
 */
public final class Validasi {

    private Validasi() {
    }

    /** Teks wajib diisi, tidak boleh mengandung ';' atau baris baru (pemisah file data). */
    public static String teksWajib(String nilai, String namaField, int panjangMaks) throws ValidasiException {
        if (nilai == null || nilai.trim().isEmpty()) {
            throw new ValidasiException(namaField, namaField + " wajib diisi.");
        }
        return teksOpsional(nilai, namaField, panjangMaks);
    }

    /** Teks boleh kosong, tetapi tetap dibatasi panjang dan karakter terlarang. */
    public static String teksOpsional(String nilai, String namaField, int panjangMaks) throws ValidasiException {
        String bersih = nilai == null ? "" : nilai.trim();
        if (bersih.length() > panjangMaks) {
            throw new ValidasiException(namaField, namaField + " maksimal " + panjangMaks + " karakter.");
        }
        if (bersih.contains(";") || bersih.contains("\n") || bersih.contains("\r")) {
            throw new ValidasiException(namaField, namaField + " tidak boleh mengandung tanda ';' atau baris baru.");
        }
        return bersih;
    }

    public static double angkaTidakNegatif(double nilai, String namaField) throws ValidasiException {
        if (Double.isNaN(nilai) || nilai < 0) {
            throw new ValidasiException(namaField, namaField + " tidak boleh negatif.");
        }
        return nilai;
    }

    public static int bilanganDalamRentang(int nilai, int min, int maks, String namaField) throws ValidasiException {
        if (nilai < min || nilai > maks) {
            throw new ValidasiException(namaField, namaField + " harus di antara " + min + " dan " + maks + ".");
        }
        return nilai;
    }

    public static <T> T wajibAda(T nilai, String namaField) throws ValidasiException {
        if (nilai == null) {
            throw new ValidasiException(namaField, namaField + " wajib dipilih.");
        }
        return nilai;
    }

    /** Mengubah teks seperti "12.500.000" atau "12500000" menjadi angka. */
    public static double parseRupiah(String teks, String namaField) throws ValidasiException {
        if (teks == null || teks.trim().isEmpty()) {
            throw new ValidasiException(namaField, namaField + " wajib diisi.");
        }
        String bersih = teks.trim().replace("Rp", "").replace(" ", "");
        if (bersih.contains(",")) {
            // format Indonesia: titik = ribuan, koma = desimal (1.500.000,50)
            bersih = bersih.replace(".", "").replace(",", ".");
        } else if (!bersih.matches("-?\\d+\\.\\d{1,2}")) {
            // titik sebagai pemisah ribuan (12.500.000); "1500000.50" tetap dianggap desimal
            bersih = bersih.replace(".", "");
        }
        try {
            return angkaTidakNegatif(Double.parseDouble(bersih), namaField);
        } catch (NumberFormatException e) {
            throw new ValidasiException(namaField, namaField + " harus berupa angka, contoh: 12500000.");
        }
    }
}
