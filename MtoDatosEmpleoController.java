package mx.com.solser.fx.controller.core.mantenimiento;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import mx.com.solser.controls.SChoiceBox;
import mx.com.solser.controls.STextField;
import mx.com.solser.controls.STitledPane;
import mx.com.solser.controls.api.EnterNavigable;
import mx.com.solser.controls.api.NavegacionControlesService;
import mx.com.solser.cplasu.beans.dto.Cliente;
import mx.com.solser.cplasu.beans.dto.DireccionBean;
import mx.com.solser.cplasu.beans.dto.altaunica.altaproductos.ProductoBean;
import mx.com.solser.cplasu.beans.dto.altaunica.datosgenenerales.ActividadBean;
import mx.com.solser.cplasu.beans.dto.altaunica.datosgenenerales.SubActividadBean;
import mx.com.solser.cplasu.beans.dto.prospecto.EmpleoProspectoBean;
import mx.com.solser.cplasu.beans.req.prospecto.empleo.ReqActividad;
import mx.com.solser.cplasu.beans.req.prospecto.empleo.ReqSubActividad;
import mx.com.solser.cplasu.enums.EstadoCivilEnum;
import mx.com.solser.cplasu.enums.TelefonosInvalidosEnum;
import mx.com.solser.cplasu.enums.TipoDireccionEnum;
import mx.com.solser.cplasu.enums.TipoMovtoCte;
import mx.com.solser.cplasu.enums.TipoTelefonoEnum;
import mx.com.solser.cplasu.interfaces.Callback;
import mx.com.solser.cplasu.util.Constantes;
import mx.com.solser.cplasu.util.StringUtils;
import mx.com.solser.cplasu.util.Utilerias;
import mx.com.solser.fx.config.data.AbstractPanelFactoryWizard;
import mx.com.solser.fx.config.data.MenuData;
import mx.com.solser.fx.controller.genericos.DireccionController;
import mx.com.solser.fx.entrypoint.config.SFxmlLoader;
import mx.com.solser.fx.entrypoint.config.SolserApplicationConfig;
import mx.com.solser.fx.enums.AltaUnicaAccionEnum;
import mx.com.solser.fx.service.altaunica.datosgenerales.DireccionService;
import mx.com.solser.fx.service.productos.EmpleoProductosService;
import mx.com.solser.model.ui.MensajesHardCoded;
import mx.com.solser.model.ui.MessageConfigs;
import mx.com.solser.ui.controls.SolserModalSameStage;
@Log4j2
public class MtoDatosEmpleoController extends AbstractPanelFactoryWizard {
  
  private final String[] TELEFONOS_INVALIDOS = { TelefonosInvalidosEnum.TEL_1_A_0.getNumero(),
      TelefonosInvalidosEnum.TEL_0_A_1.getNumero(), TelefonosInvalidosEnum.TEL_0S.getNumero(),
      TelefonosInvalidosEnum.TEL_VACIO.getNumero(), TelefonosInvalidosEnum.TEL_1_A_1.getNumero(),
      TelefonosInvalidosEnum.TEL_2_A_2.getNumero(), TelefonosInvalidosEnum.TEL_3_A_3.getNumero(),
      TelefonosInvalidosEnum.TEL_4_A_4.getNumero(), TelefonosInvalidosEnum.TEL_5_A_5.getNumero(),
      TelefonosInvalidosEnum.TEL_6_A_6.getNumero(), TelefonosInvalidosEnum.TEL_7_A_7.getNumero(),
      TelefonosInvalidosEnum.TEL_8_A_8.getNumero(), TelefonosInvalidosEnum.TEL_9_A_9.getNumero()};
  @FXML
  private STextField txtNombreEmpresa, txtTelTrabajo, txtExtension;
  @FXML
  private SChoiceBox<SubActividadBean> cmbSubActividad;
  @FXML
  private SChoiceBox<ActividadBean> cmbActividad;

  @FXML
  private Accordion accEmpleo;
  
  @FXML
  private STitledPane paneDireccion;
  @FXML
  private VBox vbTelefono;
  @FXML @Getter
  private VBox vbPeriodicidad;

  @FXML
  private VBox vbImporte;
  
  private DireccionController direccionController;
  
  private DireccionBean direccionEmpleo;

  private EmpleoProspectoBean empleoOriginal;
  private EmpleoProspectoBean empleoNuevo;
  private Cliente cliente;
  private AltaUnicaAccionEnum acc;

  private String actividad;
  private String subAtividad;
  private String telefono;
  @Setter
  private String sTipoPantalla;

