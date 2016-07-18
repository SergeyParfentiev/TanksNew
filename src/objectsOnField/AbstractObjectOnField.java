package objectsOnField;

import tanks.Destroyable;
import tanks.Drawable;

import java.awt.*;

public abstract class AbstractObjectOnField implements Drawable, Destroyable {

    private int x;
    private int y;
    private int objectBody = 64;
    private int imageBasicCoordinate = 65;
    private int differenceOfPixels = 2;
    protected int numberInQueue;
    protected int imageX;

    private boolean isDestroyed = false;
    protected Color color;
    protected Image image;

    public AbstractObjectOnField(int x, int y, Image image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    @Override
    public void destroy() {
        isDestroyed = true;
    }

    @Override
    public void draw(Graphics g) {
        if (!isDestroyed) {
            if(image != null) {

                g.drawImage(image, getX(), getY(), getX() + objectBody, getY() + objectBody,
                        imageBasicCoordinate + objectBody * numberInQueue, imageBasicCoordinate,
                        objectBody * (numberInQueue + differenceOfPixels), objectBody * differenceOfPixels, null);

            } else {
                g.setColor(this.color);
                g.fillRect(this.getX(), this.getY(), objectBody, objectBody);
            }
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(this.getX(), this.getY(), objectBody, objectBody);
        }
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
