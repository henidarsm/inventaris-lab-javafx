package inventarislab.controller;

import java.util.List;

import inventarislab.AppContext;
import inventarislab.model.Perawatan;
import inventarislab.service.InventarisService;
import inventarislab.view.DashboardView;

/**
 * Mengisi DashboardView dengan statistik dari InventarisService.
 */
public class DashboardController {

    private final AppContext ctx;
    private final DashboardView view;
    private final InventarisService service;

    public DashboardController(AppContext ctx, DashboardView view) {
        this.ctx = ctx;
        this.view = view;
        this.service = ctx.getInventarisService();
        view.getBtnMuatUlang().setOnAction(e -> muatData());
    }

    public void muatData() {
        if (ctx.getAuthService().getPenggunaAktif() != null) {
            view.setSapaan("Selamat datang kembali, " + ctx.getAuthService().getPenggunaAktif().getNamaLengkap());
        }
        List<Perawatan> perluPerawatan = service.asetPerluPerawatan();
        view.setStatistik(service.semuaAset().size(), service.totalNilaiPerolehan(),
                service.totalNilaiBuku(), perluPerawatan.size());
        view.setDataPerJenis(service.jumlahPerJenis(), service.nilaiBukuPerJenis());
        view.setDataPerawatan(perluPerawatan);
    }
}
