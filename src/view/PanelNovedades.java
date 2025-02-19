
package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class PanelNovedades extends javax.swing.JPanel {


    public PanelNovedades() {
        initComponents();
        addPus();
    }
    
    private void addPus(){
        for (int i = 0; i < 8; i++) {
            JPanel panelpubliUser= new JPanel();
            panelpubliUser.setPreferredSize(new Dimension(340, 150));
            panelpubliUser.setBackground(new Color(i*10, i*20, i*25));
            
            JTextArea txtArea = new javax.swing.JTextArea();
            txtArea.setEditable(false);
            txtArea.setColumns(20);
            txtArea.setFont(new java.awt.Font("SansSerif", 0, 10)); // NOI18N
            txtArea.setForeground(new java.awt.Color(0, 0, 0));
            txtArea.setLineWrap(true);
            txtArea.setRows(5);
            txtArea.setWrapStyleWord(true);
           
            panelpubliUser.setLayout(new FlowLayout(FlowLayout.LEFT));
            
            JLabel label = new JLabel("Panel " + (i + 1));
            JLabel espacio = new JLabel("    ");
            //panelpubliUser.add(label);
            panelpubliUser.add(txtArea);
            
            panelContenido.add(panelpubliUser);
            panelContenido.add(espacio);
        }
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScroll = new javax.swing.JScrollPane();
        panelContenido = new javax.swing.JPanel();

        jScroll.setBorder(null);
        jScroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScroll.setFocusable(false);
        jScroll.setWheelScrollingEnabled(true);
        jScroll.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                jScroll.getVerticalScrollBar().setValue(jScroll.getVerticalScrollBar().getValue() + notches * 40);
            }
        });

        panelContenido.setLayout(new javax.swing.BoxLayout(panelContenido, javax.swing.BoxLayout.Y_AXIS));
        jScroll.setViewportView(panelContenido);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScroll)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 62, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScroll;
    private javax.swing.JPanel panelContenido;
    // End of variables declaration//GEN-END:variables
}
