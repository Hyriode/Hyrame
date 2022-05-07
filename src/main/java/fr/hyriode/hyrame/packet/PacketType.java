package fr.hyriode.hyrame.packet;

import fr.hyriode.hyrame.HyrameLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 06/05/2022 at 18:12
 */
public class PacketType {

    public static final PacketType ALL = new PacketType(null, null, null);

    public static class Handshake {

        public static final Protocol PROTOCOL = Protocol.HANDSHAKING;

        public static class Client {

            public static final Bound BOUND = Bound.CLIENT;
            private static final Client INSTANCE = new Client();

            public static final PacketType SET_PROTOCOL = new PacketType(PROTOCOL, BOUND, "SetProtocol");

        }

    }

    public static class Status {

        public static final Protocol PROTOCOL = Protocol.STATUS;

        public static class Client {

            public static final Bound BOUND = Bound.CLIENT;

            public static final PacketType START = new PacketType(PROTOCOL, BOUND, "Start");
            public static final PacketType PING = new PacketType(PROTOCOL, BOUND, "Ping");

        }

        public static class Server {

            public static final Bound BOUND = Bound.SERVER;

            public static final PacketType START = new PacketType(PROTOCOL, BOUND, "ServerInfo");
            public static final PacketType PING = new PacketType(PROTOCOL, BOUND, "Pong");

        }

    }

    public static class Login {

        public static final Protocol PROTOCOL = Protocol.LOGIN;

        public static class Client {

            public static final Bound BOUND = Bound.CLIENT;

            public static final PacketType START = new PacketType(PROTOCOL, BOUND, "Disconnect");
            public static final PacketType ENCRYPTION_BEGIN = new PacketType(PROTOCOL, BOUND, "EncryptionBegin");
            public static final PacketType SUCCESS = new PacketType(PROTOCOL, BOUND, "Success");
            public static final PacketType SET_COMPRESSION = new PacketType(PROTOCOL, BOUND, "SetCompression");
            public static final PacketType CUSTOM_PAYLOAD = new PacketType(PROTOCOL, BOUND, "CustomPayload");

        }

        public static class Server {

            public static final Bound BOUND = Bound.SERVER;

            public static final PacketType START = new PacketType(PROTOCOL, BOUND, "Start");
            public static final PacketType ENCRYPTION_BEGIN = new PacketType(PROTOCOL, BOUND, "EncryptionBegin");
            public static final PacketType CUSTOM_PAYLOAD = new PacketType(PROTOCOL, BOUND, "CustomPayload");

        }

    }

    public static class Play {

        public static final Protocol PROTOCOL = Protocol.PLAY;

        public static class Client {

            public static final Bound BOUND = Bound.CLIENT;

