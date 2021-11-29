package net.airgame.application.terminal.manager.dialog;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import java.io.File;
import java.util.HashMap;

public class NewTerminalDialog extends Dialog<HashMap<String, String>> {

    public NewTerminalDialog() {
        super();
        DialogPane dialogPane = getDialogPane();
        setTitle("新建窗口");

        VBox vBox = new VBox();
        dialogPane.setContent(vBox);
        vBox.widthProperty().addListener((observable, oldValue, newValue) -> vBox.setPrefWidth(newValue.doubleValue()));
        vBox.setFillWidth(true);

        HBox hBox = new HBox();
        TextField windowNameField = new TextField("新窗口 - " + System.currentTimeMillis());
        hBox.widthProperty().addListener((observable, oldValue, newValue) -> windowNameField.setPrefWidth(newValue.doubleValue()));

        VBox.setMargin(hBox, new Insets(10, 0, 0, 0));
        Text text = new Text("名称: ");
        text.setFont(Font.font(14));
        HBox.setMargin(text, new Insets(1, 0, 0, 0));
        text.setBoundsType(TextBoundsType.LOGICAL_VERTICAL_CENTER);
        hBox.getChildren().add(text);
        hBox.getChildren().add(windowNameField);
        vBox.getChildren().add(hBox);

        hBox = new HBox();
        TextField startCommandField = new TextField("powershell");
        hBox.widthProperty().addListener((observable, oldValue, newValue) -> startCommandField.setPrefWidth(newValue.doubleValue()));

        VBox.setMargin(hBox, new Insets(10, 0, 0, 0));
        text = new Text("启动指令: ");
        text.setFont(Font.font(14));
        HBox.setMargin(text, new Insets(1, 0, 0, 0));
        hBox.getChildren().add(text);
        hBox.getChildren().add(startCommandField);
        vBox.getChildren().add(hBox);

        hBox = new HBox();
        TextField workspaceField = new TextField(new File("").getAbsolutePath());
        hBox.widthProperty().addListener((observable, oldValue, newValue) -> workspaceField.setPrefWidth(newValue.doubleValue()));

        VBox.setMargin(hBox, new Insets(10, 0, 0, 0));
        text = new Text("工作路径: ");
        text.setFont(Font.font(14));
        HBox.setMargin(text, new Insets(1, 0, 0, 0));
        hBox.getChildren().add(text);
        hBox.getChildren().add(workspaceField);
        vBox.getChildren().add(hBox);

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        setResultConverter((dialogButton) -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            if (data == ButtonBar.ButtonData.OK_DONE) {
                HashMap<String, String> map = new HashMap<>();
                map.put("windowName", windowNameField.getText());
                map.put("startCommand", startCommandField.getText());
                map.put("workspace", workspaceField.getText());
                return map;
            }
            return null;
        });

        vBox.setPrefSize(450, 130);
    }

}
