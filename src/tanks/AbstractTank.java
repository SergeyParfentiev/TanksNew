package tanks;

import objectsOnField.*;

import java.awt.*;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.util.List;
import java.util.Random;

public abstract class AbstractTank implements Tank {


    protected int speed = 20;

    protected int x;
    protected int y;

    protected Color tankColor;
    protected Color towerColor;

    private boolean destroyed;

    protected Direction direction;

    protected Image image;
    protected Image[] images;

    protected BattleField bf;

    protected boolean player;
    private boolean goLeft;

    protected String crewName;

    protected Tank tankOpponent;
    protected AbstractObjectOnField eagleOpponent;

    protected Action action;
    protected Action playerAction;

    protected long recharge = 0;

    public AbstractTank(BattleField bf, int x, int y, Direction direction, Image image, boolean player, String crewName) {
        this.bf = bf;
        this.x = x;
        this.y = y;
        this.image = image;
        this.direction = direction;
        this.destroyed = false;
        this.player = player;
        this.crewName = crewName;
        playerAction = Action.NONE;
    }

    protected Action destroyOpponent(Tank opponent){

        if(getX() == opponent.getX() && getY() > opponent.getY()) {
            turn(Direction.UP);
        } else
        if(getX() == opponent.getX() && getY() < opponent.getY()) {
            turn(Direction.DOWN);
        } else
        if(getX() > opponent.getX() && getY() == opponent.getY()) {
            turn(Direction.LEFT);
        } else
        if(getX() < opponent.getX() && getY() == opponent.getY()) {
            turn(Direction.RIGHT);
        } else
        if(Math.abs(getX() - opponent.getX()) < Math.abs(getY() - opponent.getY())) {
            if(getX() > opponent.getX()) {
               if(theQuadrantToTheLeft() instanceof Water) {
                   turn(Direction.DOWN);
                   if((theQuadrantBelow() instanceof Brick || theQuadrantBelow() instanceof Rock) && !theQuadrantBelow().isDestroyed()) {
                       return Action.FIRE;
                   } else {
                       action = Action.MOVE;
                   }
               } else
                    turn(Direction.LEFT);
                    if((theQuadrantToTheLeft() instanceof Brick || theQuadrantToTheLeft() instanceof Rock) && !theQuadrantToTheLeft().isDestroyed()) {
                        return Action.FIRE;
                   } else {
                        action = Action.MOVE;
                    }
            } else {
                if(theQuadrantToTheRight() instanceof Water) {
                    turn(Direction.DOWN);
                    if((theQuadrantBelow() instanceof Brick || theQuadrantBelow() instanceof Rock) && !theQuadrantBelow().isDestroyed()) {
                        return Action.FIRE;
                    } else {
                        action = Action.MOVE;
                    }
                } else {
                    turn(Direction.RIGHT);
                    if((theQuadrantToTheRight() instanceof Brick || theQuadrantToTheRight() instanceof Rock) && !theQuadrantToTheRight().isDestroyed()) {
                        return Action.FIRE;
                    } else {
                        action = Action.MOVE;
                    }
                }
            }
        }else {
            if(getY() > opponent.getY()) {
                if(theQuadrantAbove() instanceof Water) {
                    turn(Direction.RIGHT);
                    if((theQuadrantToTheRight() instanceof Brick || theQuadrantToTheRight() instanceof Rock) && !theQuadrantToTheRight().isDestroyed()) {
                        return Action.FIRE;
                    } else {
                        action = Action.MOVE;
                    }
                } else {
                    turn(Direction.UP);
                    if((theQuadrantAbove() instanceof Brick || theQuadrantAbove() instanceof Rock) && !theQuadrantAbove().isDestroyed()) {
                        return Action.FIRE;
                    } else {
                        action = Action.MOVE;
                    }
                }
            } else {
                if(theQuadrantBelow() instanceof Water) {
                    turn(Direction.RIGHT);
                    if((theQuadrantToTheRight() instanceof Brick || theQuadrantToTheRight() instanceof Rock) && !theQuadrantToTheRight().isDestroyed()) {
                        return Action.FIRE;
                    } else {
                        action = Action.MOVE;
                    }
                } else {
                    turn(Direction.DOWN);
                    if((theQuadrantBelow() instanceof Brick || theQuadrantBelow() instanceof Rock) && !theQuadrantBelow().isDestroyed()) {
                        return Action.FIRE;
                    } else {
                        action = Action.MOVE;
                    }
                }
            }
        }

        theLevelOfDifficulty();

        return action;
    }

    private void theLevelOfDifficulty() {
        if(new Random().nextInt(100) < 95) {
            action = Action.NONE;
        } else
        if(new Random().nextInt(100) < 30) {
            action = Action.MOVE;
        } else
        if(new Random().nextInt(100) < 70) {
            action = Action.FIRE;
        }
    }

