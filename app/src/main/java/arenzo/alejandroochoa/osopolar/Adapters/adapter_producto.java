package arenzo.alejandroochoa.osopolar.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import arenzo.alejandroochoa.osopolar.ClasesBase.producto;
import arenzo.alejandroochoa.osopolar.R;

/**
 * Created by AlejandroMissael on 27/04/2017.
 */

public class adapter_producto extends ArrayAdapter<producto> {
    private static String TAG="adapter_producto";

    public adapter_producto(Context context, List<producto> resource) {
        super(context, 0,resource);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        producto prenda = getItem(position);
        if (null == convertView) {
                //Si no existe, entonces inflarlo
                convertView = inflater.inflate(R.layout.item_producto, parent, false);
                holder = new ViewHolder();
                holder.txtProductoItem = (TextView) convertView.findViewById(R.id.txtProductoItem);
                holder.txtCantidadItem = (TextView) convertView.findViewById(R.id.txtCantidadItem);
                holder.txtSubtotalItem = (TextView) convertView.findViewById(R.id.txtSubtotalItem);
                convertView.setTag(holder);
        } else {
                holder = (ViewHolder) convertView.getTag();
        }
        // Setup.
        holder.txtProductoItem.setText(prenda.getNombre());
        //holder.txtCantidadItem.setText(String.valueOf(prenda.getCantidad()));
        //holder.txtSubtotalItem.setText(String.valueOf(prenda.getPrecio()));

        return convertView;
        }



    static class ViewHolder {
        TextView txtProductoItem;
        TextView txtCantidadItem;
        TextView txtSubtotalItem;
    }

}
