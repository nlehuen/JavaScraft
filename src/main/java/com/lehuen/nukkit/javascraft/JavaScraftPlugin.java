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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch(command.getName()) {
            case "eval":
                try {
                    final String expression = String.join(" ", args);
                    final Object result = engine.eval(expression);
                    if (result != null) {
                        sender.sendMessage(result.toString());
                    }
                    return true;
                } catch (ScriptException e) {
                    sender.sendMessage(e.getMessage());
                    return false;
                }
        }
        return false;
    }
}
