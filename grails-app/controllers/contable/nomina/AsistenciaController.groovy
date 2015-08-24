package contable.nomina

import org.springframework.dao.DataIntegrityViolationException
import contable.seguridad.Shield


/**
 * Controlador que muestra las pantallas de manejo de Asistencia
 */
class AsistenciaController extends Shield {

    static allowedMethods = [save_ajax: "POST", delete_ajax: "POST"]

    /**
     * Acción que redirecciona a la lista (acción "list")
     */
    def index() {
        redirect(action: "list", params: params)
    }

    /**
     * Función que saca la lista de elementos según los parámetros recibidos
     * @param params objeto que contiene los parámetros para la búsqueda:: max: el máximo de respuestas, offset: índice del primer elemento (para la paginación), search: para efectuar búsquedas
     * @param all boolean que indica si saca todos los resultados, ignorando el parámetro max (true) o no (false)
     * @return lista de los elementos encontrados
     */
    def getList(params, all) {
        params = params.clone()
        params.max = params.max ? Math.min(params.max.toInteger(), 100) : 10
        params.offset = params.offset ?: 0
        if (all) {
            params.remove("max")
            params.remove("offset")
        }
        def list
        if (params.search) {
            def c = Asistencia.createCriteria()
            list = c.list(params) {
                or {
                    /* TODO: cambiar aqui segun sea necesario */

                }
            }
        } else {
            list = Asistencia.list(params)
        }
        if (!all && params.offset.toInteger() > 0 && list.size() == 0) {
            params.offset = params.offset.toInteger() - 1
            list = getList(params, all)
        }
        return list
    }

    /**
     * Acción que muestra la lista de elementos
     */
    def list() {
        def asistenciaInstanceList = getList(params, false)
        def asistenciaInstanceCount = getList(params, true).size()
        return [asistenciaInstanceList: asistenciaInstanceList, asistenciaInstanceCount: asistenciaInstanceCount]
    }

    /**
     * Acción llamada con ajax que muestra la información de un elemento particular
     */
    def show_ajax() {
        if (params.id) {
            def asistenciaInstance = Asistencia.get(params.id)
            if (!asistenciaInstance) {
                render "ERROR*No se encontró Asistencia."
                return
            }
            return [asistenciaInstance: asistenciaInstance]
        } else {
            render "ERROR*No se encontró Asistencia."
        }
    } //show para cargar con ajax en un dialog

    /**
     * Acción llamada con ajax que muestra un formaulario para crear o modificar un elemento
     */
    def form_ajax() {
        def asistenciaInstance = new Asistencia()
        if (params.id) {
            asistenciaInstance = Asistencia.get(params.id)
            if (!asistenciaInstance) {
                render "ERROR*No se encontró Asistencia."
                return
            }
        }
        asistenciaInstance.properties = params
        return [asistenciaInstance: asistenciaInstance]
    } //form para cargar con ajax en un dialog

    /**
     * Acción llamada con ajax que guarda la información de un elemento
     */
    def save_ajax() {
        def asistenciaInstance = new Asistencia()
        if (params.id) {
            asistenciaInstance = Asistencia.get(params.id)
            if (!asistenciaInstance) {
                render "ERROR*No se encontró Asistencia."
                return
            }
        }
        asistenciaInstance.properties = params
        if (!asistenciaInstance.save(flush: true)) {
            render "ERROR*Ha ocurrido un error al guardar Asistencia: " + renderErrors(bean: asistenciaInstance)
            return
        }
        render "SUCCESS*${params.id ? 'Actualización' : 'Creación'} de Asistencia exitosa."
        return
    } //save para grabar desde ajax

    /**
     * Acción llamada con ajax que permite eliminar un elemento
     */
    def delete_ajax() {
        if (params.id) {
            def asistenciaInstance = Asistencia.get(params.id)
            if (!asistenciaInstance) {
                render "ERROR*No se encontró Asistencia."
                return
            }
            try {
                asistenciaInstance.delete(flush: true)
                render "SUCCESS*Eliminación de Asistencia exitosa."
                return
            } catch (DataIntegrityViolationException e) {
                render "ERROR*Ha ocurrido un error al eliminar Asistencia"
                return
            }
        } else {
            render "ERROR*No se encontró Asistencia."
            return
        }
    } //delete para eliminar via ajax

}
