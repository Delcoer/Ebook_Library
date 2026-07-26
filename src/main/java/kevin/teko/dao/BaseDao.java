package kevin.teko.dao;

import java.util.List;

public interface BaseDao<T> {
    void save(T entity);
    T findById(int id);
    List<T> findAll();
    void update(T entity);
    void delete(int id);
}