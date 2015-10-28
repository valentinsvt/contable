package contable

import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import org.springframework.beans.SimpleTypeConverter
import org.springframework.context.MessageSourceResolvable
import org.springframework.web.servlet.support.RequestContextUtils

class ElementosTagLib {

    static encodeAsForTags = [tagName: 'raw']

    static namespace = "elm"

    def pdfLink = { attrs ->
        /*
           var url = "${createLink(action: 'avanceReportePDF')}?id=" + elems;
                    location.href = "${createLink(controller:'pdf',action:'pdfLink')}?url=" + url
         */
        def link = attrs.href
        if (!link) {
            link = attrs.url
        }
        def filename = attrs.filename
        if (!filename) {
            filename = "doc.pdf"
        }
        def parms = "?filename=${filename}&url=" + link
        out << g.createLink(controller: 'pdf', action: 'pdfLink') + parms
    }

    /**
     * pone un field segun el standar rapido rapido
     */
    def fieldRapido = { attrs, body ->
        def html = ""
        def claseField = (attrs.claseField ? attrs.claseField : 'col-md-3')
        def claseLabel = (attrs.claseLabel ? attrs.claseLabel : 'col-md-2')
        html += '<div class="form-group keeptogether">'
        html += '<div class="col-sm-12">'
        html += '<span class="grupo">'
        html += '<label class="' + claseLabel + ' control-label">'
        html += attrs.label
        html += '</label>'
        html += '<div class="' + claseField + '">'
        html += body()
        html += '</div>'
        html += '</span>'
        html += '</div>'
        html += '</div>'
        out << html
    }
    def fieldRapidoDoble = { attrs, body ->
        def html = ""
        def claseField1 = (attrs.claseField1 ?: 'col-md-3')
        def claseLabel1 = (attrs.claseLabel1 ?: 'col-md-2')
        def claseField2 = (attrs.claseField2 ?: 'col-md-3')
        def claseLabel2 = (attrs.claseLabel2 ?: 'col-md-2')

        def parts = body().toString().split("<br/>")

        html += '<div class="form-group keeptogether">'

        html += '<div class="col-sm-6">'
        html += '<span class="grupo">'
        html += '<label class="' + claseLabel1 + ' control-label">'
        html += attrs.label1
        html += '</label>'
        html += '<div class="' + claseField1 + '">'
        html += parts[0]
        html += '</div>'
        html += '</span>'
        html += '</div>'

        html += '<div class="col-sm-6">'
        html += '<span class="grupo">'
        html += '<label class="' + claseLabel2 + ' control-label">'
        html += attrs.label2
        html += '</label>'
        html += '<div class="' + claseField2 + '">'
        html += parts[1]
        html += '</div>'
        html += '</span>'
        html += '</div>'

        html += '</div>'
        out << html
    }

    /**
     * pone un contenedor vertical u horizontal
     * Ejemplo de como usar en asignacion/asignacionProyectov2
     * @param tipo es el tipo de container, puede ser vertica u horizontal
     * @param border indica si el container debe tener borde
     * @param style cualqueir estilo para el container
     * @param linea indica si debe llevar o no linea bajo el título
     * @param color es el color del título
     * @param titulo un String con el título del container
     */

