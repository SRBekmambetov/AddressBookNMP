package ru.srb.addressbooknmp;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.srb.addressbooknmp.fxml.MainView;
import ru.srb.addressbooknmp.utils.LocaleManager;

import java.util.Locale;

@SpringBootApplication
public class Main extends JavaFxSpringIntegrator {

    @Autowired
    private MainView mainView;

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        super.start(primaryStage);
        loadMainFXML(LocaleManager.RU_LOCALE, primaryStage);
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    // загружает дерево компонентов и возвращает в виде VBox (корневой элемент в FXML)
    private void loadMainFXML(Locale locale, Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setScene(new Scene(mainView.getView(locale)));
        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(600);
        primaryStage.centerOnScreen();
        primaryStage.setTitle(mainView.getResourceBundle().getString("address_book"));
        primaryStage.show();


    }

    public static void main(String[] args) {
        // старт приложения
        launchSpringJavaFXApp(Main.class, args);
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
    }
}
