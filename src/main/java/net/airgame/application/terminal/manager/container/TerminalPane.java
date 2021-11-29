package net.airgame.application.terminal.manager.container;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import net.airgame.application.terminal.manager.thread.StreamRedirectThread;

import java.io.File;
import java.io.IOException;

public class TerminalPane extends AnchorPane {
    private final TextArea outputTextArea;
    private final TextField inputField;
    private final Process process;
    private final String inputCharset;
    private final String outputCharset;
    private final StreamRedirectThread outputRedirectThread;
    private String name;

    public TerminalPane(String name, String command, File workspace, String inputCharset, String outputCharset) throws IOException {
        this.name = name;
        this.inputCharset = inputCharset;
        this.outputCharset = outputCharset;

        outputTextArea = new TextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setWrapText(true);
        getChildren().add(outputTextArea);
        setTopAnchor(outputTextArea, 1D);
        setBottomAnchor(outputTextArea, 31D);
        setLeftAnchor(outputTextArea, 1D);
        setRightAnchor(outputTextArea, 1D);


        inputField = new TextField();
        getChildren().add(inputField);
        setBottomAnchor(inputField, 1D);
        setLeftAnchor(inputField, 1D);
        setRightAnchor(inputField, 1D);

        ProcessBuilder builder = new ProcessBuilder()
                .directory(workspace)
                .command(command.split(" "));

        String path = builder.environment().getOrDefault("Path", "");
        if (path.endsWith(";")) {
            path += workspace.getAbsolutePath();
        } else {
            path += ";" + workspace.getAbsolutePath();
        }
        builder.environment().put("Path", path);

        process = builder.start();

        inputField.setOnAction(event -> {
            if (!process.isAlive()) {
                return;
            }
            String text = inputField.getText() + "\n";
            try {
                process.getOutputStream().write(text.getBytes(getOutputCharset()));
                process.getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Platform.runLater(inputField::clear);
        });

        outputTextArea.focusedProperty().addListener((observable, oldValue, newValue) -> outputTextArea.setEditable(!newValue));

        visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                inputField.requestFocus();
            }
        });

        outputRedirectThread = new StreamRedirectThread(this);
        outputRedirectThread.start();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Process getProcess() {
        return process;
    }

    public TextArea getOutputTextArea() {
        return outputTextArea;
    }

    public String getInputCharset() {
        return inputCharset;
    }

    public String getOutputCharset() {
        return outputCharset;
    }

    public void closeProcess() {
        outputRedirectThread.setStop(true);
        if (process.isAlive()) {
            process.destroy();
        }
    }
}
