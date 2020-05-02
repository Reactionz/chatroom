package revisedchatroom;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;

public class Group implements Comparable<Group>, Serializable {
    public int id = -1;
    public String name = null;
    public int count = 0;
    public Vector<Visitor> vectorVisitor = new Vector<Visitor>(5);

    public Visitor get(int k) {
        if(vectorVisitor.isEmpty()|| k >= vectorVisitor.size()) {
            return null;
        }
        return vectorVisitor.get(k);
    }

    public void add (Visitor v) {
        count ++;
        vectorVisitor.add(v);
    }

    public void remove (Visitor v) {
        count--;
        vectorVisitor.remove(v);
    }
    public int size() {
        return vectorVisitor.size();
    }
    
    public void clear() {
        count = 0;
        // if(vectorVisitor == null || vectorVisitor.size() < 1) {
        if(vectorVisitor == null || vectorVisitor.isEmpty() ) {
            return;
        }
        Visitor v;
        for (int i = 0; i < vectorVisitor.size(); i++ ) {
            v = vectorVisitor.get(i);
            try {
                if ( v.out != null) {
                    v.out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            vectorVisitor.clear();
        }
    }

    public void add(String visitorList) {
        StringTokenizer token = new StringTokenizer(visitorList, ";_");
        String visitorName;
        int visitorID = 0;
        int i = 0;

        while (token.hasMoreTokens() ) {
            visitorName = new String (token.nextToken() );
            visitorID = Integer.parseInt(token.nextToken() );
            add ( new Visitor ( visitorID, visitorName, false, null));
            count++;
        }
    }

    public Visitor find( int visitorID) {
        for (int i = 0; i < vectorVisitor.size(); i++) {
            if (visitorID == vectorVisitor.get(i).id ) {
                return vectorVisitor.get(i);
            }
        }
        return null;
    }

    public static Group toGroup(String groupName_groupID_size) {
        StringTokenizer token = new StringTokenizer(groupName_groupID_size, "_ ");
        String groupName = new String(token.nextToken());
        int groupID = Integer.parseInt(token.nextToken());
        int size = Integer.parseInt(token.nextToken() );
        return new Group (groupID, groupName, size);
    }

    public Group () {

    }

    public Group (Group group) {
        this.id = group.id;
        this.name = new String(group.name);
        this.count = group.count;
    }
    public Group(int id) {
        this.id = id;
    }
    public Group(int id, String name) {
        this.id = id;
        this.name = new String(name);
    }

    public Group ( int id, String name, int count) {
        this.id = id;
        this.name = new String(name);
        this.count = count;
    }
    public String visitorList() {
        return visitorList(true ) + "; " + visitorList (false);
    }

    public String visitorList(boolean checked) {
        StringBuffer stringBuff = new StringBuffer();
        Visitor v;
        int size = vectorVisitor.size() - 1;
        for (int i = 0; i <= size; i++) {
            v = vectorVisitor.get(i);
            if ( v.checked == checked) {
                stringBuff.append( v + (i < size ? "; " : ""));
            }
        }
        return stringBuff.toString();
    }

    public String toString() {
        return String.format("%s_%d %d", name, id, count);
    }

    public String visitorIDList() {
        StringBuffer buff = new StringBuffer();
        int size = vectorVisitor.size() - 1;
        for (int i = 0; i <= size; i++) {
            buff.append(vectorVisitor.get(i).id + (i < size ? "; " : ""));
        }
        return buff.toString();
    }

    public String visitorIDList(boolean check) {
        StringBuffer buff = new StringBuffer();
        int count = 0;
        int size = vectorVisitor.size() - 1;

        for (int i = 0; i <= size; i++) {
            if (vectorVisitor.get(i).checked == check) {
                buff.append( (count == 0 ? "" : "; " ) + vectorVisitor.get(i).id);
                count++;
            }
        }
        return buff.toString();
    }

    public int[] visitorIDListToArray( String visitorIDList) {
        int [] a = new int[10];
        int count = 0;
        StringTokenizer token = new StringTokenizer(visitorIDList, "; ");
        while(token.hasMoreTokens() ) {
            a[count] = Integer.parseInt( token.nextToken() );
            count++;
        }

        if (count < 1) {
            return null;
        }

        return Arrays.copyOfRange(a,0,count);
    }

    public int compareTo(Group g) {
        return id - g.id;
    }

    public boolean equals(Group g) {
        return id == g.id;
    }

    public synchronized void broadcast(Message message) {
        if (message == null) {
            return;
        }
        Iterator <Visitor> itr = vectorVisitor.iterator();
        ObjectOutputStream out;
        Visitor v;

        while (itr.hasNext()) { 
            try {
                v = itr.next();
                out = v.out;
                if ( out == null ) {
                    System.out.printf("\t\t\t[%s] sent to [%s]\n", message.body, v);
                } else {
                    out.writeObject(message);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void broadcast (Message message, String visitorIDList) {
        if (message == null || visitorIDList == null || visitorIDList.trim().length() < 1 || vectorVisitor.size() < 1) {
            return;
        }
        int[] visitorIDArray = visitorIDListToArray(visitorIDList);
        Arrays.sort(visitorIDArray);
        Iterator<Visitor> itr = vectorVisitor.iterator();

        ObjectOutputStream out;
        int count = 0;
        int aID;
        int vID;
        Visitor v;

        while ( count < visitorIDArray.length ) {
            aID = visitorIDArray[count];
            do {
                if (! itr.hasNext() ) {
                    return;
                }
                v = itr.next();
                vID = v.id;
            } while (aID > vID);

            if (aID == vID) {
                try {
                    out = v.out;
                    if (out == null) {
                        System.out.printf("\t\t\t[%s] sent to [%s}\n", message.body, v);
                    } else {
                        out.writeObject(message);
                        out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                count++;
            }
        }
    }
}