  private final EmpleoProductosService empleoService = new EmpleoProductosService();
  private final DireccionService direccionService = new DireccionService();
  private SolserModalSameStage modal = SolserApplicationConfig.getSolserModalSameStage();
  private  ProductoBean producto = new ProductoBean();
  
  private boolean cambiosEmpleo;
  private boolean cambiosDireccion;
  @Getter
  @Setter
  private boolean cargaPantalla;

  private final Callback callbackNavegacion = new Callback() {
    @Override
    public void callback(final Object object) {
      NavegacionControlesService.getInstance().creaNavegacion();
    }
  };
  private final Callback callbackEstado = new Callback() {
    @Override
    public void callback(final Object object) {
      cargaCatalogos();
    }
  };
  
  private final Callback callbackGuardaEmpleo = new Callback() {
    @Override
    public void callback(final Object object) {
      getInformacionGuardada().callback(Boolean.TRUE);
    }
  };
  private final Callback callbackMensajeTelefono = new Callback() {
    @Override
    public void callback(final Object object) {
      txtTelTrabajo.setText(telefono);
      notificacionTelefono.callback(txtTelTrabajo);
    }
  };
  private final Callback callbackCapturaCiega = new Callback() {
    @Override
    public void callback(final Object object) {
      if ((boolean) object) {
        txtTelTrabajo.setText(telefono);
        ponFoco(txtExtension);
      } else {
        muestraMensaje(MensajesHardCoded.DATO_INCONSISTENTE.getConfig(), callbackMensajeTelefono);
      }
    }
  };
  //FIXME Verificar funcionamiento ya que se eliminó el titlePane de Telefono
  private Callback notificacionTelefono = new Callback() {
    @Override
    public void callback(Object object) {
//      if (paneTelefono.isExpanded() && object instanceof Control) {
        if (object instanceof Control) {
        ((Control) object).requestFocus();
        }
//      } else {
//        paneTelefono.setNodoObjetivo((Node) object);
//        accEmpleo.setExpandedPane(paneTelefono);
//      }
    }
  };
  
  private Callback notificacionDireccion = new Callback() {
    @Override
    public void callback(Object object) {
      if (paneDireccion.isExpanded() && object instanceof Control) {
        ((Control) object).requestFocus();
      } else {
        paneDireccion.setNodoObjetivo((Node) object);
        accEmpleo.setExpandedPane(paneDireccion);
      }
    }
  };

  private void loadDireccion() {
    final Object[] loader = SFxmlLoader.createLoaderSystem(MenuData.DIRECCION2);
    final AnchorPane anchor = (AnchorPane)loader[Constantes.CERO];
    this.paneDireccion.setContent(anchor.getChildren().get(0));
    this.direccionController = (DireccionController) loader[Constantes.UNO];
    this.direccionController.setTipoDireccion(TipoDireccionEnum.OFICINA);
    this.direccionController.setCallBackBilletesFalsos(this.callbackEstado);
    this.direccionController.setCallBackNavegacion(this.callbackNavegacion);
    this.direccionController.setPostNotificacionValidacion(this.notificacionDireccion);
    this.direccionController.setColumnas(Constantes.CINCO);
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    log.info("initialize EmpleoController");
    cargaPantalla = Boolean.TRUE;
    cargaCatalogos();
    loadDireccion();
    inicioCampos();
    iniciaEventos();
  }
  
  @Override
  public void resetView() {
    this.cmbActividad.clear();
    this.cargaBean();
    this.direccionController.resetView();
  }
  
  public void resetView(boolean pantallaTrabajo) {
    this.cmbActividad.clear();
    this.cargaBean();
  }
  
  
  private void inicioCampos() {
    this.txtNombreEmpresa.addTextLimiter(txtNombreEmpresa.getMaxLength());
    this.txtTelTrabajo.addTextLimiter(txtTelTrabajo.getMaxLength());
    this.txtExtension.addTextLimiter(txtExtension.getMaxLength());
    this.txtNombreEmpresa.enableCopyPasteCut(Boolean.FALSE);
    this.txtTelTrabajo.enableCopyPasteCut(Boolean.FALSE);
    this.txtExtension.enableCopyPasteCut(Boolean.FALSE);
    this.cmbActividad.setDisable(Boolean.TRUE);
    this.txtNombreEmpresa.setDisable(Boolean.TRUE);
    this.accEmpleo.setDisable(Boolean.TRUE);
  }

  

  
  @Override
  public void setParams(final Object[] params) {
    super.setParams(params);
  }

