package testutils;

import entity.User;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.jspecify.annotations.NonNull;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.HashMap;
import java.util.Map;

public class HibernateUtilForTests {

    private static volatile SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory(PostgreSQLContainer<?> postgres) {
        if (sessionFactory == null) {
            try {
                Map<String, Object> settings = getStringObjectMap(postgres);

                StandardServiceRegistry standardServiceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(settings)
                        .build();

                Metadata metadata = new MetadataSources(standardServiceRegistry)
                        .addAnnotatedClass(User.class)
                        .buildMetadata();

                sessionFactory = metadata.buildSessionFactory();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sessionFactory;
    }

    private static @NonNull Map<String, Object> getStringObjectMap(PostgreSQLContainer<?> postgres) {
        Map<String, Object> settings = new HashMap<>();
        settings.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        settings.put("hibernate.connection.url", postgres.getJdbcUrl());
        settings.put("hibernate.connection.username", postgres.getUsername());
        settings.put("hibernate.connection.password", postgres.getPassword());
        settings.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        settings.put("hibernate.show_sql", true);
        settings.put("hibernate.hbm2ddl.auto", "create-drop");
        return settings;
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}