package arenzo.alejandroochoa.osopolar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by AlejandroMissael on 28/04/2017.
 */

public class busqueda extends DialogFragment {
//TODO FALTA HACER LA BUSQUEDA CON LA LATIDUD Y LA LONGITUD
    private ListView lvClientesCercas;
    ActionBar actionBar;

    public busqueda() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Obtener instancia de la action bar
        actionBar = ((AppCompatActivity) getActivity())
                .getSupportActionBar();

        if (actionBar != null) {
            // Habilitar el Up Button
            actionBar.setDisplayHomeAsUpEnabled(true);
            // Cambiar icono del Up Button
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_busqueda, container, false);
        lvClientesCercas = (ListView) view.findViewById(R.id.lvClientesCercas);
        seleccionarCliente();
        return view;
    }

    private void seleccionarCliente(){
        lvClientesCercas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Se selecciono el elemento "+lvClientesCercas.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_full_screen_dialog, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                actionBar.setDisplayHomeAsUpEnabled(false);
                this.dismiss();
                break;
            case R.id.action_save:
                // procesarGuardar()
                Intent intent = new Intent(getContext(), venta.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
