/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.BanList$Type
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package dev.serverpanel.plugin.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.serverpanel.plugin.ServerPanelPlugin;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BanHttpServer {
    private final ServerPanelPlugin plugin;
    private final String secret;
    private HttpServer server;

    public BanHttpServer(ServerPanelPlugin plugin, String secret) {
        this.plugin = plugin;
        this.secret = secret;
    }

    public void start(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.createContext("/api/ban", this::handleBan);
        this.server.createContext("/api/unban", this::handleUnban);
        this.server.createContext("/api/whitelist/add", this::handleWhitelistAdd);
        this.server.createContext("/api/unmute", this::handleUnmute);
        this.server.setExecutor(Executors.newFixedThreadPool(4));
        this.server.start();
        this.plugin.getLogger().info("Ban HTTP server started on port " + port);
    }

    public void stop() {
        if (this.server != null) {
            this.server.stop(0);
            this.plugin.getLogger().info("Ban HTTP server stopped.");
        }
    }

    private void handleBan(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            this.respond(exchange, 405, "Method Not Allowed");
            return;
        }
        JsonObject body = this.readJson(exchange);
        if (body == null || !this.secret.equals(this.getStr(body, "secret"))) {
            this.respond(exchange, 403, "Forbidden");
            return;
        }
        String nickname = this.getStr(body, "nickname");
        if (nickname.isEmpty()) {
            this.respond(exchange, 400, "Missing nickname");
            return;
        }
        Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
            Bukkit.getBanList((BanList.Type)BanList.Type.NAME).addBan(nickname, "3 \u0432\u0430\u0440\u043d\u0430", null, "ServerPanel");
            Player target = Bukkit.getPlayer((String)nickname);
            if (target != null) {
                target.kickPlayer("\u00a7c\u0412\u044b \u0437\u0430\u0431\u0430\u043d\u0435\u043d\u044b: 3 \u0432\u0430\u0440\u043d\u0430");
            }
            this.plugin.getLogger().info("Banned player: " + nickname);
        });
        this.respond(exchange, 200, "ok");
    }

    private void handleUnban(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            this.respond(exchange, 405, "Method Not Allowed");
            return;
        }
        JsonObject body = this.readJson(exchange);
        if (body == null || !this.secret.equals(this.getStr(body, "secret"))) {
            this.respond(exchange, 403, "Forbidden");
            return;
        }
        String nickname = this.getStr(body, "nickname");
        if (nickname.isEmpty()) {
            this.respond(exchange, 400, "Missing nickname");
            return;
        }
        Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "unban " + nickname + " Разбан через магазин");
            this.plugin.getLogger().info("Pardoned player: " + nickname);
        });
        this.respond(exchange, 200, "ok");
    }

    private void handleWhitelistAdd(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            this.respond(exchange, 405, "Method Not Allowed");
            return;
        }
        JsonObject body = this.readJson(exchange);
        if (body == null || !this.secret.equals(this.getStr(body, "secret"))) {
            this.respond(exchange, 403, "Forbidden");
            return;
        }
        String nickname = this.getStr(body, "nickname");
        if (nickname.isEmpty()) {
            this.respond(exchange, 400, "Missing nickname");
            return;
        }
        Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "whitelist add " + nickname);
            this.plugin.getLogger().info("Whitelisted player: " + nickname);
        });
        this.respond(exchange, 200, "ok");
    }

    private void handleUnmute(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            this.respond(exchange, 405, "Method Not Allowed");
            return;
        }
        JsonObject body = this.readJson(exchange);
        if (body == null || !this.secret.equals(this.getStr(body, "secret"))) {
            this.respond(exchange, 403, "Forbidden");
            return;
        }
        String nickname = this.getStr(body, "nickname");
        if (nickname.isEmpty()) {
            this.respond(exchange, 400, "Missing nickname");
            return;
        }
        Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "unmute " + nickname + " Размут через магазин");
            this.plugin.getLogger().info("Unmuted player: " + nickname);
        });
        this.respond(exchange, 200, "ok");
    }

    private JsonObject readJson(HttpExchange exchange) {
        JsonObject jsonObject;
        block8: {
            InputStream is = exchange.getRequestBody();
            try {
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                jsonObject = JsonParser.parseString(body).getAsJsonObject();
                if (is == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (is != null) {
                        try {
                            is.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception e) {
                    return null;
                }
            }
            try { is.close(); } catch (IOException e) { /* ignore */ }
        }
        return jsonObject;
    }

    private String getStr(JsonObject obj, String key) {
        return obj.has(key) ? obj.get(key).getAsString() : "";
    }

    private void respond(HttpExchange exchange, int code, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody();){
            os.write(bytes);
        }
    }
}

