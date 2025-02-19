package view;

import controller.CtrlBO;
import controller.DenunciaBO;
import controller.Lugar_DenunciaBO;
import controller.TramoBO;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.WindowConstants;
import mapa.Mapa;
import model.Denuncia;
import model.Lugar_Denuncia;
import model.Tramo;
import model.Usuario;
import org.jxmapviewer.viewer.GeoPosition;

public class DenunciaView extends javax.swing.JFrame {

    public DenunciaView(float latitud, float longitud, int zoom, String road, Usuario usu) {
        initComponents();
        this.latitude = latitud;
        this.longitude = longitud;
        NodoUsuario = usu;
        ini();
        mp.init(latitud, longitud, zoom);
    }

    private void ini() {
        nodoLuDenu = new Lugar_Denuncia();
        luDenuBO = new Lugar_DenunciaBO();
        ndCtrl = new CtrlBO();
        nodoTramo = new Tramo();
        tramoBO = new TramoBO();
        denunBO = new DenunciaBO();
        nodoDenun = new Denuncia();
        opTramo();
    }

    public DenunciaView() {
        initComponents();
    }

    public boolean isObligatorio() {
        return (!txtTitulo.getText().isEmpty() && !txtDescripcion.getText().isEmpty() && rbPuntoA.isSelected() && rbPuntoB.isSelected()
                && cmbEvento.getSelectedIndex() != 0 && cmbCarretera.getSelectedIndex() != 0);
    }

    public void limpiar() {
        cmbEvento.setSelectedIndex(0);
        txtTitulo.setText("");
        txtDescripcion.setText("");
        rbPuntoA.setSelected(false);
        rbPuntoB.setSelected(false);
    }

    public void msjError() {
        String mensajeError = "*";
        if (cmbEvento.getSelectedIndex() == 0) {
            txtErrorEvento.setText(mensajeError);
        } else {
            txtErrorEvento.setText("");
        }

        if (cmbCarretera.getSelectedIndex() == 0) {
            txtErrorVia.setText(mensajeError);
        } else {
            txtErrorVia.setText("");
        }

        if (txtTitulo.getText().isEmpty()) {
            txtErrorTitulo.setText(mensajeError);
        } else {
            txtErrorTitulo.setText("");
        }

        if (txtDescripcion.getText().isEmpty()) {
            txtErrorDescripcion.setText(mensajeError);
        } else {
            txtErrorDescripcion.setText("");
        }

        if (!rbPuntoB.isSelected() || !rbPuntoA.isSelected()) {
            txtErrorTramo.setText(mensajeError);
        } else {
            txtErrorTramo.setText("");
        }
    }

    public boolean captura() {
        int idEvt = ndCtrl.buscarID("EVENTO", "EVNT_ID", "EVNT_NOMBRE", cmbEvento.getSelectedItem().toString());
        int idld = addLugarDenuncia();
        int idTrm = addTramo();
        if (idEvt != -1 && idld != -1 && idTrm != -1) {
            System.out.println("capt");
            nodoDenun.setDen_fecha(fecActual());
            nodoDenun.setDen_descrpcion(txtDescripcion.getText());
            nodoDenun.setTramo_id(idTrm);
            nodoDenun.setEs_id(1);
            nodoDenun.setLu_id(idld);
            nodoDenun.setUsr_id((int) NodoUsuario.getIdUsuario());
            nodoDenun.setEvnt_id(idEvt);
            return true;
        } else {
            //JOptionPane.showMessageDialog(null, "Error en la transaccion. verifique sus datos.", "Transaccion erronea.", JOptionPane.WARNING_MESSAGE);
        }
        return false;
    }

    private int addLugarDenuncia() {
        nodoLuDenu.setLatutud(latitude);
        nodoLuDenu.setLongitud(longitude);
        luDenuBO.addLugar_Denuncia(nodoLuDenu);
        int idLDla = ndCtrl.buscarIDfloat("LUGAR_DENUNCIA", "LU_ID", "LU_LATITUD", latitude);
        int idLDlo = ndCtrl.buscarIDfloat("LUGAR_DENUNCIA", "LU_ID", "LU_LONGITUD", longitude);
        if (idLDla != -1 && idLDlo != -1 && idLDla == idLDlo) {
            System.out.println("entre-lude");
            nodoLuDenu.setLu_id(idLDla);
            return idLDla;
        }
        return -1;
    }

