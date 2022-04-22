package fr.hyriode.hyrame.utils;

import com.google.common.collect.ImmutableList;
import fr.hyriode.api.HyriAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 22/04/2022 at 07:35
 */
public class UUIDFetcher {

    public UUID getUUID(String name, boolean allowMojangCheck) {
        final Player player = Bukkit.getPlayer(name);

        if (player != null) {
            return player.getUniqueId();
        }

        final UUID uuid = HyriAPI.get().getRedisProcessor().get(jedis -> {
            final String uuidStr = jedis.get("uuid-cache:" + name.toLowerCase());

            if (uuidStr != null) {
                return UUID.fromString(uuidStr);
            }
            return null;
        });

        if (uuid != null) {
            return uuid;
        }

        if (!allowMojangCheck) {
            return null;
        }

        try {
            final Map<String, UUID> map = new Requester(Collections.singletonList(name)).call();

            for (Map.Entry<String, UUID> entry : map.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(name)) {
                    final UUID toReturn = entry.getValue();

                    HyriAPI.get().getRedisProcessor().process(jedis -> {
                        final String key = "uuid-cache:" + entry.getKey().toLowerCase();

                        jedis.set(key, toReturn.toString());
                        jedis.expire(key, 345600);
                    });

                    return toReturn;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private static class Requester implements Callable<Map<String, UUID>> {

        private static final double PROFILES_PER_REQUEST = 100;
        private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";

        private final List<String> names;
        private final boolean rateLimiting;

        public Requester(List<String> names, boolean rateLimiting) {
            this.names = ImmutableList.copyOf(names);
            this.rateLimiting = rateLimiting;
        }

        public Requester(List<String> names)
        {
            this(names, true);
        }

        private void writeBody(HttpURLConnection connection, String body) throws Exception {
            final OutputStream stream = connection.getOutputStream();

            stream.write(body.getBytes(StandardCharsets.UTF_8));
            stream.flush();
            stream.close();
        }

        private HttpURLConnection createConnection() throws Exception {
            final URL url = new URL(PROFILE_URL);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            return connection;
        }

        private UUID getUUID(String id) {
            return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
        }

        public Map<String, UUID> call() throws Exception {
            final Map<String, UUID> uuidMap = new HashMap<>();
            final int requests = (int) Math.ceil(names.size() / PROFILES_PER_REQUEST);

            for (int i = 0; i < requests; i++) {
                final HttpURLConnection connection = this.createConnection();
                final String body = HyriAPI.GSON.toJson(names.subList(i * 100, Math.min((i + 1) * 100, names.size())));

                this.writeBody(connection, body);

                final Profile[] profiles = HyriAPI.GSON.fromJson(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8), Profile[].class);

                for (Profile profile : profiles) {
                    uuidMap.put(profile.name, this.getUUID(profile.getId()));
                }

                if (rateLimiting && i != requests - 1) {
                    Thread.sleep(100L);
                }
            }
            return uuidMap;
        }

        private static class Profile {

            private final String id;
            private final String name;

            public Profile(String id, String name) {
                this.id = id;
                this.name = name;
            }

            public String getId() {
                return this.id;
            }

            public String getName() {
                return this.name;
            }

        }
    }

}
