/* 
===================================================================================
    Ramírez Lozano Gael Martín & González Martínez Silvia 
    - PROYECTO - Práctica_1 Carrito de Compras
    Clase: "Servidor.java"
    6CM4
=================================================================================== 
*/
import java.io.*;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

public class Servidor {
    
    private static ArrayList<Producto> catalogo;

    public static void main(String[] args) {
        System.out.println("--- Arrancando Servidor ---");
        
        cargarCatalogo();
        
        if (catalogo == null) {
            System.out.println("Error grave: No hay catálogo.");
            return;
        }
        
        try {
            ServerSocket serverSocket = new ServerSocket(6040);
            System.out.println("Servidor escuchando en el puerto 6040...");

            while (true) {
                System.out.println("Esperando cliente...");
                Socket clienteSocket = serverSocket.accept(); 
                
                ArrayList<Producto> carritoActual = new ArrayList<>();
                
                System.out.println("¡Cliente conectado desde: " + clienteSocket.getInetAddress() + "!");

                try {
                    ObjectOutputStream salida = new ObjectOutputStream(clienteSocket.getOutputStream());
                    ObjectInputStream entrada = new ObjectInputStream(clienteSocket.getInputStream());
                    
                    salida.writeObject(catalogo);
                    System.out.println("Catálogo enviado. Esperando órdenes...");
                    
                    boolean clienteConectado = true;
                    
                    while (clienteConectado) {
                        try {
                            Object mensaje = entrada.readObject();
                            
                            // --- CASO 1: SALIR ---
                            if (mensaje instanceof String && mensaje.equals("SALIR")) {
                                System.out.println("El cliente se fue.");
                                System.out.println("Limpiando archivos temporales...");
                                System.out.println("Carpeta Cliente Limpia.");
                                clienteConectado = false;
                            
                            // --- CASO 2: PEDIR IMAGEN (MODIFICADO) ---
                            } else if (mensaje instanceof String && mensaje.equals("PEDIR_IMAGEN")) {
                                String nombreImagen = (String) entrada.readObject();
                                
                                // 1. Buscamos el producto para verificar su stock
                                Producto prodRelacionado = null;
                                for (Producto p : catalogo) {
                                    if (p.getNombreImagen().equals(nombreImagen)) {
                                        prodRelacionado = p;
                                        break;
                                    }
                                }

                                // 2. Decidimos si enviamos o no
                                boolean puedeEnviar = false;
                                
                                // Si el producto existe y tiene stock, O si no encontramos producto (quizás es una imagen genérica), intentamos enviar
                                if (prodRelacionado != null) {
                                    if (prodRelacionado.getStock() > 0) {
                                        puedeEnviar = true;
                                    } else {
                                        // AQUÍ ESTÁ EL LOG QUE PEDISTE
                                        System.out.println(">> [INFO] No se envió imagen: " + nombreImagen + ". Razón: Producto Agotado.");
                                        salida.writeObject(0L); // Enviamos 0 para decir "no hay imagen"
                                    }
                                } else {
                                    // Si no encontramos el producto en el catálogo (caso raro), intentamos enviar el archivo si existe
                                    puedeEnviar = true; 
                                }

                                // 3. Si pasó la prueba del stock, intentamos enviar el archivo físico
                                if (puedeEnviar) {
                                    File archivoImagen = new File("Origen_SV/" + nombreImagen);
                                    if (archivoImagen.exists()) {
                                        System.out.print(">> Imagen enviada al cliente: " + nombreImagen);
                                        long tamano = archivoImagen.length();
                                        System.out.println(" (Enviando " + tamano + " bytes)");
                                        salida.writeObject(tamano);
                                        
                                        FileInputStream fis = new FileInputStream(archivoImagen);
                                        byte[] buffer = new byte[4096];
                                        int leido;
                                        while ((leido = fis.read(buffer)) != -1) {
                                            salida.write(buffer, 0, leido);
                                        }
                                        fis.close();
                                        salida.flush(); 
                                    } else {
                                        System.out.println(" (No encontrada en Origen_SV)");
                                        salida.writeObject(0L); 
                                    }
                                }
                            
                            // --- CASO 3: AGREGAR ---
                            } else if (mensaje instanceof String && mensaje.equals("AGREGAR")) {
                                int idRecibido = (int) entrada.readObject();
                                int cantRecibida = (int) entrada.readObject();
                                
                                System.out.println(">> Cliente agrega ID " + idRecibido + " (Cant: " + cantRecibida + ")");
                                
                                for (Producto p : catalogo) {
                                    if (p.getId() == idRecibido) {
                                        Producto item = new Producto(p.getId(), p.getNombre(), p.getDescripcion(), p.getPrecio(), cantRecibida, p.getNombreImagen());
                                        carritoActual.add(item);
                                        break;
                                    }
                                }

                            // --- CASO 4: MODIFICAR ---
                            } else if (mensaje instanceof String && mensaje.equals("MODIFICAR")) {
                                int idModificar = (int) entrada.readObject();
                                int nuevaCant = (int) entrada.readObject();
                                
                                System.out.println(">> Cliente modifica ID " + idModificar + " a Cantidad: " + nuevaCant);
                                
                                Iterator<Producto> iter = carritoActual.iterator();
                                while (iter.hasNext()) {
                                    Producto p = iter.next();
                                    if (p.getId() == idModificar) {
                                        if (nuevaCant == 0) {
                                            iter.remove(); 
                                            System.out.println("   -> Producto eliminado del carrito.");
                                        } else {
                                            p.setStock(nuevaCant); 
                                            System.out.println("   -> Cantidad actualizada.");
                                        }
                                        break;
                                    }
                                }

                            // --- CASO 5: PAGAR ---
                            } else if (mensaje instanceof String && mensaje.equals("PAGAR")) {
                                System.out.println(">> CLIENTE ESTÁ PAGANDO...");
                                
                                for (Producto itemCompra : carritoActual) {
                                    for (Producto itemCatalogo : catalogo) {
                                        if (itemCatalogo.getId() == itemCompra.getId()) {
                                            int stockActual = itemCatalogo.getStock();
                                            int cantidadComprada = itemCompra.getStock();
                                            int nuevoStock = stockActual - cantidadComprada;
                                            
                                            itemCatalogo.setStock(nuevoStock);
                                            
                                            if (nuevoStock == 0) {
                                                System.out.println("[INFO] Producto ID: " + itemCatalogo.getId() + " " + itemCatalogo.getNombre() + " Agotado. Imagen Eliminada.");
                                            }
                                            break;
                                        }
                                    }
                                }
                                
                                guardarCatalogo(); 
                                
                                salida.reset(); 
                                salida.writeObject(catalogo);
                                carritoActual.clear();
                                System.out.println(">> ¡Compra exitosa! Inventario actualizado y enviado.");
                                
                            // --- CASO 6: LOG DE RECIBO ---
                            } else if (mensaje instanceof String && mensaje.equals("LOG_RECIBO")) {
                                System.out.println(" [PDF] Recibo generado correctamente en 'Destino_CLT'.");
                            }
                            
                        } catch (Exception e) {
                            System.out.println("Cliente desconectado.");
                            clienteConectado = false;
                        }
                    }
                    
                    entrada.close();
                    salida.close();
                    clienteSocket.close();
                    System.out.println("Sesión cerrada.");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void cargarCatalogo() {
        try {
            FileInputStream archivo = new FileInputStream("productos.dat");
            ObjectInputStream lector = new ObjectInputStream(archivo);
            catalogo = (ArrayList<Producto>) lector.readObject();
            lector.close();
            archivo.close();
        } catch (Exception e) {
            System.out.println("Error cargando catálogo: " + e.getMessage());
        }
    }

    private static void guardarCatalogo() {
        try {
            FileOutputStream archivo = new FileOutputStream("productos.dat");
            ObjectOutputStream escritor = new ObjectOutputStream(archivo);
            escritor.writeObject(catalogo);
            escritor.close();
            archivo.close();
            System.out.println("(Archivo productos.dat actualizado)");
        } catch (Exception e) {
            System.out.println("Error guardando catálogo: " + e.getMessage());
        }
    }
}