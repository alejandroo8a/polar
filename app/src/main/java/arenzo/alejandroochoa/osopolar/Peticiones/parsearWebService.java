package arenzo.alejandroochoa.osopolar.Peticiones;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import arenzo.alejandroochoa.osopolar.ClasesBase.oVenta;
import arenzo.alejandroochoa.osopolar.ClasesBase.producto;
import arenzo.alejandroochoa.osopolar.ClasesBase.ventaDetalle;

/**
 * Created by AlejandroMissael on 08/05/2017.
 */

public class parsearWebService {

    private final static String TAG = "parsearWebService";

    public JSONArray parsearVentas(ArrayList<oVenta> aVentas){
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

    public JSONArray parsearVentaDetalles(ArrayList<ventaDetalle> aVentaDetalles) {
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

    public ArrayList<producto> parsearProductos(JSONArray jaProductos) throws JSONException {
        ArrayList<producto> aProducto = new ArrayList<>();
        for (int i = 0; i<jaProductos.length() ; i++){
            producto producto = new producto();
            JSONObject jProducto = jaProductos.getJSONObject(i);
            producto.setIdProducto(jProducto.getInt("IdProducto"));
            producto.setNombre(jProducto.getString("Nombre"));
            producto.setUnidadMedida(jProducto.getString("UnidadMedida"));
            producto.setActivo(jProducto.getBoolean("Activo"));
            aProducto.add(producto);
        }
        return aProducto;
    }

    //TODO PARSEAR LAS 5 CLASES FALTANTES
}