package revisedchatroom;

import java.io.*;
import java.util.*;

public class Message implements Serializable {
    // I don't need any groups so I should only need these values.

    public static final int PUBLIC = 0;
    public static final int PRIVATE = 1;
    public static final int LOGIN = 2;
    public static final int LOGOUT = 3;

    public static final String NAME_SEPARATOR = ";";

    public int type = PUBLIC;

    public String body = "";

    public String others = "";

    public Message () {
        set ( PUBLIC, "invalid", null);
    }

    public Message(Message m) {
        set(m.type, m.body, m.others);
    }

    public Message(int type, String body, String others) {
        set(type,body, others);
    }

    public void set(int type, String body, String others) {
        this.type = type;
        this.body = body == null ? null : new String (body);
        this.others = others == null ? null : new String (others);
    }

    public String toString() {
        return String.format("%3s %s $s", type, body, others);
    }

    public Vector<String> getRecceiverNames() {
        Vector<String> v = new Vector<String> (10);
        // If there are no receivers, then return null.
        if(others == null) {
            return null;
        } 
        else if (others.trim().equals("") ) {
            return v;
        }
        else {
            StringTokenizer token = new StringTokenizer(others, NAME_SEPARATOR);
            while (token.hasMoreTokens() ) {
                v.add( token.nextToken() );
            }
     
        }
        Collections.sort(v);
        return v;
    }
}