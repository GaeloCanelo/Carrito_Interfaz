/* ===================================================================================
    Ramírez Lozano Gael Martín & González Martínez Silvia 
    - PROYECTO - Práctica_1 Carrito de Compras
    - Clase: ClienteGUI.java (Ventana Principal y Lógica)
    - Grupo: 6CM4
=================================================================================== 
*/
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.awt.Desktop; 

// Librerías PDF
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.Phrase;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ClienteGUI extends JFrame {
    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private ArrayList<Producto> catalogo;
    private ArrayList<Producto> carrito = new ArrayList<>();

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private PanelCatalogo panelCatalogo;
    private PanelCarrito panelCarrito;
    
    // Variable para el diálogo de carga
    private JDialog dialogoEspera;
    // Variable para guardar el nombre del PDF actual
    private String nombreReciboActual = null;

    public ClienteGUI() {
        setTitle("Tiendita de la Esquina - Cliente v3.0");
        setSize(1200, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel loginPanel = crearPanelLogin();
        mainPanel.add(loginPanel, "LOGIN");

        add(mainPanel);
    }

    private JPanel crearPanelLogin() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 

        Font fuenteGrande = new Font("Arial", Font.PLAIN, 20); 

        JLabel lblIp = new JLabel("IP Servidor:");
        lblIp.setFont(fuenteGrande);
        JTextField txtIp = new JTextField("localhost", 15);
        txtIp.setFont(fuenteGrande);
        
        JLabel lblPort = new JLabel("Puerto:");
        lblPort.setFont(fuenteGrande);
        JTextField txtPort = new JTextField("6040", 5);
        txtPort.setFont(fuenteGrande);
        
        JButton btnConectar = new JButton("Conectar");
        btnConectar.setFont(fuenteGrande);

        // --- LÓGICA DE CONEXIÓN CON HILO (THREAD) ---
        btnConectar.addActionListener(e -> {
            String ip = txtIp.getText();
            int puerto = Integer.parseInt(txtPort.getText());
            
            mostrarDialogoEspera();
            
            new Thread(() -> {
                conectar(ip, puerto);
            }).start();
        });

        gbc.gridx = 0; gbc.gridy = 0; p.add(lblIp, gbc);
        gbc.gridx = 1; p.add(txtIp, gbc);
        gbc.gridx = 0; gbc.gridy = 1; p.add(lblPort, gbc);
        gbc.gridx = 1; p.add(txtPort, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add(btnConectar, gbc);

        return p;
    }
    
    private void mostrarDialogoEspera() {
        dialogoEspera = new JDialog(this, "Conectando...", true); // Modal
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblMensaje = new JLabel("<html><center>Contactando al Servidor...<br><br>" +
                "Si la tienda está atendiendo a otro cliente,<br>" +
                "permanecerás en esta fila de espera.</center></html>", SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Arial", Font.BOLD, 14));
        
        JProgressBar barra = new JProgressBar();
        barra.setIndeterminate(true); 
        
        p.add(lblMensaje, BorderLayout.CENTER);
        p.add(barra, BorderLayout.SOUTH);
        
        dialogoEspera.add(p);
        dialogoEspera.setSize(400, 200);
        dialogoEspera.setLocationRelativeTo(this);
        dialogoEspera.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); 
        
        SwingUtilities.invokeLater(() -> dialogoEspera.setVisible(true));
    }

    private void conectar(String ip, int puerto) {
        try {
            // 1. PRIMERO INTENTAMOS CONECTAR (Esto bloquea si hay fila)
            socket = new Socket(ip, puerto);
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());

            // 2. YA DENTRO: Ahora sí es seguro limpiar los recibos viejos de ESTA máquina
            // porque ya es nuestro turno.
            File carpeta = new File("Destino_CLT");
            if (carpeta.exists() && carpeta.isDirectory()) {
                for (File f : carpeta.listFiles()) {
                    if (f.getName().toLowerCase().endsWith(".pdf")) {
                        f.delete();
                    }
                }
            }

            // 3. Continuamos con la descarga
            catalogo = (ArrayList<Producto>) entrada.readObject();
            descargarImagenes(catalogo);

            if (dialogoEspera != null) dialogoEspera.dispose();

            SwingUtilities.invokeLater(() -> iniciarInterfazPrincipal());

        } catch (Exception ex) {
            if (dialogoEspera != null) dialogoEspera.dispose();
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE)
            );
        }
    }

    private void descargarImagenes(ArrayList<Producto> listaProds) {
        new File("Destino_CLT").mkdirs();
        try {
            for (Producto p : listaProds) {
                salida.writeObject("PEDIR_IMAGEN");
                salida.writeObject(p.getNombreImagen());
                long tamano = (long) entrada.readObject();
                
                if (tamano > 0) {
                    FileOutputStream fos = new FileOutputStream("Destino_CLT/" + p.getNombreImagen());
                    byte[] buffer = new byte[4096];
                    int leido;
                    long total = 0;
                    while (total < tamano) {
                        leido = entrada.read(buffer);
                        fos.write(buffer, 0, leido);
                        total += leido;
                    }
                    fos.close();
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void iniciarInterfazPrincipal() {
        JPanel appPanel = new JPanel(new BorderLayout());
        
        panelCatalogo = new PanelCatalogo(this, catalogo);
        panelCarrito = new PanelCarrito(this, carrito);

        appPanel.add(new JScrollPane(panelCatalogo), BorderLayout.CENTER);
        appPanel.add(panelCarrito, BorderLayout.EAST);

        mainPanel.add(appPanel, "APP");
        cardLayout.show(mainPanel, "APP");
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    salida.writeObject("SALIR");
                    // Limpieza al salir: Borramos SOLO imágenes, respetamos el PDF actual
                    File carpeta = new File("Destino_CLT");
                    if (carpeta.exists()) {
                        for (File f : carpeta.listFiles()) {
                            if (!f.getName().toLowerCase().endsWith(".pdf")) {
                                f.delete();
                            }
                        }
                    }
                } catch (Exception e) {}
            }
        });
    }

    public int getStockReal(int idProducto) {
        for(Producto p : catalogo) {
            if(p.getId() == idProducto) {
                return p.getStock();
            }
        }
        return 0;
    }

    public void agregarProducto(Producto p, int cantidad) {
        boolean existe = false;
        for (Producto item : carrito) {
            if (item.getId() == p.getId()) {
                if (item.getStock() + cantidad <= p.getStock()) {
                    item.setStock(item.getStock() + cantidad);
                    enviarActualizacion("AGREGAR", p.getId(), cantidad);
                } else {
                    JOptionPane.showMessageDialog(this, "No hay suficiente stock.\nDisponibles: " + p.getStock() + "\nEn carrito: " + item.getStock());
                }
                existe = true;
                break;
            }
        }
        
        if (!existe) {
            if(cantidad <= p.getStock()) {
                Producto itemNuevo = new Producto(p.getId(), p.getNombre(), p.getDescripcion(), p.getPrecio(), cantidad, p.getNombreImagen());
                carrito.add(itemNuevo);
                enviarActualizacion("AGREGAR", p.getId(), cantidad);
            } else {
                JOptionPane.showMessageDialog(this, "Cantidad solicitada excede el stock disponible.");
            }
        }
        panelCarrito.actualizarTabla();
    }

    public void modificarProducto(int id, int nuevaCant) {
        try {
            salida.writeObject("MODIFICAR");
            salida.writeObject(id);
            salida.writeObject(nuevaCant);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void enviarActualizacion(String comando, int id, int cant) {
        try {
            salida.writeObject(comando);
            salida.writeObject(id);
            salida.writeObject(cant);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void procesarPago() {
        try {
            double total = 0;
            for(Producto p : carrito) total += p.getPrecio() * p.getStock();

            salida.writeObject("PAGAR");
            catalogo = (ArrayList<Producto>) entrada.readObject();
            
            for (Producto p : catalogo) {
                if (p.getStock() == 0) {
                    new File("Destino_CLT/" + p.getNombreImagen()).delete();
                }
            }

            generarReciboPDF(carrito, total);
            salida.writeObject("LOG_RECIBO");

            carrito.clear();
            panelCarrito.actualizarTabla();
            panelCatalogo.refrescarCatalogo(catalogo); 
            
            JOptionPane.showMessageDialog(this, "¡Compra Exitosa! Abriendo recibo...");

            if (Desktop.isDesktopSupported()) {
                try {
                    if (nombreReciboActual != null) {
                        File myFile = new File(nombreReciboActual);
                        Desktop.getDesktop().open(myFile);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "No se pudo abrir el PDF automáticamente.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al procesar pago: " + e.getMessage());
        }
    }

    // --- PDF MEJORADO: Centrado, Negritas y Fecha Restaurada ---
    private void generarReciboPDF(ArrayList<Producto> listaCarrito, double total) throws Exception {
        // Generamos nombre único por sesión (Timestamp)
        SimpleDateFormat sdfArchivo = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdfArchivo.format(new Date());
        nombreReciboActual = "Destino_CLT/Recibo_Compra_" + timestamp + ".pdf";

        Document documento = new Document();
        PdfWriter.getInstance(documento, new FileOutputStream(nombreReciboActual));
        documento.open();
        
        com.itextpdf.text.Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLACK);
        com.itextpdf.text.Font fuenteNegrita = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        com.itextpdf.text.Font fuenteNormal = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        com.itextpdf.text.Font fuenteGrande = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);

        Paragraph titulo = new Paragraph("TIENDITA DE LA ESQUINA", fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(titulo);
        
        // --- FECHA RESTAURADA ---
        SimpleDateFormat formatoFecha = new SimpleDateFormat("EEEE dd 'de' MMMM 'de' yyyy HH:mm:ss", new Locale("es", "MX"));
        String fechaActual = formatoFecha.format(new Date());
        fechaActual = fechaActual.substring(0, 1).toUpperCase() + fechaActual.substring(1);
        
        Paragraph fecha = new Paragraph("Fecha: " + fechaActual + "\n\n", fuenteNormal);
        fecha.setAlignment(Element.ALIGN_CENTER); 
        documento.add(fecha);
        // ------------------------

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1, 4, 2, 2}); 

        String[] headers = {"Cant", "Producto", "P.Unit", "Subtotal"};
        for(String h : headers){
            PdfPCell celda = new PdfPCell(new Phrase(h, fuenteNegrita));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
            celda.setPadding(8);
            tabla.addCell(celda);
        }

        for (Producto p : listaCarrito) {
            double sub = p.getPrecio() * p.getStock();
            
            PdfPCell c1 = new PdfPCell(new Phrase(String.valueOf(p.getStock()), fuenteNormal));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(c1);
            
            PdfPCell c2 = new PdfPCell(new Phrase(p.getNombre(), fuenteNormal));
            c2.setHorizontalAlignment(Element.ALIGN_CENTER); 
            tabla.addCell(c2);
            
            PdfPCell c3 = new PdfPCell(new Phrase("$" + p.getPrecio(), fuenteNormal));
            c3.setHorizontalAlignment(Element.ALIGN_CENTER); 
            tabla.addCell(c3);
            
            PdfPCell c4 = new PdfPCell(new Phrase("$" + sub, fuenteNormal));
            c4.setHorizontalAlignment(Element.ALIGN_CENTER); 
            tabla.addCell(c4);
        }
        documento.add(tabla);
        
        Paragraph totalP = new Paragraph("\nGRAN TOTAL: $" + total, fuenteGrande);
        totalP.setAlignment(Element.ALIGN_RIGHT);
        documento.add(totalP);
        
        Paragraph despedida = new Paragraph("\n¡Gracias por su preferencia!", fuenteNormal);
        despedida.setAlignment(Element.ALIGN_CENTER);
        documento.add(despedida);

        documento.close();
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> {
            new ClienteGUI().setVisible(true);
        });
    }
}