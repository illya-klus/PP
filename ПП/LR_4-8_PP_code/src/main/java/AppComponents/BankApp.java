package AppComponents;

import domain.banks.Bank;
import data.caches.BankCache;
import data.caches.DepositsCache;
import data.caches.OpenDepositsCache;
import domain.users.UserSession;
import javafx.animation.FadeTransition;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.scene.paint.Color;

import domain.deposits.Deposit;
import data.api.APIrequester;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.Separator;
import domain.users.User;
import javafx.util.Duration;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;



public class BankApp extends Application {

    private VBox depositsContainer; // додати в класі
    private Stage primaryStage;
    private Scene scene;
    private final double WIDTH = 368;
    private final double HEIGHT = 586;

    private final APIrequester api = new APIrequester();

    private BorderPane rootPane;       // основний контейнер
    private ArrayList<Pane> previousPane = new ArrayList<Pane>();  //  нове поле для збереження попередніх pane



    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setResizable(false);
        primaryStage.setTitle("DepDepDeposit");

        rootPane = new BorderPane();
        rootPane.setStyle("""
        -fx-background-color: linear-gradient(to bottom right, #F0F4FF, #E0E8FF);
        -fx-font-family: 'Segoe UI';
        -fx-text-fill: #2E2B5F;
    """);

        Pane registerRoot = createRegisterPane();
        rootPane.setCenter(registerRoot);