    private int addTramo() {
        float longi = calcLongitud(geoPuntoA, geoPuntoB);
        int idCarr = ndCtrl.buscarID("CARRETERA", "CARR_ID", "CARR_NOMBRE", cmbCarretera.getSelectedItem().toString());
        System.out.println(idCarr);
        if (idCarr != -1) {
            System.out.println("entre-tramo");
            nodoTramo.setLongitud(longi);
            nodoTramo.setInicio_tramo(iniTra);
            nodoTramo.setFin_tramo(finTra);
            nodoTramo.setCarr_id(idCarr);
            tramoBO.addTramo(nodoTramo);
            return ndCtrl.buscarIDTramo("TRAMO_INICIO", "TRAMO_FIN", iniTra, finTra);
        }
        return -1;
    }

    private void opTramo() {
        frTramo.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frTramo.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        frTramo.setSize(340, 500);
        frTramo.setResizable(false);
        frTramo.setUndecorated(true);
        frTramo.add(mp, BorderLayout.CENTER);
        mp.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1 && evt.getButton() == MouseEvent.BUTTON1) {
                    java.awt.Point p = evt.getPoint();
                        GeoPosition geo = mp.convertPointToGeoPosition(p);
                    String infoUbi = mp.getLocationInfo(geo);
                    if (!mp.road.isEmpty()) {
                        int op = JOptionPane.showConfirmDialog(null, "Ingresara una tramo en:\n" + infoUbi, "Ubicacion tramo.", JOptionPane.YES_NO_OPTION);
                        if (op == JOptionPane.OK_OPTION) {
                            if (geoPuntoA == null) {
                                geoPuntoA = geo;
                                iniTra = sector(mp.getLocationInfo(geoPuntoA));
                                mp.road = "";
                            } else {
                                geoPuntoB = geo;
                                finTra = sector(mp.getLocationInfo(geoPuntoA));
                            }
                            frTramo.setVisible(false);
                            frTramo.dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Elija una carrtera, via, etc.", "Ubicacion no valida", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        frTramo.setLocationRelativeTo(null);
    }

    private float calcLongitud(GeoPosition puntoA, GeoPosition puntoB) {
        double latitud1 = Math.toRadians(puntoA.getLatitude());
        double longitud1 = Math.toRadians(puntoA.getLongitude());
        double latitud2 = Math.toRadians(puntoB.getLatitude());
        double longitud2 = Math.toRadians(puntoB.getLongitude());

        double diferenciaLatitud = latitud2 - latitud1;
        double diferenciaLongitud = longitud2 - longitud1;

        double a = Math.sin(diferenciaLatitud / 2.0) * Math.sin(diferenciaLatitud / 2.0)
                + Math.cos(latitud1) * Math.cos(latitud2)
                * Math.sin(diferenciaLongitud / 2.0) * Math.sin(diferenciaLongitud / 2.0);

        return (float) ((float) 6371.0 * (2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a))));
    }

    private String sector(String infoUbi) {
        String[] partes = infoUbi.split(",");
        return partes[1].trim();
    }

