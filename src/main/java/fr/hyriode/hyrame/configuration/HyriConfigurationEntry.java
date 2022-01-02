package fr.hyriode.hyrame.configuration;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 31/12/2021 at 23:06
 */
public abstract class HyriConfigurationEntry<T> {

    /** The path of the configuration entry */
    protected final String path;
    /** The Spigot {@link FileConfiguration} instance. It can be used to set and get values if you create a new entry */
    protected final FileConfiguration configuration;

    /**
     * Constructor of {@link HyriConfigurationEntry}
     *
     * @param path The path of the entry
     * @param configuration The Spigot {@link FileConfiguration}
     */
    public HyriConfigurationEntry(String path, FileConfiguration configuration) {
        this.path = path;
        this.configuration = configuration;
    }

    /**
     * Get the entry path<br>
     * You need to separate each sections with a dot.<br>
     * Example: mysection.myothersection.myentry
     *
     * @return A path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Get the Spigot default {@link FileConfiguration} instance
     *
     * @return A {@link FileConfiguration} instance
     */
    public FileConfiguration getConfiguration() {
        return this.configuration;
    }

    /**
     * Get the value of the entry
     *
     * @return The object {@link T} of the key
     */
    public abstract T get();

    /**
     * Set a new value for the entry
     *
     * @param value The new {@link T} object to set
     */
    public abstract void set(T value);

    /**
     * Set a default value for the entry
     *
     * @param value The new {@link T} object to set
     */
    public void setDefault(T value) {
        if (this.get() == null) {
            this.set(value);
        }
    }

    public static class StringEntry extends HyriConfigurationEntry<String> {

        /**
         * Constructor of {@link StringEntry}
         *
         * @param path          The path of the entry
         * @param configuration The Spigot {@link FileConfiguration}
         */
        public StringEntry(String path, FileConfiguration configuration) {
            super(path, configuration);
        }

        @Override
        public String get() {
            return this.configuration.getString(this.path);
        }

        @Override
        public void set(String value) {
            this.configuration.set(this.path, value);
        }

    }

    public static class IntegerEntry extends HyriConfigurationEntry<Integer> {

        /**
         * Constructor of {@link IntegerEntry}
         *
         * @param path          The path of the entry
         * @param configuration The Spigot {@link FileConfiguration}
         */
        public IntegerEntry(String path, FileConfiguration configuration) {
            super(path, configuration);
        }

        @Override
        public Integer get() {
            return this.configuration.getInt(this.path);
        }

        @Override
        public void set(Integer value) {
            this.configuration.set(this.path, value);
        }

    }

    public static class LongEntry extends HyriConfigurationEntry<Long> {

        /**
         * Constructor of {@link LongEntry}
         *
         * @param path          The path of the entry
         * @param configuration The Spigot {@link FileConfiguration}
         */
        public LongEntry(String path, FileConfiguration configuration) {
            super(path, configuration);
        }

        @Override
        public Long get() {
            return this.configuration.getLong(this.path);
        }

        @Override
        public void set(Long value) {
            this.configuration.set(this.path, value);
        }

    }

    public static class DoubleEntry extends HyriConfigurationEntry<Double> {

        /**
         * Constructor of {@link DoubleEntry}
         *
         * @param path          The path of the entry
         * @param configuration The Spigot {@link FileConfiguration}
         */
        public DoubleEntry(String path, FileConfiguration configuration) {
            super(path, configuration);
        }

        @Override
        public Double get() {
            return this.configuration.getDouble(this.path);
        }

        @Override
        public void set(Double value) {
            this.configuration.set(this.path, value);
        }

    }

    public static class BooleanEntry extends HyriConfigurationEntry<Boolean> {

        /**
         * Constructor of {@link BooleanEntry}
         *
         * @param path          The path of the entry
         * @param configuration The Spigot {@link FileConfiguration}
         */
        public BooleanEntry(String path, FileConfiguration configuration) {
            super(path, configuration);
        }

        @Override
        public Boolean get() {
            return this.configuration.getBoolean(this.path);
        }

        @Override
        public void set(Boolean value) {
            this.configuration.set(this.path, value);
        }

    }

    public static class LocationEntry extends HyriConfigurationEntry<Location> {

