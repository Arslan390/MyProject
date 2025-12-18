package dao;

import entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.util.List;

public class UserDaoImpl implements UserDao {
    private static final SessionFactory factory;

    static {
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        factory = new MetadataSources(serviceRegistry).buildMetadata().buildSessionFactory();
    }

    @Override
    public List<User> findAll() {
        try (Session session = factory.openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    @Override
    public User findById(Long id) {
        try (Session session = factory.openSession()) {
            return session.find(User.class, id);
        }
    }

    @Override
    public void save(User user) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        }
    }

    @Override
    public void update(User user) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
        }
    }

    @Override
    public void delete(Long id) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            User userToDelete = session.find(User.class, id);
            if (userToDelete != null) {
                session.remove(userToDelete);
            }
            transaction.commit();
        }
    }
}
