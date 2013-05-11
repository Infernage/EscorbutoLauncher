package elr.gui.utilities;

import elr.core.Stack;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Class used to customize the JList.
 * @author Infernage
 */
public class ListRender extends JLabel implements ListCellRenderer{
    
    public ListRender(){
        setOpaque(true);
    }
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        this.setText((String) value);
        if (Stack.versions.isDeprecated((String) value)){
            this.setForeground(Color.red);
        } else if (Stack.versions.isRecent((String) value)){
            this.setForeground(Color.yellow);
        } else{
            this.setForeground(Color.green);
        }
        if (isSelected){
            this.setFont(new Font("Tahoma", Font.BOLD, 11));
            this.setBackground(Color.blue);
        } else{
            this.setFont(null);
            this.setBackground(Color.gray);
        }
        return this;
    }
    
}
