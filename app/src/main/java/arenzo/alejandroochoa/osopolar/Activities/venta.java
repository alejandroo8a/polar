package arenzo.alejandroochoa.osopolar.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import arenzo.alejandroochoa.osopolar.ClasesBase.oVenta;
import arenzo.alejandroochoa.osopolar.ClasesBase.producto;
import arenzo.alejandroochoa.osopolar.Adapters.adapter_producto;
import arenzo.alejandroochoa.osopolar.ClasesBase.ventaDetalle;
import arenzo.alejandroochoa.osopolar.R;
import arenzo.alejandroochoa.osopolar.SQlite.baseDatos;

public class venta extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
//TODO OCULTAR TECLADO - OBTENER EL PRODUCTO REAL - GUARDAR EL PRODUCTO REAL
    private final static String TAG = "venta";
    private final String RESULTADO = "RESULTADO";
    private final String IDEQUIPO = "IDEQUIPO";
    private double total = 0.0;
    private GoogleApiClient mgoogleApiClient;
    private Location mLastLocation;
    private LocationManager locationManager;

    private TextView txtNombreCliente, txtSubtotal,txtTotal, txtPrecioUnitario;
    private EditText edtCantidad;
    private Button btnFinalizarVenta, btnCancelarVenta;
    private ImageButton btnAgregarProducto;
    private GridView grdProductos;
    private Spinner spProductos;

    private adapter_producto adapter_producto;
    private ArrayList<producto> aProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);
        cargarElementosVista();
        centrarTituloActionBar();
        cargarNombreCliente();
        eventosVista();
    }

    private void cargarElementosVista(){
        txtNombreCliente = (TextView)findViewById(R.id.txtNombreCliente);
        txtSubtotal = (TextView)findViewById(R.id.txtSubtotal);
        txtTotal = (TextView)findViewById(R.id.txtTotal);
        txtPrecioUnitario = (TextView) findViewById(R.id.txtPrecioUnitario);
        edtCantidad = (EditText)findViewById(R.id.edtCantidad);
        btnFinalizarVenta = (Button)findViewById(R.id.btnFinalizarVenta);
        btnCancelarVenta = (Button)findViewById(R.id.btnCancelarVenta);
        btnAgregarProducto = (ImageButton)findViewById(R.id.btnAgregarProducto);
        grdProductos = (GridView)findViewById(R.id.grdProductos);
        spProductos = (Spinner)findViewById(R.id.spProductos);
        aProducto = new ArrayList<>();
        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
    }

    private void centrarTituloActionBar() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }

    private void eventosVista(){
        btnAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarProducto();
            }
        });

        edtCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtSubtotal.setText(String.valueOf(obtenerSubtotal()));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
