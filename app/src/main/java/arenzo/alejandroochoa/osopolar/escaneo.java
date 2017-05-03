package arenzo.alejandroochoa.osopolar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;


public class escaneo extends AppCompatActivity {
//TODO FALTA HACER LA BUSQUEDA DEL CLIENTE EN LA BD
    private final static String TAG = "escaneo";
    private final String RESULTADO = "RESULTADO";

    private Button btnEscaner, btnNuevoCliente;
    private ImageButton btnBuscarCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                mostrarVenta();
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
        btnEscaner = (Button)findViewById(R.id.btnEscaner);
        btnNuevoCliente = (Button)findViewById(R.id.btnNuevoCliente);
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
                intent.putExtra(RESULTADO, result.getContents());
                startActivity(intent);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void mostrarVenta(){
        Intent intent = new Intent(this, venta.class);
        intent.putExtra(RESULTADO, "ALEJANDRO");
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
