package testutils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.HashMap;
import java.util.Map;

public class HibernateUtilForTests {

    private static volatile SessionFactory sessionFactory;


    public static synchronized void resetSessionFactory() {
        if (sessionFactory != null && sessionFactory.isOpen()) {
            sessionFactory.close();
        }
        sessionFactory = null;
    }

    public static synchronized SessionFactory getSessionFactory(String dbUrl, String dbUsername, String dbPassword) {
        if (sessionFactory == null || !sessionFactory.isOpen()) {
            BootstrapServiceRegistry bootstrapServiceRegistry = new BootstrapServiceRegistryBuilder()
                    .build();

            StandardServiceRegistry standardServiceRegistry = new StandardServiceRegistryBuilder(bootstrapServiceRegistry)
                    .applySettings(getHibernateProperties(dbUrl, dbUsername, dbPassword))
                    .build();

            Metadata metadata = new MetadataSources(standardServiceRegistry)
                    .addAnnotatedClass(entity.User.class) // Зарегистрируйте остальные модели, если нужно
                    .buildMetadata();

            sessionFactory = metadata.buildSessionFactory();
        }
        return sessionFactory;
    }

    private static Map<String, Object> getHibernateProperties(String dbUrl, String dbUsername, String dbPassword) {
        Map<String, Object> settings = new HashMap<>();
        settings.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        settings.put("hibernate.connection.url", dbUrl);
        settings.put("hibernate.connection.username", dbUsername);
        settings.put("hibernate.connection.password", dbPassword);
        settings.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        settings.put("hibernate.show_sql", true);
        settings.put("hibernate.hbm2ddl.auto", "create-drop");
        return settings;
    }
}