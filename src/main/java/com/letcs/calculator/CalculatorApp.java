package com.letcs.calculator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public class CalculatorApp extends Application {
    final static Logger logger = LoggerFactory.getLogger(CalculatorApp.class);

    private final String JAVAFX_CONFIG = "calculator.fxml";
    private final String JAVAFX_CSS = "application.css";
    private final String IMAGE_NAME = "image.png";
    private final String GUI_TITLE = "Calculator";
    private final int SCENE_WIDTH = 360;
    private final int SCENE_HEIGHT = 600;

    private ClassLoader classLoader = CalculatorApp.class.getClassLoader();
    private CalculatorContext context = CalculatorContext.getInstance();

    @Override
    public void start(Stage primaryStage) {
        try {
            // load GUI configuration and show the calculator interface
            FXMLLoader loader = new FXMLLoader(classLoader.getResource(JAVAFX_CONFIG));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
            scene.getStylesheets().add(Objects.requireNonNull(classLoader.getResource(JAVAFX_CSS)).toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setTitle(GUI_TITLE + " - " + context.getVersion());
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(classLoader.getResourceAsStream(IMAGE_NAME))));
            primaryStage.show();

            // setup the context
            CalculatorGUIController controller = loader.getController();
            controller.setListener(new CalculatorCore());
            context.setController(controller);
            context.setScene(scene);
            logger.info("LETCS calculator launch!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}