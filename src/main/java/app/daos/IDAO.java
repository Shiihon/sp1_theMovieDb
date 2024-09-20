package app.daos;

import java.util.Set;

public interface IDAO<T> {
    T getById(Integer id);

    Set<T> getAll();

    T create(T t);

    T update(T t);

    void delete(Integer id);
}
