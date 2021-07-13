package fr.hyriode.hyrame.gameMethods;

public enum DeathMethods {

    GIVESTUFFTOKILLER(0),
    KEEPINVENTORY(1),
    DESTROYSTUFF(2),
    DROPATDEATHLOCATION(3),
    DROPATKILLERLOCATION(4);

    private final int id;

    DeathMethods(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
