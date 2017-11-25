package arenzo.alejandroochoa.osopolar.Peticiones;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import arenzo.alejandroochoa.osopolar.ClasesBase.cliente;
import arenzo.alejandroochoa.osopolar.ClasesBase.listaPrecio;
import arenzo.alejandroochoa.osopolar.ClasesBase.oVenta;
import arenzo.alejandroochoa.osopolar.ClasesBase.producto;
import arenzo.alejandroochoa.osopolar.ClasesBase.productoLista;
import arenzo.alejandroochoa.osopolar.ClasesBase.ventaDetalle;

/**
 * Created by AlejandroMissael on 08/05/2017.
 */

public final class parsearWebService {

    public parsearWebService() {
    }

    private final static String TAG = "parsearWebService";

    public static JSONArray parsearVentas(ArrayList<oVenta> aVentas){
        JSONArray jaVentas=new JSONArray();
        for(oVenta venta : aVentas){
            JSONObject jVenta = new JSONObject();
            try {
                jVenta.put("IdVenta", venta.getIdVenta());
                jVenta.put("Vendedor", venta.getVendedor());
                jVenta.put("Fecha", venta.getFecha());
                jVenta.put("Total", venta.getTotal());
                jVenta.put("Latitud", venta.getLatitud());
                jVenta.put("Longitud", venta.getLongitud());
                jVenta.put("Cancelada", venta.getCancelada());
                jVenta.put("Sincronizado", venta.getSincronizado());
                jaVentas.put(jVenta);
            } catch (JSONException e) {
                Log.e(TAG, "Ocurrio un error en el parseo de las ventas: "+e );
            }
        }
        return jaVentas;
    }

    public static JSONArray parsearVentaDetalles(ArrayList<ventaDetalle> aVentaDetalles) {
        JSONArray jaVentaDetalles=new JSONArray();
        for(ventaDetalle ventaDetalles : aVentaDetalles){
            JSONObject jVentaDetalles = new JSONObject();
            try {
                jVentaDetalles.put("IdVenta", ventaDetalles.getIdVenta());
                jVentaDetalles.put("IdProducto", ventaDetalles.getIdProducto());
                jVentaDetalles.put("Cantidad", ventaDetalles.getCantidad());
                jVentaDetalles.put("PUnitario", ventaDetalles.getpUnitario());
                jVentaDetalles.put("Subtotal", ventaDetalles.getSubtotal());
                jVentaDetalles.put("Sincronizado", ventaDetalles.isSincronizado());
                jaVentaDetalles.put(jVentaDetalles);
            } catch (JSONException e) {
                Log.e(TAG, "Ocurrio un error en el parseo de las ventas: "+e );
            }
        }
        return jaVentaDetalles;
    }

    public static ArrayList<producto> parsearProductos(JSONObject joProductos) throws JSONException {
        ArrayList<producto> aProducto = new ArrayList<>();
        JSONArray jaProductos = joProductos.getJSONArray("response");
        for (int i = 0; i<jaProductos.length() ; i++){
            producto producto = new producto();
            JSONObject jProducto = jaProductos.getJSONObject(i);
            producto.setIdProducto(jProducto.getInt("IdProducto"));
            producto.setNombre(jProducto.getString("Nombre"));
            producto.setUnidadMedida(jProducto.getString("UnidadMedida"));
            producto.setActivo(jProducto.getString("Activo"));
            aProducto.add(producto);
        }
        return aProducto;
    }

    public static ArrayList<listaPrecio> parsearListaPrecio (JSONObject joListaPrecio) throws JSONException{
        ArrayList<listaPrecio> aListaPrecio = new ArrayList<>();
        JSONArray jaListaPrecio = joListaPrecio.getJSONArray("response");
        for (int i = 0; i<jaListaPrecio.length() ; i++){
            listaPrecio lista = new listaPrecio();
            JSONObject jListaPrecio = jaListaPrecio.getJSONObject(i);
            lista.setidListaPrecio(jListaPrecio.getInt("IdListaPrecio"));
            lista.setDescripcion(jListaPrecio.getString("Descripcion"));
            aListaPrecio.add(lista);
        }
        return aListaPrecio;
    }

    public static ArrayList<productoLista> parsearProductoLista (JSONObject joProductoLista) throws JSONException{
        ArrayList<productoLista> aProductoLista = new ArrayList<>();
        JSONArray jaProductoLista = joProductoLista.getJSONArray("response");
        for (int i = 0; i<jaProductoLista.length() ; i++){
            productoLista lista = new productoLista();
            JSONObject jListaProducto = jaProductoLista.getJSONObject(i);
            lista.setIdProducto(jListaProducto.getInt("IdProducto"));
            lista.setIdListaPrecio(jListaProducto.getInt("IdListaPrecio"));
            lista.setPrecio(jListaProducto.getDouble("Precio"));
            aProductoLista.add(lista);
        }
        return aProductoLista;
    }

    public static ArrayList<cliente> parsearClientes (JSONObject joClientes) throws JSONException{
        ArrayList<cliente> aClientes = new ArrayList<>();
        JSONArray jaClientes = joClientes.getJSONArray("response");
        for(int i = 0 ; i<jaClientes.length() ; i++){
            cliente cliente = new cliente();
            JSONObject jCliente = jaClientes.getJSONObject(i);
            cliente.setIdCliente(jCliente.getInt("IdCliente"));
            cliente.setNombre(jCliente.getString("Nombre"));
            cliente.setCalle(jCliente.getString("Calle"));
            cliente.setNumero(jCliente.getString("Numero"));
            cliente.setLatitud(jCliente.getString("Lat"));
            cliente.setLongitud(jCliente.getString("Long"));
            cliente.setIdListaPrecios(jCliente.getInt("IdListaPrecio"));
            aClientes.add(cliente);
        }
        return aClientes;
    }
}