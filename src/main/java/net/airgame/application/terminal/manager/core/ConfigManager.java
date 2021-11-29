package net.airgame.application.terminal.manager.core;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.toml.TomlParser;
import net.airgame.application.terminal.manager.data.TerminalConfig;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ConfigManager {
    private static String inputCharset;
    private static String outputCharset;

    private static ArrayList<TerminalConfig> terminalConfigs;

    public static void init() {
        CommentedConfig config = new TomlParser().parse(new File("config.toml"), FileNotFoundAction.READ_NOTHING, StandardCharsets.UTF_8);
        inputCharset = config.get("inputCharset");
        outputCharset = config.get("outputCharset");
        Config terminals = config.get("terminals");
        terminalConfigs = new ArrayList<>();
        for (Config.Entry entry : terminals.entrySet()) {
            terminalConfigs.add(new TerminalConfig(entry.getKey(), entry.getValue()));
        }
    }

    public static String getInputCharset() {
        return inputCharset;
    }

    public static String getOutputCharset() {
        return outputCharset;
    }

    public static ArrayList<TerminalConfig> getTerminalConfigs() {
        return terminalConfigs;
    }
}
