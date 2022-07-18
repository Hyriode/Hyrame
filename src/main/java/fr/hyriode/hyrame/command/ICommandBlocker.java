package fr.hyriode.hyrame.command;

import java.util.List;

/**
 * Created by AstFaster
 * on 17/07/2022 at 16:22
 */
public interface ICommandBlocker {

    /**
     * Add blocked commands by giving their prefix and names
     *
     * @param prefix The prefix of the commands
     * @param commands The names of the commands
     */
    void addBlockedCommands(String prefix, String... commands);

    /**
     * Remove commands by giving their prefix and names
     *
     * @param prefix The prefix of the commands
     * @param commands The names of the commands to remove
     */
    void removeCommands(String prefix, String... commands);

    /**
     * Get the list of all blocked commands
     *
     * @return A list of command names
     */
    List<String> getBlockedCommands();

}
