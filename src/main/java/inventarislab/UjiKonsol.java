package inventarislab;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import inventarislab.exception.AutentikasiException;
import inventarislab.exception.DataAksesException;
import inventarislab.exception.ValidasiException;
import inventarislab.model.Aset;
import inventarislab.model.Furnitur;
import inventarislab.model.Kondisi;
import inventarislab.model.Perawatan;
import inventarislab.service.InventarisService;
import inventarislab.util.Format;

/**
 * Pengujian lapisan model + DAO lewat konsol (tanpa GUI), untuk checkpoint Progres 1.
 * Memakai folder data sementara agar data asli tidak berubah.
 */
public class UjiKonsol {

    public static void main(String[] args) throws Exception {
        Path folder = Files.createTempDirectory("uji-inventaris");
        AppContext ctx = new AppContext(folder);
        InventarisService svc = ctx.getInventarisService();

        System.out.println("=== 1. Polimorfisme: hitungNilaiBuku() lewat referensi Aset ===");
        List<Aset> semua = svc.semuaAset();
        for (Aset a : semua) {
            System.out.printf("%-8s %-20s %-38s perolehan %-15s nilai buku %s%n", a.getId(), a.getJenis(),
                    a.getMetodePenyusutan(), Format.rupiah(a.getHargaPerolehan()), Format.rupiah(a.hitungNilaiBuku()));
        }

        System.out.println("\n=== 2. Interface Perawatan (hanya Komputer, Jaringan, Embedded) ===");
        for (Perawatan p : svc.asetPerluPerawatan()) {
            System.out.println("PERLU " + p.getJenisPerawatan() + " -> " + ((Aset) p).getNama()
                    + " (jadwal " + Format.tanggal(p.getJadwalPerawatanBerikutnya()) + ")");
        }

        System.out.println("\n=== 3. CRUD + persistensi file ===");
        String id = svc.buatIdBaru(inventarislab.model.JenisAset.FURNITUR);
        svc.tambahAset(new Furnitur(id, "Papan Tulis Kaca", "Sakana", LocalDate.now().minusMonths(1),
                1_200_000, Kondisi.BAIK, "LAB-01", "Kaca"));
        System.out.println("Tambah " + id + " -> jumlah aset: " + svc.semuaAset().size());
        AppContext ctxBaru = new AppContext(folder); // baca ulang dari file
        System.out.println("Setelah dibaca ulang dari file: " + ctxBaru.getInventarisService().semuaAset().size());
        svc.hapusAset(id);
        System.out.println("Hapus " + id + " -> jumlah aset: " + svc.semuaAset().size());

        System.out.println("\n=== 4. Validasi & custom exception ===");
        try {
            new Furnitur("FUR-999", "Kursi", "X", LocalDate.now(), -5000, Kondisi.BAIK, "LAB-01", "Kayu");
        } catch (ValidasiException e) {
            System.out.println("ValidasiException: " + e.getMessage());
        }
        try {
            ctx.getRuanganService().hapus("LAB-01");
        } catch (ValidasiException e) {
            System.out.println("ValidasiException: " + e.getMessage());
        }
        try {
            ctx.getAuthService().login("admin", "salah".toCharArray());
        } catch (AutentikasiException e) {
            System.out.println("AutentikasiException: " + e.getMessage());
        }
        try {
            svc.hapusAset("TIDAK-ADA");
        } catch (DataAksesException e) {
            System.out.println("DataAksesException: " + e.getMessage());
        }
        System.out.println("\nLogin admin: " + ctx.getAuthService().login("admin", "admin123".toCharArray()).getNamaLengkap());
        System.out.println("Folder data uji: " + folder);
    }
}