    def container = { attrs, body ->
        def tipo = attrs.tipo
        def clase = ""
        def titulo = ""

        if (tipo == "vertical") {
            clase = "vertical-container ${attrs.border ? 'bordered ui-corner-all' : ''}"
            titulo = '<div class="css-vertical-text" style="color:' + attrs.color + '">' + attrs.titulo + '</div>'
            if (!attrs.linea) {
                titulo += '<div class="linea"></div>'
            }
        } else {
            if (attrs.titulo != "") {
                clase = "horizontal-container ${attrs.border ? 'bordered ui-corner-all' : 'not-bordered'}"
                titulo = '<div class="svt-note svt-bg-accent"  style="color:' + attrs.color + '">' + attrs.titulo + '</div>'
            }

        }

        def html = ""
        html += '<div class=" svt-content ' + clase + '" style="' + attrs.style + '">'
        html += titulo
        out << html << body() + "</div>"
    }
/**
 * crea un modal, al modal hay que agregarle el modal-body y el modal-footer
 */
    def modal = { attrs, body ->
        def id = attrs.id
        def html = '<div class="modal fade ' + attrs.clase + ' " id="' + id + '" tabindex="-1" role="dialog" aria-labelledby="myModalLabel' + attrs.id + '" aria-hidden="true" style="' + attrs.style + '">'
        html += '<div class="modal-dialog">\n' +
                '    <div class="modal-content">\n' +
                '      <div class="modal-header">'
        html += ' <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
        html += ' <h4 class="modal-title" id="myModalLabel-' + attrs.id + '">' + attrs.titulo + '</h4></div>'
        out << html << body() << '</div></div></div>'
    }
    /**
     * crea un div para el not found (con el fantasmita)
     */
    def notFound = { attrs ->
        def elem = attrs.elem ?: "elemento"
        def genero = attrs.genero ?: "o"
        def mensaje = attrs.mensaje ?: "No se encontró ${genero == 'o' ? 'el' : 'la'} ${elem} solicitad${genero}."
        def html = ""
        html += '<div class="alert alert-info text-center not-found">'
        html += '<i class="icon-ghost fa-6x text-shadow"></i>'
        html += '<p>' + mensaje + '</p>'
        html += '</div>'
        out << html
    }

    /**
     * crea el div para el flash message
     */
    def message = { attrs, body ->
        def contenido = body()

        if (!contenido) {
            if (attrs.contenido) {
                contenido = attrs.contenido
            }
        }

        if (contenido) {
            def finHtml = "</p></div>"

            def icono = ""
            def clase = attrs.clase ?: ""

            if (attrs.icon) {
                icono = attrs.icon
            } else {
                switch (attrs.tipo?.toLowerCase()) {
                    case "error":
                        icono = "fa fa-times"
                        clase += "alert-danger"
                        break;
                    case "success":
                        icono = "fa fa-check"
                        clase += "alert-success"
                        break;
                    case "notfound":
                        icono = "icon-ghost"
                        clase += "alert-info"
                        break;
                    case "warning":
                        icono = "fa fa-warning"
                        clase += "alert-warning"
                        break;
                    case "info":
                        icono = "fa fa-info-circle"
                        clase += "alert-info"
                        break;
                    case "bug":
                        icono = "fa fa-bug"
                        clase += "alert-warning"
                        break;
                    default:
                        clase += "alert-info"
                }
            }
            def html = "<div class=\"alert alert-dismissable ${clase}\">"
            html += "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>"
            html += "<p style='margin-bottom:15px;'>"
            html += "<i class=\"${icono} fa-2x pull-left iconMargin text-shadow\"></i> "
            out << html << contenido << finHtml
        } else {
            out << ""
        }
    }

    /**
     * marca el texto encontrado en el texto:
     *      se puede usar con o sin body
     *          <elm:textoBusqueda busca="busca">Texto donde buscar "busca"</textoBusqueda>
     *          <elm:textoBusqueda busca="busca" contenido='Texto donde buscar "busca"'/>
     *
     *          params:
     *              busca/search                            el texto a buscar y subrayar si se encuentra
     *              contenido/texto/text/body del tag       el texto donde buscar
     */
    def textoBusqueda = { attrs, body ->
        def texto = body()

        def busca = ""
        if (attrs.search != null) {
            busca = attrs.search
        } else if (attrs.busca != null) {
            busca = attrs.busca
        }

        if (!texto) {
            if (attrs.contenido) {
                texto = attrs.contenido
            }
            if (attrs.text) {
                texto = attrs.text
            }
            if (attrs.texto) {
                texto = attrs.texto
            }
        }

        if (busca && busca != "") {
            try {
                texto = texto.toString().replaceAll("(?iu)" + busca) {
                    "<span class='found'>" + it + "</span>"
                }
            } catch (e) {
                println e
            }
        }

        out << texto
    }