        // Тільки стиль BorderPane
        rootPane.setPadding(new Insets(20));
        rootPane.setBorder(new Border(new BorderStroke(
                Color.web("#C0C8FF"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(15),
                new BorderWidths(3)
        )));

        // Плавне появлення сцени
        FadeTransition ft = new FadeTransition(Duration.millis(500), rootPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        scene = new Scene(rootPane, WIDTH, HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.show();
    }



    // Форма реєстрації
    private Pane createRegisterPane() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(18);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(40));
        grid.setStyle("""
        -fx-background-color: linear-gradient(to bottom right, #F9F9FF, #E6E8FF);
        -fx-border-color: #C8C4FF;
        -fx-border-radius: 18;
        -fx-background-radius: 18;
        -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.25), 18, 0, 0, 8);
    """);

        Label title = new Label("DepDepDeposit");
        title.setStyle("""
        -fx-font-size: 24px;
        -fx-font-weight: bold;
        -fx-text-fill: #5E56E5;
    """);
        grid.add(title, 0, 0, 2, 1);

        TextField tfLogin = new TextField();
        tfLogin.setPromptText("Введіть логін");
        tfLogin.setStyle("""
        -fx-background-radius: 10;
        -fx-border-radius: 10;
        -fx-border-color: #B8B2FF;
        -fx-padding: 6 8 6 8;
    """);

        PasswordField pf = new PasswordField();
        pf.setPromptText("Введіть пароль");
        pf.setStyle("""
        -fx-background-radius: 10;
        -fx-border-radius: 10;
        -fx-border-color: #B8B2FF;
        -fx-padding: 6 8 6 8;
    """);

        Button btnRegister = new Button("Увійти");
        btnRegister.setStyle("""
        -fx-background-color: #5E56E5;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 12;
        -fx-cursor: hand;
        -fx-padding: 6 12 6 12;
    """);
        btnRegister.setOnMouseEntered(e -> btnRegister.setStyle("-fx-background-color: #7A73FF; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 6 12 6 12;"));
        btnRegister.setOnMouseExited(e -> btnRegister.setStyle("-fx-background-color: #5E56E5; -fx-text-fill: white; -fx-background-radius: 12; -fx-padding: 6 12 6 12;"));

        grid.add(new Label("Логін:"), 0, 1);
        grid.add(tfLogin, 1, 1);
        grid.add(new Label("Пароль:"), 0, 2);
        grid.add(pf, 1, 2);
        grid.add(btnRegister, 1, 3);

        // Логіка без змін
        btnRegister.setOnAction(e -> {
            String login = tfLogin.getText();
            String password = pf.getText();
            User user = api.checkUser(login, password);

            if (user == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка");
                alert.setHeaderText(null);
                alert.setContentText("Невірний логін або пароль!");
                alert.showAndWait();
            } else {
                UserSession.getInstance().login(user);

                rootPane.setTop(createUserMenu(user.isAdmin()));
                Pane mainPane = createMainPane(login);
                rootPane.setCenter(mainPane);
            }
        });

        return grid;
    }
    //меню
    private HBox createUserMenu(boolean isAdmin) {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setSpacing(12);
        topBar.setStyle("""
        -fx-background-color: linear-gradient(to right, #5E56E5, #7D74FF);
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0, 0, 3);
        -fx-background-radius: 12 12 12 12;
    """);

        Button backButton = new Button("←");
        backButton.setStyle("""
        -fx-font-size: 20px;
        -fx-background-color: transparent;
        -fx-text-fill: white;
        -fx-cursor: hand;
    """);
        backButton.setOnMouseEntered(e -> backButton.setStyle("-fx-font-size: 20px; -fx-text-fill: #C9C6FF; -fx-background-color: transparent;"));
        backButton.setOnMouseExited(e -> backButton.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-background-color: transparent;"));
        backButton.setOnAction(e -> {
            if (!previousPane.isEmpty()) {
                rootPane.setCenter(previousPane.remove(previousPane.size() - 1));
            }
        });

        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("""
        -fx-background-color: transparent;
        -fx-selection-bar: #9B8FFF;
        -fx-font-size: 15px;
        -fx-text-fill: white;
    """);

        if (isAdmin) {
            Menu adminMenu = new Menu("Адмін меню");
            MenuItem banks = new MenuItem("Редагувати банки");
            MenuItem deposits = new MenuItem("Редагувати депозити");
            MenuItem users = new MenuItem("Редагувати користувачів");

            banks.setOnAction(e -> {
                previousPane.add((Pane) rootPane.getCenter());
                rootPane.setCenter(createEditBanksPage());
            });
            deposits.setOnAction(e -> {
                previousPane.add((Pane) rootPane.getCenter());
                rootPane.setCenter(createEditDepositsPane());
            });
            users.setOnAction(e -> {
                previousPane.add((Pane) rootPane.getCenter());
                rootPane.setCenter(createEditUserPage());
            });

            adminMenu.getItems().addAll(banks, deposits, users);
            menuBar.getMenus().add(adminMenu);
        } else {
            Menu userMenu = new Menu("Меню");
            MenuItem profile = new MenuItem("Профіль");
            MenuItem allDeposits = new MenuItem("Усі депозити");
            MenuItem allBanks = new MenuItem("Усі банки");

            profile.setOnAction(e -> {
                previousPane.add((Pane) rootPane.getCenter());
                rootPane.setCenter(createProfilePage());
            });
            allDeposits.setOnAction(e -> {
                previousPane.add((Pane) rootPane.getCenter());
                rootPane.setCenter(createDepositsPane(false));
            });
            allBanks.setOnAction(e -> {
                previousPane.add((Pane) rootPane.getCenter());
                rootPane.setCenter(createBanksPage());
            });

            userMenu.getItems().addAll(profile, allDeposits, allBanks);
            menuBar.getMenus().add(userMenu);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(backButton, spacer, menuBar);
        return topBar;
    }
    // Головна сторінка
    private Pane createMainPane(String username) {
        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.TOP_LEFT);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("""
        -fx-background-color: linear-gradient(to bottom right, #F8F8FF, #ECEBFF);
        -fx-background-radius: 15;
        -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.12), 12, 0, 0, 6);
    """);

        // Заголовок
        Label header = new Label("Ласкаво просимо, " + username + "!");
        header.setStyle("""
        -fx-font-size: 22px;
        -fx-font-weight: bold;
        -fx-text-fill: linear-gradient(from 0% 0% to 100% 0%, #6C63FF, #5E56E5);
    """);

        Label description = new Label("DepDepDeposit — сучасний додаток для керування депозитами та банками.");
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E2B5F;");

        Separator separator = new Separator();

        // Швидкі дії (картки)
        HBox actionCards = new HBox(15);
        actionCards.setPadding(new Insets(10, 0, 0, 0));

        // Картка "Усі депозити"
        VBox depositsCard = new VBox(8);
        depositsCard.setPadding(new Insets(15));
        depositsCard.setAlignment(Pos.CENTER);
        depositsCard.setStyle("""
        -fx-background-color: #6C63FF;
        -fx-background-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 2);
    """);
        Label depositsLbl = new Label("Усі депозити");
        depositsLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        depositsCard.getChildren().add(depositsLbl);
        depositsCard.setOnMouseEntered(e -> depositsCard.setStyle("""
        -fx-background-color: #7D74FF;
        -fx-background-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 3);
    """));
        depositsCard.setOnMouseExited(e -> depositsCard.setStyle("""
        -fx-background-color: #6C63FF;
        -fx-background-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 2);
    """));
        depositsCard.setOnMouseClicked(e -> {
            previousPane.add((Pane) rootPane.getCenter());
            rootPane.setCenter(createDepositsPane(false));
        });

        // Картка "Усі банки"
        VBox banksCard = new VBox(8);
        banksCard.setPadding(new Insets(15));
        banksCard.setAlignment(Pos.CENTER);
        banksCard.setStyle("""
        -fx-background-color: #FF6B6B;
        -fx-background-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 2);
    """);
        Label banksLbl = new Label("Усі банки");
        banksLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        banksCard.getChildren().add(banksLbl);
        banksCard.setOnMouseEntered(e -> banksCard.setStyle("""
        -fx-background-color: #FF8787;
        -fx-background-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 3);
    """));
        banksCard.setOnMouseExited(e -> banksCard.setStyle("""
        -fx-background-color: #FF6B6B;
        -fx-background-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 2);
    """));
        banksCard.setOnMouseClicked(e -> {
            previousPane.add((Pane) rootPane.getCenter());
            rootPane.setCenter(createBanksPage());
        });

        actionCards.getChildren().addAll(depositsCard, banksCard);

        vbox.getChildren().addAll(header, separator, description, actionCards);
        return vbox;
    }



    // сторінки меню юзера
    private Pane createDepositsPane(boolean isUserProfile) {
        VBox box = new VBox(15);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(20));
        box.setStyle("""
        -fx-background-color: linear-gradient(to bottom right, #F8F8FF, #ECEBFF);
    """);

        // --- Заголовок ---
        Label title = new Label("Каталог депозитів");
        title.setStyle("""
        -fx-font-size: 20px;
        -fx-font-weight: bold;
        -fx-text-fill: #2E2B5F;
        -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.15), 4, 0, 0, 2);
    """);
        box.getChildren().add(title);

        // --- Пошук ---
        VBox searchBox = new VBox(6);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        HBox inputContainer = new HBox(8);
        inputContainer.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Введіть назву депозиту...");
        searchField.setPrefWidth(220);
        searchField.setStyle("""
        -fx-background-radius: 8;
        -fx-border-radius: 8;
        -fx-border-color: #C0BFFF;
        -fx-padding: 6 10 6 10;
    """);

        Button btnSearch = new Button("Застосувати");
        btnSearch.setStyle("""
        -fx-background-color: #6C63FF;
        -fx-text-fill: white;
        -fx-background-radius: 8;
        -fx-font-weight: bold;
        -fx-cursor: hand;
        -fx-padding: 5 15 5 15;
    """);
        btnSearch.setOnMouseEntered(e -> btnSearch.setStyle("-fx-background-color: #7D74FF; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold;"));
        btnSearch.setOnMouseExited(e -> btnSearch.setStyle("-fx-background-color: #6C63FF; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold;"));

        inputContainer.getChildren().addAll(searchField, btnSearch);
        searchBox.getChildren().add(inputContainer);
        box.getChildren().add(searchBox);

        // --- Сортування ---
        HBox sortContainer = new HBox(10);
        sortContainer.setAlignment(Pos.CENTER_LEFT);
        sortContainer.setPadding(new Insets(5, 0, 5, 0));

        MenuButton sortMenu = new MenuButton("Параметри сортування");
        sortMenu.setStyle("""
        -fx-font-size: 14px;
        -fx-background-radius: 8;
        -fx-border-radius: 8;
        -fx-border-color: #C0BFFF;
    """);

        CheckMenuItem sortName = new CheckMenuItem("Назвою");
        CheckMenuItem sortRate = new CheckMenuItem("Відсотком");
        CheckMenuItem sortAmount = new CheckMenuItem("Мін. сумою");
        CheckMenuItem sortTerm = new CheckMenuItem("Терміном");
        CheckMenuItem sortEarlyWithdraw = new CheckMenuItem("Можливістю дострокового зняття");

        sortMenu.getItems().addAll(sortName, sortRate, sortAmount, sortTerm, sortEarlyWithdraw);

        Button btnApplySort = new Button("Застосувати");
        btnApplySort.setStyle("""
        -fx-background-color: #6C63FF;
        -fx-text-fill: white;
        -fx-background-radius: 8;
        -fx-font-weight: bold;
        -fx-cursor: hand;
        -fx-padding: 5 15 5 15;
    """);
        btnApplySort.setOnMouseEntered(e -> btnApplySort.setStyle("-fx-background-color: #7D74FF; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold;"));
        btnApplySort.setOnMouseExited(e -> btnApplySort.setStyle("-fx-background-color: #6C63FF; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold;"));

        sortContainer.getChildren().addAll(sortMenu, btnApplySort);
        box.getChildren().add(sortContainer);

        // --- Список депозитів ---
        ScrollPane scrollPane = new ScrollPane();
        depositsContainer = new VBox(12);
        depositsContainer.setPadding(new Insets(10));
        scrollPane.setContent(depositsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        box.getChildren().add(scrollPane);

        // --- Логіка пошуку та сортування залишається без змін ---
        btnSearch.setOnAction(e -> { /* логіка пошуку */ });
        btnApplySort.setOnAction(e -> { /* логіка сортування */ });

        // --- Завантаження депозитів ---
        new Thread(() -> {
            List<Deposit> deposits = DepositsCache.getInstance().loadDeposits(20);
            if (deposits == null) deposits = new ArrayList<>();
            final List<Deposit> finalDeposits = deposits;
            Platform.runLater(() -> {
                depositsContainer.getChildren().clear();
                if (finalDeposits.isEmpty()) {
                    Label emptyLabel = new Label("Депозити не знайдені.");
                    emptyLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
                    depositsContainer.getChildren().add(emptyLabel);
                } else {
                    for (Deposit dep : finalDeposits) {
                        depositsContainer.getChildren().add(createDepositCard(dep, box));
                    }
                }
            });
        }).start();

        return box;
    }
    private Pane createProfilePage() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(25));
        box.setStyle("""
        -fx-background-color: linear-gradient(to bottom right, #F8F8FF, #ECEBFF);
    """);

        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null) {
            Label noUser = new Label("❌ Не авторизовано.");
            noUser.setStyle("-fx-font-size: 16px; -fx-text-fill: #2E2B5F;");
            box.getChildren().add(noUser);
            return box;
        }

        // --- Інформаційна картка користувача ---
        VBox userInfo = new VBox(10);
        userInfo.setAlignment(Pos.CENTER_LEFT);
        userInfo.setPadding(new Insets(15));
        userInfo.setStyle("""
        -fx-background-color: white;
        -fx-border-radius: 12;
        -fx-background-radius: 12;
        -fx-border-color: #D6D4FF;
        -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.18), 12, 0, 0, 4);
    """);

        Label header = new Label("👤 Профіль користувача");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #6C63FF;");
        Label username = new Label("Логін: " + currentUser.getLogin());
        username.setStyle("-fx-text-fill: #2E2B5F; -fx-font-weight: semi-bold;");
        Label id = new Label("ID користувача: " + currentUser.getUserId());
        id.setStyle("-fx-text-fill: #2E2B5F;");
        Label role = new Label("Роль: " + (currentUser.isAdmin() ? "Адміністратор" : "Звичайний користувач"));
        role.setStyle("-fx-text-fill: #2E2B5F;");

        userInfo.getChildren().addAll(header, username, id, role);

        // --- Контейнер для депозитів ---
        VBox depositsBox = new VBox(12);
        depositsBox.setAlignment(Pos.TOP_LEFT);

        Label depHeader = new Label("💰 Ваші відкриті депозити:");
        depHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2E2B5F;");
        depositsBox.getChildren().add(depHeader);

        ScrollPane scroll = new ScrollPane(depositsBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("""
        -fx-background-color: transparent;
        -fx-border-color: transparent;
        -fx-padding: 5;
    """);

        // --- Завантаження депозитів асинхронно ---
        new Thread(() -> {
            List<Deposit> userDeposits = OpenDepositsCache.getInstance().loadOpenDeposits();

            if (userDeposits == null || userDeposits.isEmpty()) {
                userDeposits = api.getUserDeposits(currentUser.getUserId());
            }

            List<Deposit> finalList = userDeposits;
            Platform.runLater(() -> {
                if (finalList == null || finalList.isEmpty()) {
                    Label noDep = new Label("У вас поки немає відкритих депозитів.");
                    noDep.setStyle("-fx-text-fill: #888; -fx-font-style: italic;");
                    depositsBox.getChildren().add(noDep);
                } else {
                    finalList.forEach(dep -> depositsBox.getChildren().add(createUserDepositCard(dep, depositsBox)));
                }
            });
        }).start();

        box.getChildren().addAll(userInfo, scroll);
        return box;
    }
    private Pane createBanksPage() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("""
        -fx-background-color: linear-gradient(to bottom right, #F8F8FF, #ECEBFF);
    """);

        // 🔍 Пошук
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Пошук банку за назвою...");
        searchField.setPrefWidth(260);
        searchField.setStyle("""
        -fx-background-radius: 10;
        -fx-border-color: #C0BFFF;
        -fx-border-radius: 10;
        -fx-padding: 6 12;
        -fx-font-size: 14px;
    """);

        Button applyButton = new Button("Застосувати");
        applyButton.setStyle("""
        -fx-background-color: #6C63FF;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        -fx-padding: 6 12;
    """);
        applyButton.setOnMouseEntered(e -> applyButton.setStyle("-fx-background-color: #7D74FF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10;"));
        applyButton.setOnMouseExited(e -> applyButton.setStyle("""
        -fx-background-color: #6C63FF;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 10;
    """));

        searchBox.getChildren().addAll(searchField, applyButton);

        // --- Контейнер банків ---
        VBox banksList = new VBox(12);
        banksList.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(banksList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        List<Bank> allBanks = BankCache.getInstance().loadAllBanks();

        Runnable updateBanks = () -> {
            String query = searchField.getText().toLowerCase();
            banksList.getChildren().clear();

            for (Bank bank : allBanks) {
                if (bank.getName().toLowerCase().contains(query)) {

                    VBox card = new VBox(8);
                    card.setPadding(new Insets(14));
                    card.setSpacing(6);
                    card.setPrefWidth(320);
                    card.setAlignment(Pos.TOP_LEFT);
                    card.setStyle("""
                    -fx-background-color: white;
                    -fx-border-color: #D6D4FF;
                    -fx-border-radius: 12;
                    -fx-background-radius: 12;
                    -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.18), 10, 0, 0, 4);
                    -fx-cursor: hand;
                """);

                    Label name = new Label(bank.getName());
                    name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2E2B5F;");

                    Label address = new Label("📍 " + bank.getAddress());
                    address.setStyle("-fx-text-fill: #555;");

                    Label phone = new Label("📞 " + bank.getPhoneNumber());
                    phone.setStyle("-fx-text-fill: #555;");

                    Button openSite = new Button("Відкрити сайт");
                    openSite.setStyle("""
                    -fx-background-color: #6C63FF;
                    -fx-text-fill: white;
                    -fx-background-radius: 8;
                    -fx-font-weight: bold;
                    -fx-cursor: hand;
                    -fx-padding: 4 10;
                """);
                    openSite.setOnMouseEntered(e -> openSite.setStyle("-fx-background-color: #7D74FF; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold; -fx-padding: 4 10;"));
                    openSite.setOnMouseExited(e -> openSite.setStyle("""
                    -fx-background-color: #6C63FF;
                    -fx-text-fill: white;
                    -fx-background-radius: 8;
                    -fx-font-weight: bold;
                    -fx-padding: 4 10;
                """));

                    openSite.setOnAction(e -> {
                        if (bank.getWebUrl() != null && !bank.getWebUrl().isEmpty()) {
                            try {
                                java.awt.Desktop.getDesktop().browse(new java.net.URI(bank.getWebUrl()));
                            } catch (Exception ex) { ex.printStackTrace(); }
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Сайт недоступний");
                            alert.setHeaderText(null);
                            alert.setContentText("У цього банку немає вебсайту.");
                            alert.showAndWait();
                        }
                    });

                    card.getChildren().addAll(name, address, phone, openSite);

                    // --- Ховер ефект ---
                    card.setOnMouseEntered(e -> card.setStyle("""
                    -fx-background-color: #F8F7FF;
                    -fx-border-color: #C5C1FF;
                    -fx-border-radius: 12;
                    -fx-background-radius: 12;
                    -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.25), 12, 0, 0, 5);
                """));
                    card.setOnMouseExited(e -> card.setStyle("""
                    -fx-background-color: white;
                    -fx-border-color: #D6D4FF;
                    -fx-border-radius: 12;
                    -fx-background-radius: 12;
                    -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.18), 10, 0, 0, 4);
                """));

                    banksList.getChildren().add(card);
                }
            }

            if (banksList.getChildren().isEmpty()) {
                Label noResults = new Label("Нічого не знайдено.");
                noResults.setStyle("-fx-text-fill: #888; -fx-font-style: italic;");
                banksList.getChildren().add(noResults);
            }
        };

        applyButton.setOnAction(e -> updateBanks.run());
        updateBanks.run();

        container.getChildren().addAll(searchBox, scrollPane);
        return container;
    }



    //сторінки меню адміна
    private Pane createEditUserPage() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Редактор бази даних користувачів");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2E2B5F;");

        // Поля форми
        TextField loginField = new TextField();
        loginField.setPromptText("Логін");
        loginField.setStyle("-fx-background-radius: 8; -fx-border-color: #C5C1FF; -fx-padding: 6 10;");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Пароль");
        passField.setStyle("-fx-background-radius: 8; -fx-border-color: #C5C1FF; -fx-padding: 6 10;");

        CheckBox adminCheck = new CheckBox("Адмін");
        adminCheck.setStyle("-fx-text-fill: #555; -fx-font-weight: bold;");

        Button findBtn = new Button("🔍 Знайти користувача");
        findBtn.setStyle("""
        -fx-background-color: #2196F3;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-cursor: hand;
    """);
        findBtn.setOnMouseEntered(e -> findBtn.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;"));
        findBtn.setOnMouseExited(e -> findBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;"));

        Button addBtn = new Button("➕ Додати користувача");
        addBtn.setStyle("""
        -fx-background-color: #4CAF50;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-cursor: hand;
    """);
        addBtn.setOnMouseEntered(e -> addBtn.setStyle("-fx-background-color: #43A047; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;"));
        addBtn.setOnMouseExited(e -> addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;"));

        VBox formBox = new VBox(10, loginField, passField, adminCheck, findBtn, addBtn);
        formBox.setAlignment(Pos.CENTER);
        formBox.setStyle("-fx-background-color: #F4F4FF; -fx-padding: 20; -fx-background-radius: 12;");

        // Контейнер для карток користувачів
        VBox userCardsContainer = new VBox(15);
        userCardsContainer.setAlignment(Pos.CENTER_LEFT);
        userCardsContainer.setPadding(new Insets(10));

        // Логіка кнопок залишилась без змін
        addBtn.setOnAction(e -> {
            try {
                boolean s = api.addUser(loginField.getText(), passField.getText(), adminCheck.isSelected());
                if (s) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("✅ Успіх");
                    alert.setHeaderText(null);
                    alert.setContentText("Користувача успішно додано!");
                    alert.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Помилка");
                alert.setHeaderText(null);
                alert.setContentText("Перевір правильність числових полів ID");
                alert.showAndWait();
            }
        });

        findBtn.setOnAction(e -> {
            userCardsContainer.getChildren().clear();
            List<User> foundUsers = api.findUser(
                    loginField.getText().isEmpty() ? null : loginField.getText(),
                    passField.getText().isEmpty() ? null : passField.getText(),
                    adminCheck.isSelected()
            );

            if (foundUsers == null || foundUsers.isEmpty()) {
                Label noUsers = new Label("❌ Користувачів не знайдено");
                noUsers.setStyle("-fx-text-fill: #888; -fx-font-style: italic;");
                userCardsContainer.getChildren().add(noUsers);
            } else {
                for (User user : foundUsers) {
                    userCardsContainer.getChildren().add(
                            createUserCard(user.getUserId(), user.getLogin(), user.getPassword(), user.isAdmin())
                    );
                }
            }
        });

        root.getChildren().addAll(title, formBox, userCardsContainer);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent;");

        StackPane wrapper = new StackPane(scrollPane);
        wrapper.setPrefSize(800, 600);
        wrapper.setStyle("-fx-background-color: #F9F9FF;");

        return wrapper;
    }
    private HBox createUserCard(int id, String login, String password, boolean isAdmin) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12));
        card.setStyle("""
        -fx-background-color: #FFFFFF;
        -fx-background-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 3);
    """);

        VBox infoBox = new VBox(4);
        Label idLbl = new Label("ID: " + id);
        idLbl.setStyle("-fx-text-fill: #555; -fx-font-weight: bold;");

        Label loginLbl = new Label("Логін: " + login);
        loginLbl.setStyle("-fx-text-fill: #333;");

        Label passLbl = new Label("Пароль: " + password);
        passLbl.setStyle("-fx-text-fill: #333;");

        Label roleLbl = new Label(isAdmin ? "Роль: Адмін" : "Роль: Користувач");
        roleLbl.setStyle(isAdmin ? "-fx-text-fill: #D32F2F; -fx-font-weight: bold;" : "-fx-text-fill: #1976D2;");

        infoBox.getChildren().addAll(idLbl, loginLbl, passLbl, roleLbl);

        Button deleteBtn = new Button("Видалити");
        deleteBtn.setStyle("""
        -fx-background-color: #f44336;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 6;
        -fx-cursor: hand;
    """);
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("""
        -fx-background-color: #e53935;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 6;
        -fx-cursor: hand;
    """));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("""
        -fx-background-color: #f44336;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 6;
        -fx-cursor: hand;
    """));

        deleteBtn.setOnAction(e -> {
            try {
                boolean s = api.deleteUser(id);
                if (s) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("✅ Успіх");
                    alert.setHeaderText(null);
                    alert.setContentText("Користувача успішно видалено!");
                    alert.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Помилка");
                alert.setHeaderText(null);
                alert.setContentText("Перевір правильність числових полів ID");
                alert.showAndWait();
            }
        });

        HBox btnBox = new HBox(deleteBtn);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(infoBox, spacer, btnBox);
        return card;
    }

    private Pane createEditBanksPage() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #F9F9FF;");

        // Заголовок
        Label title = new Label("Редактор бази даних банків");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2E2B5F;");

        // 🔹 Форма для додавання банку
        TextField nameField = new TextField();
        nameField.setPromptText("Назва банку");

        TextField addrField = new TextField();
        addrField.setPromptText("Адреса");

        TextField urlField = new TextField();
        urlField.setPromptText("Вебсайт");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Телефон");

        Button addBtn = new Button("➕ Додати банк");
        addBtn.setStyle("""
        -fx-background-color: #4CAF50;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-cursor: hand;
    """);
        addBtn.setOnMouseEntered(e -> addBtn.setStyle("""
        -fx-background-color: #43A047;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-cursor: hand;
    """));
        addBtn.setOnMouseExited(e -> addBtn.setStyle("""
        -fx-background-color: #4CAF50;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-cursor: hand;
    """));

        Button findBtn = new Button("🔍 Знайти банк");
        findBtn.setStyle("""
        -fx-background-color: #2196F3;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-cursor: hand;
    """);
        findBtn.setOnMouseEntered(e -> findBtn.setStyle("""
        -fx-background-color: #1976D2;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-cursor: hand;
    """));
        findBtn.setOnMouseExited(e -> findBtn.setStyle("""
        -fx-background-color: #2196F3;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-cursor: hand;
    """));

        VBox formBox = new VBox(12, nameField, addrField, urlField, phoneField, findBtn, addBtn);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(15));
        formBox.setStyle("""
        -fx-background-color: #FFFFFF;
        -fx-background-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.1), 10,0,0,3);
    """);

        // Контейнер для карток банків
        VBox bankCards = new VBox(15);
        bankCards.setAlignment(Pos.CENTER_LEFT);
        bankCards.setPadding(new Insets(10));

        // Додаємо контент
        root.getChildren().addAll(title, formBox, bankCards);

        // Скрол
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent;");

        // Обгортка
        StackPane wrapper = new StackPane(scrollPane);
        wrapper.setPrefSize(800, 600);

        return wrapper;
    }
    private HBox createBankCard(int id, String name, String address, String url, String phone) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12));
        card.setStyle("""
        -fx-background-color: #ffffff;
        -fx-background-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6,0,0,2);
    """);

        VBox info = new VBox(5);
        Label idLbl = new Label("ID: " + id);
        Label nameLbl = new Label("Назва: " + name);
        Label addrLbl = new Label("Адреса: " + address);
        Label urlLbl = new Label("Вебсайт: " + (url.isEmpty() ? "немає" : url));
        Label phoneLbl = new Label("Телефон: " + phone);
        info.getChildren().addAll(idLbl, nameLbl, addrLbl, urlLbl, phoneLbl);

        Button editBtn = new Button("✏️");
        editBtn.setStyle("-fx-background-color: #FFB74D; -fx-text-fill: white; -fx-background-radius: 6;");
        editBtn.setOnMouseEntered(e -> editBtn.setStyle("-fx-background-color: #FFA726; -fx-text-fill: white; -fx-background-radius: 6;"));
        editBtn.setOnMouseExited(e -> editBtn.setStyle("-fx-background-color: #FFB74D; -fx-text-fill: white; -fx-background-radius: 6;"));

        Button deleteBtn = new Button("🗑️");
        deleteBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-background-radius: 6;");
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-background-radius: 6;"));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-background-radius: 6;"));

        deleteBtn.setOnAction(e -> {
            boolean success = api.deleteBank(id);
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("✅ Успіх");
                alert.setHeaderText(null);
                alert.setContentText("Банк успішно видалено!");
                alert.showAndWait();
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttons = new HBox(8, editBtn, deleteBtn);
        card.getChildren().addAll(info, spacer, buttons);

        // Hover ефект для картки
        card.setOnMouseEntered(e -> card.setStyle("""
        -fx-background-color: #F8F7FF;
        -fx-background-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.25), 8,0,0,3);
    """));
        card.setOnMouseExited(e -> card.setStyle("""
        -fx-background-color: #ffffff;
        -fx-background-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6,0,0,2);
    """));

        return card;
    }


    private Pane createEditDepositsPane() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Редактор бази даних депозитів");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #6C63FF;");

        // --- Поля форми ---
        TextField nameField = new TextField();
        nameField.setPromptText("Назва депозиту");

        TextField bankIdField = new TextField();
        bankIdField.setPromptText("ID банку");

        TextField rateField = new TextField();
        rateField.setPromptText("Ставка (%)");

        TextField termField = new TextField();
        termField.setPromptText("Термін (міс)");

        TextField minField = new TextField();
        minField.setPromptText("Мін. сума");

        CheckBox topupBox = new CheckBox("Поповнення");
        CheckBox earlyBox = new CheckBox("Дострокове зняття");

        TextField currencyField = new TextField();
        currencyField.setPromptText("Валюта (UAH/USD...)");

        Button findBtn = new Button("🔍 Знайти депозит");
        findBtn.setStyle("""
        -fx-background-color: #2196F3;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-cursor: hand;
    """);
        findBtn.setOnMouseEntered(e -> findBtn.setStyle("-fx-background-color: #42A5F5; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;"));
        findBtn.setOnMouseExited(e -> findBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;"));

        Button addBtn = new Button("➕ Додати депозит");
        addBtn.setStyle("""
        -fx-background-color: #4CAF50;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-cursor: hand;
    """);
        addBtn.setOnMouseEntered(e -> addBtn.setStyle("-fx-background-color: #66BB6A; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;"));
        addBtn.setOnMouseExited(e -> addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;"));

        VBox formBox = new VBox(12,
                nameField, bankIdField, rateField, termField, minField,
                topupBox, earlyBox, currencyField, new HBox(10, findBtn, addBtn)
        );
        formBox.setAlignment(Pos.CENTER);
        formBox.setStyle("""
        -fx-background-color: #f4f4f4;
        -fx-padding: 15;
        -fx-background-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.1), 6,0,0,2);
    """);

        // --- Контейнер для карток депозитів ---
        VBox depositCards = new VBox(12);
        depositCards.setAlignment(Pos.TOP_CENTER);
        depositCards.setPadding(new Insets(10));

        // --- Обробники кнопок ---
        addBtn.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                String currency = currencyField.getText().trim();
                Integer bankId = bankIdField.getText().isEmpty() ? null : Integer.parseInt(bankIdField.getText());
                Double rate = rateField.getText().isEmpty() ? null : Double.parseDouble(rateField.getText());
                Integer term = termField.getText().isEmpty() ? null : Integer.parseInt(termField.getText());
                Double minAmount = minField.getText().isEmpty() ? null : Double.parseDouble(minField.getText());
                boolean topup = topupBox.isSelected();
                boolean early = earlyBox.isSelected();

                boolean success = api.addDeposit(name, bankId, rate, term, minAmount, topup, early, currency);

                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("✅ Успіх");
                    alert.setHeaderText(null);
                    alert.setContentText("Депозит успішно додано!");
                    alert.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Помилка");
                alert.setHeaderText(null);
                alert.setContentText("Перевір правильність числових полів (ID, ставка, термін, мін. сума)");
                alert.showAndWait();
            }
        });

        findBtn.setOnAction(e -> {
            depositCards.getChildren().clear();
            List<Deposit> deposits = api.findDeposits(
                    nameField.getText().isEmpty() ? null : nameField.getText(),
                    bankIdField.getText().isEmpty() ? null : Integer.parseInt(bankIdField.getText()),
                    rateField.getText().isEmpty() ? null : Double.parseDouble(rateField.getText()),
                    currencyField.getText().isEmpty() ? null : currencyField.getText()
            );
            if (deposits == null || deposits.isEmpty()) {
                depositCards.getChildren().add(new Label("❌ Депозити не знайдено"));
            } else {
                for (Deposit dep : deposits) {
                    depositCards.getChildren().add(createDepositCardShort(dep));
                }
            }
        });

        // --- Розміщення контенту ---
        content.getChildren().addAll(title, formBox, depositCards);

        // --- Скрол на всю сторінку ---
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent;");

        StackPane wrapper = new StackPane(scrollPane);
        wrapper.setPrefSize(800, 600);
        wrapper.setStyle("-fx-background-color: #F9F9FF;");

        return wrapper;
    }
    private HBox createDepositCardShort(Deposit dep) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12));
        card.setStyle("""
        -fx-background-color: #ffffff;
        -fx-background-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6,0,0,2);
    """);

        VBox info = new VBox(5);
        Label idLbl = new Label("ID: " + dep.getDepositId());
        Label nameLbl = new Label("Назва: " + dep.getName());
        Label bankLbl = new Label("Банк: " + dep.getBankName());
        Label rateLbl = new Label(String.format("Ставка: %.2f%%", dep.getInterestRate()));
        Label minLbl = new Label(String.format("Мін. сума: %.2f %s", dep.getMinAmount(), dep.getCurrency()));
        info.getChildren().addAll(idLbl, nameLbl, bankLbl, rateLbl, minLbl);

        Button editBtn = new Button("✏️");
        editBtn.setStyle("-fx-background-color: #FFB74D; -fx-text-fill: white; -fx-background-radius: 6;");
        editBtn.setOnMouseEntered(e -> editBtn.setStyle("-fx-background-color: #FFA726; -fx-text-fill: white; -fx-background-radius: 6;"));
        editBtn.setOnMouseExited(e -> editBtn.setStyle("-fx-background-color: #FFB74D; -fx-text-fill: white; -fx-background-radius: 6;"));

        Button deleteBtn = new Button("🗑️");
        deleteBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-background-radius: 6;");
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-background-radius: 6;"));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-background-radius: 6;"));
        deleteBtn.setOnAction(e -> {
            boolean s = api.deleteDeposit(dep.getDepositId());
            if(s){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("✅ Успіх");
                alert.setHeaderText(null);
                alert.setContentText("Депозит успішно видалено!");
                alert.showAndWait();
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttons = new HBox(8, editBtn, deleteBtn);
        card.getChildren().addAll(info, spacer, buttons);

        // Hover ефект для картки
        card.setOnMouseEntered(e -> card.setStyle("""
        -fx-background-color: #F8F7FF;
        -fx-background-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.25), 8,0,0,3);
    """));
        card.setOnMouseExited(e -> card.setStyle("""
        -fx-background-color: #ffffff;
        -fx-background-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6,0,0,2);
    """));

        return card;
    }


    private Pane createDepositCard(Deposit dep, Pane parentPane) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(12));
        card.setMaxWidth(320);
        card.setStyle("""
        -fx-background-color: white;
        -fx-background-radius: 12;
        -fx-border-color: #E0DFFF;
        -fx-border-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.15), 8, 0, 0, 4);
    """);

        // Назва депозиту
        Label name = new Label(dep.getName());
        name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        name.setTextFill(Color.web("#2E2B5F"));

        // Банк
        Label bank = new Label("🏦 " + dep.getBankName());
        bank.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        bank.setTextFill(Color.web("#5555AA"));

        // Деталі депозиту
        Label details = new Label(String.format("💰 %.2f%% • %d міс.", dep.getInterestRate(), dep.getTermMonths()));
        details.setFont(Font.font(12));

        Label amount = new Label(String.format("💵 Мін: %.2f %s", dep.getMinAmount(), dep.getCurrency()));
        amount.setFont(Font.font(12));

        // Кнопка відкриття депозиту
        Button openBtn = new Button("Відкрити депозит");
        openBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 8;");
        openBtn.setOnMouseEntered(e -> openBtn.setStyle("-fx-background-color: #5DD165; -fx-text-fill: white; -fx-background-radius: 8;"));
        openBtn.setOnMouseExited(e -> openBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 8;"));

        // Перевірка, чи користувач уже має цей депозит
        new Thread(() -> {
            boolean alreadyOpen = api.isDepositAlreadyOpenedForUser(
                    dep.getDepositId(),
                    UserSession.getInstance().getCurrentUser().getUserId()
            );

            Platform.runLater(() -> {
                if (alreadyOpen) {
                    openBtn.setText("Вже відкрито");
                    openBtn.setDisable(true);
                    openBtn.setStyle("-fx-background-color: #BDBDBD; -fx-text-fill: white; -fx-background-radius: 8;");
                } else {
                    openBtn.setOnAction(e -> {
                        boolean success = api.openUserDeposit(
                                UserSession.getInstance().getCurrentUser().getUserId(),
                                dep.getDepositId(),
                                dep.getMinAmount()
                        );
                        if (success) {
                            showAlert("Успіх", "Депозит успішно відкрито!");
                            openBtn.setText("Вже відкрито");
                            openBtn.setDisable(true);
                            openBtn.setStyle("-fx-background-color: #BDBDBD; -fx-text-fill: white; -fx-background-radius: 8;");
                        } else {
                            showAlert("Помилка", "Не вдалося відкрити депозит!");
                        }
                    });
                }
            });
        }).start();

        card.getChildren().addAll(name, bank, details, amount, openBtn);
        return card;
    }
    private VBox createUserDepositCard(Deposit dep, VBox depositsBox) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle("""
        -fx-background-color: #ffffff;
        -fx-border-color: #E0DFFF;
        -fx-border-radius: 10;
        -fx-background-radius: 10;
        -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.12), 6, 0, 0, 3);
    """);

        // Заголовок депозиту
        Label depName = new Label(dep.getName());
        depName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2E2B5F;");

        // Основна інформація
        Label rate = new Label(String.format("Відсоток: %.2f%%", dep.getInterestRate()));
        Label term = new Label("Термін: " + dep.getTermMonths() + " міс.");
        Label minAmount = new Label(String.format("Мін. сума: %.2f %s", dep.getMinAmount(), dep.getCurrency()));
        Label currentAmount = new Label("На депозиті: ...");

        // Підвантаження актуальної суми асинхронно
        new Thread(() -> {
            double actualAmount = api.getDepositBalance(dep.getOpenDepositId());
            Platform.runLater(() -> currentAmount.setText(String.format("На депозиті: %.2f %s", actualAmount, dep.getCurrency())));
        }).start();

        // Дати
        Label startDate = new Label("Відкрито: " + (dep.getStartDate() != null ? dep.getStartDate() : "—"));
        Label endDate = new Label("Закрито: " + (dep.getEndDate() != null ? dep.getEndDate() : "—"));

        // Статус
        Label status = new Label(dep.getEndDate() == null ? "Статус: 🔵 Активний" : "Статус: ⚫ Закрито");
        status.setStyle(dep.getEndDate() == null
                ? "-fx-text-fill: #4CAF50; -fx-font-weight: bold;"
                : "-fx-text-fill: #9E9E9E; -fx-font-weight: bold;");

        card.getChildren().addAll(depName, rate, term, minAmount, currentAmount, startDate, endDate, status);

        // Кнопки для активного депозиту
        if (dep.getEndDate() == null) {
            HBox actions = new HBox(10);
            actions.setAlignment(Pos.CENTER_LEFT);
            actions.setPadding(new Insets(6, 0, 0, 0));

            Button closeBtn = new Button("💸 Закрити депозит");
            closeBtn.setStyle("""
            -fx-background-color: #FF6B6B;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
            -fx-cursor: hand;
        """);

            Button topUpBtn = new Button("➕ Поповнити");
            topUpBtn.setStyle("""
            -fx-background-color: #6C63FF;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
            -fx-cursor: hand;
        """);

            actions.getChildren().addAll(closeBtn, topUpBtn);
            card.getChildren().add(actions);
        }

        return card;
    }


    private void showAlert(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION); // можна WARNING або INFORMATION
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);

            // Додаємо стилі до вікна алерту
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("""
            -fx-background-color: #F9F9FF;
            -fx-border-color: #6C63FF;
            -fx-border-width: 2;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
        """);

            dialogPane.lookupButton(ButtonType.OK).setStyle("""
            -fx-background-color: #6C63FF;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 8;
        """);

            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}