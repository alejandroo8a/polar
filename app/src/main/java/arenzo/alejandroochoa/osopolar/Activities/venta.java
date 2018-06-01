package arenzo.alejandroochoa.osopolar.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.regex.Pattern;

import arenzo.alejandroochoa.osopolar.ClasesBase.DecimalDigitsInputFilter;
import arenzo.alejandroochoa.osopolar.ClasesBase.cliente;
import arenzo.alejandroochoa.osopolar.ClasesBase.oVenta;
import arenzo.alejandroochoa.osopolar.ClasesBase.producto;
import arenzo.alejandroochoa.osopolar.Adapters.adapter_producto;
import arenzo.alejandroochoa.osopolar.ClasesBase.ventaDetalle;
import arenzo.alejandroochoa.osopolar.R;
import arenzo.alejandroochoa.osopolar.SQlite.baseDatos;

public class venta extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final static String TAG = "venta";

    private final String RESULTADO = "RESULTADO";
    private  String precio_venta;
    private final String IDEQUIPO = "IDEQUIPO";
    private int credito_venta;
    private double total = 0.0;
    private GoogleApiClient mgoogleApiClient;
    private Location mLastLocation;
    private LocationManager locationManager;
    private Switch credito;

    private TextView txtNombreCliente, txtSubtotal,txtTotal, txtPrecioUnitario,txtCredito,txtNuevo;
    private EditText edtCantidad,edtprecioNuevo;
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
    private  Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//checar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta);
        cargarElementosVista();
        centrarTituloActionBar();


        cargarNombreCliente();
        cargarProductos();
        eventosVista();
       edtCantidad.requestFocus();

        if (edtCantidad.hasFocus()){
            mostrarTeclado(edtCantidad);
            //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        //mostrarTeclado(edtCantidad);


    }


    private void cargarElementosVista(){//cambio

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        ab.show();
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtNombreCliente = (TextView)findViewById(R.id.txtNombreCliente);
        txtCredito=(TextView)findViewById(R.id.textCreditover);
        txtSubtotal = (TextView)findViewById(R.id.txtSubtotal);
        txtTotal = (TextView)findViewById(R.id.txtTotal);
        txtPrecioUnitario = (TextView) findViewById(R.id.txtPrecioUnitario);
        edtCantidad = (EditText)findViewById(R.id.edtCantidad);
        edtprecioNuevo=(EditText)findViewById(R.id.edtprecionego);
        credito=(Switch)findViewById(R.id.swCreditos);
        btnFinalizarVenta = (Button)findViewById(R.id.btnFinalizarVenta);
        btnCancelarVenta = (Button)findViewById(R.id.btnCancelarVenta);
        btnAgregarProducto = (ImageButton)findViewById(R.id.btnAgregarProducto);
        grdProductos = (GridView)findViewById(R.id.grdProductos);
        spProductos = (Spinner)findViewById(R.id.spProductos);
        txtNuevo=(TextView)findViewById(R.id.txtprecionego);
        txtNuevo.setText("Precio ");
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
       edtCantidad.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(1)});
        //limpiarVista();

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

        edtCantidad.addTextChangedListener(new TextWatcher() {//cambio
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //mostrarTeclado(edtCantidad);
               //
                // edtCantidad.clearFocus();
                edtCantidad.requestFocus();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //mostrarTeclado(edtCantidad);
                edtCantidad.requestFocus();

            }

            @Override
            public void afterTextChanged(Editable s) {
                DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
                simbolos.setDecimalSeparator('.');
                DecimalFormat formatea = new DecimalFormat("###,###.##",simbolos);
                String sumando=formatea.format(obtenerSubtotal());
                txtSubtotal.setText("$ " + sumando);
                edtprecioNuevo.setHint("$");
                ocultarTeclado(edtCantidad);
                mostrarTeclado(edtCantidad);

            }
        });

        edtprecioNuevo.addTextChangedListener(new TextWatcher() {//cambio
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                // ocultarTeclado( edtprecioNuevo);


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
        spProductos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//cambio
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtPrecioUnitario.setText("$ " + aProductoVender.get(position).getPrecio().toString());
                edtprecioNuevo.setText(aProductoVender.get(position).getPrecio().toString());
                precio_venta=edtprecioNuevo.getText().toString();
                posicionProducto = position;
                edtCantidad.requestFocus();

                edtCantidad.setInputType(InputType.TYPE_CLASS_PHONE);
                //edtCantidad.setFocusable(false);

                mostrarTeclado(edtCantidad);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void cargarNombreCliente(){
        cliente = (cliente) getIntent().getSerializableExtra(RESULTADO);
        txtNombreCliente.setText(cliente.getNombre());//aqui cambiar

        if (cliente.getCredito()>=1){
            credito.setChecked(true);
            credito_venta=1;
        }

        if (credito.isChecked() || cliente.getCredito()>=1){
            credito.setVisibility(View.INVISIBLE);
            txtCredito.setVisibility(View.VISIBLE);
            txtCredito.setText("Activado");
            credito_venta=1;
        }




        // Toast.makeText(this,cliente.getCredito(),Toast.LENGTH_SHORT).show();

    }


    public  int poner_credito(){
        if (credito.isChecked() || cliente.getCredito()==1) {
            return 1;
        }

        else {
            return 0;
        }

    }

    private void cargarProductos(){//cambio metodo

        if(cliente.getIdListaPrecios() != null)
            aProductoVender = bd.obtenerProductos(cliente.getIdListaPrecios());
        else
            Log.d("ulto_lista",String.valueOf(bd.obtenerUltimoIdPrecio()));
            aProductoVender = bd.obtenerProductos(bd.obtenerUltimoIdPrecio());
        ArrayList<String> aNombreProducto = new ArrayList<>();
        for(producto producto : aProductoVender){

            if (producto.getPrecio()!=null){
                aNombreProducto.add(producto.getNombre());

            }

        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, aNombreProducto);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProductos.setAdapter(spinnerArrayAdapter);

    }

    private void agregarProducto(int idProducto){//cambio
        if (edtCantidad.length() != 0 && txtPrecioUnitario.length() != 0) {
            if (aProducto.size() > 0) {
                if (verificarProducto(idProducto)) {
                    int posicion = posicionProducto(idProducto);
                    double precio=0.0;
                    Float cantidad = aProducto.get(posicion).getCantidad() + Float.parseFloat(edtCantidad.getText().toString());


                    precio = aProducto.get(posicion).getPrecio() + Double.parseDouble(txtSubtotal.getText().toString().split(" ")[1]);




                    aProducto.get(posicion).setCantidad(cantidad);
                    aProducto.get(posicion).setPrecio(precio);
                    adapter_producto = new adapter_producto(getApplicationContext(), aProducto);
                    grdProductos.setAdapter(adapter_producto);
                    agregarTotal();
                    limpiarVista();
                   // edtCantidad.requestFocus();

                    edtCantidad.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    return;

                }
            }
            producto oProducto = new producto();
            oProducto.setIdProducto(idProducto);
            oProducto.setNombre(spProductos.getSelectedItem().toString());
            oProducto.setCantidad(Float.parseFloat(edtCantidad.getText().toString()));
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
            double precioUnitario;
            if(edtprecioNuevo.length()>0){

                if (edtprecioNuevo.getText().toString().equals("0")){
                    precioUnitario = Double.parseDouble(aPrecio[1]);
                    edtprecioNuevo.setHint("$");
                    edtprecioNuevo.setText("");
                    Toast.makeText(getBaseContext(),"Ingrese Precio Mayor a 0",Toast.LENGTH_SHORT).show();

                }
                else{
                    edtprecioNuevo.setHint("$");
                    precioUnitario = Double.parseDouble(edtprecioNuevo.getText().toString());


                }

            }
            else{
                precioUnitario = Double.parseDouble(aPrecio[1]);
                edtprecioNuevo.setHint("$");
                edtprecioNuevo.setText("");

            }

            double cantidad = Double.parseDouble(edtCantidad.getText().toString());
            return precioUnitario * cantidad;
        }
        return 0.00;
    }

    private void agregarTotal(){

        total += obtenerSubtotal();
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setDecimalSeparator('.');
        DecimalFormat formatea = new DecimalFormat("###,###.##");

        String sumando=formatea.format(total);
        txtTotal.setText("$ " + sumando);
    }



    private void limpiarVista(){//cambio
        edtCantidad.setText("");
        edtprecioNuevo.setHint("$");
        // edtprecioNuevo.setText("");
        edtprecioNuevo.setText(precio_venta);
        txtPrecioUnitario.setText("$ " + aProductoVender.get(posicionProducto).getPrecio().toString());
        //edtCantidad.requestFocus();
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ocultarTeclado(edtCantidad);
        edtCantidad.clearFocus();


        edtCantidad.setInputType(InputType.TYPE_NULL);

    }

    private void ocultarTeclado(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void mostrarTeclado(View v){
        //edtCantidad.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtCantidad, InputMethodManager.SHOW_IMPLICIT);

    }

    private void finalizarVenta(boolean tipoGuardado){//cambio
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

                        Intent favoritos=new Intent(venta.this,MainActivity.class);
                        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(favoritos);
                        finish();

                    }
                }else{
                    Intent favoritos=new Intent(venta.this,MainActivity.class);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(favoritos);
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
        //double sumando = Double.parseDouble(total);
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
        venta.setCredito(poner_credito());
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
            ventaDetalle.setCredito(poner_credito());
            Log.d("poner",String.valueOf(poner_credito()));

            aVentaDetalle.add(ventaDetalle);
            Log.d("vista_venta",ventaDetalle.getCredito().toString());

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

    private void dialogFinalizarVenta(){//cambio
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Éxito")
                .setMessage("Tu venta fue guardada con éxito.")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent favoritos=new Intent(venta.this,MainActivity.class);
                        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(favoritos);
                        finish();
                        return;

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
            //Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_SHORT).show();
            dialogActivarGps();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {//cambio
        super.onStart();
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mostrarTeclado(edtCantidad);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {//cambio
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // ProjectsActivity is my 'home' activity
                Intent favoritos=new Intent(venta.this,MainActivity.class);
                favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(favoritos);
                finish();

                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void dialogGps(){//cambio
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Advertencia")
                .setMessage("La Ubicaciòn no fue encontrada active su Gps")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent favoritos=new Intent(venta.this,MainActivity.class);
                        favoritos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        favoritos.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        favoritos.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        favoritos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(favoritos);
                        finish();
                        return;

                    }
                })
                .show();
    }
}





