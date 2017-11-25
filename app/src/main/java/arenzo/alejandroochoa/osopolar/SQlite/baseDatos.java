package arenzo.alejandroochoa.osopolar.SQlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import arenzo.alejandroochoa.osopolar.ClasesBase.cliente;
import arenzo.alejandroochoa.osopolar.ClasesBase.listaPrecio;
import arenzo.alejandroochoa.osopolar.ClasesBase.oVenta;
import arenzo.alejandroochoa.osopolar.ClasesBase.producto;
import arenzo.alejandroochoa.osopolar.ClasesBase.productoLista;
import arenzo.alejandroochoa.osopolar.ClasesBase.ventaDetalle;

/**
 * Created by AlejandroMissael on 23/04/2017.
 */

public class baseDatos extends SQLiteOpenHelper {

    //CONFIGURACION DE LA BASE DE DATOS
    private final static String nombreDB = "osopolar.sqlite";
    private final static String TAG = "baseDatos";
    private final static int version = 1;
    private final String VENTA = "Venta", PRODUCTO = "Producto", PRODUCTOLISTA = "ProductoLista", LISTAPRECIO = "ListaPrecio", VENTADETALLE = "VentaDetalle", CLIENTE = "Cliente";

    public baseDatos(Context context){
        super(context,nombreDB,null,version);
    }

