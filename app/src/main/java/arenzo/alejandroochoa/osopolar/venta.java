package arenzo.alejandroochoa.osopolar;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import arenzo.alejandroochoa.osopolar.ClasesBase.producto;
import arenzo.alejandroochoa.osopolar.adapters.adapter_producto;

public class venta extends AppCompatActivity {

    private final static String TAG = "venta";
    private final String RESULTADO = "RESULTADO";
    private double total = 0.0;

    private TextView txtNombreCliente, txtSubtotal,txtTotal;
    private EditText edtPrecioUnitario,edtCantidad;
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
        ocultarTeclado();
    }

    private void cargarElementosVista(){
        txtNombreCliente = (TextView)findViewById(R.id.txtNombreCliente);
        txtSubtotal = (TextView)findViewById(R.id.txtSubtotal);
        txtTotal = (TextView)findViewById(R.id.txtTotal);
        edtPrecioUnitario = (EditText)findViewById(R.id.edtPrecioUnitario);
        edtCantidad = (EditText)findViewById(R.id.edtCantidad);
        btnFinalizarVenta = (Button)findViewById(R.id.btnFinalizarVenta);
        btnCancelarVenta = (Button)findViewById(R.id.btnCancelarVenta);
        btnAgregarProducto = (ImageButton)findViewById(R.id.btnAgregarProducto);
        grdProductos = (GridView)findViewById(R.id.grdProductos);
        spProductos = (Spinner)findViewById(R.id.spProductos);
        aProducto = new ArrayList<>();
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
                txtSubtotal.setText("El subtotal es: "+obtenerSubtotal());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtPrecioUnitario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtSubtotal.setText("El subtotal es: "+obtenerSubtotal());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void cargarNombreCliente(){
        txtNombreCliente.setText(getIntent().getStringExtra(RESULTADO));
    }

    private void agregarProducto(){
        producto oProducto = new producto();
        oProducto.setNombre(spProductos.getSelectedItem().toString());
        oProducto.setCantidad(Integer.parseInt(edtCantidad.getText().toString()));
        oProducto.setPrecio(obtenerSubtotal());
        aProducto.add(oProducto);
        adapter_producto = new adapter_producto(getApplicationContext(),aProducto);
        grdProductos.setAdapter(adapter_producto);
        agregarTotal();
        limpiarVista();
    }

    private double obtenerSubtotal(){
        if (edtCantidad.length() != 0 && edtPrecioUnitario.length() != 0)
            return Double.parseDouble(edtPrecioUnitario.getText().toString()) * Double.parseDouble(edtCantidad.getText().toString());
        return 0.0;
    }

    private void agregarTotal(){
        total += obtenerSubtotal();
        txtTotal.setText("Total venta: "+total);
    }

    private void limpiarVista(){
        edtCantidad.setText("");
        edtPrecioUnitario.setText("");
    }

    private void ocultarTeclado(){

    }
}