        /**
         * Constructor of {@link LocationEntry}
         *
         * @param path          The path of the entry
         * @param configuration The Spigot {@link FileConfiguration}
         */
        public LocationEntry(String path, FileConfiguration configuration) {
            super(path, configuration);
        }

        @Override
        public Location get() {
            final Object object = this.configuration.get(this.path);

            if (object instanceof Location) {
                return (Location) object;
            }
            return null;
        }

        @Override
        public void set(Location value) {
            this.configuration.set(this.path, value);
        }

    }

    public static class ColorEntry extends HyriConfigurationEntry<Color> {

        /**
         * Constructor of {@link ColorEntry}
         *
         * @param path          The path of the entry
         * @param configuration The Spigot {@link FileConfiguration}
         */
        public ColorEntry(String path, FileConfiguration configuration) {
            super(path, configuration);
        }

        @Override
        public Color get() {
            return this.configuration.getColor(this.path);
        }

        @Override
        public void set(Color value) {
            this.configuration.set(this.path, value);
        }

    }

    public static class VectorEntry extends HyriConfigurationEntry<Vector> {

        /**
         * Constructor of {@link VectorEntry}
         *
         * @param path          The path of the entry
         * @param configuration The Spigot {@link FileConfiguration}
         */
        public VectorEntry(String path, FileConfiguration configuration) {
            super(path, configuration);
        }

        @Override
        public Vector get() {
            return this.configuration.getVector(this.path);
        }

        @Override
        public void set(Vector value) {
            this.configuration.set(this.path, value);
        }

    }

    public static class ItemStackEntry extends HyriConfigurationEntry<ItemStack> {

        /**
         * Constructor of {@link ItemStackEntry}
         *
         * @param path          The path of the entry
         * @param configuration The Spigot {@link FileConfiguration}
         */
        public ItemStackEntry(String path, FileConfiguration configuration) {
            super(path, configuration);
        }

        @Override
        public ItemStack get() {
            return this.configuration.getItemStack(this.path);
        }

        @Override
        public void set(ItemStack value) {
            this.configuration.set(this.path, value);
        }

    }

    public static class OfflinePlayerEntry extends HyriConfigurationEntry<OfflinePlayer> {

        /**
         * Constructor of {@link OfflinePlayerEntry}
         *
         * @param path          The path of the entry
         * @param configuration The Spigot {@link FileConfiguration}
         */
        public OfflinePlayerEntry(String path, FileConfiguration configuration) {
            super(path, configuration);
        }

        @Override
        public OfflinePlayer get() {
            return this.configuration.getOfflinePlayer(this.path);
        }

        @Override
        public void set(OfflinePlayer value) {
            this.configuration.set(this.path, value);
        }

    }

    public static class ListEntry extends HyriConfigurationEntry<List<?>> {

        /**
         * Constructor of {@link ListEntry}
         *
         * @param path          The path of the entry
         * @param configuration The Spigot {@link FileConfiguration}
         */
        public ListEntry(String path, FileConfiguration configuration) {
            super(path, configuration);
        }

        @Override
        public List<?> get() {
            return this.configuration.getList(this.path);
        }

        @Override
        public void set(List<?> value) {
            this.configuration.set(this.path, value);
        }

        public static class CharacterListEntry extends HyriConfigurationEntry<List<Character>> {

            /**
             * Constructor of {@link CharacterListEntry}
             *
             * @param path          The path of the entry
             * @param configuration The Spigot {@link FileConfiguration}
             */
            public CharacterListEntry(String path, FileConfiguration configuration) {
                super(path, configuration);
            }

            @Override
            public List<Character> get() {
                return this.configuration.getCharacterList(this.path);
            }

            @Override
            public void set(List<Character> value) {
                this.configuration.set(this.path, value);
            }

        }

        public static class StringListEntry extends HyriConfigurationEntry<List<String>> {

            /**
             * Constructor of {@link StringListEntry}
             *
             * @param path          The path of the entry
             * @param configuration The Spigot {@link FileConfiguration}
             */
            public StringListEntry(String path, FileConfiguration configuration) {
                super(path, configuration);
            }

            @Override
            public List<String> get() {
                return this.configuration.getStringList(this.path);
            }

            @Override
            public void set(List<String> value) {
                this.configuration.set(this.path, value);
            }

        }

        public static class ShortListEntry extends HyriConfigurationEntry<List<Short>> {

