package view;

import java.awt.Cursor;
import javax.swing.JPanel;
import model.Usuario;

public final class Main extends javax.swing.JFrame {

    PanelMapa pMapa;
    Usuario nodoUsu;

    public Main() {
        initComponents();
        pMapa = new PanelMapa(this, nodoUsu);
        showPanels(pMapa);
    }
    
    public Main(Usuario nodoUsu) {
        initComponents();
        this.nodoUsu = nodoUsu;
        pMapa = new PanelMapa(this, nodoUsu);
        showPanels(pMapa);
    }

    public void showPanels(JPanel p) {
        p.setSize(340, 500);
        p.setLocation(0, 0);

        content.removeAll();
        content.add(p);
        content.revalidate();
        content.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnUser = new javax.swing.JButton();
        btnWebGobierno = new javax.swing.JButton();
        btnConsultar = new javax.swing.JButton();
        btnMapa = new javax.swing.JButton();
        btnNovedad = new javax.swing.JButton();
        content = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TRANSPORMAP");
        setResizable(false);

        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(29, 137, 246));

        btnUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-btnUser-32px.png"))); // NOI18N
        btnUser.setContentAreaFilled(false);
        btnUser.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUser.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-btnUser-Pressed-32px.png"))); // NOI18N
        btnUser.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-btnUser-48px.png"))); // NOI18N
        btnUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserActionPerformed(evt);
            }
        });

        btnWebGobierno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-way-32px.png"))); // NOI18N
        btnWebGobierno.setContentAreaFilled(false);
        btnWebGobierno.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnWebGobierno.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-wayPressed-32px.png"))); // NOI18N
        btnWebGobierno.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-way-48px.png"))); // NOI18N
        btnWebGobierno.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnWebGobiernoMouseClicked(evt);
            }
        });
        btnWebGobierno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWebGobiernoActionPerformed(evt);
            }
        });

        btnConsultar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-lupa-32px.png"))); // NOI18N
        btnConsultar.setContentAreaFilled(false);
        btnConsultar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConsultar.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-lupaPressed-32px.png"))); // NOI18N
        btnConsultar.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-lupa-48px.png"))); // NOI18N
        btnConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarActionPerformed(evt);
            }
        });

        btnMapa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-mapa-32px.png"))); // NOI18N
        btnMapa.setContentAreaFilled(false);
        btnMapa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMapa.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-mapa-48px.png"))); // NOI18N
        btnMapa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapaActionPerformed(evt);
            }
        });

        btnNovedad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-novedad-32px.png"))); // NOI18N
        btnNovedad.setContentAreaFilled(false);
        btnNovedad.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNovedad.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-NovedadPressed-32px.png"))); // NOI18N
        btnNovedad.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-Novedad-48px.png"))); // NOI18N
        btnNovedad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovedadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(btnMapa, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNovedad, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnWebGobierno, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUser, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnUser, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
            .addComponent(btnWebGobierno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnConsultar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnMapa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnNovedad, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel2);
        jPanel2.setBounds(0, 500, 340, 50);

        content.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout contentLayout = new javax.swing.GroupLayout(content);
        content.setLayout(contentLayout);
        contentLayout.setHorizontalGroup(
            contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 340, Short.MAX_VALUE)
        );
        contentLayout.setVerticalGroup(
            contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );

        jPanel1.add(content);
        content.setBounds(0, 0, 340, 500);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleDescription("");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUserActionPerformed
        showPanels(new PanelUser());
    }//GEN-LAST:event_btnUserActionPerformed

    private void btnNovedadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovedadActionPerformed
        showPanels(new PanelNovedades());
    }//GEN-LAST:event_btnNovedadActionPerformed

    private void btnWebGobiernoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWebGobiernoActionPerformed
        
    }//GEN-LAST:event_btnWebGobiernoActionPerformed

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnConsultarActionPerformed

    private void btnWebGobiernoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnWebGobiernoMouseClicked
        if (evt.getClickCount() == 1) {
            btnWebGobierno.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            WebGobierno web = new WebGobierno();
            web.abrirWeb(this);
            btnWebGobierno.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }//GEN-LAST:event_btnWebGobiernoMouseClicked

    private void btnMapaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapaActionPerformed
        showPanels(pMapa);
    }//GEN-LAST:event_btnMapaActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConsultar;
    private javax.swing.JButton btnMapa;
    private javax.swing.JButton btnNovedad;
    private javax.swing.JButton btnUser;
    private javax.swing.JButton btnWebGobierno;
    private javax.swing.JPanel content;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
