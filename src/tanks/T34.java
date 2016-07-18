package tanks;


import objectsOnField.BattleField;

import java.awt.*;
import java.util.List;

public class T34 extends AbstractTank {

    public T34(BattleField bf, int x, int y, Direction direction, Image image, String crewName) {
        super(bf, x, y, direction, image, true, crewName);
        tankColor = new Color(0, 0, 255);
        towerColor = new Color(255, 0, 0);
        setImage();

    }

    private void setImage() {
        images = new Image[4];

        for(int i = 0; i < images.length; i++) {
            images[i] = createImage(image, 64 * (i + 4), 0, 64, 64);
        }
    }

    @Override
    public Action setUp(List<Tank> tankList) {

        return getAction();
    }
}
