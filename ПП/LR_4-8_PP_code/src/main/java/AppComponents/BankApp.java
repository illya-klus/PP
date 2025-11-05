package AppComponents;

import domain.banks.Bank;
import domain.banks.BankCache;
import domain.deposits.DepositsCache;
import domain.deposits.OpenDepositsCache;
import domain.users.UserSession;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.scene.paint.Color;

import domain.deposits.Deposit;
import data.APIrequester;
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

import java.util.ArrayList;
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
            -fx-background-color: linear-gradient(to bottom right, #F8F8FF, #ECEBFF);
            -fx-font-family: 'Segoe UI';
            -fx-text-fill: #2E2B5F;
        """);

        Pane registerRoot = createRegisterPane();
        rootPane.setCenter(registerRoot);

        scene = new Scene(rootPane, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    // Форма реєстрації
    private Pane createRegisterPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(30));
        grid.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #D6D4FF;
            -fx-border-radius: 15;
            -fx-background-radius: 15;
            -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.2), 15, 0, 0, 6);
        """);

        Label title = new Label("DepDepDeposit");
        title.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: bold;
            -fx-text-fill: #6C63FF;
        """);
        grid.add(title, 0, 0, 2, 1);

        TextField tfLogin = new TextField();
        tfLogin.setPromptText("Введіть логін");
        tfLogin.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #C0BFFF;");

        PasswordField pf = new PasswordField();
        pf.setPromptText("Введіть пароль");
        pf.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #C0BFFF;");

        Button btnRegister = new Button("Увійти");
        btnRegister.setStyle("""
            -fx-background-color: #6C63FF;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
            -fx-cursor: hand;
        """);
        btnRegister.setOnMouseEntered(e -> btnRegister.setStyle("-fx-background-color: #7D74FF; -fx-text-fill: white; -fx-background-radius: 10;"));
        btnRegister.setOnMouseExited(e -> btnRegister.setStyle("-fx-background-color: #6C63FF; -fx-text-fill: white; -fx-background-radius: 10;"));

        grid.add(new Label("Логін:"), 0, 1);
        grid.add(tfLogin, 1, 1);
        grid.add(new Label("Пароль:"), 0, 2);
        grid.add(pf, 1, 2);
        grid.add(btnRegister, 1, 3);

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
    // Меню користувача
    private HBox createUserMenu(boolean isAdmin) {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(8, 15, 8, 15));
        topBar.setSpacing(10);
        topBar.setStyle("""
            -fx-background-color: #6C63FF;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 2);
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
            -fx-selection-bar: #7D74FF;
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
                rootPane.setCenter(createEditDepositsPane(false));
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
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.TOP_LEFT);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.15), 10, 0, 0, 4);");

        Label header = new Label("Ласкаво просимо, " + username + "!");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2E2B5F;");
        Label description = new Label("DepDepDeposit — сучасний додаток для керування депозитами та банками.");
        description.setWrapText(true);

        vbox.getChildren().addAll(header, new Separator(), description);
        return vbox;
    }



    // сторінки меню юзера
    private Pane createDepositsPane(boolean isUserProfile) {
        VBox box = new VBox(15);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: transparent;");

        // Заголовок
        Label title = new Label("Каталог депозитів");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2E2B5F;");
        box.getChildren().add(title);

        // --- Пошук ---
        VBox searchBox = new VBox(6);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        MenuButton searchFieldsMenu = new MenuButton("Виберіть поле пошуку");
        searchFieldsMenu.setStyle("-fx-font-size: 14px;");
        ToggleGroup toggleGroup = new ToggleGroup();
        String[] fields = {"Назва депозиту", "Відсоток", "Мін. сума", "Термін (місяці)", "Валюта"};
        for (String field : fields) {
            RadioMenuItem item = new RadioMenuItem(field);
            item.setToggleGroup(toggleGroup);
            searchFieldsMenu.getItems().add(item);
        }

        HBox inputContainer = new HBox(8);
        inputContainer.setAlignment(Pos.CENTER_LEFT);
        TextField searchField = new TextField();
        searchField.setPromptText("Введіть значення для пошуку...");
        searchField.setPrefWidth(220);
        searchField.setStyle("-fx-background-radius: 8; -fx-border-color: #C0BFFF;");
        Button btnSearch = new Button("Застосувати");
        btnSearch.setStyle("-fx-background-color: #6C63FF; -fx-text-fill: white; -fx-background-radius: 8;");
        btnSearch.setOnMouseEntered(e -> btnSearch.setStyle("-fx-background-color: #7D74FF; -fx-text-fill: white; -fx-background-radius: 8;"));
        btnSearch.setOnMouseExited(e -> btnSearch.setStyle("-fx-background-color: #6C63FF; -fx-text-fill: white; -fx-background-radius: 8;"));

        inputContainer.getChildren().addAll(searchField, btnSearch);
        searchBox.getChildren().addAll(searchFieldsMenu, inputContainer);
        box.getChildren().add(searchBox);

        // --- Сортування (простий інтерфейс) ---
        HBox sortContainer = new HBox(10);
        sortContainer.setAlignment(Pos.CENTER_LEFT);
        sortContainer.setPadding(new Insets(5, 0, 5, 0));
        MenuButton sortMenu = new MenuButton("Параметри сортування");
        sortMenu.setStyle("-fx-font-size: 14px;");
        CheckMenuItem sortName = new CheckMenuItem("Назвою");
        CheckMenuItem sortRate = new CheckMenuItem("Відсотком");
        CheckMenuItem sortAmount = new CheckMenuItem("Мін. сумою");
        sortMenu.getItems().addAll(sortName, sortRate, sortAmount);

        Button btnApplySort = new Button("Застосувати");
        btnApplySort.setStyle("-fx-background-color: #6C63FF; -fx-text-fill: white; -fx-background-radius: 8;");
        btnApplySort.setOnMouseEntered(e -> btnApplySort.setStyle("-fx-background-color: #7D74FF; -fx-text-fill: white; -fx-background-radius: 8;"));
        btnApplySort.setOnMouseExited(e -> btnApplySort.setStyle("-fx-background-color: #6C63FF; -fx-text-fill: white; -fx-background-radius: 8;"));

        sortContainer.getChildren().addAll(sortMenu, btnApplySort);
        box.getChildren().add(sortContainer);

        // --- Список депозитів у ScrollPane ---
        ScrollPane scrollPane = new ScrollPane();
        depositsContainer = new VBox(10);
        depositsContainer.setPadding(new Insets(10));
        scrollPane.setContent(depositsContainer);
        scrollPane.setFitToWidth(true);
        box.getChildren().add(scrollPane);

        // Кнопки дії (підключити фільтр/сортування)
        btnSearch.setOnAction(e -> {
            // тимчасово: просто фільтр за назвою, якщо обрано поле "Назва депозиту"
            RadioMenuItem sel = (RadioMenuItem) toggleGroup.getSelectedToggle();
            String q = searchField.getText().trim().toLowerCase();
            depositsContainer.getChildren().clear();
            List<Deposit> cached = DepositsCache.getInstance().getDeposits();
            if (cached == null) cached = new ArrayList<>();
            for (Deposit d : cached) {
                if (q.isEmpty() || (sel != null && sel.getText().equals("Назва депозиту") && d.getName().toLowerCase().contains(q))
                        || q.isEmpty() && (sel == null)) {
                    depositsContainer.getChildren().add(createDepositCard(d, box));
                }
            }
        });

        btnApplySort.setOnAction(e -> {
            // тут можна додати реальну логіку сортування; для зараз — просто повідомлення
            List<String> sel = new ArrayList<>();
            if (sortName.isSelected()) sel.add("Назвою");
            if (sortRate.isSelected()) sel.add("Відсотком");
            if (sortAmount.isSelected()) sel.add("Мін. сумою");
            System.out.println("Сортування застосоване: " + (sel.isEmpty() ? "нічого" : String.join(", ", sel)));
        });

        // --- Завантаження депозитів у фоні з кеша / API ---
        new Thread(() -> {
            List<Deposit> deposits = DepositsCache.getInstance().loadDeposits(20);
            if (deposits == null) deposits = new ArrayList<>(); // безпечний fallback

            // Оновлюємо UI в JavaFX-потоці
            final List<Deposit> finalDeposits = deposits;
            Platform.runLater(() -> {
                depositsContainer.getChildren().clear();
                if (finalDeposits.isEmpty()) {
                    depositsContainer.getChildren().add(new Label("Депозити не знайдені."));
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
        VBox box = new VBox(15);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: #F6F4FF;");

        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null) {
            Label noUser = new Label("❌ Не авторизовано.");
            noUser.setStyle("-fx-font-size: 16px; -fx-text-fill: #2E2B5F;");
            box.getChildren().add(noUser);
            return box;
        }

        // 🔹 Інфо про користувача
        VBox userInfo = new VBox(6);
        userInfo.setAlignment(Pos.CENTER_LEFT);
        userInfo.setStyle("""
        -fx-background-color: white;
        -fx-border-color: #E0DFFF;
        -fx-border-radius: 12;
        -fx-background-radius: 12;
        -fx-padding: 15;
        -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.15), 8, 0, 0, 3);
    """);

        Label header = new Label("👤 Профіль користувача");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #6C63FF;");
        Label username = new Label("Логін: " + currentUser.getLogin());
        Label id = new Label("ID користувача: " + currentUser.getUserId());
        Label role = new Label("Роль: " + (currentUser.isAdmin() ? "Адміністратор" : "Звичайний користувач"));

        userInfo.getChildren().addAll(header, username, id, role);

        // 🔹 Контейнер для депозитів
        VBox depositsBox = new VBox(10);
        depositsBox.setAlignment(Pos.TOP_LEFT);

        Label depHeader = new Label("💰 Ваші відкриті депозити:");
        depHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2E2B5F;");
        depositsBox.getChildren().add(depHeader);

        ScrollPane scroll = new ScrollPane(depositsBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");


        new Thread(() -> {

            List<Deposit> userDeposits = OpenDepositsCache.getInstance().getOpenDeposits();

            if (userDeposits == null || userDeposits.isEmpty()) {
                userDeposits = api.getUserDeposits(currentUser.getUserId());
            }

            List<Deposit> finalList = userDeposits;
            Platform.runLater(() -> {
                if (finalList == null || finalList.isEmpty()) {
                    Label noDep = new Label("У вас поки немає відкритих депозитів.");
                    noDep.setStyle("-fx-text-fill: #888;");
                    depositsBox.getChildren().add(noDep);
                } else {
                    for (Deposit dep : finalList) {
                        VBox card = new VBox(6);
                        card.setPadding(new Insets(10));
                        card.setStyle("""
                        -fx-background-color: white;
                        -fx-border-color: #E0DFFF;
                        -fx-border-radius: 10;
                        -fx-background-radius: 10;
                        -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.1), 6, 0, 0, 2);
                    """);

                        Label depName = new Label(dep.getName());
                        depName.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2E2B5F;");

                        Label rate = new Label(String.format("Відсоток: %.2f%%", dep.getInterestRate()));
                        Label term = new Label("Термін: " + dep.getTermMonths() + " міс.");
                        Label amount = new Label(String.format("Мін. сума: %.2f %s", dep.getMinAmount(), dep.getCurrency()));

                        card.getChildren().addAll(depName, rate, term, amount);
                        depositsBox.getChildren().add(card);
                    }
                }
            });
        }).start();

        box.getChildren().addAll(userInfo, scroll);
        return box;
    }
    private Pane createBanksPage() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(15));
        container.setStyle("-fx-background-color: #F6F4FF;");

        // 🔍 Поле пошуку + кнопка
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Пошук банку за назвою...");
        searchField.setPrefWidth(250);
        searchField.setStyle("""
        -fx-background-radius: 10;
        -fx-border-color: #B7A9FF;
        -fx-border-radius: 10;
        -fx-padding: 6 10;
        -fx-font-size: 14px;
    """);

        Button applyButton = new Button("Застосувати");
        applyButton.setStyle("""
        -fx-background-color: #6C63FF;
        -fx-text-fill: white;
        -fx-font-weight: bold;
        -fx-background-radius: 10;
        -fx-cursor: hand;
    """);
        applyButton.setOnMouseEntered(e -> applyButton.setStyle("-fx-background-color: #7D74FF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10;"));
        applyButton.setOnMouseExited(e -> applyButton.setStyle("-fx-background-color: #6C63FF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10;"));

        searchBox.getChildren().addAll(searchField, applyButton);

        // Контейнер банків (одна колонка)
        VBox banksList = new VBox(15);
        banksList.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(banksList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        List<Bank> allBanks = BankCache.getInstance().loadAllBanks();

        Runnable updateBanks = () -> {
            String query = searchField.getText().toLowerCase();
            banksList.getChildren().clear();

            for (Bank bank : allBanks) {
                if (bank.getName().toLowerCase().contains(query)) {

                    VBox card = new VBox(8);
                    card.setPadding(new Insets(12));
                    card.setSpacing(6);
                    card.setPrefWidth(320);
                    card.setAlignment(Pos.TOP_LEFT);
                    card.setStyle("""
                    -fx-background-color: white;
                    -fx-border-color: #E0DFFF;
                    -fx-border-radius: 12;
                    -fx-background-radius: 12;
                    -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.15), 8, 0, 0, 3);
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
                """);
                    openSite.setOnMouseEntered(e -> openSite.setStyle("-fx-background-color: #7D74FF; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold;"));
                    openSite.setOnMouseExited(e -> openSite.setStyle("-fx-background-color: #6C63FF; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold;"));

                    openSite.setOnAction(e -> {
                        if (bank.getWebUrl() != null && !bank.getWebUrl().isEmpty()) {
                            try {
                                java.awt.Desktop.getDesktop().browse(new java.net.URI(bank.getWebUrl()));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Сайт недоступний");
                            alert.setHeaderText(null);
                            alert.setContentText("У цього банку немає вебсайту.");
                            alert.showAndWait();
                        }
                    });

                    card.getChildren().addAll(name, address, phone, openSite);

                    // Ховер ефект для самої картки
                    card.setOnMouseEntered(e -> card.setStyle("""
                    -fx-background-color: #F8F7FF;
                    -fx-border-color: #C5C1FF;
                    -fx-border-radius: 12;
                    -fx-background-radius: 12;
                    -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.25), 10, 0, 0, 4);
                """));
                    card.setOnMouseExited(e -> card.setStyle("""
                    -fx-background-color: white;
                    -fx-border-color: #E0DFFF;
                    -fx-border-radius: 12;
                    -fx-background-radius: 12;
                    -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.15), 8, 0, 0, 3);
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


    // сторінки меню адміна
    private Pane createEditUserPage() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);

        Label title = new Label("Редактор бази даних юзерів");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        box.getChildren().add(title);

        return box;
    }
    private Pane createEditBanksPage() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);

        Label label = new Label("Редактор бази даних банків");
        label.setStyle("-fx-font-size: 16px;");
        box.getChildren().add(label);

        return box;
    }
    private Pane createEditDepositsPane(boolean isUserProfile) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);

        Label title = new Label("Каталог депозитів");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        box.getChildren().add(title);

        return box;
    }


    private Pane createDepositCard(Deposit dep, Pane parentPane) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(10));
        card.setMaxWidth(320);
        card.setStyle("""
        -fx-background-color: white;
        -fx-background-radius: 12;
        -fx-border-color: #E0DFFF;
        -fx-border-radius: 12;
        -fx-effect: dropshadow(gaussian, rgba(108,99,255,0.12), 8, 0, 0, 3);
    """);

        Label name = new Label(dep.getName());
        name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        name.setTextFill(Color.web("#2E2B5F"));

        Label bank = new Label("🏦 " + dep.getBankName());
        Label details = new Label(String.format("💰 %.2f%% • %d міс.", dep.getInterestRate(), dep.getTermMonths()));
        Label amount = new Label(String.format("💵 Мін: %.2f %s", dep.getMinAmount(), dep.getCurrency()));

        Button detailsBtn = new Button("Деталі");
        detailsBtn.setStyle("-fx-background-color: #6C63FF; -fx-text-fill: white; -fx-background-radius: 8;");
        detailsBtn.setOnMouseEntered(e -> detailsBtn.setStyle("-fx-background-color: #7D74FF; -fx-text-fill: white; -fx-background-radius: 8;"));
        detailsBtn.setOnMouseExited(e -> detailsBtn.setStyle("-fx-background-color: #6C63FF; -fx-text-fill: white; -fx-background-radius: 8;"));

        detailsBtn.setOnAction(e -> {
            // Зберігаємо поточну сторінку у стек
            previousPane.add((Pane) rootPane.getCenter());

            VBox detailPage = new VBox(12);
            detailPage.setPadding(new Insets(20));
            detailPage.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
            Label header = new Label(dep.getName());
            header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
            Label bankInfo = new Label("Банк: " + dep.getBankName());
            Label interest = new Label(String.format("Ставка: %.2f%%", dep.getInterestRate()));
            Label term = new Label("Термін: " + dep.getTermMonths() + " міс.");
            Label minAmount = new Label(String.format("Мін. сума: %.2f %s", dep.getMinAmount(), dep.getCurrency()));
            Label description = new Label(dep.getDescription() != null && !dep.getDescription().isEmpty() ? dep.getDescription() : "Опис відсутній.");
            description.setWrapText(true);

            HBox actionBox = new HBox(10);
            Button openBtn = new Button("Відкрити");
            openBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 8;");
            Button closeBtn = new Button("Закрити");
            closeBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-background-radius: 8;");
            Button earlyBtn = new Button("Достроково");
            earlyBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-background-radius: 8;");

            actionBox.getChildren().addAll(openBtn, closeBtn, earlyBtn);

            detailPage.getChildren().addAll(header, bankInfo, interest, term, minAmount, description, new Separator(), actionBox);
            rootPane.setCenter(detailPage);
        });

        card.getChildren().addAll(name, bank, details, amount, detailsBtn);
        return card;
    }

    public static void main(String[] args) {
        launch(args);
    }
}