package arenzo.alejandroochoa.osopolar.ClasesBase;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by AlejandroMissael on 27/04/2017.
 */

public class cliente implements Serializable {

    private Integer idCliente;
    private String nombre;
    private String calle;
    private String numero;
    private String latitud;
    private String longitud;
    private Integer idListaPrecios;
    private Integer credito;

    public Integer getCredito() {
        return credito;
    }

    public void setCredito(Integer credito) {
        this.credito = credito;
    }

    public cliente() {
    }

    public cliente(Integer idCliente, String nombre) {
        this.idCliente = idCliente;
        this.nombre = nombre;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdListaPrecios() {
        return idListaPrecios;
    }

    public void setIdListaPrecios(Integer idListaPrecios) {
        this.idListaPrecios = idListaPrecios;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
