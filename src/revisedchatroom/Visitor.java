package revisedchatroom;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.StringTokenizer;

public class Visitor implements Comparable<Visitor>, Serializable {
    public int id;
    public String name ; // name of visitor
    public boolean checked = true; // check to receive private message
    public ObjectOutputStream out; // output stream to the visitor

    // used on the chatroom server side to add visitor to server list.

    public Visitor (ObjectOutputStream out) {
        set(-1, null, true, out);
    }

    public Visitor(int id) {
        set(id, "", true, null);
    }

    public Visitor(Visitor visitor) {
        set(visitor.id, visitor.name, visitor.checked, visitor.out);
    }

    public Visitor(String name) {
        set (-1, name, true, null);
    }

    public Visitor(String name, boolean checked) {
        set(-1, name, checked, null);
    }

    public Visitor(int id, String name, boolean checked, ObjectOutputStream out) {
        set (id, name, checked, out);
    }

    public void set(int id, String name, boolean checked, ObjectOutputStream out) {
        this.id = id;
        this.name = (name == null) ? null : new String(name);
        this.checked = checked;
        this.out = (out == null) ? null : out;
    }

    @Override
    public int compareTo (Visitor visitor) {
        return id - visitor.id;
    }

    @Override
    public boolean equals(Object visitor) {
        return id == ((Visitor) visitor).id;
    }

    public String toString() {
        return String.format("%s_%d", name, id);
    }

    public String longString() {
        return String.format("%10s_%4d %5s %s", name, id,
        (checked ? "true" : false), (out == null ? "null" : "not null"));
    }

    public static Visitor toVisitor(String visitorName_visitorID) {
        if(visitorName_visitorID == null) {
            return null;
        }
        StringTokenizer token = new StringTokenizer(visitorName_visitorID, "_; ");
        if ( !token.hasMoreTokens() ) {
            return null;
        }

        String name = token.nextToken();
        return new Visitor ( Integer.parseInt(token.nextToken() ), name, false, null);
    }

}