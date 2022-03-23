package fr.hyriode.hyrame.npc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.hyriode.api.HyriAPI;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class NPCProfileLoader {

    /** Profiles redis key */
    private String redisKey;

    /** Skin owner */
    private final String skinOwner;

    /** Profile's name */
    private final String name;

    /** Profile's {@link UUID} */
    private final UUID uuid;

    /**
     * Constructor of {@link NPCProfileLoader}
     *
     * @param uuid - Profile's uuid
     * @param name - Profile's name
     * @param skinOwner - Skin owner
     */
    public NPCProfileLoader(UUID uuid, String name, String skinOwner) {
        this.uuid = uuid;
        this.name = name;
        this.skinOwner = this.getUUID(skinOwner);
    }

    /**
     * Constructor of {@link NPCProfileLoader}
     *
     * @param uuid - Profile's uuid
     * @param name - Profile's name
     * @param skinOwner - Skin owner
     * @param redisKey - Profiles redis key
     */
    public NPCProfileLoader(UUID uuid, String name, String skinOwner, String redisKey) {
        this.uuid = uuid;
        this.name = name;
        this.skinOwner = this.getUUID(skinOwner);
        this.redisKey = redisKey;
    }

    /**
     * Load a profile without Redis
     *
     * @return - A {@link GameProfile} object
     */
    public GameProfile loadProfile() {
        final UUID uuid = UUID.fromString(this.skinOwner);
        final GameProfile skinProfile = new GameProfile(this.uuid, this.name);

        skinProfile.getProperties().putAll(MinecraftServer.getServer().aD().fillProfileProperties(new GameProfile(uuid, null), true).getProperties());

        return skinProfile;
    }

    /**
     * Load a profile from Redis
     *
     * @return A {@link GameProfile} object
     */
    public GameProfile loadProfileFromRedis() {
        try (final Jedis jedis = HyriAPI.get().getRedisResource()) {
            final UUID uuid = UUID.fromString(this.skinOwner);
            final GameProfile skinProfile = new GameProfile(this.uuid, this.name);
            final String key = this.redisKey + "cacheSkin:";
            final String json = jedis.get(key + uuid);

            GameProfile profile;
            if (json == null) {
                profile = MinecraftServer.getServer().aD().fillProfileProperties(new GameProfile(uuid, null), true);

                if (profile.getName() != null) {
                    final JsonArray jsonArray = new JsonArray();

                    for (Property property : profile.getProperties().values()) {
                        jsonArray.add(new Gson().toJsonTree(property));
                    }

                    jedis.set(key + uuid, jsonArray.toString());
                    jedis.expire(key + uuid, 172800L);
                }

                skinProfile.getProperties().putAll(profile.getProperties());
            } else {
                final JsonArray parse = new JsonParser().parse(json).getAsJsonArray();

                for (JsonElement element : parse) {
                    final Property property = new Gson().fromJson(element.toString(), Property.class);

                    skinProfile.getProperties().put(property.getName(), property);
                }
            }
            return skinProfile;
        }
    }

    @SuppressWarnings("deprecation")
    private String getUUID(String name) {
        return Bukkit.getOfflinePlayer(name).getUniqueId().toString();
    }

}
