package inventarislab.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import inventarislab.exception.DataAksesException;
import inventarislab.exception.ValidasiException;
import inventarislab.model.Identifiable;

/**
 * DAO abstrak berbasis file teks (format CSV dengan pemisah ';').
 * Data disimpan di Map<String, T> (key = ID) dan ditulis ulang ke file setiap ada perubahan,
 * sehingga data tetap ada walau aplikasi ditutup.
 *
 * Kelas turunan cukup menentukan cara mengubah objek <-> satu baris teks.
 */
public abstract class AbstractFileDao<T extends Identifiable> implements Dao<T> {

    protected static final String PEMISAH = ";";

    private final Path file;
    private final Map<String, T> data = new LinkedHashMap<>();

    protected AbstractFileDao(Path file) throws DataAksesException {
        this.file = file;
        muat();
    }

    /** Header kolom yang ditulis di baris pertama file. */
    protected abstract String getHeader();

    protected abstract String keBaris(T entitas);

    protected abstract T dariBaris(String[] kolom) throws ValidasiException;

    // ------------------------------------------------------------- I/O file

    private void muat() throws DataAksesException {
        data.clear();
        if (!Files.exists(file)) {
            return;
        }
        int nomorBaris = 0;
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String baris;
            while ((baris = reader.readLine()) != null) {
                nomorBaris++;
                if (nomorBaris == 1 || baris.trim().isEmpty()) {
                    continue; // lewati header & baris kosong
                }
                T entitas = dariBaris(baris.split(PEMISAH, -1));
                data.put(entitas.getId(), entitas);
            }
        } catch (IOException e) {
            throw new DataAksesException("Gagal membaca file " + file.getFileName() + ": " + e.getMessage(), e);
        } catch (ValidasiException | ArrayIndexOutOfBoundsException | IllegalArgumentException
                 | java.time.format.DateTimeParseException e) {
            throw new DataAksesException("Data rusak di " + file.getFileName() + " baris " + nomorBaris
                    + ": " + e.getMessage(), e);
        }
    }

    /** Tulis ke file sementara dulu lalu ganti file asli, agar file tidak korup bila gagal di tengah. */
    private void simpan() throws DataAksesException {
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            Path sementara = file.resolveSibling(file.getFileName() + ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(sementara, StandardCharsets.UTF_8)) {
                writer.write(getHeader());
                writer.newLine();
                for (T entitas : data.values()) {
                    writer.write(keBaris(entitas));
                    writer.newLine();
                }
            }
            Files.move(sementara, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new DataAksesException("Gagal menyimpan file " + file.getFileName() + ": " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------ CRUD

    @Override
    public List<T> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<T> findById(String id) {
        return id == null ? Optional.empty() : Optional.ofNullable(data.get(id.toUpperCase()))
                .or(() -> Optional.ofNullable(data.get(id)));
    }

    @Override
    public void insert(T entitas) throws DataAksesException {
        if (data.containsKey(entitas.getId())) {
            throw new DataAksesException("Data dengan ID " + entitas.getId() + " sudah ada.");
        }
        data.put(entitas.getId(), entitas);
        try {
            simpan();
        } catch (DataAksesException e) {
            data.remove(entitas.getId()); // rollback di memori
            throw e;
        }
    }

    @Override
    public void update(T entitas) throws DataAksesException {
        if (!data.containsKey(entitas.getId())) {
            throw new DataAksesException("Data dengan ID " + entitas.getId() + " tidak ditemukan.");
        }
        T lama = data.put(entitas.getId(), entitas);
        try {
            simpan();
        } catch (DataAksesException e) {
            data.put(entitas.getId(), lama);
            throw e;
        }
    }

    @Override
    public void delete(String id) throws DataAksesException {
        T lama = data.remove(id);
        if (lama == null) {
            throw new DataAksesException("Data dengan ID " + id + " tidak ditemukan.");
        }
        try {
            simpan();
        } catch (DataAksesException e) {
            data.put(id, lama);
            throw e;
        }
    }

    public int jumlah() {
        return data.size();
    }
}
