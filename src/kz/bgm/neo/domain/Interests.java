package kz.bgm.neo.domain;


public enum Interests {
    TOYS(0),
    DILDOS(1),
    CARS(2),
    GAMES(3),
    ANIMALS(4),
    GIRLS(5),
    MEN(6),
    MOVIES(7),
    PORN(8),
    TV(9),
    BOOKS(10),
    ANIME(11),
    FOOD(12),
    NONE(99);

    int val;

    Interests(int val) {
        this.val = val;
    }

    public static Interests valueOf(int i) {
        for (Interests l : values()) {
            if (l.val == i) {
                return l;
            }
        }
        return NONE;
    }
}
