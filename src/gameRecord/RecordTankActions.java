package gameRecord;

import tanks.Action;
import tanks.Direction;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RecordTankActions implements FileOfRecord {

    @Override
    public synchronized void write(String s, ObjectOutputStream oos, Direction direction, Action action) {
        try {
            oos.writeObject(s);
            oos.writeObject(direction);
            oos.writeObject(action);
            oos.flush();
        } catch (Exception e) {
            // ignore
        }
    }
    @Override
    public List read(String fileName) {
        List list = new ArrayList();
        try {
            FileInputStream fis = new FileInputStream(fileName);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(bis);

            int mapLevel = (int) ois.readObject();
            list.add(mapLevel);
            int firstAggressorIdx = (int) ois.readObject();
            list.add(firstAggressorIdx);
            int secondAggressorIdx = (int) ois.readObject();
            list.add(secondAggressorIdx);

            while (bis.available() > 0) {
                String crewName = (String) ois.readObject();
                list.add(crewName);
                Direction direction = (Direction) ois.readObject();
                list.add(direction);
                Action action = (Action) ois.readObject();
                list.add(action);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void writeGameData(ObjectOutputStream oos, int mapLevel, int firstAggressorIdx, int secondAggressorIdx) {
        try {
            oos.writeObject(mapLevel);
            oos.writeObject(firstAggressorIdx);
            oos.writeObject(secondAggressorIdx);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
