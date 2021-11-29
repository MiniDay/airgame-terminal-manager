package net.airgame.application.terminal.manager.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import net.airgame.application.terminal.manager.data.TerminalConfig;
import net.airgame.application.terminal.manager.container.TerminalPane;
import net.airgame.application.terminal.manager.core.ConfigManager;
import net.airgame.application.terminal.manager.dialog.NewTerminalDialog;
import net.airgame.application.terminal.manager.util.TerminalUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainController {
    public final ArrayList<TerminalPane> terminalPanes;

    @FXML
    public ListView<String> listView;
    @FXML
    public AnchorPane mainPane;
    @FXML
    public MenuButton quickStart;

    public MainController() {
        terminalPanes = new ArrayList<>();
        reloadConfig();
    }

    public void addTerminal(String windowName, String startCommand, File workspace, String inputCharset, String outputCharset) {
        TerminalPane terminalPane;
        try {
            terminalPane = new TerminalPane(windowName, startCommand, workspace, inputCharset, outputCharset);
        } catch (IOException e) {
            TerminalUtils.error(e);
            return;
        }
        for (TerminalPane pane : terminalPanes) {
            pane.setVisible(false);
        }
        terminalPanes.add(terminalPane);

        mainPane.getChildren().add(terminalPane);
        AnchorPane.setTopAnchor(terminalPane, 0D);
        AnchorPane.setBottomAnchor(terminalPane, 0D);
        AnchorPane.setLeftAnchor(terminalPane, 0D);
        AnchorPane.setRightAnchor(terminalPane, 0D);
        terminalPane.autosize();

        listView.getItems().add(terminalPane.getName());
        listView.getSelectionModel().selectLast();
    }

    public void newTerminal() {
        NewTerminalDialog newTerminalDialog = new NewTerminalDialog();
        newTerminalDialog.showAndWait();

        HashMap<String, String> map = newTerminalDialog.getResult();
        if (map == null) {
            return;
        }
        System.out.println(map);
        String windowName = map.get("windowName");
        if (windowName == null || windowName.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "窗口名称不能为空!").show();
            return;
        }

        File workspace = new File(map.get("workspace"));
        if (!workspace.exists()) {
            new Alert(Alert.AlertType.ERROR, "文件夹 " + workspace.getAbsolutePath() + " 不存在!").show();
            return;
        }

        String startCommand = map.get("startCommand");

        addTerminal(
                windowName,
                startCommand,
                workspace,
                ConfigManager.getInputCharset(),
                ConfigManager.getOutputCharset()
        );
    }

    public void deleteTerminal() {
        int index = listView.getSelectionModel().getSelectedIndex();

        if (index < 0) {
            return;
        }

        TerminalPane choosePane = terminalPanes.get(index);

        if (choosePane.getProcess().isAlive()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "该终端中的程序仍未结束，确定要强行关闭它吗？", ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
                return;
            }
        }

        terminalPanes.remove(index);
        mainPane.getChildren().remove(choosePane);
        choosePane.closeProcess();

        if (index > 0) {
            terminalPanes.get(index - 1).setVisible(true);
        }

        if (listView.getItems().size() > index) {
            listView.getItems().remove(index);
        }

    }

    public void renameTerminal() {
        int index = listView.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            return;
        }
        TextInputDialog dialog = new TextInputDialog("新名称");
        dialog.setTitle("请输入新名称: ");
        dialog.setHeaderText(null);

        dialog.showAndWait();

        if (listView.getItems().size() > index) {
            listView.getItems().set(index, dialog.getResult());
        }
        if (terminalPanes.size() > index) {
            terminalPanes.get(index).setName(dialog.getResult());
        }
        listView.getSelectionModel().select(index);
    }

    public void changeTerminal() {
        int index = listView.getSelectionModel().getSelectedIndex();

        if (index < 0) {
            return;
        }

        if (terminalPanes.size() > index) {
            for (int i = 0; i < terminalPanes.size(); i++) {
                terminalPanes.get(i).setVisible(index == i);
            }
        }
    }

    public void reloadConfig() {
        Platform.runLater(() -> {
            quickStart.getItems().clear();

            try {
                ConfigManager.init();
                for (TerminalConfig config : ConfigManager.getTerminalConfigs()) {
                    MenuItem item = new MenuItem(config.getName());
                    quickStart.getItems().add(item);

                    item.setOnAction(event -> addTerminal(
                            config.getName(),
                            config.getStartCommand(),
                            new File(config.getWorkspace()),
                            config.getInputCharset(),
                            config.getOutputCharset()
                    ));
                }
            } catch (Exception e) {
                TerminalUtils.error(e);
            }

            MenuItem item = new MenuItem("重载配置");
            quickStart.getItems().add(item);
            item.setOnAction(event -> reloadConfig());
        });
    }

    public ArrayList<TerminalPane> getTerminalPanes() {
        return terminalPanes;
    }
}
