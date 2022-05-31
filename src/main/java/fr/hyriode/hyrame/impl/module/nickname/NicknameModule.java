package fr.hyriode.hyrame.impl.module.nickname;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.nickname.IHyriNickname;
import fr.hyriode.api.player.nickname.IHyriNicknameManager;
import fr.hyriode.api.rank.type.HyriPlayerRankType;
import fr.hyriode.api.util.Skin;
import fr.hyriode.hyrame.HyrameLogger;
import fr.hyriode.hyrame.impl.Hyrame;
import fr.hyriode.hyrame.language.HyriLanguageMessage;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.hyrame.utils.ProfileLoader;
import fr.hyriode.hyrame.utils.ThreadUtil;
import fr.hyriode.hyrame.utils.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 21:16
 */
public class NicknameModule {

    public static final List<HyriPlayerRankType> AVAILABLE_RANKS = Arrays.asList(HyriPlayerRankType.PLAYER, HyriPlayerRankType.VIP, HyriPlayerRankType.VIP_PLUS, HyriPlayerRankType.EPIC);

    private final List<String> randomNicknames;
    private final File nicknamesFile;

    private final List<Skin> randomSkins;
    private final File skinsFile;

    private final JavaPlugin plugin;

    public NicknameModule(Hyrame hyrame, JavaPlugin plugin) {
        this.randomNicknames = new ArrayList<>();
        this.nicknamesFile = new File(plugin.getDataFolder(), "nicknames.txt");
        this.randomSkins = new ArrayList<>();
        this.skinsFile = new File(plugin.getDataFolder(), "skins.txt");
        this.plugin = plugin;

        if (!this.nicknamesFile.exists()) {
            try {
                this.nicknamesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!this.skinsFile.exists()) {
            try {
                this.skinsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.loadRandomNicknames();
        this.loadRandomSkins();

        plugin.getServer().getPluginManager().registerEvents(new NicknameHandler(hyrame.getTabListManager().getRanksHandler(), this), plugin);
    }

    private void loadRandomNicknames() {
        HyrameLogger.log("Loading random nicknames...");

        try (final BufferedReader reader = new BufferedReader(new FileReader(this.nicknamesFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                this.randomNicknames.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HyrameLogger.log("Loaded random nicknames (" + this.randomNicknames.size() + ").");
    }

    private void loadRandomSkins() {
        HyrameLogger.log("Loading random skins...");

        try (final BufferedReader reader = new BufferedReader(new FileReader(this.skinsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] splitted = line.split(";");

                if (splitted.length == 2) {
                    this.randomSkins.add(new Skin(splitted[0], splitted[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HyrameLogger.log("Loaded random skins (" + this.randomSkins.size() + ").");
    }

    public Result checkNickname(UUID playerId, String nickname) {
        final IHyriNicknameManager nicknameManager = HyriAPI.get().getPlayerManager().getNicknameManager();
        final UUID user = nicknameManager.getPlayerUsingNickname(nickname);

        if (user != null && user.equals(playerId)) {
            return Result.FINE;
        }

        if (!nicknameManager.isNicknameAvailable(nickname)) {
            return Result.ALREADY_IN_USE;
        }

        if (new UUIDFetcher().getUUID(nickname, true) != null) {
            return Result.PLAYER_EXISTS;
        }
        return Result.FINE;
    }

    public void processNickname(Player player, String nick, String skinOwner, Skin skin, HyriPlayerRankType rankType) {
        ThreadUtil.ASYNC_EXECUTOR.execute(() -> {
            final UUID playerId = player.getUniqueId();

            Result result = this.checkNickname(playerId, nick);

            if (result != Result.FINE) {
                player.sendMessage(result.getMessage(player));
                return;
            }

            final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(playerId);
            final IHyriNickname nickname = account.createNickname(nick, skinOwner, skin);

            result = this.applyNickname(player, nickname.getName(), skin);

            if (result == Result.FINE) {
                nickname.setRank(rankType);
                nickname.update(account);
                account.update();

                HyriAPI.get().getPlayerManager().getNicknameManager().addUsedNickname(nick, playerId);
            }

            player.sendMessage(result.getMessage(player).replace("%nickname%", nick));
        });
    }

    public void processNickname(Player player) {
        this.processNickname(player, this.getRandomNickname(true), null, this.getRandomSkin(), HyriPlayerRankType.PLAYER);
    }

    public Result applyNickname(Player player, String nickname, String textureData, String textureSignature) {
        final GameProfile profile = PlayerUtil.setName(player, nickname);

        if (textureData != null && textureSignature != null) {
            profile.getProperties().clear();
            profile.getProperties().put("textures", new Property("textures", textureData, textureSignature));
        }

        ThreadUtil.backOnMainThread(this.plugin, () -> {
            PlayerUtil.reloadSkin(this.plugin, player);

            for (Player target : Bukkit.getOnlinePlayers()) {
                target.hidePlayer(player);
                target.showPlayer(player);
            }
        });
        return Result.FINE;
    }

    public Result applyNickname(Player player, String nickname, Skin skin) {
        if (skin != null) {
            return this.applyNickname(player, nickname, skin.getTextureData(), skin.getTextureSignature());
        }
        return null;
    }

    public Result applyNickname(Player player, String nickname) {
        final GameProfile profile = new ProfileLoader(nickname, ProfileLoader.REDIS_KEY).loadProfile();
        final Property textures = profile.getProperties().get("textures").iterator().next();

        return this.applyNickname(player, nickname, textures.getValue(), textures.getSignature());
    }

    public void resetNickname(Player player) {
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(player.getUniqueId());
        final String playerName = account.getName();

        HyriAPI.get().getPlayerManager().getNicknameManager().removeUsedNickname(player.getName());

        account.setNickname(null);
        account.update();

        this.applyNickname(player, playerName);
    }

    public Skin getSkinFromPlayer(String player) {
        final GameProfile skinProfile = new ProfileLoader(player, ProfileLoader.REDIS_KEY).loadProfile();

        if (skinProfile != null) {
            final Property textures = skinProfile.getProperties().get("textures").iterator().next();

            return new Skin(textures.getValue(), textures.getSignature());
        }
        return null;
    }

    public String getRandomNickname(boolean mojangCheck) {
        String randomNickname = null;
        while (randomNickname == null) {
            final int index = ThreadLocalRandom.current().nextInt(this.randomNicknames.size());

            randomNickname  = this.randomNicknames.get(index);

            if (!HyriAPI.get().getPlayerManager().getNicknameManager().isNicknameAvailable(randomNickname) || (mojangCheck && new UUIDFetcher().getUUID(randomNickname, false) != null)) {
                randomNickname = null;
            }
        }
        return randomNickname;
    }

    public Skin getRandomSkin() {
        return this.randomSkins.get(ThreadLocalRandom.current().nextInt(this.randomSkins.size()));
    }

    public enum Result {

        INVALID_NICKNAME(""),
        PLAYER_EXISTS("message.nickname.player-exists"),
        ALREADY_IN_USE("message.nickname.already-in-use"),
        FINE("message.nickname.fine");

        private final HyriLanguageMessage message;

        Result(String messageKey) {
            this.message = HyriLanguageMessage.get(messageKey);
        }

        public String getMessage(Player player) {
            return this.message.getForPlayer(player);
        }

    }

}
