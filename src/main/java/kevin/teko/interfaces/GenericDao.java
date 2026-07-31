package kevin.teko.interfaces;

import java.util.List;
import java.util.Optional;

// T = Typ der Entität (z.B. EBook), ID = Typ des Primärschlüssels (z.B. Long)
public interface GenericDao<T, ID> {
    void save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
}
