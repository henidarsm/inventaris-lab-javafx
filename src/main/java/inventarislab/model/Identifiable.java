package inventarislab.model;

/**
 * Interface untuk semua entitas yang punya kunci unik.
 * Diimplementasikan oleh Aset, Ruangan, dan Pengguna sehingga DAO generik
 * {@code Dao<T extends Identifiable>} bisa menyimpan entitas apa pun.
 */
public interface Identifiable {

    String getId();
}
