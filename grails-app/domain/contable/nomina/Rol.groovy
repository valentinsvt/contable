package contable.nomina

class Rol {
    Empleado empleado
    MesNomina mes
    String estado  = "N"   /*A-->aprobado    N--> no aprobado*/
    Date registro = new  Date()
    String usuario
    Double totalEgresos=0
    Double totalIngresos=0

    static auditable = [ignore: []]

    /**
     * Define el mapeo entre los campos del dominio y las columnas de la base de datos
     */
    static mapping = {

        table 'ROL'
        cache usage: 'read-write', include: 'non-lazy'
        version false
        id generator: "increment"
        columns {
            id column:'ID'
            empleado column:'EMPLEADO_ID'
            mes column: 'MES'
            estado column: 'ESTADO'
            registro column: 'FECHA_REGISTRO'
            usuario column: 'USUARIO_CREA'
            totalEgresos column: 'TOTAL_EGRESOS'
            totalIngresos column: 'TOTAL_INGRESOS'
        }
    }

    static constraints = {
        usuario(size: 1..16)
        estado(size: 1..1)
    }
}
