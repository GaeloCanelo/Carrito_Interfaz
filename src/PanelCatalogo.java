/* 
===================================================================================
    Ramírez Lozano Gael Martín & González Martínez Silvia 
    - PROYECTO - Práctica_1 Carrito de Compras
    Clase: "PanelCatalogo.java" (Vista de Productos)
    6CM4
=================================================================================== 
*/
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PanelCatalogo extends JPanel {
    private ClienteGUI parent;
    private ArrayList<Producto> catalogo;
    private JPanel gridPanel; // Panel interno para la cuadrícula

    public PanelCatalogo(ClienteGUI parent, ArrayList<Producto> catalogo) {
        this.parent = parent;
        this.catalogo = catalogo;
        
        // --- CORRECCIÓN DE ESTIRAMIENTO ---
        // Usamos BorderLayout en el panel principal
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Creamos un panel interno EXCLUSIVO para la cuadrícula
        // GridLayout(0, 3) significa: Filas automáticas, 3 Columnas fijas
        gridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        
        // Agregamos el panel de la cuadrícula al NORTE (Arriba)
        // Esto evita que se estire verticalmente cuando hay pocos productos
        add(gridPanel, BorderLayout.NORTH);
        
        renderizarProductos();
    }

    public void refrescarCatalogo(ArrayList<Producto> nuevoCatalogo) {
        this.catalogo = nuevoCatalogo;
        gridPanel.removeAll(); // Limpiamos el panel interno, no el principal
        renderizarProductos();
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void renderizarProductos() {
        for (Producto p : catalogo) {
            if (p.getStock() > 0) {
                gridPanel.add(crearTarjetaProducto(p)); // Agregamos a gridPanel
            }
        }
    }

    private JPanel crearTarjetaProducto(Producto p) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        tarjeta.setBackground(Color.WHITE);
        
        // Fijamos un tamaño preferido para que no se aplasten si hay muchos
        tarjeta.setPreferredSize(new Dimension(200, 320));

        // Imagen
        String rutaImg = "Destino_CLT/" + p.getNombreImagen();
        ImageIcon icon = new ImageIcon(rutaImg);
        Image img = icon.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
        JLabel lblImg = new JLabel(new ImageIcon(img));
        lblImg.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Nombre
        JLabel lblNombre = new JLabel(p.getNombre());
        lblNombre.setFont(new Font("Arial", Font.BOLD, 18));
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNombre.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Descripción
        JLabel lblDesc = new JLabel("<html><div style='width:180px; text-align:center;'>"+p.getDescripcion()+"</div></html>");
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);

        // Precio
        JLabel lblPrecio = new JLabel("$" + p.getPrecio());
        lblPrecio.setFont(new Font("Arial", Font.BOLD, 16));
        lblPrecio.setForeground(new Color(0, 100, 0)); 
        lblPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Stock
        JLabel lblStock = new JLabel("Disp: " + p.getStock());
        lblStock.setFont(new Font("Arial", Font.BOLD, 12));
        lblStock.setForeground(Color.RED);
        lblStock.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel de Controles
        JPanel panelControles = new JPanel();
        panelControles.setBackground(Color.WHITE);
        panelControles.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        SpinnerNumberModel modeloSpinner = new SpinnerNumberModel(1, 1, 9999, 1);
        JSpinner spinner = new JSpinner(modeloSpinner);
        spinner.setPreferredSize(new Dimension(60, 30));
        
        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setPreferredSize(new Dimension(100, 30));
        
        btnAgregar.addActionListener(e -> {
            try {
                spinner.commitEdit(); 
            } catch (java.text.ParseException pe) { return; }
            
            int cant = (int) spinner.getValue();
            
            if (cant > p.getStock()) {
                JOptionPane.showMessageDialog(this, "¡Cuidado! Cantidad inválida.\nMáximo disponible: " + p.getStock());
                spinner.setValue(p.getStock());
            } else {
                parent.agregarProducto(p, cant);
            }
        });

        panelControles.add(spinner);
        panelControles.add(btnAgregar);

        // Espaciadores
        tarjeta.add(Box.createVerticalStrut(15));
        tarjeta.add(lblImg);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(lblNombre);
        tarjeta.add(lblDesc);
        tarjeta.add(Box.createVerticalStrut(5));
        tarjeta.add(lblPrecio);
        tarjeta.add(lblStock);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(panelControles);
        tarjeta.add(Box.createVerticalStrut(15));

        return tarjeta;
    }
}