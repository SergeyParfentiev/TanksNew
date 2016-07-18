package objectsOnField;

import java.util.ArrayList;
import java.util.List;

public class MapsOfBattleField {

    List<String[][]> mapLis;

    public MapsOfBattleField() {
        mapLis = new ArrayList<>();
        mapLis.add(map1);
        mapLis.add(map2);
        mapLis.add(map3);
        mapLis.add(map4);
    }
    private String[][] map1 = {
            {"V", "V", "R", "V", "B", "W", "W", "V", "V"},
            {"V", "B", "W", "W", "V", "V", "R", "V", "V"},
            {"B", "R", "V", "S", "S", "S", "V", "V", "V"},
            {"V", "B", "S", "V", "V", "V", "S", "R", "R"},
            {"R", "V", "S", "V", "V", "V", "S", "V", "V"},
            {"B", "V", "S", "V", "V", "V", "S", "B", "V"},
            {"B", "R", "V", "S", "S", "S", "V", "B", "V"},
            {"B", "B", "V", "B", "B", "B", "V", "V", "V"},
            {"V", "V", "V", "B", "E", "B", "V", "V", "V"}};

    private String[][] map2 = {
            {"V", "V", "R", "B", "R", "V", "W", "V", "V"},
            {"V", "B", "V", "R", "V", "V", "V", "V", "V"},
            {"V", "V", "V", "S", "B", "V", "R", "W", "W"},
            {"V", "R", "V", "S", "W", "V", "V", "V", "V"},
            {"V", "B", "V", "S", "W", "V", "V", "V", "B"},
            {"B", "R", "V", "S", "W", "V", "V", "V", "B"},
            {"B", "B", "V", "S", "S", "S", "V", "V", "V"},
            {"R", "B", "V", "B", "B", "B", "V", "V", "B"},
            {"V", "B", "V", "B", "E", "B", "V", "B", "B"}};

    private String[][] map3 = {
            {"V", "V", "V", "V", "B", "V", "V", "V", "V"},
            {"B", "B", "R", "R", "B", "V", "V", "B", "B"},
            {"V", "V", "V", "S", "S", "S", "V", "V", "B"},
            {"V", "V", "V", "S", "W", "W", "V", "R", "R"},
            {"V", "B", "V", "S", "S", "S", "V", "V", "B"},
            {"B", "B", "V", "S", "W", "W", "V", "V", "B"},
            {"B", "B", "V", "S", "S", "S", "V", "V", "V"},
            {"R", "R", "V", "V", "B", "V", "V", "V", "V"},
            {"V", "V", "V", "B", "E", "B", "V", "V", "V"}};

    private String[][] map4 = {
            {"V", "V", "R", "B", "R", "V", "V", "V", "V"},
            {"V", "B", "V", "R", "V", "V", "V", "V", "V"},
            {"W", "W", "V", "S", "S", "S", "R", "W", "W"},
            {"W", "R", "V", "S", "V", "V", "V", "V", "V"},
            {"V", "B", "V", "S", "V", "V", "V", "V", "B"},
            {"B", "R", "V", "S", "V", "S", "V", "V", "B"},
            {"B", "B", "V", "S", "S", "S", "W", "W", "V"},
            {"R", "B", "V", "V", "B", "V", "V", "V", "B"},
            {"V", "B", "V", "V", "E", "V", "V", "B", "B"}};

    public String[][] getMap(int idx) {
        return mapLis.get(idx);
    }

    public int getMapListSize() {
        return mapLis.size();
    }
}