            public static final PacketType TELEPORT_ACCEPT = new PacketType(PROTOCOL, BOUND, "TeleportAccept");
            public static final PacketType TILE_NBT_QUERY = new PacketType(PROTOCOL, BOUND, "TileNBTQuery");
            public static final PacketType DIFFICULT_CHANGE = new PacketType(PROTOCOL, BOUND, "DifficultyChange");
            public static final PacketType CHAT = new PacketType(PROTOCOL, BOUND, "Chat");
            public static final PacketType CLIENT_COMMAND = new PacketType(PROTOCOL, BOUND, "ClientCommand");
            public static final PacketType SETTINGS = new PacketType(PROTOCOL, BOUND, "Settings");
            public static final PacketType TAB_COMPLETE = new PacketType(PROTOCOL, BOUND, "TabComplete");
            public static final PacketType ENCHANT_ITEM = new PacketType(PROTOCOL, BOUND, "EnchantItem");
            public static final PacketType WINDOW_CLICK = new PacketType(PROTOCOL, BOUND, "WindowClick");
            public static final PacketType CLOSE_WINDOW = new PacketType(PROTOCOL, BOUND, "CloseWindow");
            public static final PacketType CUSTOM_PAYLOAD = new PacketType(PROTOCOL, BOUND, "CustomPayload");
            public static final PacketType B_EDIT = new PacketType(PROTOCOL, BOUND, "BEdit");
            public static final PacketType ENTITY_NBT_QUERY = new PacketType(PROTOCOL, BOUND, "EntityNBTQuery");
            public static final PacketType USE_ENTITY = new PacketType(PROTOCOL, BOUND, "UseEntity");
            public static final PacketType JIGSAW_GENERATE = new PacketType(PROTOCOL, BOUND, "JigsawGenerate");
            public static final PacketType KEEP_ALIVE = new PacketType(PROTOCOL, BOUND, "KeepAlive");
            public static final PacketType DIFFICULTY_LOCK = new PacketType(PROTOCOL, BOUND, "DifficultyLock");
            public static final PacketType POSITION = new PacketType(PROTOCOL, BOUND, "Flying$PacketPlayInPosition");
            public static final PacketType POSITION_LOOK = new PacketType(PROTOCOL, BOUND, "Flying$PacketPlayInPositionLook");
            public static final PacketType LOOK = new PacketType(PROTOCOL, BOUND, "Flying$PacketPlayInLook");
            public static final PacketType GROUND = new PacketType(PROTOCOL, BOUND, "Flying$d");
            public static final PacketType VEHICLE_MOVE = new PacketType(PROTOCOL, BOUND, "VehicleMove");
            public static final PacketType BOAT_MOVE = new PacketType(PROTOCOL, BOUND, "BoatMove");
            public static final PacketType PICK_ITEM = new PacketType(PROTOCOL, BOUND, "PickItem");
            public static final PacketType AUTO_RECIPE = new PacketType(PROTOCOL, BOUND, "AutoRecipe");
            public static final PacketType ABILITIES = new PacketType(PROTOCOL, BOUND, "Abilities");
            public static final PacketType BLOCK_DIG =  new PacketType(PROTOCOL, BOUND, "BlockDig");
            public static final PacketType ENTITY_ACTION = new PacketType(PROTOCOL, BOUND, "EntityAction");
            public static final PacketType STEER_VEHICLE = new PacketType(PROTOCOL, BOUND, "SteerVehicle");
            public static final PacketType PONG = new PacketType(PROTOCOL, BOUND, "Pong");
            public static final PacketType RECIPE_SETTINGS = new PacketType(PROTOCOL, BOUND, "RecipeSettings");
            public static final PacketType RECIPE_DISPLAYED = new PacketType(PROTOCOL, BOUND, "RecipeDisplayed");
            public static final PacketType ITEM_NAME = new PacketType(PROTOCOL, BOUND, "ItemName");
            public static final PacketType RESOURCE_PACK_STATUS = new PacketType(PROTOCOL, BOUND, "ResourcePackStatus");
            public static final PacketType ADVANCEMENTS = new PacketType(PROTOCOL, BOUND, "Advancements");
            public static final PacketType TR_SEL = new PacketType(PROTOCOL, BOUND, "TrSel");
            public static final PacketType BEACON = new PacketType(PROTOCOL, BOUND, "Beacon");
            public static final PacketType HELD_ITEM_SLOT = new PacketType(PROTOCOL, BOUND, "HeldItemSlot");
            public static final PacketType SET_COMMAND_BLOCK = new PacketType(PROTOCOL, BOUND, "SetCommandBlock");
            public static final PacketType SET_COMMAND_MINECART = new PacketType(PROTOCOL, BOUND, "SetCommandMinecart");
            public static final PacketType SET_CREATIVE_SLOT = new PacketType(PROTOCOL, BOUND, "SetCreativeSlot");
            public static final PacketType SET_JIGSAW = new PacketType(PROTOCOL, BOUND, "SetJigsaw");
            public static final PacketType STRUCT = new PacketType(PROTOCOL, BOUND, "Struct");
            public static final PacketType UPDATE_SIGN = new PacketType(PROTOCOL, BOUND, "UpdateSign");
            public static final PacketType ARM_ANIMATION = new PacketType(PROTOCOL, BOUND, "ArmAnimation");
            public static final PacketType SPECTATE = new PacketType(PROTOCOL, BOUND, "Spectate");
            public static final PacketType USE_ITEM = new PacketType(PROTOCOL, BOUND, "UseItem");
            public static final PacketType BLOCK_PLACE = new PacketType(PROTOCOL, BOUND, "BlockPlace");

        }

