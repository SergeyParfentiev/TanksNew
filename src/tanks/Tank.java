package tanks;

import java.awt.*;
import java.util.List;

public interface Tank extends Drawable, Destroyable {
	
    Action setUp(List<Tank> tankList);

    Action getAction();

    Bullet fire();

    int getX();

	int getY();

    void setX(int x);

    void setY(int y);

    boolean isRecharge();
	
	Direction getDirection();

	void updateX(int x);

	void updateY(int y);

	int getSpeed();

    void setDirection(Direction direction);

    void turn(Direction direction);

    void setPlayerAction(Action playerAction);

    String getCrewName();

    Image createImage(Image image, int x1, int y1, int x2, int y2);
}