            /**
             * Constructor of {@link ShortListEntry}
             *
             * @param path          The path of the entry
             * @param configuration The Spigot {@link FileConfiguration}
             */
            public ShortListEntry(String path, FileConfiguration configuration) {
                super(path, configuration);
            }

            @Override
            public List<Short> get() {
                return this.configuration.getShortList(this.path);
            }

            @Override
            public void set(List<Short> value) {
                this.configuration.set(this.path, value);
            }

        }

        public static class IntegerListEntry extends HyriConfigurationEntry<List<Integer>> {

            /**
             * Constructor of {@link IntegerListEntry}
             *
             * @param path          The path of the entry
             * @param configuration The Spigot {@link FileConfiguration}
             */
            public IntegerListEntry(String path, FileConfiguration configuration) {
                super(path, configuration);
            }

            @Override
            public List<Integer> get() {
                return this.configuration.getIntegerList(this.path);
            }

            @Override
            public void set(List<Integer> value) {
                this.configuration.set(this.path, value);
            }

        }

        public static class LongListEntry extends HyriConfigurationEntry<List<Long>> {

            /**
             * Constructor of {@link LongListEntry}
             *
             * @param path          The path of the entry
             * @param configuration The Spigot {@link FileConfiguration}
             */
            public LongListEntry(String path, FileConfiguration configuration) {
                super(path, configuration);
            }

            @Override
            public List<Long> get() {
                return this.configuration.getLongList(this.path);
            }

            @Override
            public void set(List<Long> value) {
                this.configuration.set(this.path, value);
            }

        }

        public static class FloatListEntry extends HyriConfigurationEntry<List<Float>> {

            /**
             * Constructor of {@link FloatListEntry}
             *
             * @param path          The path of the entry
             * @param configuration The Spigot {@link FileConfiguration}
             */
            public FloatListEntry(String path, FileConfiguration configuration) {
                super(path, configuration);
            }

            @Override
            public List<Float> get() {
                return this.configuration.getFloatList(this.path);
            }

            @Override
            public void set(List<Float> value) {
                this.configuration.set(this.path, value);
            }

        }

        public static class DoubleListEntry extends HyriConfigurationEntry<List<Double>> {

            /**
             * Constructor of {@link DoubleListEntry}
             *
             * @param path          The path of the entry
             * @param configuration The Spigot {@link FileConfiguration}
             */
            public DoubleListEntry(String path, FileConfiguration configuration) {
                super(path, configuration);
            }

            @Override
            public List<Double> get() {
                return this.configuration.getDoubleList(this.path);
            }

            @Override
            public void set(List<Double> value) {
                this.configuration.set(this.path, value);
            }

        }

        public static class BooleanListEntry extends HyriConfigurationEntry<List<Boolean>> {

            /**
             * Constructor of {@link BooleanListEntry}
             *
             * @param path          The path of the entry
             * @param configuration The Spigot {@link FileConfiguration}
             */
            public BooleanListEntry(String path, FileConfiguration configuration) {
                super(path, configuration);
            }

            @Override
            public List<Boolean> get() {
                return this.configuration.getBooleanList(this.path);
            }

            @Override
            public void set(List<Boolean> value) {
                this.configuration.set(this.path, value);
            }

        }

        public static class ByteListEntry extends HyriConfigurationEntry<List<Byte>> {

            /**
             * Constructor of {@link ByteListEntry}
             *
             * @param path          The path of the entry
             * @param configuration The Spigot {@link FileConfiguration}
             */
            public ByteListEntry(String path, FileConfiguration configuration) {
                super(path, configuration);
            }

            @Override
            public List<Byte> get() {
                return this.configuration.getByteList(this.path);
            }

            @Override
            public void set(List<Byte> value) {
                this.configuration.set(this.path, value);
            }

        }

        public static class MapListEntry extends HyriConfigurationEntry<List<Map<?, ?>>> {

            /**
             * Constructor of {@link MapListEntry}
             *
             * @param path          The path of the entry
             * @param configuration The Spigot {@link FileConfiguration}
             */
            public MapListEntry(String path, FileConfiguration configuration) {
                super(path, configuration);
            }

            @Override
            public List<Map<?, ?>> get() {
                return this.configuration.getMapList(this.path);
            }

            @Override
            public void set(List<Map<?, ?>> value) {
                this.configuration.set(this.path, value);
            }

        }

    }

}
