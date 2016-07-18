package objectsOnField;

import java.awt.*;

public class Water extends AbstractObjectOnField {

    public Water(int x, int y, Image image) {
        super(x, y, image);
        color = new Color(0, 0, 255);
        numberInQueue = 6;
    }
}