    private Date fecActual() {
        LocalDateTime localDateTime = LocalDateTime.now();
        java.time.Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    @SuppressWarnings("checked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtTitulo = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        cmbEvento = new javax.swing.JComboBox<>();
        btnAgregar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        txtErrorTitulo = new javax.swing.JLabel();
        txtErrorVia = new javax.swing.JLabel();
        txtErrorDescripcion = new javax.swing.JLabel();
        txtErrorEvento = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        rbPuntoA = new javax.swing.JRadioButton();
        rbPuntoB = new javax.swing.JRadioButton();
        txtErrorTramo = new javax.swing.JLabel();
        cmbCarretera = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Serif", 1, 24)); // NOI18N
        jLabel1.setText("Crear denuncia.");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Titulo:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Via/Carretera:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Descripcion:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Evento:");

        txtTitulo.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        txtTitulo.setActionCommand("");
        txtTitulo.setOpaque(true);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        txtDescripcion.setColumns(20);
        txtDescripcion.setFont(new java.awt.Font("Segoe UI Emoji", 0, 10)); // NOI18N
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setRows(5);
        txtDescripcion.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtDescripcion);

        cmbEvento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Deslizamiento", "Mantenimiento vial", "Pérdida de la banca", "Alto flujo vehicular", "Derrumbe", "Desbordamiento de rio", "Fallas en la estructura del puente", "Caída de arbol", "Estabilización de talúd", "Evento Deportivo", "Restricción de carga", "Evento Especial", "Material en la vía", "Para vehiculos" }));

        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        txtErrorTitulo.setForeground(new java.awt.Color(255, 0, 0));

        txtErrorVia.setForeground(new java.awt.Color(255, 0, 0));

        txtErrorDescripcion.setForeground(new java.awt.Color(255, 0, 0));

        txtErrorEvento.setForeground(new java.awt.Color(255, 0, 0));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Tramo:");

        rbPuntoA.setText("Punto A");
        rbPuntoA.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                rbPuntoAMousePressed(evt);
            }
        });

        rbPuntoB.setText("Punto B");
        rbPuntoB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                rbPuntoBMousePressed(evt);
            }
        });

        txtErrorTramo.setForeground(new java.awt.Color(255, 0, 0));

        cmbCarretera.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Ruta Nacional 01", "Ruta Nacional 03", "Ruta Nacional 05", "Ruta Nacional 08", "Ruta Nacional 10", "Ruta Nacional 12", "Ruta Nacional 13", "Ruta Nacional 17", "Ruta Nacional 19", "Ruta Nacional 21", "Ruta Nacional 25B" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnAgregar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCancelar))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtErrorDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtErrorVia, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel5)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(txtErrorEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(txtErrorTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel6)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(txtErrorTramo, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(1, 1, 1)))
                                .addGap(17, 17, 17)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(rbPuntoA)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(rbPuntoB))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtTitulo)
                                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                            .addComponent(cmbEvento, 0, 178, Short.MAX_VALUE)
                                            .addComponent(cmbCarretera, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addContainerGap(21, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtErrorTitulo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txtTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(cmbCarretera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtErrorVia, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtErrorDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbEvento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addComponent(txtErrorEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(rbPuntoA)
                        .addComponent(rbPuntoB))
                    .addComponent(txtErrorTramo, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 145, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar)
                    .addComponent(btnAgregar))
                .addContainerGap())
        );

        jLayeredPane1.setLayer(jPanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        setVisible(false);
        Main main = new Main();
        main.setVisible(true);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        if (isObligatorio()) {
            if (captura()) {
                int idld = addLugarDenuncia();
                limpiar();
            } else {
                //JOptionPane.showMessageDialog(null, "Se genero un error al insertar los datos. Intentelo mas tarde.", "Error!", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            msjError();
        }
        setVisible(false);
        Main main = new Main();
        main.setVisible(true);
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void rbPuntoAMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rbPuntoAMousePressed
        frTramo.setVisible(true);
    }//GEN-LAST:event_rbPuntoAMousePressed

    private void rbPuntoBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rbPuntoBMousePressed
        frTramo.setVisible(true);
    }//GEN-LAST:event_rbPuntoBMousePressed

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
            java.util.logging.Logger.getLogger(DenunciaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DenunciaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DenunciaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DenunciaView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DenunciaView().setVisible(true);
            }
        });
    }

    TramoBO tramoBO;
    Tramo nodoTramo;
    String iniTra, finTra;
    Denuncia nodoDenun;
    DenunciaBO denunBO;
    Lugar_Denuncia nodoLuDenu;
    Lugar_DenunciaBO luDenuBO;
    CtrlBO ndCtrl;
    float latitude, longitude;
    Usuario NodoUsuario;
    JFrame frTramo = new JFrame();
    Mapa mp = new Mapa();
    GeoPosition geoPuntoA, geoPuntoB;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JComboBox<String> cmbCarretera;
    private javax.swing.JComboBox<String> cmbEvento;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JRadioButton rbPuntoA;
    private javax.swing.JRadioButton rbPuntoB;
    private javax.swing.JTextArea txtDescripcion;
    private javax.swing.JLabel txtErrorDescripcion;
    private javax.swing.JLabel txtErrorEvento;
    private javax.swing.JLabel txtErrorTitulo;
    private javax.swing.JLabel txtErrorTramo;
    private javax.swing.JLabel txtErrorVia;
    private javax.swing.JTextField txtTitulo;
    // End of variables declaration//GEN-END:variables

}