        public static class Server {

            public static final Bound BOUND = Bound.SERVER;

            public static final PacketType SPAWN_ENTITY = new PacketType(PROTOCOL, BOUND, "SpawnEntity");
            public static final PacketType SPAWN_ENTITY_EXPERIENCE_ORB = new PacketType(PROTOCOL, BOUND, "SpawnEntityExperienceOrb");
            public static final PacketType SPAWN_ENTITY_LIVING = new PacketType(PROTOCOL, BOUND, "SpawnEntityLiving");
            public static final PacketType SPAWN_ENTITY_PAINTING = new PacketType(PROTOCOL, BOUND, "SpawnEntityPainting");
            public static final PacketType NAMED_ENTITY_SPAWN = new PacketType(PROTOCOL, BOUND, "NamedEntitySpawn");
            public static final PacketType ADD_VIBRATION_SIGNAL = new PacketType(PROTOCOL, BOUND, "AddVibrationSignal");
            public static final PacketType ANIMATION = new PacketType(PROTOCOL, BOUND, "Animation");
            public static final PacketType STATISTIC = new PacketType(PROTOCOL, BOUND, "Statistic");
            public static final PacketType BLOCK_BREAK = new PacketType(PROTOCOL, BOUND, "BlockBreak");
            public static final PacketType BLOCK_BREAK_ANIMATION = new PacketType(PROTOCOL, BOUND, "BlockBreakAnimation");
            public static final PacketType TILE_ENTITY_DATA = new PacketType(PROTOCOL, BOUND, "TileEntityData");
            public static final PacketType BLOCK_ACTION = new PacketType(PROTOCOL, BOUND, "BlockAction");
            public static final PacketType BLOCK_CHANGE = new PacketType(PROTOCOL, BOUND, "BlockChange");
            public static final PacketType BOSS = new PacketType(PROTOCOL, BOUND, "Boss");
            public static final PacketType SERVER_DIFFICULTY = new PacketType(PROTOCOL, BOUND, "ServerDifficulty");
            public static final PacketType CHAT = new PacketType(PROTOCOL, BOUND, "Chat");
            public static final PacketType CLEAR_TITLES = new PacketType(PROTOCOL, BOUND, "ClearTitles");
            public static final PacketType TAB_COMPLETE = new PacketType(PROTOCOL, BOUND, "TabComplete");
            public static final PacketType COMMANDS = new PacketType(PROTOCOL, BOUND, "Commands");
            public static final PacketType CLOSE_WINDOW = new PacketType(PROTOCOL, BOUND, "CloseWindow");
            public static final PacketType WINDOW_ITEMS = new PacketType(PROTOCOL, BOUND, "WindowItems");
            public static final PacketType WINDOW_DATA = new PacketType(PROTOCOL, BOUND, "WindowData");
            public static final PacketType SET_SLOT = new PacketType(PROTOCOL, BOUND, "SetSlot");
            public static final PacketType SET_COOLDOWN = new PacketType(PROTOCOL, BOUND, "SetCooldown");
            public static final PacketType CUSTOM_PAYLOAD = new PacketType(PROTOCOL, BOUND, "CustomPayload");
            public static final PacketType CUSTOM_SOUND_EFFECT = new PacketType(PROTOCOL, BOUND, "CustomSoundEffect");
            public static final PacketType KICK_DISCONNECT = new PacketType(PROTOCOL, BOUND, "KickDisconnect");
            public static final PacketType ENTITY_STATUS = new PacketType(PROTOCOL, BOUND,  "EntityStatus");
            public static final PacketType EXPLOSION = new PacketType(PROTOCOL, BOUND, "Explosion");
            public static final PacketType UNLOAD_CHUNK = new PacketType(PROTOCOL, BOUND, "UnloadChunk");
            public static final PacketType GAME_STATE_CHANGE = new PacketType(PROTOCOL, BOUND, "GameStateChange");
            public static final PacketType OPEN_WINDOW_HORSE = new PacketType(PROTOCOL, BOUND, "OpenWindowHorse");
            public static final PacketType INITIALIZE_BORDER = new PacketType(PROTOCOL, BOUND, "InitializeBorder");
            public static final PacketType KEEP_ALIVE = new PacketType(PROTOCOL, BOUND, "KeepAlive");
            public static final PacketType MAP_CHUNK = new PacketType(PROTOCOL, BOUND, "MapChunk");
            public static final PacketType WORLD_EVENT = new PacketType(PROTOCOL, BOUND, "WorldEvent");
            public static final PacketType WORLD_PARTICLES = new PacketType(PROTOCOL, BOUND, "WorldParticles");
            public static final PacketType LIGHT_UPDATE = new PacketType(PROTOCOL, BOUND, "LightUpdate");
            public static final PacketType LOGIN = new PacketType(PROTOCOL, BOUND, "Login");
            public static final PacketType MAP = new PacketType(PROTOCOL, BOUND, "Map");
            public static final PacketType OPEN_WINDOW_MERCHANT = new PacketType(PROTOCOL, BOUND, "OpenWindowMerchant");
            public static final PacketType REL_ENTITY_MOVE = new PacketType(PROTOCOL, BOUND, "Entity$PacketPlayOutRelEntityMove");
            public static final PacketType REL_ENTITY_MOVE_LOOK = new PacketType(PROTOCOL, BOUND, "Entity$PacketPlayOutRelEntityMoveLook");
            public static final PacketType ENTITY_LOOK = new PacketType(PROTOCOL, BOUND, "Entity$PacketPlayOutEntityLook");
            public static final PacketType VEHICLE_MOVE = new PacketType(PROTOCOL, BOUND, "VehicleMove");
            public static final PacketType OPEN_BOOK = new PacketType(PROTOCOL, BOUND, "OpenBook");
            public static final PacketType OPEN_WINDOW = new PacketType(PROTOCOL, BOUND, "OpenWindow");
            public static final PacketType OPEN_SIGN_EDITOR = new PacketType(PROTOCOL, BOUND, "OpenSignEditor");
            public static final PacketType PING = new PacketType(PROTOCOL, BOUND, "Ping");
            public static final PacketType AUTO_RECIPE = new PacketType(PROTOCOL, BOUND, "AutoRecipe");
            public static final PacketType ABILITIES = new PacketType(PROTOCOL, BOUND, "Abilities");
            public static final PacketType PLAYER_COMBAT_END = new PacketType(PROTOCOL, BOUND, "PlayerCombatEnd");
            public static final PacketType PLAYER_COMBAT_ENTER = new PacketType(PROTOCOL, BOUND, "PlayerCombatEnter");
            public static final PacketType PLAYER_COMBAT_KILL = new PacketType(PROTOCOL, BOUND, "PlayerCombatKill");
            public static final PacketType PLAYER_INFO =  new PacketType(PROTOCOL, BOUND, "PlayerInfo");
            public static final PacketType LOOK_AT = new PacketType(PROTOCOL, BOUND, "LookAt");
            public static final PacketType POSITION = new PacketType(PROTOCOL, BOUND, "Position");
            public static final PacketType RECIPES = new PacketType(PROTOCOL, BOUND, "Recipes");
            public static final PacketType ENTITY_DESTROY = new PacketType(PROTOCOL, BOUND, "EntityDestroy");
            public static final PacketType REMOVE_ENTITY_EFFECT = new PacketType(PROTOCOL, BOUND, "RemoveEntityEffect");
            public static final PacketType RESOURCE_PACK_SEND = new PacketType(PROTOCOL, BOUND, "ResourcePackSend");
            public static final PacketType RESPAWN = new PacketType(PROTOCOL, BOUND, "Respawn");
            public static final PacketType ENTITY_HEAD_ROTATION = new PacketType(PROTOCOL, BOUND, "EntityHeadRotation");
            public static final PacketType MULTI_BLOCK_CHANGE = new PacketType(PROTOCOL, BOUND, "MultiBlockChange");
            public static final PacketType SELECT_ADVANCEMENT_TAB = new PacketType(PROTOCOL, BOUND, "SelectAdvancementTab");
            public static final PacketType SET_ACTION_BAR_TEXT = new PacketType(PROTOCOL, BOUND, "SetActionBarText");
            public static final PacketType SET_BORDER_CENTER = new PacketType(PROTOCOL, BOUND, "SetBorderCenter");
            public static final PacketType SET_BORDER_LERP_SIZE = new PacketType(PROTOCOL, BOUND, "SetBorderLerpSize");
            public static final PacketType SET_BORDER_SIZE = new PacketType(PROTOCOL, BOUND, "SetBorderSize");
            public static final PacketType SET_BORDER_WARNING_DELAY = new PacketType(PROTOCOL, BOUND, "SetBorderWarningDelay");
            public static final PacketType SET_BORDER_WARNING_DISTANCE = new PacketType(PROTOCOL, BOUND, "SetBorderWarningDistance");
            public static final PacketType CAMERA = new PacketType(PROTOCOL, BOUND, "Camera");
            public static final PacketType HELD_ITEM_SLOT = new PacketType(PROTOCOL, BOUND, "HeldItemSlot");
            public static final PacketType VIEW_CENTRE = new PacketType(PROTOCOL, BOUND, "ViewCentre");
            public static final PacketType VIEW_DISTANCE = new PacketType(PROTOCOL, BOUND, "ViewDistance");
            public static final PacketType SPAWN_POSITION = new PacketType(PROTOCOL, BOUND, "SpawnPosition");
            public static final PacketType SCOREBOARD_DISPLAY_OBJECTIVE = new PacketType(PROTOCOL, BOUND, "ScoreboardDisplayObjective");
            public static final PacketType ENTITY_METADATA = new PacketType(PROTOCOL, BOUND, "EntityMetadata");
            public static final PacketType ATTACH_ENTITY = new PacketType(PROTOCOL, BOUND, "AttachEntity");
            public static final PacketType ENTITY_VELOCITY = new PacketType(PROTOCOL, BOUND, "EntityVelocity");
            public static final PacketType ENTITY_EQUIPMENT = new PacketType(PROTOCOL, BOUND, "EntityEquipment");
            public static final PacketType EXPERIENCE = new PacketType(PROTOCOL, BOUND, "Experience");
            public static final PacketType UPDATE_HEALTH = new PacketType(PROTOCOL, BOUND, "UpdateHealth");
            public static final PacketType SCOREBOARD_OBJECTIVE = new PacketType(PROTOCOL, BOUND, "ScoreboardObjective");
            public static final PacketType MOUNT = new PacketType(PROTOCOL, BOUND, "Mount");
            public static final PacketType SCOREBOARD_TEAM = new PacketType(PROTOCOL, BOUND, "ScoreboardTeam");
            public static final PacketType SCOREBOARD_SCORE = new PacketType(PROTOCOL, BOUND, "ScoreboardScore");
            public static final PacketType UPDATE_SIMULATION_DISTANCE = new PacketType(PROTOCOL, BOUND, "SetSimulationDistance");
            public static final PacketType SET_SUBTITLE_TEXT = new PacketType(PROTOCOL, BOUND, "SetSubtitleText");
            public static final PacketType UPDATE_TIME = new PacketType(PROTOCOL, BOUND, "UpdateTime");
            public static final PacketType SET_TITLE_TEXT = new PacketType(PROTOCOL, BOUND, "SetTitleText");
            public static final PacketType SET_TITLES_ANIMATION = new PacketType(PROTOCOL, BOUND, "SetTitlesAnimation");
            public static final PacketType ENTITY_SOUND = new PacketType(PROTOCOL, BOUND, "EntitySound");
            public static final PacketType NAMED_SOUND_EFFECT = new PacketType(PROTOCOL, BOUND, "NamedSoundEffect");
            public static final PacketType STOP_SOUND = new PacketType(PROTOCOL, BOUND, "StopSound");
            public static final PacketType PLAYER_LIST_HEADER_FOOTER = new PacketType(PROTOCOL, BOUND, "PlayerListHeaderFooter");
            public static final PacketType NBT_QUERY = new PacketType(PROTOCOL, BOUND, "NBTQuery");
            public static final PacketType COLLECT = new PacketType(PROTOCOL, BOUND, "Collect");
            public static final PacketType ENTITY_TELEPORT = new PacketType(PROTOCOL, BOUND, "EntityTeleport");
            public static final PacketType ADVANCEMENTS = new PacketType(PROTOCOL, BOUND, "Advancements");
            public static final PacketType UPDATE_ATTRIBUTES = new PacketType(PROTOCOL, BOUND, "UpdateAttributes");
            public static final PacketType ENTITY_EFFECT = new PacketType(PROTOCOL, BOUND, "EntityEffect");
            public static final PacketType RECIPE_UPDATE = new PacketType(PROTOCOL, BOUND, "RecipeUpdate");
            public static final PacketType TAGS = new PacketType(PROTOCOL, BOUND, "Tags");
            public static final PacketType MAP_CHUNK_BULK = new PacketType(PROTOCOL, BOUND, "MapChunkBulk");
            public static final PacketType SET_COMPRESSION = new PacketType(PROTOCOL, BOUND, "SetCompression");
            public static final PacketType UPDATE_ENTITY_NBT = new PacketType(PROTOCOL, BOUND, "UpdateEntityNBT");
            public static final PacketType UPDATE_SIGN = new PacketType(PROTOCOL, BOUND, "UpdateSign");
            public static final PacketType BED = new PacketType(PROTOCOL, BOUND, "Bed");
            public static final PacketType SPAWN_ENTITY_WEATHER = new PacketType(PROTOCOL, BOUND, "SpawnEntityWeather");
            public static final PacketType TITLE = new PacketType(PROTOCOL, BOUND, "Title");
            public static final PacketType WORLD_BORDER = new PacketType(PROTOCOL, BOUND, "WorldBorder");
            public static final PacketType COMBAT_EVENT = new PacketType(PROTOCOL, BOUND, "CombatEvent");
            public static final PacketType TRANSACTION = new PacketType(PROTOCOL, BOUND, "Transaction");
            public static final PacketType ENTITY = new PacketType(PROTOCOL, BOUND, "Entity");

        }

    }

