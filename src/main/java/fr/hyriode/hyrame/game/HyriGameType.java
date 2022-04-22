package fr.hyriode.hyrame.game;

import fr.hyriode.api.HyriAPI;

/**
 * Game type interface.<br>
 * You can use it in an enumeration of game types or just as an object
 */
public interface HyriGameType {

    /**
     * Get game type name
     *
     * @return Type name
     */
    String getName();

    /**
     * Get the game type display name
     *
     * @return A display name
     */
    String getDisplayName();

    /**
     * Get the minimum of players in this game type
     *
     * @return A minimum of players
     */
    int getMinPlayers();

    /**
     * Get the maximum of players in this game type
     *
     * @return A maximum of players
     */
    int getMaxPlayers();

    /**
     * Get the game type from the one provided in the server data
     *
     * @param values All the values available for a game type
     * @return The {@link HyriGameType} found
     */
    static HyriGameType getFromData(HyriGameType[] values) {
        for (HyriGameType type : values) {
            if (type.getName().equals(HyriAPI.get().getServer().getGameType())) {
                return type;
            }
        }
        return null;
    }

}
