package fr.hyriode.hyrame.team;

public enum TeamColor {

    WHITE("white", 0),
    ORANGE("orange", 1),
    MAGENTA("magenta", 2),
    LBLUE("light blue", 3),
    YELLOW("yellow", 4),
    LIME("lime", 5),
    PINK("pink", 6),
    GRAY("gray", 7),
    LGRAY("light gray", 8),
    CYAN("cyan", 9),
    PURPLE("purple", 10),
    BLUE("blue", 11),
    BROWN("brown", 12),
    GREEN("green", 13);

    private final String color;
    private final int ID;

    TeamColor(String color, int ID) {
        this.color = color;
        this.ID = ID;
    }

    public String getColor() {
        return color;
    }

    public int getID() {
        return ID;
    }

}
