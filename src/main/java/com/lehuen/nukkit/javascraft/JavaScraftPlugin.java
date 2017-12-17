package com.lehuen.nukkit.javascraft;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;

public class JavaScraftPlugin extends PluginBase {
    private ScriptEngine engine;

    @Override
    public void onLoad() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByMimeType("text/javascript");
        if (engine == null) {
            getLogger().error("No JavaScript engine was found!");
            return;
        }
        getLogger().info(TextFormat.WHITE + "Javascript engine: " + engine.getFactory().getEngineName() + " " + engine.getFactory().getEngineVersion());

        // We inject the server in the global scope.
        // CAUTION: this allows anyone that has javascraft.command.eval permission to do nasty things, such as
        // "/eval server.shutdown()".
        engine.put("server", getServer());

        // Initialize the engine from init.js
        try (Reader reader = new InputStreamReader(getResource("init.js"))) {
            engine.eval(reader);
        } catch (Exception e) {
            getLogger().error("Could not load init.js", e);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (engine == null) {
            sender.sendMessage(new TextContainer("No JavaScript engine was found"));
            return false;
        }
        switch (command.getName()) {
            case "eval":
                return eval(sender, args);
        }
        return false;
    }

    private synchronized boolean eval(CommandSender sender, String[] args) {
        final String expression = String.join(" ", args);
        Object result;
        if (sender.isPlayer()) {
            Player player = (Player) sender;
            engine.put("me", player);
            engine.put("level", player.getPosition().level);
            engine.put("pos", player.getPosition());
        } else {
            engine.put("me", null);
            engine.put("level", getServer().getDefaultLevel());
            engine.put("pos", null);
        }
        try {
            result = engine.eval(expression);
        } catch (ScriptException e) {
            sender.sendMessage(e.getMessage());
            return false;
        }
        if (result != null) {
            sender.sendMessage(result.toString());
        }
        return true;
    }
}
