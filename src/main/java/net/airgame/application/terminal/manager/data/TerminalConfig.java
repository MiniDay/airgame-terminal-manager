package net.airgame.application.terminal.manager.data;

import com.electronwill.nightconfig.core.Config;

public class TerminalConfig {
    private final String name;
    private final String startCommand;
    private final String workspace;

    private final String inputCharset;
    private final String outputCharset;

    public TerminalConfig(String name, Config config) {
        this.name = name;
        startCommand = config.get("startCommand");
        workspace = config.get("workspace");
        inputCharset = config.get("inputCharset");
        outputCharset = config.get("outputCharset");
    }

    public String getName() {
        return name;
    }

    public String getStartCommand() {
        return startCommand;
    }

    public String getWorkspace() {
        return workspace;
    }

    public String getInputCharset() {
        return inputCharset;
    }

    public String getOutputCharset() {
        return outputCharset;
    }

    @Override
    public String toString() {
        return "TerminalConfig{" +
                "name='" + name + '\'' +
                ", startCommand='" + startCommand + '\'' +
                ", workspace='" + workspace + '\'' +
                ", inputCharset='" + inputCharset + '\'' +
                ", outputCharset='" + outputCharset + '\'' +
                '}';
    }
}
