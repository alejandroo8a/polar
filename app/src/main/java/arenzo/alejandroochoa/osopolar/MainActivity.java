package arenzo.alejandroochoa.osopolar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private final String EXISTEIDEQUIPO = "EXISTEIDEQUIPO";
    private final String IDEQUIPO = "IDEQUIPO";

    private SharedPreferences sharedPreferences;

    private Button btnNuevaVenta;
    // PARA AGREGAR LA FUENTE POR CODIGO tv.setTypeface(Fuentes.myFont(this));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        centrarTituloActionBar();
        cargarElementosVista();
        agregarIdEquipo();
        btnNuevaVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarEscaneo();
            }
        });
    }

    private void cargarElementosVista(){
        btnNuevaVenta = (Button)findViewById(R.id.btnNuevaVenta);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void mostrarEscaneo(){
        Intent intent = new Intent(this, escaneo.class);
        startActivity(intent);
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

    private void agregarIdEquipo(){
        Boolean saberSiExiste = sharedPreferences.getBoolean(EXISTEIDEQUIPO, false);
        if (!saberSiExiste){
            alertaAgregarIdEquipo();
        }
    }

    private void alertaAgregarIdEquipo(){
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (getApplicationContext().LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_inicio_id, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ID Equipo")
                .setMessage("Agrega un ID para iniciar sesión")
                .setView(view)
                .setPositiveButton("INICIAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edtID = (EditText)view.findViewById(R.id.edtIdEquipo);
                if (edtID.length() == 4){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(EXISTEIDEQUIPO, true);
                    editor.putInt(IDEQUIPO, Integer.parseInt(edtID.getText().toString()));
                    editor.commit();
                    dialog.dismiss();
                }else{
                    Toast.makeText(MainActivity.this, "Debes de añadir un ID de cuatro digitos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
