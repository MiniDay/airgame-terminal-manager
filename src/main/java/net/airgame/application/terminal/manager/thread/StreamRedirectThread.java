package net.airgame.application.terminal.manager.thread;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import net.airgame.application.terminal.manager.container.TerminalPane;

public class StreamRedirectThread extends Thread {
    private final TerminalPane pane;
    private final Process process;

    private volatile boolean stop;

    public StreamRedirectThread(TerminalPane pane) {
        this.pane = pane;
        process = pane.getProcess();

        stop = false;
    }

    @Override
    public void run() {
        byte[] bytes = new byte[1024 * 1024];
        while (!stop) {
            try {
                int readSize = process.getInputStream().read(bytes);
                if (readSize <= 0) {
                    printExit();
                    break;
                }
                String output = new String(bytes, 0, readSize, pane.getInputCharset());
                if (process.getErrorStream().available() > 0) {
                    readSize = process.getErrorStream().read(bytes);
                    if (readSize > 0) {
                        output = output + new String(bytes, 0, readSize, pane.getInputCharset());
                    }
                }
                printText(output);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    private void printText(String output) {
        Platform.runLater(() -> {
            TextArea textArea = pane.getOutputTextArea();
            textArea.appendText(output);
            String text = textArea.getText();
            int subLength = text.length() - 50000;
            if (subLength <= 1000) {
                return;
            }
            textArea.setText(text.substring(subLength));
        });
    }

    private void printExit() {
        if (process == null) {
            return;
        }
        int exitValue = process.exitValue();
        printText("\n程序已结束，退出代码: " + exitValue + "\n");
    }
}
