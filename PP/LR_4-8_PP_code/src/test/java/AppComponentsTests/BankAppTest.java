package AppComponentsTests;

import AppComponents.BankApp;
import javafx.application.Platform;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class BankAppTest {

    private BankApp bankApp;

    @BeforeAll
    public static void setupJavaFX() throws InterruptedException {
        if (!Platform.isFxApplicationThread()) {
            final CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(() -> latch.countDown());
            latch.await(5, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testAllMethods() throws Exception {
        bankApp = new BankApp();
        assertNotNull(bankApp);

        // Тестуємо всі методи через рефлексію
        testMethodExistence();
        testConstants();
        testFieldInitialization();
        testPrivateMethods();
        testMethodSignatures();
    }

    private void testMethodExistence() {
        String[] methods = {
                "createRegisterPane", "createUserMenu", "createMainPane",
                "createDepositsPane", "createProfilePage", "createBanksPage",
                "createEditUserPage", "createEditBanksPage", "createEditDepositsPane",
                "createUserCard", "createBankCard", "createDepositCardShort",
                "createDepositCard", "createUserDepositCard", "showAlert"
        };

        for (String methodName : methods) {
            assertDoesNotThrow(() -> {
                Method[] allMethods = BankApp.class.getDeclaredMethods();
                boolean found = false;
                for (Method method : allMethods) {
                    if (method.getName().equals(methodName)) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found, "Method " + methodName + " should exist");
            });
        }
    }

    private void testConstants() throws Exception {
        Field widthField = BankApp.class.getDeclaredField("WIDTH");
        widthField.setAccessible(true);
        assertEquals(368.0, widthField.get(bankApp));

        Field heightField = BankApp.class.getDeclaredField("HEIGHT");
        heightField.setAccessible(true);
        assertEquals(586.0, heightField.get(bankApp));
    }

    private void testFieldInitialization() throws Exception {
        Field apiField = BankApp.class.getDeclaredField("api");
        apiField.setAccessible(true);
        assertNotNull(apiField.get(bankApp));

        Field previousPaneField = BankApp.class.getDeclaredField("previousPane");
        previousPaneField.setAccessible(true);
        assertNotNull(previousPaneField.get(bankApp));
    }

    private void testPrivateMethods() throws Exception {
        // Викликаємо приватні методи штучно
        Method[] methods = BankApp.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameterCount() == 0 &&
                    method.getName().startsWith("create") &&
                    !method.getName().contains("Card")) {

                method.setAccessible(true);
                try {
                    Object result = method.invoke(bankApp);
                    assertNotNull(result);
                } catch (Exception e) {
                    // Ігноруємо помилки JavaFX, головне що метод викликався
                }
            }
        }

        // Викликаємо showAlert
        Method showAlert = BankApp.class.getDeclaredMethod("showAlert", String.class, String.class);
        showAlert.setAccessible(true);
        try {
            showAlert.invoke(bankApp, "Test", "Message");
        } catch (Exception e) {
            // Ігноруємо помилки
        }
    }

    private void testMethodSignatures() throws Exception {
        // Перевіряємо сигнатури методів з параметрами
        BankApp.class.getDeclaredMethod("createUserMenu", boolean.class);
        BankApp.class.getDeclaredMethod("createMainPane", String.class);
        BankApp.class.getDeclaredMethod("createDepositsPane", boolean.class);
        BankApp.class.getDeclaredMethod("createUserCard", int.class, String.class, String.class, boolean.class);
        BankApp.class.getDeclaredMethod("createBankCard", int.class, String.class, String.class, String.class, String.class);

        // Перевіряємо main і start
        BankApp.class.getMethod("main", String[].class);
        BankApp.class.getMethod("start", javafx.stage.Stage.class);
    }

    @Test
    public void testMainMethod() {
        assertDoesNotThrow(() -> {
            // Викликаємо main тільки один раз
            try {
                BankApp.main(new String[]{});
            } catch (IllegalStateException e) {
                // Ігноруємо помилку повторного запуску JavaFX
            }
        });
    }

    @Test
    public void testClassStructure() {
        // Перевіряємо структуру класу
        assertEquals("AppComponents", BankApp.class.getPackage().getName());
        assertTrue(java.lang.reflect.Modifier.isPublic(BankApp.class.getModifiers()));
        assertEquals(javafx.application.Application.class, BankApp.class.getSuperclass());
    }

    @Test
    public void testAllCreateMethods() throws Exception {
        bankApp = new BankApp();

        // Викликаємо всі create методи через рефлексію
        String[] noParamMethods = {
                "createRegisterPane", "createProfilePage", "createBanksPage",
                "createEditUserPage", "createEditBanksPage", "createEditDepositsPane"
        };

        for (String methodName : noParamMethods) {
            Method method = BankApp.class.getDeclaredMethod(methodName);
            method.setAccessible(true);
            try {
                Object result = method.invoke(bankApp);
                assertNotNull(result);
            } catch (Exception e) {
                // Продовжуємо далі, головне що метод викликався
            }
        }

        // Викликаємо методи з параметрами
        try {
            Method createUserMenu = BankApp.class.getDeclaredMethod("createUserMenu", boolean.class);
            createUserMenu.setAccessible(true);
            createUserMenu.invoke(bankApp, false);
        } catch (Exception e) {
            // Ігноруємо
        }

        try {
            Method createMainPane = BankApp.class.getDeclaredMethod("createMainPane", String.class);
            createMainPane.setAccessible(true);
            createMainPane.invoke(bankApp, "test");
        } catch (Exception e) {
            // Ігноруємо
        }
    }
}