    public enum Protocol {

        HANDSHAKING("Handshaking"),
        PLAY("Play"),
        STATUS("Status"),
        LOGIN("Login");

        private final String packetPart;

        Protocol(String packetPart) {
            this.packetPart = packetPart;
        }

        public String getPacketPart() {
            return this.packetPart;
        }

    }

    public enum Bound {

        CLIENT("In"),
        SERVER("Out");

        private final String packetPart;

        Bound(String packetPart) {
            this.packetPart = packetPart;
        }

        public String getPacketPart() {
            return this.packetPart;
        }

    }

    private static final List<PacketType> VALUES = new ArrayList<>();

    private final Protocol protocol;
    private final Bound bound;
    private final String packetPart;

    public PacketType(Protocol protocol, Bound bound, String packetPart) {
        this.protocol = protocol;
        this.bound = bound;
        this.packetPart = packetPart;

        if (this.protocol != null && this.bound != null && this.packetPart != null) {
            VALUES.add(this);
        }
    }

    public Protocol getProtocol() {
        return this.protocol;
    }

    public Bound getBound() {
        return this.bound;
    }

    public String getPacketPart() {
        return this.packetPart;
    }

    public String getPacketName() {
        return "Packet" + this.protocol.getPacketPart() + this.bound.getPacketPart() + this.packetPart;
    }

    public static void init() {
        HyrameLogger.log("Initializing packet types...");

        final Consumer<Class<?>> preload = clazz -> {
            try {
                Class.forName(clazz.getName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        };

        preload.accept(Handshake.Client.class);
        preload.accept(Status.Client.class);
        preload.accept(Status.Server.class);
        preload.accept(Login.Client.class);
        preload.accept(Login.Server.class);
        preload.accept(Play.Client.class);
        preload.accept(Play.Server.class);
    }

    public static Iterable<PacketType> values() {
        return VALUES;
    }

}
