package com.lehuen.nukkit.javascraft;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import com.google.common.io.ByteStreams;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.ForkJoinPool;

public class JavaScraftPlugin extends PluginBase {
    private ScriptEngine engine;
    private HttpServer httpServer;

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
    public void onEnable() {
        try {
            InetSocketAddress address = new InetSocketAddress(8080);
            httpServer = HttpServer.create(address, 8);
            httpServer.setExecutor(ForkJoinPool.commonPool());
            httpServer.createContext("/", e -> {
                String path = e.getRequestURI().getPath().substring(1);
                if (path.equals("")) {
                    path = "index.html";
                }
                final String contentType = getMimeType(path);
                e.getResponseHeaders().add("Content-Type", contentType);
                try (InputStream is = getResource(path)) {
                    if (is == null) {
                        e.sendResponseHeaders(404, -1);
                        getLogger().error("HTTP Server 404 Not Found: '" + path + "'");
                    } else {
                        e.sendResponseHeaders(200, 0);
                        OutputStream os = e.getResponseBody();
                        ByteStreams.copy(is, os);
                        os.flush();
                        getLogger().info("HTTP Server 200 '" + path + "' (" + contentType + ")");
                    }
                } catch (Exception err) {
                    getLogger().error("HTTP Server", err);
                }
                e.close();
            });
            httpServer.start();
            getLogger().info("Listening on " + TextFormat.GREEN + "http://localhost:" + address.getPort());
        } catch (IOException e) {
            getLogger().error("Cannot start HTTP server", e);
        }
    }

    private String getMimeType(String path) {
        if (path.endsWith(".html")) {
            return "text/html";
        } else if (path.endsWith(".js")) {
            return "text/javascript";
        } else {
            return "application/octet-stream";
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Stopping HTTP server");
        httpServer.stop(0);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (engine == null) {
            sender.sendMessage("No JavaScript engine was found");
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
