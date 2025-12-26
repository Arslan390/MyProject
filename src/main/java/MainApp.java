import dao.UserDao;
import dao.UserDaoImpl;
import entity.User;
import service.UserMenuManager;
import service.UserService;
import utils.HibernateUtil;

import java.util.Scanner;

public class MainApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDao<User, Long> userDao = new UserDaoImpl();
    private static final UserService userService = new UserService(userDao);
    private static final UserMenuManager menuManager = new UserMenuManager(scanner, userService);

    public static void main(String[] args) {
        menuManager.run();
        HibernateUtil.shutdown();
    }
}