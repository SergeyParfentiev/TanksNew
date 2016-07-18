package objectsOnField;

import tanks.Drawable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BattleField implements Drawable {

    final boolean COLORED_MODE = false;

    private final int BF_WIDTH = 576 + 16;
    private final int BF_HEIGHT = 576 + 39;

    private final int limitQuadrant = 64;

    private final int minRiftField = 0;
    private final int maxRiftField = 512;

    public static final String BRICK = "B";
    public static final String EAGLE = "E";
    public static final String ROCK = "R";
    public static final String WATER = "W";
    public static final String SHRUB = "S";
    public static final String VOID = "V";

    private Image image;
    private MapsOfBattleField map;

    private int eagleV;
    private int eagleH;

    private String[][] battleField = new String[9][9];

    public BattleField(Image image, int idx) {
      this.image = image;
        map = new MapsOfBattleField();
        battleField = map.getMap(idx);
        addObjectField();
    }

    private AbstractObjectOnField[][] objectOnFields = new AbstractObjectOnField[battleField.length][battleField[0].length];

    private List<Integer> shrubsX = new ArrayList<>();
    private List<Integer> shrubsY = new ArrayList<>();

        public void addObjectField() {

            for (int v = 0; v < battleField.length; v++) {
                for (int h = 0; h < battleField[0].length; h++) {

                    String obj = battleField[v][h];
                    if (obj.equals(VOID)) {
                        objectOnFields[v][h] = new Void(h * limitQuadrant, v * limitQuadrant, image);
                    }
                    if (obj.equals(BRICK)) {
                        objectOnFields[v][h] = new Brick(h * limitQuadrant, v * limitQuadrant, image);
                    }
                    if (obj.equals(ROCK)) {
                        objectOnFields[v][h] = new Rock(h * limitQuadrant, v * limitQuadrant, image);
                    }
                    if (obj.equals(WATER)) {
                        objectOnFields[v][h] = new Water(h * limitQuadrant, v * limitQuadrant, image);
                    }
                    if (obj.equals(SHRUB)) {
                        objectOnFields[v][h] = new Void(h * limitQuadrant, v * limitQuadrant, image);
                        shrubsY.add(v * limitQuadrant);
                        shrubsX.add(h * limitQuadrant);
                    }
                    if (obj.equals(EAGLE)) {
                        objectOnFields[v][h] = new Eagle(h * limitQuadrant, v * limitQuadrant, image);
                        eagleV = v;
                        eagleH = h;
                    }
                }
            }
        }

    public int getEagleX() {
        return eagleH * limitQuadrant;
    }

    public int getEagleY() {
        return eagleV * limitQuadrant;
    }

    public AbstractObjectOnField getEagleLocation() {
        return objectOnFields[eagleV][eagleH];
    }

    public String getFirstAggressorLocation() {
        return "0_0";
    }

    public String getSecondAggressorLocation() {
        return "0_512";
    }

    public String getDefenderLocation() {
        return "512_128";
    }

    public int getBF_WIDTH() {
        return BF_WIDTH;
    }

    public int getBF_HEIGHT() {
        return BF_HEIGHT;
    }

    public int getLimitQuadrant() {
        return limitQuadrant;
    }

    public int getMinRiftField() {
        return minRiftField;
    }

    public int getMaxRiftField() {
        return maxRiftField;
    }

    public AbstractObjectOnField scanQuadrant(int v, int h) {
        return objectOnFields[v][h];
    }

    public void destroyObject(int v, int h) {
        objectOnFields[v][h].destroy();
    }

    public int getMapListSize() {
        return map.getMapListSize();
    }

    @Override
    public void draw(Graphics g) {

        for (int v = 0; v < battleField.length; v++) {
            for (int h = 0; h < battleField[0].length; h++) {
                objectOnFields[v][h].draw(g);
            }
        }
    }

    public void drawShrubs(Graphics g) {

        for(int i = 0; i < shrubsX.size(); i++) {
            g.drawImage(image, shrubsX.get(i), shrubsY.get(i), shrubsX.get(i) + limitQuadrant, shrubsY.get(i) +limitQuadrant,
                    65 + 64 * 5, 65, 64 * 7, 64 * 2, null);
        }
    }
}