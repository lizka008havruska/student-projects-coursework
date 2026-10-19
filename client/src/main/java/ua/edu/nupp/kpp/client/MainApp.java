package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/** Заглушка для етапу 1: повноцінний інтерфейс буде реалізовано на наступних етапах. */
public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Подання та перевірка студентських проєктів");
        stage.setScene(new Scene(new StackPane(new Label("Етап 1: структура клієнта створена")), 480, 200));
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