    //TABLAS A GENERAR
    private final static String tablaProducto = "CREATE TABLE Producto(IdProducto INTEGER PRIMARY KEY, Nombre TEXT, UnidadMedida TEXT, Activo BOOLEAN)";
    private final static String tablaProductoLista = "CREATE TABLE ProductoLista(IdProducto INTEGER, IdListaPrecio INTEGER, Precio REAL, PRIMARY KEY(IdProducto, IdListaPrecio))";
    private final static String tablaListaPrecio = "CREATE TABLE ListaPrecio(IdListaPrecio INTEGER PRIMARY KEY, Descripcion TEXT)";
    private final static String tablaVenta = "CREATE TABLE Venta(IdVenta INTEGER PRIMARY KEY AUTOINCREMENT, IdCliente INTEGER, Vendedor INTEGER, Fecha TEXT, Total REAL, Latitud TEXT, Longitud TEXT, Cancelada BOOLEAN, Sincronizado BOOLEAN)";
    private final static String tablaVentaDetalle = "CREATE TABLE VentaDetalle(IdVenta INTEGER, IdProducto INTEGER, Cantidad Integer, PUnitario REAL, Subtotal REAL, Sincronizado BOOLEAN)";
    private final static String tablaCliente = "CREATE TABLE Cliente(IdCliente INTEGER, IdListaPrecio INTEGER, Nombre TEXT, Calle TEXT, Numero TEXT, Latitud TEXT, Longitud TEXT, PRIMARY KEY(IdCliente, IdListaPrecio))";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tablaProducto);
        db.execSQL(tablaProductoLista);
        db.execSQL(tablaListaPrecio);
        db.execSQL(tablaVenta);
        db.execSQL(tablaVentaDetalle);
        db.execSQL(tablaCliente);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Producto");
        db.execSQL("DROP TABLE IF EXISTS ProductoLista");
        db.execSQL("DROP TABLE IF EXISTS ListaPrecio");
        db.execSQL("DROP TABLE IF EXISTS Venta");
        db.execSQL("DROP TABLE IF EXISTS VentaDetalle");
        db.execSQL("DROP TABLE IF EXISTS Cliente");
        db.execSQL(tablaProducto);
        db.execSQL(tablaProductoLista);
        db.execSQL(tablaListaPrecio);
        db.execSQL(tablaVenta);
        db.execSQL(tablaVentaDetalle);
        db.execSQL(tablaCliente);
    }

    //INSERTAR*****************************
    public boolean insertarVentaDetalle(ArrayList<ventaDetalle> aVentas, Context context){
        SQLiteDatabase db = getWritableDatabase();
        boolean estadoInsercion = false;
        for (ventaDetalle venta : aVentas) {
            ContentValues nuevaVentaDetalle = new ContentValues();
            nuevaVentaDetalle.put("IdVenta", venta.getIdVenta());
            nuevaVentaDetalle.put("IdProducto", venta.getIdProducto());
            nuevaVentaDetalle.put("Cantidad", venta.getCantidad());
            nuevaVentaDetalle.put("PUnitario", venta.getIdProducto());
            nuevaVentaDetalle.put("Subtotal", venta.getSubtotal());
            nuevaVentaDetalle.put("Sincronizado", venta.isSincronizado());
            try {
                db.insertOrThrow(VENTADETALLE, null, nuevaVentaDetalle);
                estadoInsercion = true;
            } catch (SQLiteException ex) {
                Toast.makeText(context, "Error al insertar detalle de venta: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return estadoInsercion;
    }


    public boolean insertarVenta(oVenta venta, Context context){
        SQLiteDatabase db = getWritableDatabase();
        boolean estadoInsercion = false;
        ContentValues nuevaVenta = new ContentValues();
        nuevaVenta.put("IdCliente", venta.getIdCliente());
        nuevaVenta.put("Vendedor", venta.getVendedor());
        nuevaVenta.put("Fecha", obtenerFecha());
        nuevaVenta.put("Total", venta.getTotal());
        nuevaVenta.put("Latitud", venta.getLatitud());
        nuevaVenta.put("Longitud", venta.getLongitud());
        nuevaVenta.put("Cancelada", venta.getCancelada());
        nuevaVenta.put("Sincronizado", venta.getSincronizado());
        try {
            db.insertOrThrow(VENTA, null, nuevaVenta);
            estadoInsercion = true;
        }catch (SQLiteException ex){
            Toast.makeText(context, "Error al insertar venta: "+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return estadoInsercion;
    }

    public void insertarProductos(ArrayList<producto> aProductos, Context context){
        SQLiteDatabase db = getWritableDatabase();
        for(producto producto : aProductos){
            ContentValues cvProducto = new ContentValues();
            cvProducto.put("IdProducto", producto.getIdProducto());
            cvProducto.put("Nombre", producto.getNombre());
            cvProducto.put("UnidadMedida", producto.getUnidadMedida());
            cvProducto.put("Activo", producto.getActivo());
            try{
                db.insertOrThrow(PRODUCTO, null, cvProducto);
            }catch (SQLiteException ex){
                Toast.makeText(context, "Error al insertar el producto: "+ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void insertarListaPrecio(ArrayList<listaPrecio> aListaPrecio, Context context){
        SQLiteDatabase db = getWritableDatabase();
        for (listaPrecio lista : aListaPrecio){
            ContentValues cvLista = new ContentValues();
            cvLista.put("IdListaPrecio", lista.getidListaPrecio());
            cvLista.put("Descripcion", lista.getDescripcion());
            try{
                db.insertOrThrow(LISTAPRECIO, null, cvLista);
            }catch (SQLiteException ex){
                Toast.makeText(context, "Error al insertar el listaPrecio: "+ex.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    public void insertarProductoListas(ArrayList<productoLista> aProductoLista, Context context){
        SQLiteDatabase db = getWritableDatabase();
        for (productoLista producto : aProductoLista){
            ContentValues cvProducto = new ContentValues();
            cvProducto.put("IdProducto", producto.getIdProducto());
            cvProducto.put("IdListaPrecio", producto.getIdListaPrecio());
            cvProducto.put("Precio", producto.getPrecio());
            try{
                db.insertOrThrow(PRODUCTOLISTA, null, cvProducto);
            }catch (SQLiteException ex){
                Toast.makeText(context, "Error al insertar el productoLista: "+ex.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    public void insertarCliente(ArrayList<cliente> aClientes, Context context){
        SQLiteDatabase db = getWritableDatabase();
        for (cliente cliente : aClientes){
            ContentValues cvCliente = new ContentValues();
            cvCliente.put("IdCliente", cliente.getIdCliente());
            cvCliente.put("IdListaPrecio", cliente.getIdListaPrecios());
            cvCliente.put("Nombre", cliente.getNombre());
            cvCliente.put("Calle", cliente.getCalle());
            cvCliente.put("Numero", cliente.getNumero());
            cvCliente.put("Latitud", cliente.getLatitud());
            cvCliente.put("Longitud", cliente.getLongitud());
            try{
                db.insertOrThrow(CLIENTE, null, cvCliente);
            }catch (SQLiteException ex){
                Toast.makeText(context, "Error al insertar el cliente: "+ex.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
    //ACTUALIZAR*****************************


    //BORRAR*****************************

    public void borrarTodosDatos(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(PRODUCTO, null, null);
        db.delete(PRODUCTOLISTA, null, null);
        db.delete(LISTAPRECIO, null, null);
        db.delete(CLIENTE, null, null);
    }


    //SELECCIONAR*****************************

    public int obtenerUltimoIdVenta(){
        SQLiteDatabase db = getWritableDatabase();
        int idVenta = 0;
        Cursor cursor = db.rawQuery("SELECT  MAX(IdVenta) FROM "+VENTA,null);
        if (cursor.moveToFirst())
            idVenta = cursor.getInt(0);
        return idVenta;
    }


    public int obtenerCantidadVentasHoy(){
        SQLiteDatabase db = getWritableDatabase();
        int cantidadVentas = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+VENTA+" WHERE Cancelada = 0", null);
        if (cursor.moveToFirst()){
            cantidadVentas = cursor.getInt(0);
        }
        return cantidadVentas;
    }


    public double obtenerMontoVentasHoy(){
        SQLiteDatabase db = getWritableDatabase();
        double montoVentas = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(Total) FROM "+VENTA+" WHERE Cancelada = 0", null);
        if (cursor.moveToFirst()){
            montoVentas = cursor.getInt(0);
        }
        return montoVentas;
    }

    public cliente obtenerCliente(int idCliente){
        SQLiteDatabase db = getWritableDatabase();
        cliente cliente = new cliente();
        Cursor cursor = db.rawQuery("SELECT * FROM "+CLIENTE+" WHERE IdCliente = "+ idCliente, null);
        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                cliente.setIdCliente(cursor.getInt(0));
                cliente.setIdListaPrecios(cursor.getInt(1));
                cliente.setNombre(cursor.getString(2));
                cliente.setCalle(cursor.getString(3));
                cliente.setNumero(cursor.getString(4));
                cliente.setLatitud(cursor.getString(5));
                cliente.setLongitud(cursor.getString(6));
            }
        }
        return cliente;
    }

    public ArrayList<cliente> obtenerClientesCercanos(Location lastLocation){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<cliente> aClientes = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+CLIENTE, null);
        if (cursor.moveToFirst()){
            do{
                if (obtenerDistancia(cursor.getString(5), cursor.getString(6), lastLocation) < 3000){
                    cliente cliente = new cliente();
                    cliente.setIdCliente(cursor.getInt(0));
                    cliente.setIdListaPrecios(cursor.getInt(1));
                    cliente.setNombre(cursor.getString(2));
                    cliente.setCalle(cursor.getString(3));
                    cliente.setNumero(cursor.getString(4));
                    cliente.setLatitud(cursor.getString(5));
                    cliente.setLongitud(cursor.getString(6));
                    aClientes.add(cliente);
                }

            }while (cursor.moveToNext());
        }
        return aClientes;
    }

    public ArrayList<producto> obtenerProductos(int idListaPrecio){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<producto> aProducto = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+PRODUCTO, null);
        if (cursor.moveToFirst()){
            do{
                producto producto = new producto();
                producto.setIdProducto(cursor.getInt(0));
                producto.setNombre(cursor.getString(1));
                producto.setUnidadMedida(cursor.getString(2));
                producto.setActivo(cursor.getString(3));
                aProducto.add(producto);
            }while (cursor.moveToNext());
            for(producto producto : aProducto) {
                cursor = db.rawQuery("SELECT Precio FROM " + PRODUCTOLISTA + " WHERE IdProducto = " + producto.getIdProducto() + " AND IdListaPrecio = " + idListaPrecio, null);
                if(cursor.moveToFirst()){
                    producto.setPrecio(cursor.getDouble(0));
                }
            }
        }
        return aProducto;
    }

    public void verTablaVentasDetalle(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+VENTADETALLE,null);
        if (cursor.moveToFirst()) {
            do {
                Log.d(TAG, "verTablaVentasdetalle: " + cursor.getInt(0)+"-"+cursor.getInt(1));
            } while (cursor.moveToNext());
        }

    }


    //METODOS PROPIOS PARA LAS CONSULTAS

    public double obtenerDistancia(String latitud, String longitud, Location lastLocation){
        Location ubicacionCliente = new Location("uCliente");
        ubicacionCliente.setLatitude(Double.parseDouble(latitud));
        ubicacionCliente.setLongitude(Double.parseDouble(longitud));
        return lastLocation.distanceTo(ubicacionCliente);
    }


    private String obtenerFecha(){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return dateFormat.format(date);
    }
}
