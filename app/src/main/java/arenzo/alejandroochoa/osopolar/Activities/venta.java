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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import arenzo.alejandroochoa.osopolar.ClasesBase.cliente;
import arenzo.alejandroochoa.osopolar.ClasesBase.oVenta;
import arenzo.alejandroochoa.osopolar.ClasesBase.producto;
import arenzo.alejandroochoa.osopolar.Adapters.adapter_producto;
import arenzo.alejandroochoa.osopolar.ClasesBase.ventaDetalle;
import arenzo.alejandroochoa.osopolar.R;
import arenzo.alejandroochoa.osopolar.SQlite.baseDatos;

public class venta extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //GUARDAR EL PRODUCTO REAL

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
    private ArrayList<producto> aProductoVender;
    private ArrayList<ventaDetalle> aVentaDetalle;
    private cliente cliente;
    private baseDatos bd;
    private int posicionProducto = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);
        cargarElementosVista();
        centrarTituloActionBar();
        cargarNombreCliente();
        cargarProductos();
        eventosVista();
        ocultarTeclado(edtCantidad);
    }

    private void cargarElementosVista(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        aProductoVender = new ArrayList<>();
        aVentaDetalle = new ArrayList<>();
        bd = new baseDatos(getApplicationContext());
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
                agregarProducto(aProductoVender.get(posicionProducto).getIdProducto());
            }
        });

        edtCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                txtSubtotal.setText("$ " + String.valueOf(obtenerSubtotal()));
            }
        });


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
        spProductos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtPrecioUnitario.setText("$ " + aProductoVender.get(position).getPrecio().toString());
                posicionProducto = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void cargarNombreCliente(){
        cliente = (cliente) getIntent().getSerializableExtra(RESULTADO);
        txtNombreCliente.setText(cliente.getNombre());
    }

    private void cargarProductos(){
        if(cliente.getIdListaPrecios() != null)
            aProductoVender = bd.obtenerProductos(cliente.getIdListaPrecios());
        else
            aProductoVender = bd.obtenerProductos(1);
        ArrayList<String> aNombreProducto = new ArrayList<>();
        for(producto producto : aProductoVender){
            aNombreProducto.add(producto.getNombre());
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, aNombreProducto);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProductos.setAdapter(spinnerArrayAdapter);
    }

    private void agregarProducto(int idProducto){
        if (edtCantidad.length() != 0 && txtPrecioUnitario.length() != 0) {
            if (aProducto.size() > 0) {
                if (verificarProducto(idProducto)) {
                    int posicion = posicionProducto(idProducto);
                    int cantidad = aProducto.get(posicion).getCantidad() + Integer.parseInt(edtCantidad.getText().toString());
                    double precio = aProducto.get(posicion).getPrecio() + Double.parseDouble(txtSubtotal.getText().toString().split(" ")[1]);
                    aProducto.get(posicion).setCantidad(cantidad);
                    aProducto.get(posicion).setPrecio(precio);
                    adapter_producto = new adapter_producto(getApplicationContext(), aProducto);
                    grdProductos.setAdapter(adapter_producto);
                    agregarTotal();
                    limpiarVista();
                    txtPrecioUnitario.requestFocus();
                    edtCantidad.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    return;
                }
            }
            producto oProducto = new producto();
            oProducto.setIdProducto(idProducto);
            oProducto.setNombre(spProductos.getSelectedItem().toString());
            oProducto.setCantidad(Integer.parseInt(edtCantidad.getText().toString()));
            oProducto.setPrecio(obtenerSubtotal());
            aProducto.add(oProducto);
            adapter_producto = new adapter_producto(getApplicationContext(), aProducto);
            grdProductos.setAdapter(adapter_producto);
            agregarTotal();
            limpiarVista();
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
        if (edtCantidad.length() != 0 && txtPrecioUnitario.length() != 0) {
            String[] aPrecio = txtPrecioUnitario.getText().toString().split(" ");
            double precioUnitario = Double.parseDouble(aPrecio[1]);
            double cantidad = Double.parseDouble(edtCantidad.getText().toString());
            return precioUnitario * cantidad;
        }
        return 0.0;
    }

    private void agregarTotal(){
        total += obtenerSubtotal();
        txtTotal.setText("$ " + String.valueOf(total));
    }

    private void limpiarVista(){
        edtCantidad.setText("");
        txtPrecioUnitario.setText("$ " + aProductoVender.get(posicionProducto).getPrecio().toString());
    }

    private void ocultarTeclado(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void finalizarVenta(boolean tipoGuardado){
        //TIPO GUARDADO AVISA A USUARIO D ELA VENTA SINO SOLO LO CIERRA
        if (obtenerLocalizacion()){
            oVenta venta = null;
            if (tipoGuardado){
                venta = crearVenta(false);
            }else{
                venta = crearVenta(true);
            }
            if (bd.insertarVenta(venta, getApplicationContext())){
                if (aProducto.size() > 0) {
                    int idVenta = bd.obtenerUltimoIdVenta();
                    ArrayList<ventaDetalle> aVentaDetalle = crearVentaDetalle(idVenta);
                    if (bd.insertarVentaDetalle(aVentaDetalle, getApplicationContext())) {
                        if (tipoGuardado) {
                            dialogFinalizarVenta();
                            return;
                        }
                        finish();
                    }
                }else{
                    finish();
                }
            }
        }
    }

    private oVenta crearVenta(boolean cancelada){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        oVenta venta = new oVenta();
        venta.setVendedor(sharedPreferences.getString(IDEQUIPO,"1"));
        venta.setIdCliente(cliente.getIdCliente());
        String[] aTotal = txtTotal.getText().toString().split(" ");
        double total = Double.parseDouble(aTotal[1]);
        venta.setTotal(total);
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
            ventaDetalle.setCantidad(produc.getCantidad());
            ventaDetalle.setpUnitario(produc.getPrecio());
            ventaDetalle.setSubtotal(produc.getCantidad() * produc.getPrecio());
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
