package dao;

import entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import testutils.HibernateUtilForTests;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class UserDaoIntegrationTests {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    private UserDao<User, Long> userDao;

    @BeforeEach
    public void setup() throws Exception {
        HibernateUtilForTests.resetSessionFactory();

        String jdbcUrl = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();
        System.out.println("JDBC URL: " + jdbcUrl); // Отладочный вывод
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        HibernateUtilForTests.getSessionFactory(jdbcUrl, username, password);

        userDao = new UserDaoImpl();
    }

    @AfterEach
    public void teardown() {
        HibernateUtilForTests.resetSessionFactory();
    }

    @Test
    @DisplayName("saveUser: сохраняет пользователя в БД")
    void saveUser_shouldPersistUser() {
        User user = User.builder()
                .username("Arslan")
                .email("ismailov@yandex.ru")
                .age(30)
                .build();

        userDao.create(user);

        Optional<User> foundUser = userDao.findById(user.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Arslan", foundUser.get().getUsername());
    }

}