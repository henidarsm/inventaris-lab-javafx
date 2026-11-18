package inventarislab.model;

import java.time.LocalDate;

/**
 * Interface untuk aset yang wajib dirawat/dicek berkala.
 * Diimplementasikan oleh Komputer, PerangkatJaringan, dan PerangkatEmbedded
 * (Furnitur sengaja TIDAK mengimplementasikan).
 */
public interface Perawatan {

    /** Jenis tindakan, mis. "Servis & update OS" atau "Uji fungsi & update firmware". */
    String getJenisPerawatan();

    int getIntervalPerawatanBulan();

    LocalDate getTanggalPerawatanTerakhir();

    default LocalDate getJadwalPerawatanBerikutnya() {
        return getTanggalPerawatanTerakhir().plusMonths(getIntervalPerawatanBulan());
    }

    default boolean perluPerawatan(LocalDate hariIni) {
        return !getJadwalPerawatanBerikutnya().isAfter(hariIni);
    }
}
