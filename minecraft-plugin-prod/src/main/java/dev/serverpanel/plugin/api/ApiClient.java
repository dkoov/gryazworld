package dev.serverpanel.plugin.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.bukkit.plugin.java.JavaPlugin;

public class ApiClient {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient http;
    private final String baseUrl;
    private final String secret;
    private final Gson gson = new Gson();
    private final JavaPlugin plugin;

    public ApiClient(JavaPlugin plugin, String baseUrl, String secret) {
        this.plugin = plugin;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.secret = secret;
        this.http = new OkHttpClient.Builder().connectTimeout(5L, TimeUnit.SECONDS).readTimeout(10L, TimeUnit.SECONDS).build();
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public ApiResponse playerJoin(String uuid, String nickname) {
        JsonObject body = new JsonObject();
        body.addProperty("uuid", uuid);
        body.addProperty("nickname", nickname);
        return this.post("/mc/player/join", body);
    }

    public ApiResponse playerQuit(String uuid, long sessionSeconds) {
        JsonObject body = new JsonObject();
        body.addProperty("uuid", uuid);
        body.addProperty("session_seconds", sessionSeconds);
        return this.post("/mc/player/quit", body);
    }

    public ApiResponse getBalance(String uuid) {
        return this.get("/mc/bank/" + uuid + "/balance");
    }

    public ApiResponse deposit(String uuid, int amount) {
        JsonObject body = new JsonObject();
        body.addProperty("uuid", uuid);
        body.addProperty("amount", amount);
        return this.post("/mc/bank/deposit", body);
    }

    public ApiResponse transfer(String fromUuid, String toUuid, int amount) {
        JsonObject body = new JsonObject();
        body.addProperty("from_uuid", fromUuid);
        body.addProperty("to_uuid", toUuid);
        body.addProperty("amount", amount);
        return this.post("/mc/bank/transfer", body);
    }

    public ApiResponse payFine(String uuid, int fineId) {
        JsonObject body = new JsonObject();
        body.addProperty("uuid", uuid);
        body.addProperty("fine_id", fineId);
        return this.post("/mc/bank/pay_fine", body);
    }

    public ApiResponse withdraw(String nickname, int amount) {
        JsonObject body = new JsonObject();
        body.addProperty("nickname", nickname);
        body.addProperty("amount", amount);
        return this.post("/mc/bank/withdraw", body);
    }

    public ApiResponse issueFine(String adminUuid, String targetUuid, int amount, String reason, int deadlineHours) {
        JsonObject body = new JsonObject();
        body.addProperty("issued_by", adminUuid);
        body.addProperty("uuid", targetUuid);
        body.addProperty("amount", amount);
        body.addProperty("reason", reason);
        String deadline = Instant.now().plusSeconds((long)deadlineHours * 3600L).toString();
        body.addProperty("deadline", deadline);
        return this.post("/mc/fines/issue", body);
    }

    public ApiResponse issueWarn(String adminUuid, String targetUuid, String reason) {
        JsonObject body = new JsonObject();
        body.addProperty("issued_by", adminUuid);
        body.addProperty("uuid", targetUuid);
        body.addProperty("reason", reason);
        return this.post("/mc/fines/warn", body);
    }

    public ApiResponse removeWarn(String uuid, int amount) {
        JsonObject body = new JsonObject();
        body.addProperty("uuid", uuid);
        body.addProperty("amount", amount);
        return this.post("/mc/warn/remove", body);
    }

    public ApiResponse getOverdueFines() {
        return this.get("/mc/fines/overdue");
    }

    public ApiResponse getPlayerFines(String uuid) {
        return this.get("/mc/fines/" + uuid);
    }

    public ApiResponse getDiscordId(String nickname) {
        return this.get("/mc/player/discord-id?nickname=" + nickname);
    }

    public ApiResponse getOwnedCommunity(String discordId) {
        return this.get("/web/communities/owned?discord_id=" + discordId);
    }

    public ApiResponse inviteToComm(int communityId, String discordId, String targetNickname) {
        JsonObject body = new JsonObject();
        body.addProperty("discord_id", discordId);
        body.addProperty("nickname", targetNickname);
        return this.post("/web/communities/" + communityId + "/invite", body);
    }

    public ApiResponse get(String path) {
        Request request = new Request.Builder().url(this.baseUrl + path).header("X-Plugin-Secret", this.secret).build();
        return this.execute(request);
    }

    public ApiResponse post(String path, String jsonBody) {
        RequestBody rb = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder().url(this.baseUrl + path).header("X-Plugin-Secret", this.secret).post(rb).build();
        return this.execute(request);
    }

    private ApiResponse post(String path, JsonObject body) {
        return this.post(path, this.gson.toJson(body));
    }

    private ApiResponse execute(Request request) {
        Response response = null;
        try {
            response = this.http.newCall(request).execute();
            String bodyStr = response.body() != null ? response.body().string() : "{}";
            JsonObject json = JsonParser.parseString(bodyStr).getAsJsonObject();
            ApiResponse apiResponse = new ApiResponse(response.code(), json);
            if (response != null) {
                response.close();
            }
            return apiResponse;
        }
        catch (Throwable bodyStr) {
            try {
                if (response != null) {
                    try {
                        response.close();
                    }
                    catch (Throwable throwable) {
                        bodyStr.addSuppressed(throwable);
                    }
                }
                throw bodyStr;
            }
            catch (IOException e) {
                JsonObject err = new JsonObject();
                err.addProperty("error", e.getMessage());
                return new ApiResponse(0, err);
            }
        }
    }

    public static class ApiResponse {
        public final int code;
        public final JsonObject data;

        public ApiResponse(int code, JsonObject data) {
            this.code = code;
            this.data = data;
        }

        public boolean isSuccess() {
            return this.code >= 200 && this.code < 300;
        }

        public String getMessage() {
            if (this.data.has("message")) {
                return this.data.get("message").getAsString();
            }
            if (this.data.has("detail")) {
                return this.data.get("detail").getAsString();
            }
            if (this.data.has("error")) {
                return this.data.get("error").getAsString();
            }
            return "\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u0430\u044f \u043e\u0448\u0438\u0431\u043a\u0430";
        }

        public int getInt(String key) {
            return this.data.has(key) ? this.data.get(key).getAsInt() : 0;
        }

        public String getString(String key) {
            return this.data.has(key) ? this.data.get(key).getAsString() : "";
        }
    }
}
