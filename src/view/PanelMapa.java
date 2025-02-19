package view;

import controller.Lugar_DenunciaBO;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import model.Lugar_Denuncia;
import model.Usuario;
import org.jxmapviewer.viewer.GeoPosition;

public class PanelMapa extends javax.swing.JPanel {

    boolean ingresarDenu;
    float latitud;
    float longitud;
    JFrame frame;
    Lugar_DenunciaBO luDeBO;
    List<Lugar_Denuncia> luDe = new ArrayList<>();
    Usuario usu;

    public PanelMapa(JFrame frame, Usuario nodoUsu) {
        initComponents();
        mapa2.init();
        ingresarDenu = false;
        this.frame = frame;
        luDeBO = new Lugar_DenunciaBO();
        usu = nodoUsu;
        cargarDenus();
    }

    private void cargarDenus() {
        luDe = luDeBO.getLugar_Denuncia();
        for (Lugar_Denuncia indLuDe : luDe) {
            mapa2.addWaypoints(indLuDe.getLatutud(), indLuDe.getLongitud());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        mapa2 = new mapa.Mapa();
        btnAddDenuncia = new javax.swing.JButton();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setPreferredSize(new java.awt.Dimension(340, 480));

        mapa2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mapa2MouseClicked(evt);
            }
        });

        btnAddDenuncia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon-addDenuncia-32px.png"))); // NOI18N
        btnAddDenuncia.setToolTipText("Agregar denuncia.");
        btnAddDenuncia.setContentAreaFilled(false);
        btnAddDenuncia.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddDenuncia.setFocusable(false);
        btnAddDenuncia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDenunciaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mapa2Layout = new javax.swing.GroupLayout(mapa2);
        mapa2.setLayout(mapa2Layout);
        mapa2Layout.setHorizontalGroup(
            mapa2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mapa2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAddDenuncia, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(274, Short.MAX_VALUE))
        );
        mapa2Layout.setVerticalGroup(
            mapa2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mapa2Layout.createSequentialGroup()
                .addGap(424, 424, 424)
                .addComponent(btnAddDenuncia, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mapa2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mapa2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void mapa2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mapa2MouseClicked
        if (ingresarDenu) {
            if (evt.getClickCount() == 1 && evt.getButton() == MouseEvent.BUTTON1) {
                java.awt.Point p = evt.getPoint();
                GeoPosition geo = mapa2.convertPointToGeoPosition(p);
                latitud = (float) geo.getLatitude();
                longitud = (float) geo.getLongitude();
                String infoUbi = mapa2.getLocationInfo(geo);
                String valroad = mapa2.road;
                System.out.println(infoUbi);
                if (!valroad.isEmpty()) {
                    int op = JOptionPane.showConfirmDialog(null, "Ingresara una denuncia en:\n" + infoUbi, "Ubicacion de la denuncia.", JOptionPane.YES_NO_OPTION);
                    if (op == JOptionPane.OK_OPTION) {
                        frame.setVisible(false);
                        frame.dispose();
                        new DenunciaView(latitud, longitud, mapa2.getZoom(), valroad, usu).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Elija una carrtera, via, etc.", "Ubicacion no valida", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_mapa2MouseClicked

    private void btnAddDenunciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddDenunciaActionPerformed
        if (!ingresarDenu) {
            ingresarDenu = true;
            JOptionPane.showMessageDialog(null, "Click en la carretera/via para poner su denuncia.", "Denuncia,", JOptionPane.INFORMATION_MESSAGE);
        } else {
            ingresarDenu = false;
            JOptionPane.showMessageDialog(null, "Cancelo la denuncia.", "Denuncia cancelada.", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_btnAddDenunciaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddDenuncia;
    private javax.swing.JComboBox<String> jComboBox1;
    private mapa.Mapa mapa2;
    // End of variables declaration//GEN-END:variables
}
