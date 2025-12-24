package service;

import dao.UserDao;
import dao.UserDaoImpl;
import entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Scanner;

@Slf4j
public class UserService {
    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDaoImpl();
    }

    public void getAllUsers() {
        List<User> users = userDao.findAll();
        if (!users.isEmpty()) {
            for (User user : users) {
                log.info(user.toString());
            }
        }  else {
            log.warn("Нет зарегистрированных пользователей.");
        }
    }

    public void getUserById(Scanner scanner) {
        System.out.println("ID пользователя: ");
        long id = Long.parseLong(scanner.nextLine());
        User user = userDao.findById(id);
        if (user != null) {
            log.info(user.toString());
        } else {
            log.warn("Пользователь с таким ID не найден.");
        }
    }

    public void createUser(Scanner scanner) {
        System.out.println("Имя: ");
        String name = scanner.nextLine();
        System.out.println("E-mail: ");
        String email = scanner.nextLine();
        System.out.println("Возраст: ");
        int age = Integer.parseInt(scanner.nextLine());
        User user = new User(name, email, age);
        userDao.save(user);
        log.info("Пользователь успешно создан.");
    }

    public void updateUser(Scanner scanner) {
        System.out.println("ID пользователя для обновления: ");
        long id = Long.parseLong(scanner.nextLine());
        User existingUser = userDao.findById(id);
        if (existingUser != null) {
            System.out.println("Новое имя: ");
            String name = scanner.nextLine();
            System.out.println("Новый e-mail: ");
            String email = scanner.nextLine();
            System.out.println("Новый возраст: ");
            int age = Integer.parseInt(scanner.nextLine());
            existingUser.setName(name);
            existingUser.setEmail(email);
            existingUser.setAge(age);
            userDao.update(existingUser);
            log.info("Данные обновлены.");
        } else {
            log.warn("Пользователь с указанным ID не существует.");
        }
    }

    public void deleteUser(Scanner scanner) {
        System.out.println("ID пользователя для удаления: ");
        long id = Long.parseLong(scanner.nextLine());
        User user = userDao.findById(id);
        if (user != null) {
            userDao.delete(user);
            log.info("Пользователь успешно удалён.");
        } else {
            log.warn("Пользователь с данным ID не найден.");
        }
    }
}
