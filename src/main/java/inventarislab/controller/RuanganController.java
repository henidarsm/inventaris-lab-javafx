package inventarislab.controller;

import inventarislab.AppContext;
import inventarislab.exception.AutentikasiException;
import inventarislab.exception.DataAksesException;
import inventarislab.exception.ValidasiException;
import inventarislab.model.Ruangan;
import inventarislab.service.RuanganService;
import inventarislab.view.MainView;
import inventarislab.view.RuanganFormDialog;
import inventarislab.view.RuanganView;
import inventarislab.view.Ui;
import javafx.scene.control.TableRow;

/**
 * Controller CRUD ruangan.
 */
public class RuanganController {

    private final AppContext ctx;
    private final MainView jendela;
    private final RuanganView view;
    private final RuanganService service;
    private final Runnable saatDataBerubah;

    public RuanganController(AppContext ctx, MainView jendela, RuanganView view, Runnable saatDataBerubah) {
        this.ctx = ctx;
        this.jendela = jendela;
        this.view = view;
        this.service = ctx.getRuanganService();
        this.saatDataBerubah = saatDataBerubah;

        // pencarian langsung menyaring setiap kali teks berubah
        view.getTxtCari().textProperty().addListener((obs, lama, baru) -> muatData());
        view.getTxtCari().setOnAction(e -> muatData());
        view.getBtnReset().setOnAction(e -> {
            view.resetFilter();
            muatData();
        });
        view.getBtnTambah().setOnAction(e -> tambah());
        view.getBtnUbah().setOnAction(e -> ubah());
        view.getBtnHapus().setOnAction(e -> hapus());
        view.getTabel().setRowFactory(tabel -> {
            TableRow<Ruangan> baris = new TableRow<>();
            baris.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !baris.isEmpty()) {
                    ubah();
                }
            });
            return baris;
        });
    }

    public void muatData() {
        view.setData(service.cari(view.getKataKunci()), service::jumlahAset);
    }

    public void tambah() {
        RuanganFormDialog dialog = new RuanganFormDialog(jendela, null);
        dialog.showAndWait();
        Ruangan baru = dialog.getHasil();
        if (baru == null) {
            return;
        }
        try {
            service.tambah(baru);
            selesai("Ruangan " + baru.getKode() + " berhasil ditambahkan.");
        } catch (ValidasiException | DataAksesException ex) {
            Ui.error(jendela, ex.getMessage());
        }
    }

    public void ubah() {
        Ruangan terpilih = wajibPilih();
        if (terpilih == null) {
            return;
        }
        RuanganFormDialog dialog = new RuanganFormDialog(jendela, terpilih);
        dialog.showAndWait();
        Ruangan hasil = dialog.getHasil();
        if (hasil == null) {
            return;
        }
        try {
            service.ubah(hasil);
            selesai("Data ruangan " + hasil.getKode() + " berhasil diperbarui.");
        } catch (DataAksesException ex) {
            Ui.error(jendela, ex.getMessage());
        }
    }

    public void hapus() {
        Ruangan terpilih = wajibPilih();
        if (terpilih == null) {
            return;
        }
        try {
            ctx.getAuthService().wajibAdmin("menghapus ruangan");
        } catch (AutentikasiException ex) {
            Ui.peringatan(jendela, ex.getMessage());
            return;
        }
        if (!Ui.konfirmasi(jendela, "Konfirmasi Hapus", "Hapus ruangan ini?",
                terpilih.getKode() + " - " + terpilih.getNama() + "\n\nTindakan ini tidak dapat dibatalkan.")) {
            return;
        }
        try {
            service.hapus(terpilih.getKode());
            selesai("Ruangan " + terpilih.getKode() + " berhasil dihapus.");
        } catch (ValidasiException | DataAksesException ex) {
            Ui.error(jendela, ex.getMessage());
        }
    }

    private Ruangan wajibPilih() {
        Ruangan r = view.getRuanganTerpilih();
        if (r == null) {
            Ui.peringatan(jendela, "Pilih salah satu baris ruangan di tabel terlebih dahulu.");
        }
        return r;
    }

    private void selesai(String pesan) {
        muatData();
        saatDataBerubah.run();
        Ui.info(jendela, pesan);
    }
}
