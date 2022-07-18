package fr.hyriode.hyrame.language;

import fr.hyriode.api.language.HyriLanguage;
import fr.hyriode.api.language.HyriLanguageMessage;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 22/12/2021 at 21:53
 */
public class HyriCommonMessages {

    /** Player was not found */
    public static final HyriLanguageMessage PLAYER_NOT_FOUND = new HyriLanguageMessage("player.not.found")
            .addValue(HyriLanguage.EN, "Couldn't find a player called: ")
            .addValue(HyriLanguage.FR, "Impossible de trouver un joueur appelé : ");

    /** The command is invalid */
    public static final HyriLanguageMessage INVALID_COMMAND = new HyriLanguageMessage("invalid.command")
            .addValue(HyriLanguage.EN, "Invalid command: ")
            .addValue(HyriLanguage.FR, "Commande invalide : ");

    /** The input is invalid */
    public static final HyriLanguageMessage INVALID_INPUT = new HyriLanguageMessage("invalid.input")
            .addValue(HyriLanguage.EN, "Invalid input!")
            .addValue(HyriLanguage.FR, "Valeur invalide!");

    /** The number is invalid */
    public static final HyriLanguageMessage INVALID_NUMBER = new HyriLanguageMessage("invalid.number")
            .addValue(HyriLanguage.EN, "Invalid number: ")
            .addValue(HyriLanguage.FR, "Nombre invalide: ");

    /** The argument is invalid */
    public static final HyriLanguageMessage INVALID_ARGUMENT = new HyriLanguageMessage("invalid.argument")
            .addValue(HyriLanguage.EN, "Invalid argument: ")
            .addValue(HyriLanguage.FR, "Argument invalide: ");

    /** A player doesn't have the permission to execute a command */
    public static final HyriLanguageMessage DONT_HAVE_PERMISSION = new HyriLanguageMessage("dont.have.permission")
            .addValue(HyriLanguage.EN, "You don't have the permission to execute this command!")
            .addValue(HyriLanguage.FR, "Vous n'avez pas la permission d'éxécuter cette commande!");

    public static final HyriLanguageMessage SECOND = new HyriLanguageMessage("")
            .addValue(HyriLanguage.EN, "second")
            .addValue(HyriLanguage.FR, "seconde");

    public static final HyriLanguageMessage SECONDS = new HyriLanguageMessage("")
            .addValue(HyriLanguage.EN, "seconds")
            .addValue(HyriLanguage.FR, "secondes");

}
