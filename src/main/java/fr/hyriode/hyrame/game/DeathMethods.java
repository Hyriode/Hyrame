package fr.hyriode.hyrame.game;

public enum DeathMethods {

    GIVE_STUFF_TO_KILLER(0),
    KEEP_INVENTORY(1),
    DESTROY_STUFF(2),
    DROP_AT_DEATH_LOCATION(3),
    DROP_AT_KILLER_LOCATION(4);

    private final int id;

    DeathMethods(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
