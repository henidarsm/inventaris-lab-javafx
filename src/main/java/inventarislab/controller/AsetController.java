package inventarislab.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import inventarislab.AppContext;
import inventarislab.exception.AutentikasiException;
import inventarislab.exception.DataAksesException;
import inventarislab.exception.ValidasiException;
import inventarislab.model.Aset;
import inventarislab.model.Perawatan;
import inventarislab.service.InventarisService;
import inventarislab.util.Format;
import inventarislab.view.AsetFormDialog;
import inventarislab.view.AsetView;
import inventarislab.view.MainView;
import inventarislab.view.Ui;
import javafx.scene.control.TableRow;

/**
 * Controller CRUD aset. Tiap tombol diarahkan ke method terpisah lewat lambda.
 */
public class AsetController {

    private final AppContext ctx;
    private final MainView jendela;
    private final AsetView view;
    private final InventarisService service;
    private final Runnable saatDataBerubah;

    public AsetController(AppContext ctx, MainView jendela, AsetView view, Runnable saatDataBerubah) {
        this.ctx = ctx;
        this.jendela = jendela;
        this.view = view;
        this.service = ctx.getInventarisService();
        this.saatDataBerubah = saatDataBerubah;
        pasangEvent();
    }

    private void pasangEvent() {
        // pencarian langsung menyaring setiap kali teks berubah
        view.getTxtCari().textProperty().addListener((obs, lama, baru) -> muatData());
        view.getTxtCari().setOnAction(e -> muatData()); // Enter di kotak cari
        view.getCmbFilterJenis().setOnAction(e -> muatData());
        view.getChkPerluPerawatan().setOnAction(e -> muatData());
        view.getBtnReset().setOnAction(e -> {
            view.resetFilter();
            muatData();
        });
        view.getBtnTambah().setOnAction(e -> tambah());
        view.getBtnUbah().setOnAction(e -> ubah());
        view.getBtnHapus().setOnAction(e -> hapus());
        view.getBtnDetail().setOnAction(e -> detail());
        // klik dua kali pada baris = ubah
        view.getTabel().setRowFactory(tabel -> {
            TableRow<Aset> baris = new TableRow<>();
            baris.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !baris.isEmpty()) {
                    ubah();
                }
            });
            return baris;
        });
    }

    public void muatData() {
        view.setData(service.cari(view.getKataKunci(), view.getFilterJenis(), view.isHanyaPerluPerawatan()));
    }

    public void tambah() {
        AsetFormDialog dialog = new AsetFormDialog(jendela, ctx.getRuanganService().semuaRuangan(), null,
                service::buatIdBaru);
        dialog.showAndWait();
        Aset baru = dialog.getHasil();
        if (baru == null) {
            return; // dibatalkan
        }
        try {
            service.tambahAset(baru);
            selesai("Aset " + baru.getId() + " - " + baru.getNama() + " berhasil ditambahkan.");
        } catch (ValidasiException | DataAksesException ex) {
            Ui.error(jendela, ex.getMessage());
        }
    }

    public void ubah() {
        Aset terpilih = wajibPilih();
        if (terpilih == null) {
            return;
        }
        AsetFormDialog dialog = new AsetFormDialog(jendela, ctx.getRuanganService().semuaRuangan(), terpilih,
                service::buatIdBaru);
        dialog.showAndWait();
        Aset hasil = dialog.getHasil();
        if (hasil == null) {
            return;
        }
        try {
            service.ubahAset(hasil);
            selesai("Data aset " + hasil.getId() + " berhasil diperbarui.");
        } catch (ValidasiException | DataAksesException ex) {
            Ui.error(jendela, ex.getMessage());
        }
    }

    public void hapus() {
        Aset terpilih = wajibPilih();
        if (terpilih == null) {
            return;
        }
        try {
            ctx.getAuthService().wajibAdmin("menghapus aset");
        } catch (AutentikasiException ex) {
            Ui.peringatan(jendela, ex.getMessage());
            return;
        }
        boolean yakin = Ui.konfirmasi(jendela, "Konfirmasi Hapus", "Hapus aset ini secara permanen?",
                terpilih.getId() + " - " + terpilih.getNama() + "\nRuangan: " + terpilih.getKodeRuangan()
                        + "\n\nTindakan ini tidak dapat dibatalkan.");
        if (!yakin) {
            return;
        }
        try {
            service.hapusAset(terpilih.getId());
            selesai("Aset " + terpilih.getId() + " berhasil dihapus.");
        } catch (DataAksesException ex) {
            Ui.error(jendela, ex.getMessage());
        }
    }

    public void detail() {
        Aset a = wajibPilih();
        if (a == null) {
            return;
        }
        List<String[]> baris = new ArrayList<>();
        baris.add(new String[] {"ID", a.getId()});
        baris.add(new String[] {"Jenis", a.getJenis().toString()});
        baris.add(new String[] {"Spesifikasi", a.getSpesifikasi()});
        baris.add(new String[] {"Merk", a.getMerk().isEmpty() ? "-" : a.getMerk()});
        baris.add(new String[] {"Ruangan", a.getKodeRuangan()});
        baris.add(new String[] {"Kondisi", a.getKondisi().toString()});
        baris.add(new String[] {"Penyusutan"});
        baris.add(new String[] {"Tanggal perolehan", Format.tanggal(a.getTanggalPerolehan())});
        baris.add(new String[] {"Harga perolehan", Format.rupiah(a.getHargaPerolehan())});
        baris.add(new String[] {"Metode", a.getMetodePenyusutan()});
        baris.add(new String[] {"Nilai buku hari ini", Format.rupiah(a.hitungNilaiBuku()), "judul-bagian"});
        baris.add(new String[] {"Perawatan"});
        if (a instanceof Perawatan) {
            Perawatan p = (Perawatan) a;
            baris.add(new String[] {p.getJenisPerawatan() + " terakhir", Format.tanggal(p.getTanggalPerawatanTerakhir())});
            boolean lewat = p.perluPerawatan(LocalDate.now());
            baris.add(new String[] {"Jadwal berikutnya",
                Format.tanggal(p.getJadwalPerawatanBerikutnya()) + (lewat ? "  (TERLEWAT)" : ""),
                lewat ? "teks-error" : "teks-sukses"});
        } else {
            baris.add(new String[] {"Jadwal", "Tidak memerlukan perawatan berkala"});
        }
        Ui.detail(jendela, "Detail Aset", a.getNama(), baris);
    }

    private Aset wajibPilih() {
        Aset a = view.getAsetTerpilih();
        if (a == null) {
            Ui.peringatan(jendela, "Pilih salah satu baris aset di tabel terlebih dahulu.");
        }
        return a;
    }

    private void selesai(String pesan) {
        muatData();
        saatDataBerubah.run();
        Ui.info(jendela, pesan);
    }
}
