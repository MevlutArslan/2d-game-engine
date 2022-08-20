package engine;

public enum EntityCategory {
    PLAYER(0x0001),
    BLOCKS(0x0002),
    SENSOR(0x0004);

    private int value;

    EntityCategory(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