    /**
     * crea un datepicker
     *  attrs:
     *      class           clase
     *      name            name
     *      id              id (opcional, si no existe usa el mismo name)
     *      value           value (groovy Date o String)
     *      format          format para el Date (groovy)
     *      minDate         fecha mínima para el datepicker. cualquier cosa anterior se deshabilita
     *                          ej: +5d para 5 días después de la fecha actual
     *      maxDate         fecha máxima para el datepicker. cualquier cosa posterior se deshabilita
     *      orientation     String. Default: “auto”
     *                               A space-separated string consisting of one or two of “left” or “right”, “top” or “bottom”, and “auto” (may be omitted);
     *                                      for example, “top left”, “bottom” (horizontal orientation will default to “auto”), “right” (vertical orientation will default to “auto”),
     *                                      “auto top”. Allows for fixed placement of the picker popup.
     *                               “orientation” refers to the location of the picker popup’s “anchor”; you can also think of it as the location of the trigger element (input, component, etc)
     *                               relative to the picker.
     *                               “auto” triggers “smart orientation” of the picker.
     *                                  Horizontal orientation will default to “left” and left offset will be tweaked to keep the picker inside the browser viewport;
     *                                  vertical orientation will simply choose “top” or “bottom”, whichever will show more of the picker in the viewport.
     *      autoclose       boolean. default: true cierra automaticamente el datepicker cuando se selecciona una fecha
     *      todayHighlight  boolean. default: true marca la fecha actual
     *      beforeShowDay   funcion. funcion que se ejecuta antes de mostrar el día. se puede utilizar para deshabilitar una fecha en particular
     *                          ej:
     *                               beforeShowDay: function (date){*                                   if (date.getMonth() == (new Date()).getMonth())
     *                                       switch (date.getDate()){*                                           case 4:
     *                                               return {*                                                   tooltip: 'Example tooltip',
     *                                                   classes: 'active'
     *};
     *                                           case 8:
     *                                               return false;
     *                                           case 12:
     *                                               return "green";
     *}*}*                                }
     *      onChangeDate    funcion. funcion q se ejecuta al cambiar una fecha. se manda solo el nombre, sin parentesis, como parametro recibe el datepicker y el objeto
     *                          ej: onChangeDate="miFuncion"
     *                          function miFuncion($elm, e) {*                              console.log($elm); //el objeto jquery del datepicker, el textfield
     *                              console.log(e); //el objeto que pasa el plugin
     *}*      daysOfWeekDisabled  lista de números para deshabilitar ciertos días: 0:domingo, 1:lunes, 2:martes, 3:miercoles, 4:jueves, 5:viernes, 6:sabado
     *      img             imagen del calendario. clase de glyphicons o font awsome
     **/
    def datepicker = { attrs ->
        def name = attrs.name
        def nameInput = name + "_input"
        def nameHiddenDay = name + "_day"
        def nameHiddenMonth = name + "_month"
        def nameHiddenYear = name + "_year"
        def placeHolder = attrs.placeHolder?:''

        def nameHiddenHour = name + "_hour"
        def nameHiddenMin = name + "_minute"

        def id = nameInput
        if (attrs.id) {
            id = attrs.id
        }
        def idInput = id + "_input"
        def idHiddenDay = id + "_day"
        def idHiddenMonth = id + "_month"
        def idHiddenYear = id + "_year"

        def idHiddenHour = id + "_hour"
        def idHiddenMin = id + "_minute"
        
        def readonly = attrs.readonly =="true"?true: false
        def value = attrs.value

        def clase = attrs["class"] ?: ""
        def claseGrupo = ""
        if (clase.contains("input-sm")) {
            claseGrupo = "input-group-sm"
        }

        def showDate = attrs.showDate ?: true
        def showTime = attrs.showTime ?: false

        def defaultFormat = "dd-MM-YYYY"
        if (showTime) {
            defaultFormat += " hh:mm"
        }

        def format = attrs.format ?: defaultFormat
        def formatJS = attrs.formatJS ?: format.replaceAll("d", "D")

        def startDate = attrs.minDate ?: false
        def endDate = attrs.maxDate ?: false
//        println "std "+startDate+" end "+endDate
        def showMin = attrs.showMin ?: true

        def minStep = attrs.minStep ?: 1

        def orientation = attrs.orientation ?: "top auto"

        def todayHighlight = attrs.todayHighlight ?: true

        def beforeShowDay = attrs.beforeShowDay ?: false
        def onChangeDate = attrs.onChangeDate ?: false

        def daysOfWeekDisabled = attrs.daysOfWeekDisabled ?: false

        def img = attrs.img ?: "fa fa-calendar"

        if (value instanceof Date) {
            value = value.format(format)
        }
        if (!value) {
            value = ""
        }

        def valueDay = "", valueMonth = "", valueYear = "", valueHour = "", valueMin = ""
        if (value != "") {
            if (showTime) {
                def parts = value.split(" ")
                def fecha, hora
                fecha = parts[0]
                if (parts.size() == 2) {
                    hora = parts[1]
                } else {
                    hora = new Date().format("HH:mm")
                }
                parts = fecha.split("-")
                valueDay = parts[0]
                valueMonth = parts[1]
                valueYear = parts[2]
                parts = hora.split(":")
                valueHour = parts[0]
                valueMin = parts[1]
            } else {
                def parts = value.split("-")
                valueDay = parts[0]
                valueMonth = parts[1]
                valueYear = parts[2]
            }
        }

        def br = "\n"

        def textfield = "<input type='text' name='${nameInput}' id='${id}' " + (readonly ? "readonly=''" : "") + " value='${value}'" +
                " class='${clase}' data-date-format='${formatJS}' placeholder='${placeHolder}' />"

        def hiddenDay = "<input type='hidden' name='${nameHiddenDay}' id='${idHiddenDay}' value='${valueDay}'/>"
        def hiddenMonth = "<input type='hidden' name='${nameHiddenMonth}' id='${idHiddenMonth}' value='${valueMonth}'/>"
        def hiddenYear = "<input type='hidden' name='${nameHiddenYear}' id='${idHiddenYear}' value='${valueYear}'/>"

        def hiddenHour = "<input type='hidden' name='${nameHiddenHour}' id='${idHiddenHour}' value='${valueHour}'/>"
        def hiddenMin = "<input type='hidden' name='${nameHiddenMin}' id='${idHiddenMin}' value='${valueMin}'/>"

        def hidden = "<input type='hidden'  class='${clase}' name='${name}' id='${name}' value='date.struct'/>"

        def div = ""
        div += hiddenDay + br
        div += hiddenMonth + br
        div += hiddenYear + br
        if (showTime) {
            div += hiddenHour + br
            div += hiddenMin + br
        }
        div += hidden + br
        div += "<div class='input-group ${claseGrupo}'>" + br
        div += textfield + br
        div += "<span class=\"input-group-addon\"><i class=\"${img}\"></i></span>" + br
        div += "</div>" + br

        def js = "<script type=\"text/javascript\">" + br
        js += '$("#' + id + '").datetimepicker({' + br
        if (startDate) {
            if (startDate instanceof Date) {
                startDate = "moment(${startDate.format('dd/MM/yyyy')})"
            }
            js += "minDate: '${startDate}'," + br
        }
        if (endDate) {
            if (endDate instanceof Date) {
                endDate = "moment(${endDate.format('dd/MM/yyyy')})"
            }
            js += "maxDate: '${endDate}'," + br
        }
        js += 'pickDate: ' + showDate + ',' + br
        js += 'pickTime: ' + showTime + ',' + br
        js += 'useMinutes: ' + showMin + ',' + br
        js += 'useSeconds: false,' + br
        js += 'minuteStepping: ' + minStep + ',' + br
        js += 'sideBySide: true,' + br
        if (daysOfWeekDisabled) {
//            println "dowd: " + daysOfWeekDisabled
//            println "dowd: " + daysOfWeekDisabled.class
            js += "daysOfWeekDisabled: ${daysOfWeekDisabled}," + br
        }
        if (beforeShowDay) {
            js += "beforeShowDay: function() { ${beforeShowDay}() }," + br
            js += "beforeShowDay: ${beforeShowDay}," + br
        }
        js += 'language: "es",' + br
        js += 'icons: {' + br
        js += 'time: "fa fa-clock-o",' + br
        js += 'date: "fa fa-calendar",' + br
        js += 'up: "fa fa-arrow-up",' + br
        js += 'down: "fa fa-arrow-down"' + br
        js += '},' + br
//        js += "format: '${formatJS}'," + br
        js += "orientation: '${orientation}'," + br
        js += "showToday: ${todayHighlight}" + br
        js += "}).on('dp.change', function(e) {" + br
//        js += 'console.log(e.date.date(),e.date.month(),e.date.year(), e.date.hour(), e.date.minute());'
        js += "var fecha = e.date;" + br
        js += "if(fecha) {" + br
        js += '$("#' + idHiddenDay + '").val(fecha.date());' + br
        js += '$("#' + idHiddenMonth + '").val(fecha.month() + 1);' + br
        js += '$("#' + idHiddenYear + '").val(fecha.year());' + br
        if (showTime) {
            js += '$("#' + idHiddenHour + '").val(fecha.hour());' + br
            js += '$("#' + idHiddenMin + '").val(fecha.minute());' + br
        }
        js += '$(e.currentTarget).parents(".grupo").removeClass("has-error").find("label.help-block").hide();' + br
        js += "}" + br
        if (onChangeDate) {
            js += onChangeDate + "(\$(this), e);" + br
        }
        js += "});" + br
        js += "</script>" + br

        out << div
        out << js
    }

