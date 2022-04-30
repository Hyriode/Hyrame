package fr.hyriode.hyrame.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:25
 */
public class ProfileLoader {

    public static final String REDIS_KEY = "game-profiles:";
    private static final String CACHED_SKINS = "cached-skins:";

    /** Profile's {@link UUID} */
    private final UUID uuid;
    /** Profile's name */
    private final String name;
    /** Skin owner */
    private final UUID skinOwnerId;
    /** The redis key of the profiles */
    private final String redisKey;

    /**
     * Full constructor of {@link ProfileLoader}
     *
     * @param uuid Profile's uuid
     * @param name Profile's name
     * @param skinOwner Skin owner
     * @param redisKey Profiles redis key
     */
    public ProfileLoader(UUID uuid, String name, String skinOwner, String redisKey) {
        this.uuid = uuid;
        this.name = name;
        this.skinOwnerId = this.getUUID(skinOwner);
        this.redisKey = redisKey;
    }

    /**
     * Constructor of {@link ProfileLoader}
     *
     * @param skinOwnerId Skin owner id
     * @param redisKey Profiles redis key
     */
    public ProfileLoader(UUID skinOwnerId, String redisKey) {
        this.uuid = UUID.randomUUID();
        this.name = this.uuid.toString();
        this.skinOwnerId = skinOwnerId;
        this.redisKey = redisKey;
    }

    /**
     * Constructor of {@link ProfileLoader}
     *
     * @param skinOwner Skin owner
     * @param redisKey Profiles redis key
     */
    public ProfileLoader(String skinOwner, String redisKey) {
        this.uuid = UUID.randomUUID();
        this.name = this.uuid.toString();
        this.skinOwnerId = this.getUUID(skinOwner);
        this.redisKey = redisKey;
    }

    /**
     * Constructor of {@link ProfileLoader}
     *
     * @param uuid Profile's uuid
     * @param name Profile's name
     * @param skinOwner Skin owner
     */
    public ProfileLoader(UUID uuid, String name, String skinOwner) {
        this(uuid, name, skinOwner, REDIS_KEY);
    }

    public GameProfile loadFromMojang() {
        return MinecraftServer.getServer().aD().fillProfileProperties(new GameProfile(this.skinOwnerId, null), true);
    }

    /**
     * Load a profile from Redis
     *
     * @return A {@link GameProfile} object
     */
    @SuppressWarnings("deprecation")
    public GameProfile loadProfile() {
        return HyriAPI.get().getRedisProcessor().get(jedis -> {
            final GameProfile skinProfile = new GameProfile(this.uuid, this.name);
            final String key = this.redisKey + CACHED_SKINS;
            final String json = jedis.get(key + this.skinOwnerId);

            GameProfile profile;
            if (json == null) {
                profile = this.loadFromMojang();

                if (profile != null) {
                    saveProfile(profile);

                    skinProfile.getProperties().putAll(profile.getProperties());
                }
            } else {
                System.out.println("Loading skin from Redis");
                final JsonArray parse = new JsonParser().parse(json).getAsJsonArray();

                for (JsonElement element : parse) {
                    final Property property = new Gson().fromJson(element.toString(), Property.class);

                    skinProfile.getProperties().put(property.getName(), property);
                }
            }
            return skinProfile;
        });
    }

    @SuppressWarnings("deprecation")
    private UUID getUUID(String name) {
        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }

    public static void saveProfile(GameProfile profile) {
        HyriAPI.get().getRedisProcessor().process(jedis -> {
            if (profile == null) {
                return;
            }

            final JsonArray jsonArray = new JsonArray();

            for (Property property : profile.getProperties().values()) {
                jsonArray.add(new Gson().toJsonTree(property));
            }

            jedis.set(REDIS_KEY + CACHED_SKINS + profile.getId(), jsonArray.toString());
        });
    }

    public static void savePlayerProfile(Player player) {
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());

        if (!account.hasNickname()) {
            saveProfile(((CraftPlayer) player).getProfile());
        }
    }

}
