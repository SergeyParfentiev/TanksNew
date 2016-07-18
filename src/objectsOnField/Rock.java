package objectsOnField;

import java.awt.*;

public class Rock extends AbstractObjectOnField {

    public Rock(int x, int y, Image image) {
        super(x, y, image);
        imageX = 8;
        numberInQueue = 3;
    }
}
