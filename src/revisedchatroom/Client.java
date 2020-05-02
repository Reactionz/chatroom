package revisedchatroom;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import java.net.*;

public class Client extends JFrame {

    static Random randomVal = new Random();
    static String fontType = "Verdana";
    String hostAddress = "localhost";

    static JFrame clientWindow;
    String titleStr = "Chatroom";
    static int port = 9999;
    static int frameHeight = 450;
    static int frameWidth = 900;
    static int smallWidth = 280;
    int fontSize = 12;
    int smallFontSize = 10;
    // 0 - Name of Client 1 - Message
    JLabel[] labelNameMessage = { new JLabel("Name"), new JLabel("Message") };
    JTextField[] textFieldNameMessage = { new JTextField("Larry") , new JTextField(20) };
    public JTextArea messageBoard = new JTextArea();

    JPanel panelBtnNorth = new JPanel( new BorderLayout() );

    JPanel[] panelNameMessage = { new JPanel(), new JPanel()};
    JPanel panelSouth = new JPanel();
    JPanel panelVisitors = new JPanel( new FlowLayout( FlowLayout.LEFT));
    JPanel panelGroups = new JPanel ( new FlowLayout(FlowLayout.LEFT));
    JScrollPane scrollEast = new JScrollPane();
    JScrollPane scrollWest = new JScrollPane();
    JScrollPane scrollGroups = new JScrollPane();
    TitledBorder panelVisitorsTitledBorder = new TitledBorder("Members");
    JPanel panelWest = new JPanel(new BorderLayout());

    Socket chatSocket = null;
    
    boolean privateMessage = false;
    String privateReceiverIDs;
    String privateReceiverNames;
    boolean justChecked = false;
    Message message = new Message();

    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    VGroup vgroup = new VGroup();
    Group group;
    int groupID = 1;
    String groupName;
    Visitor visitor = null;
    String privateList = "";
    String visitorName;

    Container container = null;
    ClientAgent clientAgent = null;

    // public void initializeWindow() {
        // scrollEast.getViewport().add(panelVisitors);
        // scrollWest.getViewport().add(panelWest);
        // container = getContentPane();
        // panelVisitors.setBorder(panelVisitorsTitledBorder);
        // panelVisitors.setToolTipText("Select Member(s) to send a private message to.");
        // panelVisitors.setPreferredSize(new Dimension(130, frameHeight));
        // panelVisitors.setFont( new Font (fontType, Font.PLAIN, smallFontSize));

        // panelSouth.setLayout(new BorderLayout());

        // container.setBackground(Color.black);
        // messageBoard.setBackground(Color.black);
        // messageBoard.setForeground(Color.yellow);

        // for ( int i = 0; i < panelNameMessage.length; i++) {
        //     panelNameMessage[i].setLayout(new FlowLayout());
        //     labelNameMessage[i].setFont( new Font( fontType, Font.PLAIN, fontSize));
        //     textFieldNameMessage[i].setFont( new Font( fontType, Font.PLAIN, fontSize));
        //     panelNameMessage[i].add(labelNameMessage[i]);
        //     panelNameMessage[i].add(textFieldNameMessage[i]);
        //     textFieldNameMessage[i].setEnabled(false);
        // }

        // textFieldNameMessage[0].setColumns(8);
        // textFieldNameMessage[0].setForeground(Color.blue);
        // textFieldNameMessage[1].setColumns(20);
        // textFieldNameMessage[1].setToolTipText("Enter a message to send and press ENTER");

        // panelSouth.add(panelNameMessage[0], BorderLayout.WEST);
        // panelSouth.add(panelNameMessage[1], BorderLayout.CENTER);
        // container.add(new JScrollPane( messageBoard ), BorderLayout.CENTER);
        // container.add(panelSouth, BorderLayout.SOUTH);

        // addEventListeners();

        // setSize (smallWidth, frameHeight);
        // setTitle(titleStr);
        // makeConnection();
        // textFieldNameMessage[0].setText(Names.visitors[randomVal.nextInt(Names.visitors.length)]);
    // }

