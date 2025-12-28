package dao;

import entity.User;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import testutils.HibernateUtilForTests;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoIntegrationTests {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
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
    @Order(1)
    @DisplayName("findAll: возвращает список всех пользователей")
    void findAll_shouldReturnListOfUsers() {
        User user1 = User.builder()
                .username("Arslan")
                .email("ismailov05@yandex.ru")
                .age(28)
                .build();

        User user2 = User.builder()
                .username("Iba")
                .email("ibragim@mail.com")
                .age(35)
                .build();

        userDao.create(user1);
        userDao.create(user2);

        List<User> users = userDao.findAll();
        assertEquals(2, users.size());
    }

    @Test
    @Order(2)
    @DisplayName("findById: возвращает пользователя по существующему ID")
    void findById_shouldFindUserByValidID() {
        User expectedUser = User.builder()
                .username("Arslan")
                .email("ismailov95@yandex.ru")
                .age(25)
                .build();

        userDao.create(expectedUser);

        Optional<User> actualUser = userDao.findById(expectedUser.getId());

        assertTrue(actualUser.isPresent(), "Пользователь должен существовать");
        assertEquals(expectedUser.getUsername(), actualUser.get().getUsername(), "Имя пользователя должно совпасть");
        assertEquals(expectedUser.getEmail(), actualUser.get().getEmail(), "Email пользователя должен совпасть");
        assertEquals(expectedUser.getAge(), actualUser.get().getAge(), "Возраст пользователя должен совпасть");
    }

    @Test
    @Order(3)
    @DisplayName("findById: возвращает пустое значение для несуществующего ID")
    void findById_shouldReturnEmptyForNonexistentID() {
        Optional<User> nonExistentUser = userDao.findById(-1L);

        assertFalse(nonExistentUser.isPresent(), "Нет пользователя с указанным ID");
    }

    @Test
    @Order(4)
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


    @Test
    @Order(5)
    @DisplayName("update: успешно обновляет пользователя")
    void update_shouldUpdateExistingUser() {
        User initialUser = User.builder()
                .username("Aslllan")
                .email("ismailodfsdv@yandex.ru")
                .age(30)
                .build();

        userDao.create(initialUser);

        long userId = initialUser.getId();

        User updatedUser = User.builder()
                .id(userId)
                .username("Arslanbek")
                .email("ismailov105@yandex.ru")
                .age(31)
                .build();

        userDao.update(updatedUser);

        Optional<User> retrievedUser = userDao.findById(userId);
        assertTrue(retrievedUser.isPresent());
        assertEquals("Arslanbek", retrievedUser.get().getUsername());
        assertEquals("ismailov105@yandex.ru", retrievedUser.get().getEmail());
        assertEquals(31, retrievedUser.get().getAge());
    }

    @Test
    @Order(6)
    @DisplayName("delete: успешно удаляет пользователя по ID")
    void delete_shouldRemoveUserByID() {
        User userToDelete = User.builder()
                .username("Arslan")
                .email("ismailov390@yandex.ru")
                .age(40)
                .build();

        userDao.create(userToDelete);

        long userId = userToDelete.getId();

        boolean deleted = userDao.delete(userId);
        assertTrue(deleted);

        Optional<User> retrievedUser = userDao.findById(userId);
        assertFalse(retrievedUser.isPresent());
    }
}