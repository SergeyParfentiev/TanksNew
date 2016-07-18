package tanks;

import objectsOnField.AbstractObjectOnField;
import objectsOnField.BattleField;

import java.awt.*;
import java.util.List;

public class BT7 extends AbstractTank {
	
	public BT7(BattleField bf, int x, int y, Direction direction, Image image, AbstractObjectOnField opponent, boolean player, String crewName) {
		super(bf, x, y, direction, image, player, crewName);
		tankColor = new Color(255, 0, 0);
		towerColor = new Color(0, 255, 0);
        setImage();
        eagleOpponent = opponent;
	}

    private void setImage() {
        images = new Image[4];

        for(int i = 0; i < images.length; i++) {
            images[i] = createImage(image, 64 * i, 0, 64, 64);
        }
    }

	@Override
	public Action setUp(List<Tank> tankList) {
        if(player) {
            return getAction();
        } else {

            return destroyEagle(tankList);
        }

    }
}
