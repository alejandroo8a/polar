package arenzo.alejandroochoa.osopolar.ClasesBase;

/**
 * Created by AlejandroMissael on 27/04/2017.
 */

public class producto {

    private Integer IdProducto;
    private String Nombre;
    private String Activo;
    private String UnidadMedida;

    public producto(){

    }

    public Integer getIdProducto() {
        return IdProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.IdProducto = idProducto;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;
    }

    public String getActivo() {
        return Activo;
    }

    public void setActivo(String activo) {
        this.Activo = activo;
    }

    public String getUnidadMedida() {
        return UnidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.UnidadMedida = unidadMedida;
    }
}
