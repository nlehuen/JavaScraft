package com.lehuen.nukkit.javascraft;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JavaScraftPlugin extends PluginBase {
    private ScriptEngine engine;

    @Override
    public void onLoad() {
        ScriptEngineManager manager = new ScriptEngineManager();
        // We inject the server in the global scope.
        manager.put("server", getServer());
        engine = manager.getEngineByMimeType("text/javascript");
        if (engine == null) {
            getLogger().error("No JavaScript engine was found!");
        } else {
            getLogger().info(TextFormat.WHITE + "Javascript engine: " + engine.getFactory().getEngineName() + " " + engine.getFactory().getEngineVersion());
        }
    }

    @Override
    public void onEnable() {
        getServer().getCommandMap().register("javascraft", new Command("eval") {
            @Override
            public boolean execute(CommandSender commandSender, String command, String[] args) {
                if (args == null) {
                    return false;
                }
                final String expression = String.join(" ", args);
                try {
                    final Object result = engine.eval(expression);
                    if (result != null) {
                        getServer().broadcastMessage(result.toString());
                    }
                    return true;
                } catch (ScriptException e) {
                    getServer().broadcastMessage(e.getMessage());
                    return false;
                }
            }
        });
    }
}
