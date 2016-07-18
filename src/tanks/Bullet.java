package tanks;

import java.awt.*;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;

public class Bullet implements Drawable, Destroyable {

    private int x;
    private int y;

    private int Speed = 3;

    private Direction direction;
    private boolean destroyed;
    private Tank tank;
    private Image image;
    private String crewName;

    private Image[] images;

    public Bullet(int x, int y, Direction direction, Tank tank, Image image, String crewName) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.destroyed = false;
        this.tank = tank;
        this.image = image;
        this.crewName = crewName;
        setImage();
    }
    public Tank getTank() {
        return tank;
    }

    public Direction getDirection() {
        return direction;
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

    public String getCrewName() {
        return crewName;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSpeed() {
        return Speed;
    }

    public void updateX(int x) {
        this.x += x;
    }

    public void updateY(int y) {
        this.y += y;
    }

    public void destroy() {
        destroyed = true;
        x = -100;
        y = -100;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    private void setImage() {
        images = new Image[4];

        for(int i = 0; i < images.length; i++) {
            images[i] = createImage(image, 64 * i, 128, 14, 14);
        }
    }

    protected Image createImage(Image image, int x1, int y1, int x2, int y2) {
        return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), new CropImageFilter(x1, y1, x2, y2)));
    }
    @Override
    public void draw(Graphics g) {
        if(!destroyed) {
            if(images != null) {
                g.drawImage(images[getDirection().getId()], getX(), getY(), null);
            } else {
                g.setColor(new Color(255, 255, 0));
                g.fillRect(this.getX(), this.getY(), 14, 14);
            }
        }
    }



}

