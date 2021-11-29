package net.airgame.application.terminal.manager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.airgame.application.terminal.manager.container.TerminalPane;
import net.airgame.application.terminal.manager.controller.MainController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@SuppressWarnings("ConstantConditions")
public class Bootstrap extends Application {
    public static void main(String[] args) {
        File file = new File("config.toml");
        if (!file.exists()) {
            try {
                Files.copy(Bootstrap.class.getResourceAsStream("/config.toml"), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
                return;
            }
        }

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Main.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        stage.setTitle("AirGame-TerminalManager");

        stage.show();

        stage.setOnCloseRequest(event -> {
            MainController controller = fxmlLoader.getController();
            for (TerminalPane pane : controller.getTerminalPanes()) {
                if (pane.getProcess().isAlive()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "确定要强行停止正在运行的所有控制台并关闭程序吗？", ButtonType.OK, ButtonType.CANCEL);
                    alert.showAndWait();
                    if (alert.getResult().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                        break;
                    } else {
                        event.consume();
                        return;
                    }
                }
            }

            for (TerminalPane pane : controller.getTerminalPanes()) {
                pane.closeProcess();
            }

        });

    }
}