  private void procesaSubActividad(final SubActividadBean subActividad) {
    if (subActividad == null) {
      return;
    }
    if (Constantes.SUB_ACTIVIDAD_99.equals(subActividad.getCodigo())) {
      this.cmbActividad.setDisable(Boolean.TRUE);
      this.txtNombreEmpresa.setText(Constantes.BLANK);
      this.txtTelTrabajo.setText(Constantes.BLANK);
      this.txtExtension.setText(Constantes.BLANK);
      this.txtNombreEmpresa.setDisable(Boolean.TRUE);
      this.accEmpleo.setExpandedPane(null);
      this.accEmpleo.setDisable(Boolean.TRUE);
    } else {
      this.cmbActividad.setDisable(Boolean.FALSE);
      this.txtNombreEmpresa.setDisable(Boolean.FALSE);
      this.accEmpleo.setDisable(Boolean.FALSE);
    }
    verificaNavegacion();
  }

  private void procesaEspecifico(Node control, String idControl) {
    if (control instanceof EnterNavigable) {
      ((EnterNavigable) control).setIdNextEspecifico(idControl);
    }
  }

  private void verificaNavegacion() {
    creaNavegacion();
    if (accEmpleo.isDisable()) {
      if(!this.txtExtension.isDisabled()) {
          this.procesaEspecifico(this.txtExtension, Constantes.OPCION_SIGUIENTE_ID);
      }
    } else {
        if(!this.txtExtension.isDisable()) {
          this.procesaEspecifico(this.txtExtension, "paneDireccion");
        }
      }
      this.procesaEspecifico(this.direccionController.txtComplemento, Constantes.OPCION_SIGUIENTE_ID);
    }

