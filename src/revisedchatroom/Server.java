package revisedchatroom;
import java.io.*;
import java.net.*;
import java.lang.*;

public class Server {

    static int nextVisitorID = 0;
    static int count = 0; // Keeps count of how many people are in the chatroom.
    static VGroup vgroup = new VGroup();
    static Group defaultGroup = new Group();
    static ServerSocket serverSocket = null;
    static int port = 9999;

    Server() {
        // not sure if i need to call this but i'll test it.
    }

    static void reload() {
        Server.nextVisitorID = 0;
        for (int i = 0; i < Names.group.length; i++) {
            vgroup.add (new Group (0, Names.group[i]));
        }
    }

    public static void main( String args[] ) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Socket socket = null;
        Thread thread = null;
        // Try to figure out if there's any errors that I can use
        // to make this while statement not always true.
        while(true) {
            try {
                socket = serverSocket.accept();
                thread = new Thread( new ServerAgent(socket));
                thread.start();
                System.out.printf("\n Client [%d] is connected. ", ++ count);
            } catch (IOException e) {
                System.out.printf("Address is already used.");
            }
        }
    }
}

class ServerAgent implements Runnable {
    Visitor visitor = null;
    Socket socket = null;
    Group group = null;
    ObjectInputStream in;
    ObjectOutputStream out;
    Message message = null;

    public ServerAgent(Socket socket) {
        this.socket = socket;

        try {
            this.out = new ObjectOutputStream ( socket.getOutputStream());
            this.in = new ObjectInputStream (socket.getInputStream());
            visitor = new Visitor( ++Server.nextVisitorID, "unknown", false, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

  
    }

    public void run() {
        while(true) {
            try {
                try {
                    message = (Message) in.readObject();
                } catch(ClassNotFoundException e) {
                    e.printStackTrace();
                }
                catch(IOException io) {
                    io.printStackTrace();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                    return;
                } 

                if( message == null ) {
                    socket.close();
                    return;
                }

                switch(message.type) {
                    case Message.LOGIN:
                        visitor.name = new String ( message.others);
                        break;
                    case Message.LOGOUT:
                        String outVisitorInfo = new String ( visitor.toString());
                        Message logoutMessage = new Message (message.type, outVisitorInfo, group.visitorList());
                        break;
                    case Message.PUBLIC:
                        group.broadcast(new Message(message.type, message.body, visitor.toString() ));
                        break;
                    case Message.PRIVATE:
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                if (group != null ) {
                    group.remove(visitor);
                }
                return;
            }

        }
    }
}