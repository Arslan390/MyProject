package dao;

import entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import testutils.HibernateUtilForTests;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserDaoIntegrationTests {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test_name")
            .withPassword("test_pas");

    private UserDao<User, Long> userDao;

    @BeforeEach
    public void setup() throws Exception {
        var sessionFactory = HibernateUtilForTests.getSessionFactory(postgres);

        try {
            var hibernateUtilClass = Class.forName("utils.HibernateUtil");
            var sessionFactoryField = hibernateUtilClass.getDeclaredField("sessionFactory");
            sessionFactoryField.setAccessible(true);  // Открываем доступ к закрытому полю
            sessionFactoryField.set(null, sessionFactory);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        userDao = new UserDaoImpl();
    }

    @BeforeAll
    static void setUpAll() {
        postgres.start();
    }

    @AfterEach
    public void teardown() {
        try (var sessionFactory = HibernateUtilForTests.getSessionFactory(postgres).openSession()) {
            var transaction = sessionFactory.beginTransaction();
            sessionFactory.createMutationQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        } catch (Exception ex) {
            log.error(String.valueOf(ex));
        }
    }

    @AfterAll
    static void tearDownAll() {
        HibernateUtilForTests.closeSessionFactory();
        if (postgres.isRunning()) {
            postgres.stop();
        }
    }


    @Test
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
    @DisplayName("findById: возвращает пустое значение для несуществующего ID")
    void findById_shouldReturnEmptyForNonexistentID() {
        Optional<User> nonExistentUser = userDao.findById(-1L);

        assertFalse(nonExistentUser.isPresent(), "Нет пользователя с указанным ID");
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


    @Test
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