package inventarislab.dao;

import java.util.List;
import java.util.Optional;

import inventarislab.exception.DataAksesException;
import inventarislab.model.Identifiable;

/**
 * Kontrak akses data generik (CRUD). Diimplementasikan oleh AsetDao,
 * RuanganDao, dan PenggunaDao melalui AbstractFileDao.
 *
 * @param <T> tipe entitas yang punya ID unik
 */
public interface Dao<T extends Identifiable> {

    List<T> findAll();

    Optional<T> findById(String id);

    void insert(T entitas) throws DataAksesException;

    void update(T entitas) throws DataAksesException;

    void delete(String id) throws DataAksesException;
}