    protected Action destroyEagle(List<Tank> tankList) {
        if(getY() == bf.getEagleY()) {
            if(getX() > bf.getEagleX()) {
                turn(Direction.LEFT);
                action = Action.FIRE;
            } else {
                turn(Direction.RIGHT);
                action = Action.FIRE;
            }
        } else {
                if(tankBelow(tankList) || (theQuadrantBelow() instanceof Brick && !theQuadrantBelow().isDestroyed())) {
                    turn(Direction.DOWN);
                    action = Action.FIRE;
                } else {
                    turn(Direction.DOWN);
                    action = Action.MOVE;
                }
            if(getX() == 0 || theQuadrantToTheLeft() instanceof Rock || theQuadrantToTheLeft() instanceof Water) {
                goLeft = false;
            }
            if(getX() == 512 || theQuadrantToTheRight() instanceof Rock || theQuadrantToTheRight() instanceof Water) {
                goLeft = true;
            }
            if(theQuadrantBelow() instanceof Water || theQuadrantBelow() instanceof Rock) {
                if(goLeft) {
                    if (theQuadrantToTheLeft() instanceof Brick && !theQuadrantToTheLeft().isDestroyed() || tankLeft(tankList)) {
                        turn(Direction.LEFT);
                        action = Action.FIRE;
                    } else {
                        turn(Direction.LEFT);
                        action = Action.MOVE;
                    }
                } else {
                    if(theQuadrantToTheRight() instanceof Brick && !theQuadrantToTheRight().isDestroyed() || tankRight(tankList)) {
                        turn(Direction.RIGHT);
                        action = Action.FIRE;
                    } else {
                        turn(Direction.RIGHT);
                        action = Action.MOVE;
                    }
                }
            }
        }
        return action;
    }

    private AbstractObjectOnField theQuadrantBelow() {
        return bf.scanQuadrant(getY() / bf.getLimitQuadrant() + 1, getX() / bf.getLimitQuadrant());
    }

    private AbstractObjectOnField theQuadrantAbove() {
        return bf.scanQuadrant(getY() / bf.getLimitQuadrant() - 1, getX() / bf.getLimitQuadrant());
    }

    private AbstractObjectOnField theQuadrantToTheLeft() {
        return bf.scanQuadrant(getY() / bf.getLimitQuadrant(), getX() / bf.getLimitQuadrant() - 1);
    }

    private AbstractObjectOnField theQuadrantToTheRight() {
        return bf.scanQuadrant(getY() / bf.getLimitQuadrant(), getX() / bf.getLimitQuadrant() + 1);
    }

    private boolean tankBelow(List<Tank> tankList) {
        for(Tank tank : tankList) {
            if(tank != this &&(tank.getY() - getY() == bf.getLimitQuadrant()) && (tank.getX() - getX() == 0) && !tank.isDestroyed()) {
                return true;
            }
        }
        return false;
    }

    private boolean tankRight(List<Tank> tankList) {
        for(Tank tank : tankList) {
            if(tank != this &&(tank.getY() - getY() == 0) && (tank.getX() - getX() == bf.getLimitQuadrant()) && !tank.isDestroyed()) {
                return true;
            }
        }
        return false;
    }

    private boolean tankLeft(List<Tank> tankList) {
        for(Tank tank : tankList) {
            if(tank != this &&(tank.getY() - getY() == 0) && (getX() - tank.getX() == bf.getLimitQuadrant()) && !tank.isDestroyed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Action getAction() {

        Action currentAction = playerAction;

        if(currentAction == Action.FIRE) {
            return Action.FIRE;
        } else {
            if(currentAction.getId() == direction.getId()) {
                return Action.MOVE;
            } else {
                if(currentAction == Action.NONE) {
                    return Action.NONE;
                } else {
                    direction = direction.getValue(currentAction.getId());
                    return Action.NONE;
                }
            }
        }
    }

    @Override
    public void turn(Direction direction) {
        this.direction = direction;
    }

    @Override
    public Bullet fire() {
        int bulletX = -100;
        int bulletY = -100;
        if (direction == Direction.UP) {
            bulletX = x + 25;
            bulletY = y - 15;
        } else if (direction == Direction.DOWN) {
            bulletX = x + 25;
            bulletY = y + 65;
        } else if (direction == Direction.LEFT) {
            bulletX = x - 15;
            bulletY = y + 25;
        } else if (direction == Direction.RIGHT) {
            bulletX = x + 65;
            bulletY = y + 25;
        }
        recharge = System.currentTimeMillis() + 1000;
        return new Bullet(bulletX, bulletY, direction, this, image, getCrewName());

    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void destroy() {
        destroyed = true;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public String getCrewName() {
        return crewName;
    }

    @Override
    public void setPlayerAction(Action playerAction) {
        this.playerAction = playerAction;
    }

    @Override
    public void updateX(int x) {
        this.x += x;
    }

    @Override
    public void updateY(int y) {
        this.y += y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public boolean isRecharge() {
        return recharge < System.currentTimeMillis();
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public Image createImage(Image image, int x1, int y1, int x2, int y2) {
        return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), new CropImageFilter(x1, y1, x2, y2)));
    }
    @Override
    public void draw(Graphics g) {
        if (!destroyed) {
            if(images != null) {
                g.drawImage(images[getDirection().getId()], getX(), getY(), null);
            }else {
                g.setColor(tankColor);
                g.fillRect(this.getX(), this.getY(), 64, 64);

                g.setColor(towerColor);
                if (this.getDirection() == Direction.UP) {
                    g.fillRect(this.getX() + 20, this.getY(), 24, 34);

                } else if (this.getDirection() == Direction.DOWN) {
                    g.fillRect(this.getX() + 20, this.getY() + 30, 24, 34);

                } else if (this.getDirection() == Direction.LEFT) {
                    g.fillRect(this.getX(), this.getY() + 20, 34, 24);

                } else {
                    g.fillRect(this.getX() + 30, this.getY() + 20, 34, 24);
                }
                if(bf.scanQuadrant(getY() / 64, getX() / 64) instanceof Water) {
                    bf.scanQuadrant(getY() / 64, getX() / 64).draw(g);
                }
            }
        }
    }
}