    public Client () {
        // initializeWindow();
        scrollEast.getViewport().add(panelVisitors);
        scrollWest.getViewport().add(panelWest);
        scrollGroups.getViewport().add(panelGroups);
        panelWest.add(scrollGroups, BorderLayout.CENTER);
        container = getContentPane();
        panelVisitors.setBorder(panelVisitorsTitledBorder);
        panelVisitors.setToolTipText("Select Member(s) to send a private message to.");
        panelVisitors.setPreferredSize(new Dimension(130, frameHeight));
        panelVisitors.setFont( new Font (fontType, Font.PLAIN, smallFontSize));
        panelSouth.setLayout(new BorderLayout());

        container.add(scrollEast, BorderLayout.EAST);
        container.add(scrollWest, BorderLayout.WEST);

        container.setBackground(Color.black);
        messageBoard.setBackground(Color.black);
        messageBoard.setForeground(Color.yellow);
        messageBoard.setFont(new Font(fontType, Font.PLAIN, fontSize + 2));
        for ( int i = 0; i < panelNameMessage.length; i++) {
            panelNameMessage[i].setLayout(new FlowLayout());
            labelNameMessage[i].setFont( new Font( fontType, Font.PLAIN, fontSize));
            textFieldNameMessage[i].setFont( new Font( fontType, Font.PLAIN, fontSize));
            panelNameMessage[i].add(labelNameMessage[i]);
            panelNameMessage[i].add(textFieldNameMessage[i]);
            textFieldNameMessage[i].setEnabled(false);
        }

        textFieldNameMessage[0].setColumns(8);
        textFieldNameMessage[0].setForeground(Color.blue);
        textFieldNameMessage[1].setColumns(20);
        textFieldNameMessage[1].setToolTipText("Enter a message to send and press ENTER");

        panelSouth.add(panelNameMessage[0], BorderLayout.WEST);
        panelSouth.add(panelNameMessage[1], BorderLayout.CENTER);
        container.add(new JScrollPane( messageBoard ), BorderLayout.CENTER);
        container.add(panelSouth, BorderLayout.SOUTH);

        addEventListeners();

        setSize (smallWidth, frameHeight);
        setTitle(titleStr);
        makeConnection();
        textFieldNameMessage[0].setText(Names.visitors[randomVal.nextInt(Names.visitors.length)]);
    }

