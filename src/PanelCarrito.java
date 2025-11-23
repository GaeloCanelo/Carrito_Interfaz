/* 
===================================================================================
    Ramírez Lozano Gael Martín & González Martínez Silvia 
    - PROYECTO - Práctica_1 Carrito de Compras
    Clase: "PanelCarrito.java" (Sidebar del Carrito)
    6CM4
=================================================================================== 
*/
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class PanelCarrito extends JPanel {
    private ClienteGUI parent;
    private ArrayList<Producto> carrito;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JLabel lblTotal;

    public PanelCarrito(ClienteGUI parent, ArrayList<Producto> carrito) {
        this.parent = parent;
        this.carrito = carrito;
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 0)); 
        
        TitledBorder borde = BorderFactory.createTitledBorder("Mi Carrito");
        borde.setTitleFont(new Font("Arial", Font.BOLD, 18));
        borde.setTitleColor(Color.BLACK);
        setBorder(borde);

        String[] columnas = {"Producto", "Cantidad", "Subtotal"}; 
        modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tabla = new JTable(modelo);
        tabla.setRowHeight(25); 
        tabla.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelSur = new JPanel();
        panelSur.setLayout(new BoxLayout(panelSur, BoxLayout.Y_AXIS));
        panelSur.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        lblTotal = new JLabel("TOTAL: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 24)); 
        lblTotal.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton btnModificar = new JButton("Modificar Cantidad");
        btnModificar.setFont(new Font("Arial", Font.PLAIN, 16));
        btnModificar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnModificar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); 
        
        JButton btnEliminar = new JButton("Eliminar Item");
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 16));
        btnEliminar.setBackground(new Color(255, 100, 100)); 
        // --- CORRECCIÓN AQUÍ: Texto NEGRO para garantizar contraste ---
        btnEliminar.setForeground(Color.BLACK); 
        // --------------------------------------------------------------
        btnEliminar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEliminar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JButton btnPagar = new JButton("PAGAR Y GENERAR RECIBO");
        btnPagar.setFont(new Font("Arial", Font.BOLD, 16));
        btnPagar.setBackground(new Color(50, 205, 50)); 
        btnPagar.setForeground(Color.BLACK); 
        btnPagar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPagar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); 

        btnModificar.addActionListener(e -> modificarSeleccionado());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        btnPagar.addActionListener(e -> {
            if (carrito.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El carrito está vacío.");
            } else {
                parent.procesarPago();
            }
        });

        panelSur.add(lblTotal);
        panelSur.add(Box.createVerticalStrut(20));
        panelSur.add(btnModificar);
        panelSur.add(Box.createVerticalStrut(10));
        panelSur.add(btnEliminar);
        panelSur.add(Box.createVerticalStrut(25));
        panelSur.add(btnPagar);

        add(panelSur, BorderLayout.SOUTH);
    }

    public void actualizarTabla() {
        modelo.setRowCount(0);
        double total = 0;
        for (Producto p : carrito) {
            double subtotal = p.getPrecio() * p.getStock();
            total += subtotal;
            modelo.addRow(new Object[]{
                p.getNombre(),
                p.getStock(),
                "$" + subtotal
            });
        }
        lblTotal.setText("TOTAL: $" + total);
    }

    private void modificarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            String nombreProd = (String) modelo.getValueAt(fila, 0);
            Producto itemEnCarrito = null;
            for(Producto item : carrito) {
                if(item.getNombre().equals(nombreProd)) {
                    itemEnCarrito = item;
                    break;
                }
            }

            if (itemEnCarrito != null) {
                int stockMaximoReal = parent.getStockReal(itemEnCarrito.getId());
                String input = JOptionPane.showInputDialog(this, 
                    "Stock disponible en tienda: " + stockMaximoReal + "\nNueva cantidad:", 
                    itemEnCarrito.getStock());
                
                if (input != null) {
                    try {
                        int nuevaCant = Integer.parseInt(input);
                        if (nuevaCant > 0) {
                            if (nuevaCant <= stockMaximoReal) {
                                itemEnCarrito.setStock(nuevaCant);
                                parent.modificarProducto(itemEnCarrito.getId(), nuevaCant);
                                actualizarTabla();
                            } else {
                                JOptionPane.showMessageDialog(this, "Error: La cantidad supera el stock disponible (" + stockMaximoReal + ").");
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Cantidad inválida.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Ingresa un número válido.");
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la tabla.");
        }
    }

    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            String nombreProd = (String) modelo.getValueAt(fila, 0);
            Producto aBorrar = null;
            for(Producto item : carrito) if(item.getNombre().equals(nombreProd)) aBorrar = item;
            
            if (aBorrar != null) {
                carrito.remove(aBorrar);
                parent.modificarProducto(aBorrar.getId(), 0); 
                actualizarTabla();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la tabla.");
        }
    }
}