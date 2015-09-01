<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title></title>
    <imp:js src="${resource(dir: 'js/plugins/jstree-3.0.9/dist', file: 'jstree.min.js')}"/>
    <imp:css src="${resource(dir: 'js/plugins/jstree-3.0.9/dist/themes/default', file: 'style.min.css')}"/>
    <imp:css src="${resource(dir: 'css/custom', file: 'jstree-context.css')}"/>

    <imp:js src="${resource(dir: 'js/plugins/pdfObject', file: 'pdfobject.min.js')}"/>

    <imp:js src="${resource(dir: 'js/plugins/bootstrap-select-1.6.3/dist/js', file: 'bootstrap-select.min.js')}"/>
    <imp:css src="${resource(dir: 'js/plugins/bootstrap-select-1.6.3/dist/css', file: 'bootstrap-select.min.css')}"/>
    <link href="${g.resource(dir: 'css/custom/', file: 'pdfViewer.css')}" rel="stylesheet" type="text/css">
    <imp:js src="${resource(dir: 'js/plugins/pdfObject', file: 'pdfobject.min.js')}"/>

    <style type="text/css">


    .jstree-search {
        color : #f7ea57 !important;
    }

    .treePart {
        overflow-y : auto;
        height     : 440px;
    }
    </style>
</head>

<body>
<div class="pdf-viewer" style="width: 46%">
    <div class="pdf-content" >
        <div class="pdf-container" id="doc"></div>
        <div class="pdf-handler" >
            <i class="fa fa-arrow-right"></i>
        </div>
        <div class="pdf-header" id="data">
            N. Referencia: <span id="referencia-pdf" class="data"></span>
            Código: <span id="codigo" class="data"></span>
            Tipo: <span id="tipo" class="data"></span>



        </div>
        <div id="msgNoPDF">
            <p>No tiene configurado el plugin de lectura de PDF en este navegador.</p>

            <p>
                Puede
                <a class="text-info" target="_blank" style="color: white" href="http://get.adobe.com/es/reader/">
                    <u>descargar Adobe Reader aquí</u>
                </a>
            </p>
        </div>
    </div>
</div>
<elm:message tipo="${flash.tipo}" clase="${flash.clase}">${flash.message}</elm:message>
<div class="row fila">
    <div class="col-md-6">
        <div class="panel-completo" style="margin-left: 10px;min-height: 500px">
            <div class="row">
                <div class="col-md-4 titulo-panel">
                    Empleados
                </div>
                <div class="btn-group pull-right col-md-8 titulo-panel" style="margin-top: -11px">
                    <div class="input-group">
                        <input type="text" id="searchArbol" class="form-control input-search input-sm" placeholder="Buscar" value="${params.search}">
                        <span class="input-group-btn">
                            <a href="#" id="btnSearchArbol" class="btn btn-sm btn-default btn-search ">
                                <i class="fa fa-search"></i>&nbsp;
                            </a>
                        </span>
                    </div><!-- /input-group -->
                </div>
            </div>
            <div class="row fila">
                <div class="col-md-12">
                    <div id="tree">
                        ${raw(arbol)}
                    </div>

                </div>
            </div>
        </div>
    </div>
    <div class="col-md-6">
        <div class="panel-completo" style="margin-left: 10px;min-height: 500px">
            <div class="row">
                <div class="col-md-12 titulo-panel">
                    Detalle
                </div>
            </div>
            <div class="row fila">
                <div class="col-md-12" id="detalle">
                </div>
            </div>
        </div>
    </div>
</div>