  private void iniciaEventos() {
    this.cmbSubActividad.getSelectionModel().selectedItemProperty().
        addListener(new ChangeListener<SubActividadBean>() {
          @Override
          public void changed(final ObservableValue< ? extends SubActividadBean> observable,
              final SubActividadBean oldValue, final SubActividadBean newValue) {
            if (newValue != null) {
              traeCatalogoActividad(newValue);
              subAtividad = newValue.getCodigo();
            }
          }
        });
    this.cmbActividad.getSelectionModel().selectedItemProperty().
        addListener(new ChangeListener<ActividadBean>() {
          @Override
          public void changed(final ObservableValue< ? extends ActividadBean> observable,
              final ActividadBean oldValue, final ActividadBean newValue) {
            if (newValue != null) {
              actividad = newValue.getCodigoPadre();
            } else {
              actividad = Constantes.BLANK;
            }
          }
        });
    this.txtTelTrabajo.focusedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(final ObservableValue< ? extends Boolean> observable, final Boolean oldValue,
          final Boolean newValue) {
        if (!newValue && !txtTelTrabajo.getText().isEmpty()) {
          validaCapturaCiega();
        }
      }
    });
  }

  @FXML
  private void handleSubActividadAction(final MouseEvent event) {
    this.cargaPantalla = Boolean.FALSE;
    if (this.cmbSubActividad.getItems().isEmpty()) {
      this.cargaCatalogos();
    }
  }
  
  private void cargaBean() {
    this.empleoOriginal = new EmpleoProspectoBean();
    this.empleoOriginal.setCliente(new Cliente());
    if (this.empleoOriginal == null) {
      this.empleoOriginal = new EmpleoProspectoBean();
    }
    this.cliente = this.empleoOriginal.getCliente();
    if (this.sTipoPantalla == null) {
      this.sTipoPantalla = TipoMovtoCte.ALTA.getValor();
    }
    this.direccionController.setParams(new Object[] {this.empleoOriginal.getDireccion() });
  }

  
  @Override
  public <T> T getBean() {
    return null;
  }
  
  private void validaInformacion() {
    if (!validaInformacionEmpleo()) {
      return;
    }
    if (!Constantes.SUB_ACTIVIDAD_99.equals(subAtividad) && (!validaTelefonos())) {
      return;
    }
    if (!Constantes.SUB_ACTIVIDAD_99.equals(subAtividad)) {
      this.direccionEmpleo = this.direccionController.getBean();
    }
    if (!Constantes.SUB_ACTIVIDAD_99.equals(subAtividad) &&
        this.direccionEmpleo == null) {
      return;
    }
    empleoNuevo = llenaEmpleoBean(direccionEmpleo);
    empleoOriginal = empleoNuevo;
    getCallbck().callback(empleoNuevo);
  }

  public EmpleoProspectoBean getEmpleo() {
    return this.empleoNuevo;
  }

  public EmpleoProspectoBean getEmpleoOriginal() {
    return this.empleoOriginal;
  }

  private void cargaCatalogos() {
    this.cmbSubActividad.clear();
        new ReqSubActividad("27", EstadoCivilEnum.CASADO.getCodigo(), "000016912");
  }

  public void traeCatalogoActividad(final SubActividadBean subActividadBean) {
    final ReqActividad req = new ReqActividad();
    req.setCodigo(subActividadBean.getCodigo());
    req.setDescripcionSubAct(subActividadBean.getDescripcion());
    req.setEdad(Utilerias.getInteger(this.cliente.getEdad()));
    procesaSubActividad(subActividadBean);
    this.empleoService.traeCatalogoActividadMantoEmpleo(req,  this);
      
  
  
  }

  @Override
  public void guardaInformacion(final AltaUnicaAccionEnum accion) {
    acc = accion;
    validaInformacion();
  }
  private Boolean validaInformacionEmpleo() {

    if (this.cmbSubActividad.getSelectionModel().isEmpty()) {
      this.muestraMensaje(MensajesHardCoded.DE_ACTIVIDAD_SUBACTIVIDAD_REQUERIDO.getConfig(),
          traeCallbackFoco(cmbSubActividad));
      return Boolean.FALSE;
    }
    if (!Constantes.SUB_ACTIVIDAD_99.equals(this.subAtividad) &&
        this.cmbActividad.getSelectionModel().isEmpty()) {
      this.muestraMensaje(MensajesHardCoded.DE_ACTIVIDAD_SUBACTIVIDAD_REQUERIDO.getConfig(),
          traeCallbackFoco(cmbActividad));
      return Boolean.FALSE;
    }
    if (!Constantes.SUB_ACTIVIDAD_99.equals(this.subAtividad) &&
        StringUtils.isNullOrVacio(txtNombreEmpresa.getText())) {
      this.muestraMensaje(MensajesHardCoded.DE_NOMBRE_EMPRESA_REQUERIDO.getConfig(),
          traeCallbackFoco(txtNombreEmpresa));
      return Boolean.FALSE;
    }
   
   
    return Boolean.TRUE;
  }
  public Boolean validaTelefonos() {
    if (this.txtTelTrabajo.getText().isEmpty()) {
      this.muestraMensaje(MensajesHardCoded.DATO_TELEFONO_OFI_VACIO.getConfig(), new Callback() {        
        @Override
        public void callback(Object object) {
          notificacionTelefono.callback(txtTelTrabajo);
        }
      });
      return Boolean.FALSE;
    } else if (!this.txtTelTrabajo.getText().isEmpty()
        && this.txtTelTrabajo.getText().length() < this.txtTelTrabajo.getMaxLength()) {
      this.muestraMensaje(MensajesHardCoded.DATO_TELEFONO_OFI_LONG.getConfig(), new Callback() {        
        @Override
        public void callback(Object object) {
          notificacionTelefono.callback(txtTelTrabajo);
        }
      });
      return Boolean.FALSE;
    } else if (Utilerias.in(this.txtTelTrabajo.getText(), TELEFONOS_INVALIDOS)) {
      this.muestraMensaje(MensajesHardCoded.DATO_TELEFONO_OFI_INVALIDO.getConfig(), new Callback() {        
        @Override
        public void callback(Object object) {
          notificacionTelefono.callback(txtTelTrabajo);
        }
      });
      return Boolean.FALSE;
    }
    return Boolean.TRUE;
  }

  private void validaCapturaCiega() {
    telefono = this.txtTelTrabajo.getText();
    this.txtTelTrabajo.setUserData(telefono);
    this.txtTelTrabajo.setText(Constantes.BLANK);
    this.modal.showParameters(MenuData.CAPTURA_CIEGA, this.txtTelTrabajo, "Captura Ciega", callbackCapturaCiega);
  }

  public void guardaTelefono() {
    direccionService.guardarTelefonoProspecto(this.empleoNuevo.getTelefono(), this);
  }

  public void guardaDireccion() {
    if (!cambiosDireccion) {
      getInformacionGuardada().callback(Boolean.TRUE);
    }
    if (!Constantes.SUB_ACTIVIDAD_99.equals(subAtividad)) {
      direccionService.guardarDireccionProductos(this.empleoNuevo.getDireccion(), this);
    } else {
      muestraMensaje(MensajesHardCoded.DE_INFO_TRABAJO_CAPTURADA.getMessageConfig(), callbackGuardaEmpleo);
    }
  }

  public final void muestraMensajes(final String codigoError) {
    MensajesHardCoded msg = null;
    if ("200".equals(codigoError)) {
      msg = MensajesHardCoded.DE_OPERACION_INVALIDA;
    } else if ("400".equals(codigoError)) {
      msg = MensajesHardCoded.DE_SECUENCIA_INVALIDA;
    }
    modal.showParameters(MenuData.MESSAGESSAMESTAGE, msg.getMessageConfig());
  }

  private void muestraMensaje(final MessageConfigs configuracion, final Callback callback) {
    modal.showParameters(MenuData.MESSAGESSAMESTAGE, configuracion, configuracion.getTitle(), callback);
  }

  public void llenaView() {
    txtNombreEmpresa.setText(StringUtils.getOfBlank(empleoOriginal.getNombreEmpresa()));
    if (empleoOriginal.getSubAct() != null) {
      cmbSubActividad.getSelectionModel().select(empleoOriginal.getSubAct());
    }
    
    if (empleoOriginal.getTelefono() != null) {
      txtTelTrabajo.setText(StringUtils.getOfBlank(empleoOriginal.getTelefono().getTelefono()));
      txtExtension.setText(StringUtils.getOfBlank(empleoOriginal.getTelefono().getExtension()));
    }
  }

  private EmpleoProspectoBean llenaEmpleoBean(DireccionBean direccion) {
    final EmpleoProspectoBean empleoTmp = new EmpleoProspectoBean();
    empleoTmp.setCliente(cliente);
    empleoTmp.setNombreEmpresa(txtNombreEmpresa.getText());
    empleoTmp.setSubActividad(subAtividad);
    empleoTmp.setSubAct(cmbSubActividad.getValue());
    empleoTmp.setActividad(actividad);
    empleoTmp.setAct(cmbActividad.getValue());
    empleoTmp.getTelefono().setNumCliente(cliente.getNumCliente());
    empleoTmp.getTelefono().setTelefono(txtTelTrabajo.getText());
    empleoTmp.getTelefono().setExtension(txtExtension.getText());
    empleoTmp.getTelefono().setTipoTelefono(TipoTelefonoEnum.TRABAJO);
    if (null == direccion) {
      direccion = new DireccionBean();
    }
    procesaTelefonos(empleoTmp, direccion);
    empleoTmp.setDireccion(direccion);
    empleoTmp.getDireccion().setNumeroCliente(cliente.getNumCliente());
    empleoTmp.setCambios(validarCambiosEmpleo(empleoTmp) && cambiosDireccion);
    return empleoTmp;
  }
  
  private void procesaTelefonos(final EmpleoProspectoBean ingresoEmpleo, final DireccionBean direccion) {
    if (!StringUtils.isNullOrVacio(ingresoEmpleo.getTelefono().getTelefono())) {
      direccion.setNumTelefonoTres(ingresoEmpleo.getTelefono().getTelefono());
      direccion.setTipoTelefonoTres(TipoTelefonoEnum.TRABAJO.getClave());
      direccion.setExtension(ingresoEmpleo.getTelefono().getExtension());
    }
  }

  public boolean validarCambiosEmpleo(final EmpleoProspectoBean empleoTmp) {
    cambiosEmpleo = !empleoOriginal.equals(empleoNuevo);
    return cambiosEmpleo;
  }

  @SuppressWarnings("unchecked")
  public <T> void setListaCatalogo(final List<T> lista, final int tipo) {
    if (lista == null) {
      return;
    }
    switch (tipo) {
    case Constantes.UNO:
      cmbSubActividad.
          setItemsS((ObservableList<SubActividadBean>) FXCollections.observableList(lista));
      break;
    case Constantes.DOS:
      cmbActividad.setItemsS((ObservableList<ActividadBean>) FXCollections.observableList(lista));
      if (this.cargaPantalla && this.empleoOriginal.getAct() != null) {
        cmbActividad.getSelectionModel().select(this.empleoOriginal.getAct());
        this.cargaPantalla = Boolean.FALSE;
      } else if (lista.size() == Constantes.UNO) {
        cmbActividad.getSelectionModel().select(Constantes.CERO);
      }
      break;
    default:
      break;
    }
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        NavegacionControlesService.getInstance().creaNavegacion();
      }
    });
  }

  @Override
  public <T> void retornoGuardar(final T param) {
  }

  private Callback traeCallbackFoco(final Node nodo) {
    return new Callback() {
      @Override
      public void callback(final Object object) {
        ponFoco(nodo);
      }
    };
  }
  
}