    /**
     * hace la paginacion para una lista
     *  attrs:
     *          total       la cantidad total que tiene la tabla (el total de todas las páginas)
     *          maxPag      la cantidad máxima de páginas a mostrar. default: 10:       1 2 3 4 5 6 7 8 9 10 11 ... 20
     *          controller  controller para los links (si es diferente al actual)
     *          action      action para los links (si es diferente al actual)
     *          params      los parametros del link
     *                          max         cantidad máxima de registros por página
     *                          offset      el offset
     *                          sort        el ordenamiento
     *                          order       el sentido del ordenamiento
     *
     */
    def pagination = { attrs ->
//        println attrs

        if (attrs.total == null) {
            throwTagError("Tag [paginate] is missing required attribute [total]")
        }

        def maxPag = params.maxPag ?: 10

        def params = attrs.params

        def total = attrs.total
        def max = params.max ? params.max.toInteger() : 10
        def offset = params.offset ? params.offset.toInteger() : 0

        def curPag = (offset / max) + 1

        def paginas = Math.ceil(total / max).toInteger()

        def action = (attrs.action ? attrs.action : (params.action ? params.action : "list"))

        def linkParams = [:]
        if (attrs.params) linkParams.putAll(attrs.params)
//        linkParams.offset = offset - max
        linkParams.max = max
        if (params.sort) linkParams.sort = params.sort
        if (params.order) linkParams.order = params.order

        def linkTagAttrs = [action: action]
        if (attrs.controller) {
            linkTagAttrs.controller = attrs.controller
        }
        if (attrs.id != null) {
            linkTagAttrs.id = attrs.id
        }
        if (attrs.fragment != null) {
            linkTagAttrs.fragment = attrs.fragment
        }
        linkTagAttrs.params = linkParams

        def html = "<div class='row text-center'><ul class='pagination'>"

//        println "total: " + total + " max: " + max + " paginas: " + paginas + " curPag: " + curPag

        def firstPag, lastPag, link

        if (paginas > maxPag + 2) {
            firstPag = (curPag - Math.ceil(maxPag / 2)).toInteger()
            if (firstPag < 2) {
                firstPag = 2
            }
            lastPag = (curPag + Math.ceil(maxPag / 2)).toInteger()
            if (lastPag > paginas - 1) {
                lastPag = paginas - 1
            }
            def t = lastPag - firstPag
            if (t <= maxPag) {
                def extra = maxPag - t - 1
                lastPag += extra
                if (lastPag > paginas - 1) {
                    lastPag = paginas - 1
                }
            }
        } else {
            firstPag = 2
            lastPag = paginas - 1
        }

        def clase = curPag == 1 ? "active" : ""

        if (clase == "") {
//            params.offset = offset - max
//            link = createLink(action: action, params: params)

            linkParams.offset = offset - max
            link = createLink(linkTagAttrs.clone())

            html += "<li><a href='${link}'>&laquo;</a></li>"
        }

        html += "<li class='${clase}'>"
//        params.offset = 0
//        link = createLink(action: action, params: params)
        linkParams.offset = 0
        link = createLink(linkTagAttrs.clone())
        html += clase == 'active' ? "<span>1</span>" : "<a href='${link}'>1</a>"
        html += "</li>"

        if (firstPag > 2) {
            html += "<li class='disabled'><span>...</span></li>"
        }

        for (def i = firstPag; i <= lastPag; i++) {
//            params.offset = (i - 1) * max
//            link = createLink(action: action, params: params)
            linkParams.offset = (i - 1) * max
            link = createLink(linkTagAttrs.clone())
            clase = curPag == i ? "active" : ""
            html += "<li class='${clase}'>"
            html += clase == 'active' ? "<span>${i}</span>" : "<a href='${link}'>${i}</a>"
            html += "</li>"
        }

        if (lastPag < paginas - 1) {
            html += "<li class='disabled'><span>...</span></li>"
        }

        if (paginas > 1) {
            clase = curPag == paginas ? "active" : ""
//            params.offset = (paginas - 1) * max
//            link = createLink(action: action, params: params)
            linkParams.offset = (paginas - 1) * max
            link = createLink(linkTagAttrs.clone())
            html += "<li class='${clase}'>"
            html += clase == 'active' ? "<span>${paginas}</span>" : "<a href='${link}'>${paginas}</a>"
            html += "</li>"
            if (clase == "") {
//                params.offset = offset + max
//                link = createLink(action: action, params: params)
                linkParams.offset = offset + max
                link = createLink(linkTagAttrs.clone())
                html += "<li><a href='${link}'>&raquo;</a></li>"
            }
        }

        html += "</ul></div>"

        out << html
    }

