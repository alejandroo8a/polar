package arenzo.alejandroochoa.osopolar.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import arenzo.alejandroochoa.osopolar.ClasesBase.producto;
import arenzo.alejandroochoa.osopolar.R;

public class adapter_producto_detalle extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<producto> productos;

    public adapter_producto_detalle(Context context, int layout, ArrayList<producto> productos){
        this.context=context;
        this.layout=layout;
        this.productos=productos;
    }

    @Override
    public int getCount() {
        return this.productos.size();
    }

    @Override
    public Object getItem(int position) {
        return this.productos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // View Holder Pattern
        ViewHolder holder;

        if (convertView == null) {
            // Inflamos la vista que nos ha llegado con nuestro layout personalizado
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(this.layout, null);

            holder = new ViewHolder();
            // Referenciamos el elemento a modificar y lo rellenamos
            holder.txtNombre = (TextView) convertView.findViewById(R.id.txtNombre);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Nos traemos el valor actual dependiente de la posición

        //currentName = (String) getItem(position);

        // Referenciamos el elemento a modificar y lo rellenamos
        holder.txtNombre.setText(productos.get(position).getNombre());

        // devolvemos la vista inflada y modificada con nuestros datos
        return convertView;
    }

    static class ViewHolder {
        private TextView txtNombre;
    }
}
