package service;

import dao.UserDao;
import dao.UserDaoImpl;
import entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserService {
    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDaoImpl();
    }

    public void getAllUsers() {
        List<User> users = userDao.findAll();
        if (!users.isEmpty()) {
            for (User user : users) {
                System.out.println(user);
            }
        }  else {
            System.out.println("Нет зарегистрированных пользователей.");
        }
    }

    public void getUserById(Scanner scanner) {
        System.out.print("ID пользователя: ");
        long id = Long.parseLong(scanner.nextLine());
        User user = userDao.findById(id);
        if (user != null) {
            System.out.println(user);
        } else {
            System.out.println("Пользователь с таким ID не найден.");
        }
    }

    public void createUser(Scanner scanner) {
        System.out.print("Имя: ");
        String name = scanner.nextLine();
        System.out.print("E-mail: ");
        String email = scanner.nextLine();
        System.out.print("Возраст: ");
        int age = Integer.parseInt(scanner.nextLine());
        User user = new User(name, email, age);
        userDao.save(user);
        System.out.println("Пользователь успешно создан.");
    }

    public void updateUser(Scanner scanner) {
        System.out.print("ID пользователя для обновления: ");
        long id = Long.parseLong(scanner.nextLine());
        User existingUser = userDao.findById(id);
        if (existingUser != null) {
            System.out.print("Новое имя: ");
            String name = scanner.nextLine();
            System.out.print("Новый e-mail: ");
            String email = scanner.nextLine();
            System.out.print("Новый возраст: ");
            int age = Integer.parseInt(scanner.nextLine());
            existingUser.setName(name);
            existingUser.setEmail(email);
            existingUser.setAge(age);
            userDao.update(existingUser);
            System.out.println("Данные обновлены.");
        } else {
            System.out.println("Пользователь с указанным ID не существует.");
        }
    }

    public void deleteUser(Scanner scanner) {
        System.out.print("ID пользователя для удаления: ");
        long id = Long.parseLong(scanner.nextLine());
        User user = userDao.findById(id);
        if (user != null) {
            userDao.delete(id);
            System.out.println("Пользователь успешно удалён.");
        } else {
            System.out.println("Пользователь с данным ID не найден.");
        }
    }
}
