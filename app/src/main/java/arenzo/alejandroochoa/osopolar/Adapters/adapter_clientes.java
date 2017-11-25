package arenzo.alejandroochoa.osopolar.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import arenzo.alejandroochoa.osopolar.ClasesBase.cliente;
import arenzo.alejandroochoa.osopolar.R;

/**
 * Created by AlejandroMissael on 16/11/2017.
 */

public class adapter_clientes extends BaseAdapter {

    private Context context;
    private int layout;
    private List<cliente> aClientes;

    public adapter_clientes(Context context, int layout, List<cliente> aClientes) {
        this.context = context;
        this.layout = layout;
        this.aClientes = aClientes;
    }

    //La cantidad de veces de iterar sobre una coleccion de datos
    @Override
    public int getCount() {
        return aClientes.size();
    }




    //Casi no se suelen usar
    //Obtiene el item de la coleccion
    @Override
    public Object getItem(int position) {
        return null;
    }



    //Obtiene la posicion del elemento
    @Override
    public long getItemId(int position) {
        return 0;
    }



    //Pinta la vista
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //View holder pattern
        viewHolder holder;
        if (convertView == null){
            //Objeto que infla la vista
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(this.layout, null);
            holder = new viewHolder();
            holder.txtNombre = (TextView)convertView.findViewById(R.id.txtNombre);
            convertView.setTag(holder);
        }else{
            //Con esto se obtiene lo que se le puso de tag
            holder = (viewHolder) convertView.getTag();
        }
        //Se rellenan los datos
        String currentName = aClientes.get(position).getNombre();
        holder.txtNombre.setText(currentName);
        return convertView;
    }

    static class viewHolder{
        private TextView txtNombre;
    }
}