/*
        edtPrecioUnitario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtSubtotal.setText(String.valueOf(obtenerSubtotal()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        btnCancelarVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelarVenta();
            }
        });

        btnFinalizarVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aProducto.size() > 0)
                    finalizarVenta(true);
                else
                    Toast.makeText(venta.this, "Agregue productos a su venta.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarNombreCliente(){
        txtNombreCliente.setText(getIntent().getStringExtra(RESULTADO));
    }

    private void agregarProducto(){
        if (edtCantidad.length() != 0 && txtPrecioUnitario.length() != 0) {
            if (aProducto.size() > 0) {
                if (verificarProducto(1)) {
                    int posicion = posicionProducto(1);
                    //int cantidad = aProducto.get(posicion).getCantidad() + Integer.parseInt(edtCantidad.getText().toString());
                    //double precio = aProducto.get(posicion).getPrecio() + Double.parseDouble(txtSubtotal.getText().toString());
                    //aProducto.get(posicion).setCantidad(cantidad);
                    //aProducto.get(posicion).setPrecio(precio);
                    adapter_producto = new adapter_producto(getApplicationContext(), aProducto);
                    grdProductos.setAdapter(adapter_producto);
                    agregarTotal();
                    limpiarVista();
                    //TODO ELIMINAR ESTA LINEA
                    txtPrecioUnitario.setText("26");
                    txtPrecioUnitario.requestFocus();
                    edtCantidad.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    return;
                }
            }
            producto oProducto = new producto();
            oProducto.setIdProducto(1);
            oProducto.setNombre(spProductos.getSelectedItem().toString());
            //oProducto.setCantidad(Integer.parseInt(edtCantidad.getText().toString()));
            //oProducto.setPrecio(obtenerSubtotal());
            aProducto.add(oProducto);
            adapter_producto = new adapter_producto(getApplicationContext(), aProducto);
            grdProductos.setAdapter(adapter_producto);
            agregarTotal();
            limpiarVista();
            //TODO ELIMINAR ESTA LINEA2
            txtPrecioUnitario.setText("25");
            txtPrecioUnitario.requestFocus();
            edtCantidad.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }else
            Toast.makeText(this, "Agrega precio y cantidad al producto.", Toast.LENGTH_SHORT).show();
    }

    private boolean verificarProducto(int id){
        for (producto pro : aProducto){
            if (pro.getIdProducto() == id){
                return true;
            }
        }
        return false;
    }

    private int posicionProducto(int id){
        int posicion = 0;
        for (producto pro : aProducto){
            if (pro.getIdProducto() == id){
                break;
            }
            posicion++;
        }
        return posicion;
    }

    private double obtenerSubtotal(){
        if (edtCantidad.length() != 0 && txtPrecioUnitario.length() != 0)
            return Double.parseDouble(txtPrecioUnitario.getText().toString()) * Double.parseDouble(edtCantidad.getText().toString());
        return 0.0;
    }

    private void agregarTotal(){
        total += obtenerSubtotal();
        txtTotal.setText(String.valueOf(total));
    }

    private void limpiarVista(){
        edtCantidad.setText("");
        txtPrecioUnitario.setText("");
    }

    private void ocultarTeclado(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    private void finalizarVenta(boolean tipoGuardado){
        //TIPO GUARDADO AVISA A USUARIO D ELA VENTA SINO SOLO LO CIERRA
        if (obtenerLocalizacion()){
            baseDatos db = new baseDatos(getApplicationContext());
            oVenta venta = null;
            if (tipoGuardado){
                venta = crearVenta(false);
            }else{
                venta = crearVenta(true);
            }
            if (db.insertarVenta(venta, getApplicationContext())){
                if (aProducto.size() > 0) {
                    int idVenta = db.obtenerUltimoIdVenta();
                    ArrayList<ventaDetalle> aVentaDetalle = crearVentaDetalle(idVenta);
                    if (db.insertarVentaDetalle(aVentaDetalle, getApplicationContext())) {
                        if (tipoGuardado) {
                            dialogFinalizarVenta();
                            return;
                        }
                        finish();
                        //db.verTablaVentas();
                        db.verTablaVentasDetalle();
                    }
                }else{
                    //db.verTablaVentas();
                    db.verTablaVentasDetalle();
                    finish();
                }
            }
        }
    }

    private oVenta crearVenta(boolean cancelada){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        oVenta venta = new oVenta();
        venta.setVendedor(String.valueOf(sharedPreferences.getInt(IDEQUIPO,1)));
        venta.setTotal(Double.parseDouble(txtTotal.getText().toString()));
        if (mLastLocation != null) {
            venta.setLatitud(String.valueOf(mLastLocation.getLatitude()));
            venta.setLongitud(String.valueOf(mLastLocation.getLongitude()));
        }else{
            venta.setLatitud("0");
            venta.setLongitud("0");
        }
        venta.setCancelada(cancelada);
        venta.setSincronizado(false);
        return venta;
    }

    private ArrayList<ventaDetalle> crearVentaDetalle(int idVenta){
        ArrayList<ventaDetalle> aVentaDetalle = new ArrayList<>();
        for (producto produc : aProducto ){
            ventaDetalle ventaDetalle = new ventaDetalle();
            ventaDetalle.setIdVenta(idVenta);
            ventaDetalle.setIdProducto(produc.getIdProducto());
            //ventaDetalle.setCantidad(produc.getCantidad());
            //ventaDetalle.setpUnitario(produc.getPrecio());
            //ventaDetalle.setSubtotal(produc.getCantidad() * produc.getPrecio());
            ventaDetalle.setSincronizado(false);
            aVentaDetalle.add(ventaDetalle);
        }
        return aVentaDetalle;
    }

    private boolean obtenerLocalizacion(){
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            dialogActivarGps();
            return false;
        }
        return true;
    }

    private void dialogActivarGps(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Atención")
                .setMessage("Necesita activar el GPS para finalizar la venta")
                .setCancelable(false)
                .setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Cancelar",null)
                .show();
    }

    private void dialogCancelarVenta(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Atención")
                .setMessage("¿Está seguro de cancelar la venta?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finalizarVenta(false);
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }

    private void dialogFinalizarVenta(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Éxito")
                .setMessage("Tu venta fue guardada con éxito.")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mgoogleApiClient);
        if (mLastLocation == null){
            Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_SHORT).show();
         }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mgoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mgoogleApiClient.disconnect();
    }

    @Override
    public void onBackPressed() {
        dialogCancelarVenta();
    }
}
