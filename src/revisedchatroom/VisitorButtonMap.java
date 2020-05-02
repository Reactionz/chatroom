package revisedchatroom;

import javax.swing.*;
import java.awt.Font;
import java.util.HashMap;
import java.util.Set;

class VisitorButtonMap {
    public int selected = 0;
    JCheckBox button = null;

    HashMap <JCheckBox, Visitor> buttonMap = new HashMap<JCheckBox, Visitor>();
    HashMap <Visitor, JCheckBox> visitorMap = new HashMap<Visitor, JCheckBox>();

    public VisitorButtonMap () {
        // Does nothing
    }

    public void clear() {
        selected = 0; 
        buttonMap.clear();
        visitorMap.clear();
    }

    public Object[] getAllCheckBox() {
        Set<JCheckBox> set = buttonMap.keySet();
        return set.toArray();
    }

    public void add( JCheckBox btn, Visitor visitors) {
        button.setFont( new Font("Verdana", Font.PLAIN, 10));
        buttonMap.put( button, visitors);
        visitorMap.put( visitors, button);
    }

    public void remove( Visitor visitor ) {
        button = visitorMap.get(visitor);
        if ( button.isSelected()) {
            selected--;
        }
        buttonMap.remove(button);
        visitorMap.remove(visitor);
    }

    public Visitor get ( JCheckBox checkBox ) {
        return buttonMap.get(checkBox);
    }

    public JCheckBox get(Visitor visitor) {
        return visitorMap.get( visitor); 
    }

    public void setStatus (JCheckBox btn ) {
        buttonMap.get(btn).checked = button.isSelected();
        selected += button.isSelected() ? 1 : -1;
    }
}