<script type="text/javascript">
    function showPdf(div){
        $("#msgNoPDF").show();
        $("#doc").html("")
        var pathFile = div.data("file")
        $("#referencia-pdf").html(div.data("ref"))
        $("#codigo").html(div.data("codigo"))
        $("#tipo").html(div.data("tipo"))
        var path = "${resource()}/" + pathFile;
        var myPDF = new PDFObject({
            url           : path,
            pdfOpenParams : {
                navpanes: 1,
                statusbar: 0,
                view: "FitW"
            }
        }).embed("doc");
        $(".pdf-viewer").show("slide",{direction:'right'})
        $("#data").show()
    }
    $(".pdf-handler").click(function(){
        $(".pdf-viewer").hide("slide",{direction:'right'})
        $("#data").hide()
    })
    var searchRes = [];
    var posSearchShow = 0;
    var $treeContainer = $("#tree");
    function scrollToNode($scrollTo) {
//        $treeContainer.jstree("deselect_all").jstree("select_node", $scrollTo).animate({
//            scrollTop : $scrollTo.offset().top - $treeContainer.offset().top + $treeContainer.scrollTop() - 50
//        });
    }

    function scrollToRoot() {
        var $scrollTo = $("#estacion");
        scrollToNode($scrollTo);
    }

    function scrollToSearchRes() {
        var $scrollTo = $(searchRes[posSearchShow]).parents("li").first();
        $("#spanSearchRes").text("Resultado " + (posSearchShow + 1) + " de " + searchRes.length);
        scrollToNode($scrollTo);
    }

    function createContextMenu(node) {

        var nodeStrId = node.id;
        var $node = $("#" + nodeStrId);
        var nodeId = nodeStrId.split("_")[1];
        var nodeType = $node.data("jstree").type;
        var nodeText = $node.children("a").first().text();
        var esDep = nodeType == "emp";
        var esEmpleado = nodeType == "empleado";
        var esCarga = nodeType == "carga";
        var esContrato = nodeType == "contrato";
        var esCap = nodeType == "capacitacion";
        var emp = $node.attr("empleado")
        var editarEmpleado = {
            label: "Editar",
            icon: "fa fa-pencil",
            action: function () {
                openLoader()
                var id = nodeStrId.replace("e","")
                location.href="${g.createLink(controller: 'empleado',action: 'nuevoEmpleado')}/"+id
            }
        };
        var editarCarga = {
            label: "Editar",
            icon: "fa fa-pencil",
            action: function () {
                openLoader()
                var id = nodeStrId.replace("ca","")
                location.href="${g.createLink(controller: 'empleado',action: 'cargas')}/"+emp
            }
        };
        var editarContrato = {
            label: "Editar",
            icon: "fa fa-pencil",
            action: function () {
                openLoader()
                var id = nodeStrId.replace("co","")
                location.href="${g.createLink(controller: 'empleado',action: 'contratos')}/"+emp
            }
        };
        var editarCap = {
            label: "Editar",
            icon: "fa fa-pencil",
            action: function () {
                openLoader()
                var id = nodeStrId.replace("cp","")
                location.href="${g.createLink(controller: 'empleado',action: 'capacitacion')}/"+emp
            }
        };
        var hija = {
            label: "Crear cuenta hija",
            icon: "fa fa-level-down",
            action: function () {
                openLoader()
                $.ajax({
                    type: "POST",
                    url: "${createLink(controller:'cuentas', action:'form_ajax')}",
                    data: {
                        padre: nodeStrId
                    },
                    success: function (msg) {
                        closeLoader()
                        var b = bootbox.dialog({
                            id: "dlgDetalles",
                            title: "Agregar cuanta hija de: "+nodeStrId,
                            message: msg,
                            buttons: {
                                cerrar: {
                                    label: "Cerrar",
                                    className: "btn-default",
                                    callback: function () {

                                    }
                                },
                                guardar: {
                                    label: "Guardar",
                                    className: "btn-success",
                                    callback: function () {
                                        if($("#frmCuenta").valid()){
                                            openLoader()
                                            $("#frmCuenta").submit()
                                        }
                                        return false

                                    }
                                }
                            } //buttons
                        }); //dialog
                    } //success
                }); //ajax
                return false
            }
        };
        var items = {};
        if(esCarga)
        items.editar=editarCarga
        if(esContrato)
            items.editar=editarContrato
        if(esEmpleado)
            items.editar=editarEmpleado
        if(esCap)
            items.editar=editarCap
        return items;
    }

    $(function () {

        $('.select').selectpicker();
        $treeContainer.on("loaded.jstree", function () {
            $("#loading").hide();
            $treeContainer.removeClass("hidden");
        }).on("select_node.jstree", function (node, selected, event) {
            var nodeId = selected.selected[0];
            var $node = $("#" + nodeId);
            var nodeType = $node.data("jstree").type;
            if(nodeType!="pys" && nodeType!="cargas" && nodeType!="contratos" && nodeType!="capacitaciones"){
                openLoader()
                $.ajax({
                    type: "POST",
                    url: "${createLink(controller:'unidad', action:'detalle_ajax')}",
                    data: {
                        id: nodeId,
                        tipo:nodeType
                    },
                    success: function (msg) {
                        closeLoader()
                        $("#detalle").html(msg)
                        $("body").scrollTop(-10000)

                    } //success
                }); //ajax
            }



//                    $('#tree').jstree('toggle_node', selected.selected[0]);
        }).on('search.jstree', function (nodes, str, res) {
//                console.log(nodes, str, res);
            searchRes = $(".jstree-search");
            var cantRes = searchRes.length;
            posSearchShow = 0;
            $("#divSearchRes").removeClass("hidden");
            $("#spanSearchRes").text("Resultado " + (posSearchShow + 1) + " de " + cantRes);
            scrollToSearchRes();
        }).jstree({
            plugins     : ["types", "contextmenu", "search"],
            core        : {
                multiple       : false,
                check_callback : true,
                data           : {
                    async : false,
                    url   : '${createLink(action:"loadTreePart_ajax")}',
                    data  : function (node) {
                        console.log(node)
                        if(node.data){
                            return {
                                id    : node.id,
                                tipo: node.data.jstree.type
                            };
                        }else{
                            return {
                                id    : node.id
                            };
                        }

                    }
                }
            },
            contextmenu : {
                show_at_node : false,
                items        : createContextMenu
            },
            search      : {
                fuzzy             : false,
                show_only_matches : false,
                ajax              : {
                    url     : "${createLink(action:'buscarCuenta')}",
                    success : function (msg) {
                        closeLoader()

                        var json = $.parseJSON(msg);

                        $.each(json, function (i, obj) {
                            $('#tree').jstree("open_node", obj);

                        });
                        setTimeout(function () {

                            searchRes = $(".jstree-search");
                            var cantRes = searchRes.length;
                            posSearchShow = 0;
                            $("#divSearchRes").removeClass("hidden");
                            $("#spanSearchRes").text("Resultado " + (posSearchShow + 1) + " de " + cantRes);

                        }, 300);

                    }
                }
            },
            types       : {
                dep : {
                    icon : "${resource(dir:'images/tree', file:'Building_16.png')}"
                },
                empleado      : {
                    icon : "fa fa-user"
                },
                carga     : {
                    icon : "fa fa-users"
                },
                capacitacion    : {
                    icon : "fa fa-graduation-cap"
                },
                contrato    : {
                    icon : "fa fa-file-pdf-o "
                },
                pys:{
                    icon : "${resource(dir:'images/favicons', file:'favicon-16x16.png')}"
                }
            }
        });

        $("#btnExpandAll").click(function () {
            $treeContainer.jstree("open_all");
            scrollToRoot();
            return false;
        });

        $("#btnCollapseAll").click(function () {
            $treeContainer.jstree("close_all");
            scrollToRoot();
            return false;
        });

        $('#btnSearchArbol').click(function () {
            openLoader()
            $treeContainer.jstree(true).search($.trim($("#searchArbol").val()));
            return false;
        });
        $("#searchArbol").keypress(function (ev) {
            if (ev.keyCode == 13) {
                openLoader()
                $treeContainer.jstree(true).search($.trim($("#searchArbol").val()));
                return false;
            }
        });

        $("#btnPrevSearch").click(function () {
            if (posSearchShow > 0) {
                posSearchShow--;
            } else {
                posSearchShow = searchRes.length - 1;
            }
            scrollToSearchRes();
            return false;
        });

        $("#btnNextSearch").click(function () {
            if (posSearchShow < searchRes.length - 1) {
                posSearchShow++;
            } else {
                posSearchShow = 0;
            }
            scrollToSearchRes();
            return false;
        });

        $("#btnClearSearch").click(function () {
            $treeContainer.jstree("clear_search");
            $("#searchArbol").val("");
            posSearchShow = 0;
            searchRes = [];
            scrollToRoot();
            $("#divSearchRes").addClass("hidden");
            $("#spanSearchRes").text("");
        });

    });
</script>

</body>
</html>