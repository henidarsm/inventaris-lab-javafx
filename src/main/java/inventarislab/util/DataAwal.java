package inventarislab.util;

import java.time.LocalDate;

import inventarislab.dao.Dao;
import inventarislab.exception.DataAksesException;
import inventarislab.exception.ValidasiException;
import inventarislab.model.Aset;
import inventarislab.model.Furnitur;
import inventarislab.model.Komputer;
import inventarislab.model.Kondisi;
import inventarislab.model.Pengguna;
import inventarislab.model.PerangkatEmbedded;
import inventarislab.model.PerangkatJaringan;
import inventarislab.model.Peran;
import inventarislab.model.Ruangan;

/**
 * Mengisi data contoh saat aplikasi pertama kali dijalankan (file data masih kosong).
 * Ruangan mengikuti laboratorium Teknik Komputer: Multimedia, Embedded, Jaringan, Software.
 */
public final class DataAwal {

    private DataAwal() {
    }

    public static void isiJikaKosong(Dao<Pengguna> penggunaDao, Dao<Ruangan> ruanganDao, Dao<Aset> asetDao)
            throws DataAksesException, ValidasiException {
        if (penggunaDao.findAll().isEmpty()) {
            penggunaDao.insert(Pengguna.baru("admin", "admin123", "Administrator Lab", Peran.ADMIN));
            penggunaDao.insert(Pengguna.baru("laboran", "laboran123", "Laboran Teknik Komputer", Peran.LABORAN));
        }
        if (ruanganDao.findAll().isEmpty()) {
            ruanganDao.insert(new Ruangan("LAB-01", "Lab Multimedia", "Gedung TK Lt. 2", 35, true, "Dr. Hendra Wijaya"));
            ruanganDao.insert(new Ruangan("LAB-02", "Lab Embedded", "Gedung TK Lt. 2", 30, true, "Rina Oktaviani, M.T."));
            ruanganDao.insert(new Ruangan("LAB-03", "Lab Jaringan", "Gedung TK Lt. 3", 35, true, "Agus Pratama, M.Kom."));
            ruanganDao.insert(new Ruangan("LAB-04", "Lab Software", "Gedung TK Lt. 3", 40, true, "Sari Lestari, M.Kom."));
        }
        if (asetDao.findAll().isEmpty()) {
            LocalDate h = LocalDate.now();
            asetDao.insert(new Komputer("KOM-001", "PC Editing Video & Desain", "Lenovo", h.minusYears(3).minusMonths(2),
                    18_500_000, Kondisi.BAIK, "LAB-01", "Intel Core i7-12700", 32, h.minusMonths(8)));
            asetDao.insert(new Komputer("KOM-002", "PC Praktikum Pemrograman", "HP", h.minusYears(2),
                    9_800_000, Kondisi.BAIK, "LAB-04", "Intel Core i5-12400", 16, h.minusMonths(3)));
            asetDao.insert(new Komputer("KOM-003", "PC Pemrograman Mikrokontroler", "Asus", h.minusYears(5),
                    7_200_000, Kondisi.RUSAK_RINGAN, "LAB-02", "Intel Core i3-10100", 8, h.minusMonths(10)));
            asetDao.insert(new PerangkatJaringan("JAR-001", "Managed Switch 24 Port", "Cisco", h.minusYears(2).minusMonths(5),
                    12_000_000, Kondisi.BAIK, "LAB-03", 24, h.minusMonths(14)));
            asetDao.insert(new PerangkatJaringan("JAR-002", "Router MikroTik RB4011", "MikroTik", h.minusYears(1),
                    4_500_000, Kondisi.BAIK, "LAB-03", 10, h.minusMonths(2)));
            asetDao.insert(new Furnitur("FUR-001", "Meja Komputer Praktikum", "Krisbow", h.minusYears(4),
                    3_500_000, Kondisi.BAIK, "LAB-04", "Besi & HPL"));
            asetDao.insert(new Furnitur("FUR-002", "Lemari Penyimpanan Kit", "Lion", h.minusYears(6),
                    2_800_000, Kondisi.RUSAK_RINGAN, "LAB-02", "Besi"));
            asetDao.insert(new Furnitur("FUR-003", "Kursi Lab", "Chitose", h.minusYears(1).minusMonths(6),
                    650_000, Kondisi.BAIK, "LAB-01", "Plastik & Besi"));
            asetDao.insert(new PerangkatEmbedded("EMB-001", "Raspberry Pi 4 Model B 8GB", "Raspberry Pi", h.minusYears(2),
                    1_850_000, Kondisi.BAIK, "LAB-02", "ARM Cortex-A72 4 inti", 6, h.minusMonths(7)));
            asetDao.insert(new PerangkatEmbedded("EMB-002", "STM32 Nucleo-F446RE", "STMicroelectronics", h.minusYears(3),
                    450_000, Kondisi.BAIK, "LAB-02", "ARM Cortex-M4 180 MHz", 12, h.minusMonths(4)));
            asetDao.insert(new PerangkatEmbedded("EMB-003", "Board FPGA Basys 3", "Digilent", h.minusYears(4),
                    3_200_000, Kondisi.RUSAK_RINGAN, "LAB-02", "Xilinx Artix-7", 12, h.minusMonths(15)));
            asetDao.insert(new PerangkatEmbedded("EMB-004", "Arduino Mega 2560 Starter Kit", "Arduino", h.minusMonths(10),
                    650_000, Kondisi.BAIK, "LAB-02", "ATmega2560 16 MHz", 6, h.minusMonths(10)));
        }
    }
}
