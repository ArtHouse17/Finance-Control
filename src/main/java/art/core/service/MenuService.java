package art.core.service;

import art.cli.ShowcaseService;
import art.infra.MemoryUserRepositoryImpl;

import java.util.Scanner;

//Реализовать возможность отображения общей суммы доходов и расходов, а также данных по каждой категории.
//Выводить информацию о текущем состоянии бюджета для каждой категории, а также оставшийся лимит.
//Поддерживать вывод информации в терминал или в файл.

// Вынести все handler'ы в отдельный класс.
public class MenuService {
    private final LoginService loginService;
    private final BalanceService balanceService;
    private final Scanner scanner;
    private final FileService fileService;
    private final ShowcaseService showcaseService;
    private final HandleService handleService;
    public MenuService() {
        this.loginService = new LoginService(new MemoryUserRepositoryImpl());
        this.balanceService = new BalanceService();
        this.scanner = new Scanner(System.in);
        this.fileService = new FileService();
        this.showcaseService = new ShowcaseService(this.loginService, this.balanceService);
        this.handleService = new HandleService(loginService,balanceService,scanner,fileService,showcaseService);
    }
    // Метод запуска приложения
    public void start() {
        System.out.println("Добро пожаловать в приложение для контроля личных финансов");
        if (fileService.load() != null) {
            loginService.setUserMap(fileService.load());
        }
        while (true) {
            if (loginService.isLoggedIn()) {
                showcaseService.showMainMenu();
                handleService.handleMainMenu();
            } else {
                showcaseService.showLoginMenu();
                handleService.handleLoginMenu();
            }
        }
    }

}
