package engine.physics;

public enum CollisionGroup {
    // Positive index => always collide
    // Negative index => never collide
    ALL(0),
    TERRAIN(-1);

    private int groupIndex;

    CollisionGroup(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public int getGroupIndex(){
        return this.groupIndex;
    }
}
