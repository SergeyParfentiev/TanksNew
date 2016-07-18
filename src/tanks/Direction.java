package tanks;

public enum Direction {

    UP(0), DOWN(1), LEFT(2), RIGHT(3);

    private int id;

    Direction(int id) {
        this.setId(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Direction getValue(int id) {
        return Direction.values()[id];
    }
}
