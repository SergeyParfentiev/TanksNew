package tanks;

public enum Action {
	
	UP(0), DOWN(1), LEFT(2), RIGHT(3), NONE(4), MOVE(5), FIRE(6);

    private int id;

    Action(int id) {
        this.setId(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
