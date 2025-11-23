# 🛒 Sistema de Carrito de Compras (Cliente-Servidor) con Interfaz Gráfica

Este repositorio contiene la implementación de un sistema de ventas distribuido basado en la arquitectura **Cliente-Servidor** utilizando **Java Sockets**. El proyecto cuenta con una interfaz gráfica (Swing), gestión de inventario en tiempo real, transmisión de archivos (imágenes) y generación de comprobantes de compra en PDF.

**Asignatura:** Aplicaciones para Comunicaciones en Red  
**Tecnologías:** Java (Sockets, Serialization, Swing UI), iText PDF Library

---

## 🚀 Características Principales

### 🖥️ Servidor
- **Persistencia de Datos:** Carga, actualiza y guarda el inventario mediante serialización de objetos (`productos.dat`)
- **Gestión de Conexiones:** Atiende peticiones de clientes a través del puerto **6040**
- **Transmisión de Archivos:** Envía imágenes de productos bajo demanda al cliente (File Transfer)
- **Control de Stock:** Valida existencias y actualiza el inventario global en tiempo real tras cada compra
- **Logs Centralizados:** Monitorea la actividad del sistema (conexiones, compras, productos agotados, generación de recibos)

### 👤 Cliente (GUI)
- **Interfaz Gráfica (Swing):** Diseño intuitivo con paneles de conexión, catálogo visual y gestión de carrito
- **Carrito Interactivo:** Permite agregar productos, modificar cantidades con validación de stock real y eliminar ítems
- **Sincronización Visual:** El catálogo se actualiza automáticamente tras cada compra, ocultando productos agotados
- **Generación de Recibos:** Crea un **ticket de compra en PDF** profesional (con tablas y diseño estético) utilizando la librería **iText**
- **Configuración Flexible:** Permite ingresar IP y Puerto manualmente al iniciar
- **Modo Kiosco:** Limpieza automática de archivos temporales (imágenes) al cerrar sesión, protegiendo el historial de compras (PDF)

---

## 📂 Estructura del Repositorio

El proyecto está organizado de la siguiente manera:

```
Carrito_Interfaz/
├── lib/
│   └── itextpdf-5.5.13.2.jar       # Librería externa requerida para PDFs
├── src/
│   ├── Servidor.java               # Lógica del Backend/Servidor
│   ├── ClienteGUI.java             # Ventana Principal y Lógica del Cliente
│   ├── PanelCatalogo.java          # Componente visual: Cuadrícula de productos
│   ├── PanelCarrito.java           # Componente visual: Sidebar de gestión de compra
│   ├── Producto.java               # Modelo de datos (Serializable)
│   ├── GeneradorCatalogo.java      # Utilidad para resetear/crear inventario inicial
│   ├── productos.dat               # Base de datos binaria (se genera automáticamente)
│   ├── Origen_SV/                  # Carpeta de Imágenes del SERVIDOR (Fuente)
│   │   ├── churrumais.png
│   │   ├── chachitos.png
│   │   └── ...
│   └── Destino_CLT/                # Carpeta temporal del CLIENTE (Descargas y PDF)
└── README.md
```

---

## ⚙️ Requisitos Previos

- **Java Development Kit (JDK):** Versión 8 o superior
- **Librería iText:** El archivo `itextpdf-5.5.13.2.jar` debe estar presente en la carpeta `lib`

---

## 🛠️ Instrucciones de Compilación y Ejecución

Debido al uso de la librería externa para PDFs, es necesario especificar el **Classpath** (`-cp`) al compilar y ejecutar.

> **Nota:** Abre tu terminal y ubícate dentro de la carpeta `src`

### 🪟 Windows (PowerShell / CMD)

**1. Compilar todo el proyecto:**
```powershell
javac -cp ".;../lib/itextpdf-5.5.13.2.jar" *.java
```

**2. Ejecutar el Servidor:**
```powershell
java -cp ".;../lib/itextpdf-5.5.13.2.jar" Servidor
```

**3. Ejecutar el Cliente (en otra terminal):**
```powershell
java -cp ".;../lib/itextpdf-5.5.13.2.jar" ClienteGUI
```

### 🐧 Linux / MacOS

> **Atención:** El separador de rutas en sistemas Unix es dos puntos (`:`) en lugar de punto y coma (`;`)

**1. Compilar todo el proyecto:**
```bash
javac -cp ".:../lib/itextpdf-5.5.13.2.jar" *.java
```

**2. Ejecutar el Servidor:**
```bash
java -cp ".:../lib/itextpdf-5.5.13.2.jar" Servidor
```

**3. Ejecutar el Cliente:**
```bash
java -cp ".:../lib/itextpdf-5.5.13.2.jar" ClienteGUI
```

---

## 📝 Notas Importantes

1. **Configuración de Red:**
   - El servidor escucha por defecto en el puerto **6040**
   - Al iniciar el Cliente, ingresa `localhost` para pruebas locales o la **IP del Servidor** si estás en una red distribuida (ej. Máquina Virtual)

2. **Imágenes:**
   - Asegúrate de que las imágenes (`.png` o `.jpg`) existan en `src/Origen_SV` y que sus nombres coincidan exactamente con los definidos en el código

3. **Base de Datos Inicial:**
   - Si el archivo `productos.dat` no existe o quieres reiniciar el stock, ejecuta primero la utilidad:
   ```bash
   # Linux/MacOS
   java -cp ".:../lib/itextpdf-5.5.13.2.jar" GeneradorCatalogo
   
   # Windows
   java -cp ".;../lib/itextpdf-5.5.13.2.jar" GeneradorCatalogo
   ```

---

## 👥 Autores

**Ramírez Lozano Gael Martín** & **González Martínez Silvia**

**Fecha:** Noviembre 2025