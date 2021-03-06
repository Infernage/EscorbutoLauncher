package elr.gui.tabs;

import elr.core.util.Util;
import java.awt.Color;
import java.awt.Desktop;
import javax.swing.JFrame;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Tab panel which show the Minecraft blog.
 * @author Infernage
 */
public class MinecraftPage extends javax.swing.JPanel {
    private final JFrame frame;

    /**
     * Creates new form MinecraftPage
     */
    public MinecraftPage(JFrame f) {
        frame = f;
        initComponents();
        page.setMargin(null);
        page.setForeground(Color.darkGray);
        page.setText("<html><body><font color=\"#808080\"><br><br><br><br><br><br><br><center>"
                + "<h1>Loading page..</h1></center></font></body></html>");
        page.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (Exception ex) {
                       ex.printStackTrace();
                    }
                }
            }
        });
        try {
            page.setPage(Util.MINECRAFT_BLOG);
        } catch (Exception e) {
            e.printStackTrace();
            page.setText("<html><body><font color=\"#808080\"><br><br><br><br><br><br><br><center>"
                    + "<h1>Failed to get page</h1><br>" + e.toString() + "</center></font></body></html>");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        page = new javax.swing.JTextPane();

        setPreferredSize(new java.awt.Dimension(631, 243));

        page.setEditable(false);
        page.setContentType("text/html"); // NOI18N
        jScrollPane1.setViewportView(page);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane page;
    // End of variables declaration//GEN-END:variables
}