    /**
     * A helper tag for creating HTML selects.<br/>
     *
     * Examples:<br/>
     * &lt;g:select name="user.age" from="${18..65}" value="${age}" /&gt;<br/>
     * &lt;g:select name="user.company.id" from="${Company.list()}" value="${user?.company.id}" optionKey="id" /&gt;<br/>
     *
     * @emptyTag
     *
     * @attr name REQUIRED the select name
     * @attr id the DOM element id - uses the name attribute if not specified
     * @attr from REQUIRED The list or range to select from
     * @attr keys A list of values to be used for the value attribute of each "option" element.
     * @attr optionKey By default value attribute of each &lt;option&gt; element will be the result of a "toString()" call on each element. Setting this allows the value to be a bean property of each element in the list.
     * @attr optionValue By default the body of each &lt;option&gt; element will be the result of a "toString()" call on each element in the "from" attribute list. Setting this allows the value to be a bean property of each element in the list.
     * @attr optionClass permite setear una clase individualmente a cada option
     * @attr value The current selected value that evaluates equals() to true for one of the elements in the from list.
     * @attr multiple boolean value indicating whether the select a multi-select (automatically true if the value is a collection, defaults to false - single-select)
     * @attr valueMessagePrefix By default the value "option" element will be the result of a "toString()" call on each element in the "from" attribute list. Setting this allows the value to be resolved from the I18n messages. The valueMessagePrefix will be suffixed with a dot ('.') and then the value attribute of the option to resolve the message. If the message could not be resolved, the value is presented.
     * @attr noSelection A single-entry map detailing the key and value to use for the "no selection made" choice in the select box. If there is no current selection this will be shown as it is first in the list, and if submitted with this selected, the key that you provide will be submitted. Typically this will be blank - but you can also use 'null' in the case that you're passing the ID of an object
     * @attr disabled boolean value indicating whether the select is disabled or enabled (defaults to false - enabled)
     * @attr readonly boolean value indicating whether the select is read only or editable (defaults to false - editable)
     */
    Closure select = { attrs ->
        if (!attrs.name) {
            throwTagError("Tag [select] is missing required attribute [name]")
        }
        if (!attrs.containsKey('from')) {
            throwTagError("Tag [select] is missing required attribute [from]")
        }
        def messageSource = grailsAttributes.getApplicationContext().getBean("messageSource")
        def locale = RequestContextUtils.getLocale(request)
        def writer = out
        def from = attrs.remove('from')
        def keys = attrs.remove('keys')
        def optionKey = attrs.remove('optionKey')
        def optionValue = attrs.remove('optionValue')
        def optionClass = attrs.remove('optionClass')
        def value = attrs.remove('value')
        if (value instanceof Collection && attrs.multiple == null) {
            attrs.multiple = 'multiple'
        }
        if (value instanceof CharSequence) {
            value = value.toString()
        }
        def valueMessagePrefix = attrs.remove('valueMessagePrefix')
        def classMessagePrefix = attrs.remove('classMessagePrefix')
        def noSelection = attrs.remove('noSelection')
        if (noSelection != null) {
            noSelection = noSelection.entrySet().iterator().next()
        }
        booleanToAttribute(attrs, 'disabled')
        booleanToAttribute(attrs, 'readonly')

        writer << "<select "
        // process remaining attributes
        outputAttributes(attrs, writer, true)

        writer << '>'
        writer.println()

        if (noSelection) {
            renderNoSelectionOptionImpl(writer, noSelection.key, noSelection.value, value)
            writer.println()
        }

        // create options from list
        if (from) {
            from.eachWithIndex { el, i ->
                def keyValue = null
                writer << '<option '
                if (keys) {
                    keyValue = keys[i]
                    writeValueAndCheckIfSelected(keyValue, value, writer)
                } else if (optionKey) {
                    def keyValueObject = null
                    if (optionKey instanceof Closure) {
                        keyValue = optionKey(el)
                    } else if (el != null && optionKey == 'id' && grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, el.getClass().name)) {
                        keyValue = el.ident()
                        keyValueObject = el
                    } else {
                        keyValue = el[optionKey]
                        keyValueObject = el
                    }
                    writeValueAndCheckIfSelected(keyValue, value, writer, keyValueObject)
                } else {
                    keyValue = el
                    writeValueAndCheckIfSelected(keyValue, value, writer)
                }

                /** **********************************************************************************************************************************************************/
                if (optionClass) {
                    if (optionClass instanceof Closure) {
                        writer << "class='" << optionClass(el).toString().encodeAsHTML() << "'"
                    } else {
                        writer << "class='" << el[optionClass].toString().encodeAsHTML() << "'"
                    }
                } else if (el instanceof MessageSourceResolvable) {
                    writer << "class='" << messageSource.getMessage(el, locale) << "'"
                } else if (classMessagePrefix) {
                    def message = messageSource.getMessage("${classMessagePrefix}.${keyValue}", null, null, locale)
                    if (message != null) {
                        writer << "class='" << message.encodeAsHTML() << "'"
                    } else if (keyValue && keys) {
                        def s = el.toString()
                        if (s) writer << "class='" << s.encodeAsHTML() << "'"
                    } else if (keyValue) {
                        writer << "class='" << keyValue.encodeAsHTML() << "'"
                    } else {
                        def s = el.toString()
                        if (s) writer << "class='" << s.encodeAsHTML() << "'"
                    }
                } else {
                    def s = el.toString()
                    if (s) writer << "class='" << s.encodeAsHTML() << "'"
                }
                /** **********************************************************************************************************************************************************/

                writer << '>'
                if (optionValue) {
                    if (optionValue instanceof Closure) {
                        writer << optionValue(el).toString().encodeAsHTML()
                    } else {
                        writer << el[optionValue].toString().encodeAsHTML()
                    }
                } else if (el instanceof MessageSourceResolvable) {
                    writer << messageSource.getMessage(el, locale)
                } else if (valueMessagePrefix) {
                    def message = messageSource.getMessage("${valueMessagePrefix}.${keyValue}", null, null, locale)
                    if (message != null) {
                        writer << message.encodeAsHTML()
                    } else if (keyValue && keys) {
                        def s = el.toString()
                        if (s) writer << s.encodeAsHTML()
                    } else if (keyValue) {
                        writer << keyValue.encodeAsHTML()
                    } else {
                        def s = el.toString()
                        if (s) writer << s.encodeAsHTML()
                    }
                } else {
                    def s = el.toString()
                    if (s) writer << s.encodeAsHTML()
                }
                writer << '</option>'
                writer.println()
            }
        }
        // close tag
        writer << '</select>'
    }

    /********************************************************* funciones ******************************************************/

    /**
     * renders attributes in HTML compliant fashion returning them in a string
     */
    String renderAttributes(attrs) {
        def ret = ""
        attrs.remove('tagName') // Just in case one is left
        attrs.each { k, v ->
            ret += k
            ret += '="'
            if (v) {
                ret += v.encodeAsHTML()
            } else {
                ret += ""
            }
            ret += '" '
        }
        return ret
    }

    /**
     * Some attributes can be defined as Boolean values, but the html specification
     * mandates the attribute must have the same value as its name. For example,
     * disabled, readonly and checked.
     */
    private void booleanToAttribute(def attrs, String attrName) {
        def attrValue = attrs.remove(attrName)
        // If the value is the same as the name or if it is a boolean value,
        // reintroduce the attribute to the map according to the w3c rules, so it is output later
        if (Boolean.valueOf(attrValue) ||
                (attrValue instanceof String && attrValue?.equalsIgnoreCase(attrName))) {
            attrs.put(attrName, attrName)
        } else if (attrValue instanceof String && !attrValue?.equalsIgnoreCase('false')) {
            // If the value is not the string 'false', then we should just pass it on to
            // keep compatibility with existing code
            attrs.put(attrName, attrValue)
        }
    }

    /**
     * Dump out attributes in HTML compliant fashion.
     */
    void outputAttributes(attrs, writer, boolean useNameAsIdIfIdDoesNotExist = false) {
        attrs.remove('tagName') // Just in case one is left
        attrs.each { k, v ->
            writer << k
            writer << '="'
            writer << v.encodeAsHTML()
            writer << '" '
        }
        if (useNameAsIdIfIdDoesNotExist) {
            outputNameAsIdIfIdDoesNotExist(attrs, writer)
        }
    }

    Closure renderNoSelectionOption = { noSelectionKey, noSelectionValue, value ->
        renderNoSelectionOptionImpl(out, noSelectionKey, noSelectionValue, value)
    }

    def renderNoSelectionOptionImpl(out, noSelectionKey, noSelectionValue, value) {
        // If a label for the '--Please choose--' first item is supplied, write it out
        out << "<option value=\"${(noSelectionKey == null ? '' : noSelectionKey)}\"${noSelectionKey == value ? ' selected="selected"' : ''}>${noSelectionValue.encodeAsHTML()}</option>"
    }

    private outputNameAsIdIfIdDoesNotExist(attrs, out) {
        if (!attrs.containsKey('id') && attrs.containsKey('name')) {
            out << 'id="'
            out << attrs.name?.encodeAsHTML()
            out << '" '
        }
    }

    private writeValueAndCheckIfSelected(keyValue, value, writer) {
        writeValueAndCheckIfSelected(keyValue, value, writer, null)
    }

    private writeValueAndCheckIfSelected(keyValue, value, writer, el) {

        boolean selected = false
        def keyClass = keyValue?.getClass()
        if (keyClass.isInstance(value)) {
            selected = (keyValue == value)
        } else if (value instanceof Collection) {
            // first try keyValue
            selected = value.contains(keyValue)
            if (!selected && el != null) {
                selected = value.contains(el)
            }
        }
        // GRAILS-3596: Make use of Groovy truth to handle GString <-> String
        // and other equivalent types (such as numbers, Integer <-> Long etc.).
        else if (keyValue == value) {
            selected = true
        } else if (keyClass && value != null) {
            try {
                def typeConverter = new SimpleTypeConverter()
                value = typeConverter.convertIfNecessary(value, keyClass)
                selected = (keyValue == value)
            }
            catch (e) {
                // ignore
            }
        }
        writer << "value=\"${keyValue}\" "
        if (selected) {
            writer << 'selected="selected" '
        }
    }
}
