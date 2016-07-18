package gameRecord;

import tanks.Action;
import tanks.Direction;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public interface FileOfRecord extends Serializable {

    void write(String s, ObjectOutputStream oos, Direction direction, Action action);
    List read(String fileName);
}
