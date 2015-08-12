package contable.core

import org.springframework.dao.DataIntegrityViolationException
import contable.seguridad.Shield


/**
 * Controlador que muestra las pantallas de manejo de Cliente
 */
class ClienteController extends Shield {

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
            def c = Cliente.createCriteria()
            list = c.list(params) {
                or {
                    /* TODO: cambiar aqui segun sea necesario */

                    ilike("aceptaMora", "%" + params.search + "%")
                    ilike("auxiliar", "%" + params.search + "%")
                    ilike("auxiliar2", "%" + params.search + "%")
                    ilike("banco", "%" + params.search + "%")
                    ilike("ciudad", "%" + params.search + "%")
                    ilike("cliente", "%" + params.search + "%")
                    ilike("codigo", "%" + params.search + "%")
                    ilike("codigoLista", "%" + params.search + "%")
                    ilike("contacto", "%" + params.search + "%")
                    ilike("contibuyenteEspecial", "%" + params.search + "%")
                    ilike("cp", "%" + params.search + "%")
                    ilike("cuentaAnticipos", "%" + params.search + "%")
                    ilike("cuentaPorCobrar", "%" + params.search + "%")
                    ilike("cuentaPorPagar", "%" + params.search + "%")
                    ilike("cuentaPrestamos", "%" + params.search + "%")
                    ilike("direccion", "%" + params.search + "%")
                    ilike("email", "%" + params.search + "%")
                    ilike("fax", "%" + params.search + "%")
                    ilike("nombre", "%" + params.search + "%")
                    ilike("numeroCuenta", "%" + params.search + "%")
                    ilike("observacion", "%" + params.search + "%")
                    ilike("proveedor", "%" + params.search + "%")
                    ilike("retieneFuente", "%" + params.search + "%")
                    ilike("ruc", "%" + params.search + "%")
                    ilike("telefono", "%" + params.search + "%")
                    ilike("tipoCuenta", "%" + params.search + "%")
                    ilike("tipoid", "%" + params.search + "%")
                    ilike("usuario", "%" + params.search + "%")
                }
            }
        } else {
            list = Cliente.list(params)
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
        def clienteInstanceList = getList(params, false)
        def clienteInstanceCount = getList(params, true).size()
        return [clienteInstanceList: clienteInstanceList, clienteInstanceCount: clienteInstanceCount]
    }

    /**
     * Acción llamada con ajax que muestra la información de un elemento particular
     */
    def show_ajax() {
        if (params.id) {
            def clienteInstance = Cliente.findByCodigo(params.id)
            if (!clienteInstance) {
                render "ERROR*No se encontró Cliente."
                return
            }
            return [clienteInstance: clienteInstance]
        } else {
            render "ERROR*No se encontró Cliente."
        }
    } //show para cargar con ajax en un dialog

    /**
     * Acción llamada con ajax que muestra un formaulario para crear o modificar un elemento
     */
    def form_ajax() {
        def clienteInstance = new Cliente()
        if (params.id) {
            clienteInstance = Cliente.findByCodigo(params.id)
            if (!clienteInstance) {
                render "ERROR*No se encontró Cliente."
                return
            }
        }
        clienteInstance.properties = params
        return [clienteInstance: clienteInstance]
    } //form para cargar con ajax en un dialog

    /**
     * Acción llamada con ajax que guarda la información de un elemento
     */
    def save_ajax() {
        def clienteInstance = new Cliente()
        if (params.id) {
            clienteInstance = Cliente.findByCodigo(params.id)
            if (!clienteInstance) {
                render "ERROR*No se encontró Cliente."
                return
            }
        }
        clienteInstance.properties = params
        clienteInstance.usuario=session.usuario.login
        clienteInstance.empresa=session.empresa
        if (!clienteInstance.save(flush: true)) {
            render "ERROR*Ha ocurrido un error al guardar Cliente: " + renderErrors(bean: clienteInstance)
            return
        }
        render "SUCCESS*${params.id ? 'Actualización' : 'Creación'} de Cliente exitosa."
        return
    } //save para grabar desde ajax

    /**
     * Acción llamada con ajax que permite eliminar un elemento
     */
    def delete_ajax() {
        if (params.id) {
            def clienteInstance = Cliente.findByCodigo(params.id)
            if (!clienteInstance) {
                render "ERROR*No se encontró Cliente."
                return
            }
            try {
                clienteInstance.delete(flush: true)
                render "SUCCESS*Eliminación de Cliente exitosa."
                return
            } catch (DataIntegrityViolationException e) {
                render "ERROR*Ha ocurrido un error al eliminar Cliente"
                return
            }
        } else {
            render "ERROR*No se encontró Cliente."
            return
        }
    } //delete para eliminar via ajax

    /**
     * Acción llamada con ajax que valida que no se duplique la propiedad codigo
     * @render boolean que indica si se puede o no utilizar el valor recibido
     */
    def validar_unique_codigo_ajax() {
        params.codigo = params.codigo.toString().trim()
        if (params.id) {
            def obj = Cliente.findByCodigo(params.id)
            if (obj.codigo.toLowerCase() == params.codigo.toLowerCase()) {
                render true
                return
            } else {
                render Cliente.countByCodigoIlike(params.codigo) == 0
                return
            }
        } else {
            render Cliente.countByCodigoIlike(params.codigo) == 0
            return
        }
    }

    /**
     * Acción llamada con ajax que valida que no se duplique la propiedad codigoLista
     * @render boolean que indica si se puede o no utilizar el valor recibido
     */
    def validar_unique_codigoLista_ajax() {
        params.codigoLista = params.codigoLista.toString().trim()
        if (params.id) {
            def obj = Cliente.findByCodigo(params.id)
            if (obj.codigoLista.toLowerCase() == params.codigoLista.toLowerCase()) {
                render true
                return
            } else {
                render Cliente.countByCodigoListaIlike(params.codigoLista) == 0
                return
            }
        } else {
            render Cliente.countByCodigoListaIlike(params.codigoLista) == 0
            return
        }
    }

    /**
     * Acción llamada con ajax que valida que no se duplique la propiedad codigoVendedor
     * @render boolean que indica si se puede o no utilizar el valor recibido
     */
    def validar_unique_codigoVendedor_ajax() {
        params.codigoVendedor = params.codigoVendedor.toString().trim()
        if (params.id) {
            def obj = Cliente.findByCodigo(params.id)
            if (obj.codigoVendedor.toLowerCase() == params.codigoVendedor.toLowerCase()) {
                render true
                return
            } else {
                render Cliente.countByCodigoVendedorIlike(params.codigoVendedor) == 0
                return
            }
        } else {
            render Cliente.countByCodigoVendedorIlike(params.codigoVendedor) == 0
            return
        }
    }

    /**
     * Acción llamada con ajax que valida que no se duplique la propiedad email
     * @render boolean que indica si se puede o no utilizar el valor recibido
     */
    def validar_unique_email_ajax() {
        params.email = params.email.toString().trim()
        if (params.id) {
            def obj = Cliente.findByCodigo(params.id)
            if (obj.email.toLowerCase() == params.email.toLowerCase()) {
                render true
                return
            } else {
                render Cliente.countByEmailIlike(params.email) == 0
                return
            }
        } else {
            render Cliente.countByEmailIlike(params.email) == 0
            return
        }
    }

}
