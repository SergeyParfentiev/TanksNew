package tanks;


import objectsOnField.BattleField;

import java.awt.*;
import java.util.List;

public class Tiger extends AbstractTank {

    private int armor;

    public Tiger(BattleField bf, int x, int y, Direction direction, Image image, Tank opponent, boolean player, String crewName) {
        super(bf, x, y, direction, image, player, crewName);
        tankColor = new Color(255, 0, 0);
        towerColor = new Color(0, 0, 0);
        armor = 1;
        tankOpponent = opponent;
        setImage();
    }
    private void setImage() {
        images = new Image[4];

        for(int i = 0; i < images.length; i++) {
            images[i] = createImage(image, 64 * i, 64, 64, 64);
        }
    }

    @Override
    public void destroy() {
        if (armor > 0) {
            armor--;
        } else {
            super.destroy();
        }
    }

    @Override
    public Action setUp(List<Tank> tankList) {
        if(player) {
            return getAction();
        } else {
            return destroyOpponent(tankOpponent);
        }
    }
}