    public void stop() {
        try {
            // Make a LOGOUT message, and send out.
            message.set(Message.LOGOUT, null, null);
            Thread thread = new ClientSender(out, new Message(message));
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if( in != null ) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if(chatSocket != null) {
                chatSocket.close();
            }
            out = null;
            in = null;
            message = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(-1);
    }

    void addEventListeners() {

        // Action Listener for the Name Text Label
        textFieldNameMessage[0].addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                visitorName = new String(textFieldNameMessage[0].getText()).trim();
                if(visitorName.equals("") ) {
                    JOptionPane.showMessageDialog( null, "Enter your name and press ENTER.");
                    return;
                }
                textFieldNameMessage[0].setEnabled(false); // PAY ATTENTION TO THESE LINES
                textFieldNameMessage[1].setEnabled(true);
                clientWindow.setTitle(titleStr);
                clientWindow.setSize(frameWidth, frameHeight);
                sendMessage( new Message( Message.LOGIN, visitorName, null));
                return;
            }
        });

        // Action Listener for the Message Text Label

        // Necessary
        textFieldNameMessage[1].addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String msg = textFieldNameMessage[1].getText().trim();
                if(msg.length() < 1) {
                    return;
                }
                msg = visitorName + ": " + msg;
                if (privateMessage) {
                    sendMessage( new Message ( Message.PRIVATE,
                    message + String.format( "  \t <private to %s>", privateReceiverIDs),
                    privateReceiverIDs));
                }
                else {
                    sendMessage( new Message( Message.PUBLIC, msg, null));
                }
                textFieldNameMessage[1].setText("");
            }
        });

        
        this.addWindowListener( new WindowAdapter ( ) {
            public void windowClosed(WindowEvent e) { 
                System.exit(0); 
            }
            public void windowClosing(WindowEvent e) { 
                stop(); 
                System.exit(0); 
            }
        } );    
    }

    void makeConnection() {
        	// String hostAddress = "localhost";

        try {
            chatSocket = new Socket(hostAddress, port);
            out = new ObjectOutputStream(chatSocket.getOutputStream());
            in = new ObjectInputStream(chatSocket.getInputStream());
            (clientAgent = new ClientAgent()).start();

            //get existing groups from server
            // message.set(Message.GETGROUPS, null, null);
            message.set(Message.LOGIN, "null", null);
            sendMessage(message);
        } catch (IOException e) {
            System.out.printf("Error in connection to the server!\n");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    void sendMessage( Message message ) {
        /*
            Thread thr = new ClientSender( out, message);
            thr.start() ;
            try { thr.join(); } catch (InterruptedException f )  { } */
        try { 
                out.writeObject(message) ; 
            } catch(IOException e ) { 
                e.printStackTrace(); 
        }
            
    }
    static int k = 1;
	static String str = null;

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }
    
    public static void main( String[] args) {
        clientWindow = new Client();
        clientWindow.setTitle("Type in your name!");
        clientWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                clientWindow.setVisible(true);
            }
        });
    }

    class ClientSender extends Thread {
        ObjectOutputStream outStream;
        Message message;

        public ClientSender(ObjectOutputStream o, Message m) {
            out = o;
            message = m;
        }

        public void run() {
            try{
                outStream.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    VisitorButtonMap visitorMap = new VisitorButtonMap();
    boolean loggedIn = false;

    class ClientAgent extends Thread {

        public ClientAgent() {
            // Does nothing
        }

        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                AbstractButton abstractBtn = (AbstractButton) actionEvent.getSource();
                switch(abstractBtn.getClass().getName()) {
                    case "javax.swing.JCheckBox":
                        visitorMap.setStatus( (JCheckBox) abstractBtn);
                        panelVisitorsTitledBorder.setTitle(String.format("Members ( %d)", visitorMap.selected));
                        privateMessage = (visitorMap.selected > 0);
                        panelVisitors.revalidate();
                        panelVisitors.repaint();
                        if(privateMessage) {
                            privateReceiverIDs = group.visitorIDList(true);
                            privateReceiverNames = group.visitorList(true);
                        }
                        break;
                    default:
                        break;
                        
                }
            }
        };

        void loginSetup(Visitor v) {
            visitorName = visitor.toString();
            textFieldNameMessage[0].setText(visitorName);
            visitor = v;
            clientWindow.setTitle(String.format("%s Visitor[%s}", titleStr, v));
            for (Component c : panelVisitors.getComponents()) {
                c.setEnabled(false);
            }
            loggedIn = true;
        }

        void addAllVisitors(String allVisitors) {
            Group group = new Group();
            group.add(allVisitors);
            for ( int i = 0; i < group.size(); i++) {
                addOneVisitor(group.get(i));
            }
        }

        void addOneVisitor(Visitor v) {
            group.add(visitor);
            JCheckBox box = new JCheckBox(visitor.toString());
            visitorMap.add(box, v);
            box.addActionListener(listener);
            panelVisitors.add(box);
        }

        void addOneVisitor(String visitorName_visitorID) {
            Visitor v = Visitor.toVisitor(visitorName_visitorID);
            addOneVisitor(v);
        }

        void removeOneVisitor(String visitorID_visitorName) {
            Visitor v = Visitor.toVisitor(visitorID_visitorName);
            if ( v == null) {
                return;
            }
            if (group == null) {
                return;
            }
            
            v = group.find(v.id);
            panelVisitors.remove(visitorMap.get(v));
            group.remove(v);
            visitorMap.remove(v);
            panelVisitorsTitledBorder.setTitle(String.format("Members ( %d )", visitorMap.selected));
        }

        void removeAllVisitors() {
            // group.clear();
            visitorMap.clear();
            panelVisitors.removeAll();
            repaintVisitorPanel();
        }

        void repaintVisitorPanel() {
            panelVisitors.revalidate();
            panelVisitors.repaint();
        }

        public void run() {
            if (in == null || messageBoard == null) {
                return;
            }

            try {
                while(true) {
                    // Read Message

                    try {
                        message = (Message) in.readObject();
                    } catch(ClassNotFoundException notFound) {
                        messageBoard.append("Error in readObject from socket client agent. \n" + notFound);
                        return;
                    }

                    switch(message.type) {
                        case Message.LOGIN:
                            Visitor visitor = Visitor.toVisitor(message.body);
                            if((visitor.name.compareTo(textFieldNameMessage[0].getText().trim()) == 0 && !loggedIn)) {
                                loginSetup(visitor);
                                addAllVisitors(message.others);
                            } else {
                                addOneVisitor(visitor);
                            }
                            repaintVisitorPanel();
                            messageBoard.append(message.body + ": logged in. \n ");
                            break;
                        case Message.PUBLIC:
                            messageBoard.append(message.body + '\n');
                            break;
                        case Message.PRIVATE:
                            messageBoard.append(message.body + '\n');
                            break;
                        case Message.LOGOUT:
                            removeOneVisitor(message.body);
                            repaintVisitorPanel();
                            messageBoard.append(message.body + ": logged out.\n");
                            break;
                        default:
                            break;
                    }
                    if (messageBoard.getText().length() > 0) {
                        messageBoard.setCaretPosition(messageBoard.getText().length() - 1);
                    }
                }
            } catch (IOException e) {
                messageBoard.append("Chatroom server is down! \n");
                return;
            }
        }
    }
}