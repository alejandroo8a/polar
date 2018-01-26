package arenzo.alejandroochoa.osopolar.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.zxing.integration.android.IntentIntegrator;

import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import arenzo.alejandroochoa.osopolar.ClasesBase.cliente;
import arenzo.alejandroochoa.osopolar.R;
import arenzo.alejandroochoa.osopolar.Fragments.busqueda;
import arenzo.alejandroochoa.osopolar.SQlite.baseDatos;


public class escaneo extends AppCompatActivity {

    private final static String TAG = "escaneo";
    private final String RESULTADO = "RESULTADO";

    private ImageButton btnBuscarCliente, btnEscaner, btnNuevoCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_escaneo);
        cargarElementosVista();
        btnEscaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarLector();
            }
        });
        btnNuevoCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarVentaClienteNuevo();
            }
        });
        btnBuscarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verBuscarCliente();
            }
        });
    }

    private void cargarElementosVista(){

        btnEscaner = (ImageButton)findViewById(R.id.btnEscaner);
        btnNuevoCliente = (ImageButton)findViewById(R.id.btnNuevoCliente);
        btnBuscarCliente = (ImageButton)findViewById(R.id.btnBuscarCliente);
        centrarTituloActionBar();
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

    private void mostrarLector(){
        final Activity activity = this;
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                Intent intent = new Intent(this, venta.class);
                baseDatos bd = new baseDatos(getApplicationContext());
                cliente cliente = bd.obtenerClientePorId(Integer.parseInt(result.getContents()));
                intent.putExtra(RESULTADO, cliente);
                startActivity(intent);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void mostrarVentaClienteNuevo(){
        Intent intent = new Intent(this, venta.class);
        cliente cliente = new cliente(0, "CLIENTE NUEVO");
        intent.putExtra(RESULTADO, cliente);
        startActivity(intent);
    }

    private void verBuscarCliente(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        busqueda newFragment = new busqueda();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment, "busqueda")
                .commit();

    }
}
