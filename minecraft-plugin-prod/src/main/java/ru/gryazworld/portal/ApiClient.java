package ru.gryazworld.portal;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class ApiClient {

    private final PortalPlugin plugin;
    private final HttpClient http;

    public ApiClient(PortalPlugin plugin) {
        this.plugin = plugin;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * POST /portals/register
     * Регистрирует портал в FastAPI при активации.
     */
    public CompletableFuture<JsonObject> registerPortal(String portalId, Location loc) {
        JsonObject body = new JsonObject();
        body.addProperty("portal_id", portalId);
        body.addProperty("server", plugin.getConfig().getString("server-name"));
        body.addProperty("world", loc.getWorld().getName());
        body.addProperty("x", loc.getBlockX());
        body.addProperty("y", loc.getBlockY());
        body.addProperty("z", loc.getBlockZ());

        return post("/portals/register", body);
    }

    /**
     * POST /portals/teleport
     * Запрашивает у FastAPI сервер и координаты назначения.
     * Ожидаемый ответ: {"target_server":"lobby","world":"world","x":0.5,"y":64.0,"z":0.5}
     */
    public CompletableFuture<JsonObject> requestTeleport(String portalId, Player player) {
        JsonObject body = new JsonObject();
        body.addProperty("portal_id", portalId);
        body.addProperty("player_uuid", player.getUniqueId().toString());
        body.addProperty("player_name", player.getName());
        body.addProperty("from_server", plugin.getConfig().getString("server-name"));

        return post("/portals/teleport", body);
    }

    /**
     * GET /players/{uuid}/pending
     * Проверяет отложенный телепорт при входе игрока.
     * Ответ при наличии: {"world":"world","x":0.5,"y":64.0,"z":0.5}
     * При отсутствии: HTTP 404
     */
    public CompletableFuture<JsonObject> getPendingTeleport(UUID playerUuid) {
        String url = plugin.getConfig().getString("api-url") + "/players/" + playerUuid + "/pending";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        return http.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return JsonParser.parseString(response.body()).getAsJsonObject();
                    }
                    return null;
                })
                .exceptionally(ex -> {
                    plugin.getLogger().log(Level.WARNING, "Ошибка GET /players/pending: " + ex.getMessage());
                    return null;
                });
    }

    /**
     * GET /portals?server=...
     * Возвращает все зарегистрированные порталы текущего сервера.
     * Ответ: [{"portal_id","x","y","z","world"}, ...]
     */
    public CompletableFuture<JsonArray> getServerPortals(String server) {
        String url = plugin.getConfig().getString("api-url") + "/portals?server=" + server;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        return http.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200)
                        return JsonParser.parseString(response.body()).getAsJsonArray();
                    return null;
                })
                .exceptionally(ex -> {
                    plugin.getLogger().log(Level.WARNING, "[Portal] Error GET /portals: " + ex.getMessage());
                    return null;
                });
    }

    /**
     * GET /portals/nearest?server=...&x=...&z=...
     * Ищет ближайший зарегистрированный портал на указанном сервере в радиусе 50 блоков.
     * Возвращает {"portal_id","x","y","z","world"} или null если не найдено.
     */
    public CompletableFuture<JsonObject> getNearestPortal(String server, int x, int z) {
        String url = plugin.getConfig().getString("api-url")
                + "/portals/nearest?server=" + server + "&x=" + x + "&z=" + z;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        return http.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200)
                        return JsonParser.parseString(response.body()).getAsJsonObject();
                    return null; // 404 — нет портала в радиусе
                })
                .exceptionally(ex -> {
                    plugin.getLogger().log(Level.WARNING, "[Portal] Error GET /portals/nearest: " + ex.getMessage());
                    return null;
                });
    }

    // ─── Player game events ────────────────────────────────────────────────────

    public void sendJoin(String uuid, String nickname, String server) {
        JsonObject body = new JsonObject();
        body.addProperty("uuid", uuid);
        body.addProperty("nickname", nickname);
        body.addProperty("server", server);
        postWithSecret("/mc/player/join", body);
    }

    public void sendQuit(String uuid, long sessionSeconds, String server) {
        JsonObject body = new JsonObject();
        body.addProperty("uuid", uuid);
        body.addProperty("session_seconds", sessionSeconds);
        body.addProperty("server", server);
        postWithSecret("/mc/player/quit", body);
    }

    public void sendDeath(String uuid, String nickname, String deathMessage, String server) {
        JsonObject body = new JsonObject();
        body.addProperty("uuid", uuid);
        body.addProperty("nickname", nickname);
        body.addProperty("death_message", deathMessage);
        body.addProperty("server", server);
        postWithSecret("/mc/player/death", body);
    }

    public void sendChat(String uuid, String nickname, String message, String server) {
        JsonObject body = new JsonObject();
        body.addProperty("uuid", uuid);
        body.addProperty("nickname", nickname);
        body.addProperty("message", message);
        body.addProperty("server", server);
        postWithSecret("/mc/player/chat", body);
    }

    private CompletableFuture<JsonObject> post(String path, JsonObject body) {
        String url = plugin.getConfig().getString("api-url") + path;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        return http.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        return JsonParser.parseString(response.body()).getAsJsonObject();
                    }
                    plugin.getLogger().warning("API " + path + " вернул " + response.statusCode() + ": " + response.body());
                    return null;
                })
                .exceptionally(ex -> {
                    plugin.getLogger().log(Level.WARNING, "Ошибка POST " + path + ": " + ex.getMessage());
                    return null;
                });
    }

    private CompletableFuture<JsonObject> postWithSecret(String path, JsonObject body) {
        String url = plugin.getConfig().getString("api-url") + path;
        String secret = plugin.getConfig().getString("plugin-secret", "");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .header("X-Plugin-Secret", secret)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        return http.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        return JsonParser.parseString(response.body()).getAsJsonObject();
                    }
                    plugin.getLogger().warning("API " + path + " вернул " + response.statusCode() + ": " + response.body());
                    return null;
                })
                .exceptionally(ex -> {
                    plugin.getLogger().log(Level.WARNING, "Ошибка POST " + path + ": " + ex.getMessage());
                    return null;
                });
    }
}
