package revisedchatroom;

import java.io.Serializable;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import java.util.StringTokenizer;

public class VGroup implements Serializable {
    public Vector <Group> vGroup = new Vector<Group>(5);

    public void broadcast(Message message) {
        int messageType = message.type;

        for (int i = 0; i < vGroup.size(); i++ ) {
            // vGroup.get(i).broad
        }
    }
}

