package dao;

import java.util.List;
import java.util.Optional;

public interface UserDao<T, ID> {
    List<T> findAll();
    Optional<T> findById(ID id);
    boolean create(T entity);
    boolean update(T entity);
    boolean delete(ID id);
}
