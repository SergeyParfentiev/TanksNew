package objectsOnField;

import java.awt.*;

public class Brick extends AbstractObjectOnField {

    public Brick(int x, int y, Image image) {
        super(x, y, image);
        numberInQueue = 4;
    }

}
