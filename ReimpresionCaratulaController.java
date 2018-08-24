package mx.com.solser.fx.controller.core.mantoservicios.reimpresioncaratulapp;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import mx.com.solser.controls.SButton;
import mx.com.solser.controls.SChoiceBox;
import mx.com.solser.controls.STextField;
import mx.com.solser.controls.api.NavegacionControlesService;
import mx.com.solser.controls.api.FormatosCampos.Formato;
import mx.com.solser.cplasu.beans.dto.Producto;
import mx.com.solser.cplasu.beans.perifericos.RespuestaPerifericoBean;
import mx.com.solser.cplasu.beans.req.componentebusquedacliente.ReqConsultaBusquedaClientePersona;
import mx.com.solser.cplasu.beans.req.producto.ReqProducto;
import mx.com.solser.cplasu.beans.res.producto.ResImpresionCaratulaCred;
import mx.com.solser.cplasu.beans.res.producto.ResProductoOfertado;
import mx.com.solser.cplasu.interfaces.Callback;
import mx.com.solser.cplasu.util.Constantes;
import mx.com.solser.cplasu.util.UrlConstantes;
import mx.com.solser.cplasu.util.Utilerias;
import mx.com.solser.fx.config.data.AbstractPanelFactory;
import mx.com.solser.fx.config.data.MenuData;
import mx.com.solser.fx.controller.genericos.BusquedaClienteInterface;
import mx.com.solser.fx.entrypoint.MainEntry;
import mx.com.solser.fx.entrypoint.config.SolserApplicationConfig;
import mx.com.solser.fx.interfaces.RestCall;
import mx.com.solser.fx.security.SecurityUtils;
import mx.com.solser.fx.service.componentebusquedacliente.BusquedaClientePersonaService;
import mx.com.solser.fx.service.general.NavegacionService;
import mx.com.solser.fx.service.huellas.ValidadorHuellaService;
import mx.com.solser.model.ui.MensajesHardCoded;
import mx.com.solser.ui.controls.SolserModalSameStage;

@SuppressWarnings("restriction")
@Log4j2
public class ReimpresionCaratulaController extends AbstractPanelFactory {
  
  @FXML private SChoiceBox<Producto> cmbProductos;
  @FXML private STextField txtConsulta;
  @FXML private STextField txtNcliente;
  @FXML private STextField txtNomcliente;
  @FXML private STextField txtimpAsignado;
  @FXML private STextField txtPlazo;
  @FXML private STextField txtDcorte;
  @FXML private STextField txtDivisa;
  @FXML private STextField txtFecha;
  @FXML private SButton btnBuscar;
  @FXML private SButton btnImprimir;
  @FXML private SButton btnCancelar;
  @FXML private SButton btnSalir;
  @FXML private SButton btnPinPad;
  
  
  private int maxLength;
  private ResImpresionCaratulaCred respuestaCaratula = null;
  private SolserModalSameStage modal = SolserApplicationConfig.getSolserModalSameStage();
  private Callback callBackHabilitarBoton;
  private BusquedaClientePersonaService busquedaService;
  private ReqConsultaBusquedaClientePersona req;
  private Callback callBackCancelarBoton;
  private boolean longitudValida;

  @Override
  public void resetView() {
    NavegacionControlesService.getInstance().creaNavegacion();
    this.consultaProductos();
  }

  private void consultaProductos() {
    final RestCall<ReqProducto, ResProductoOfertado> call = new RestCall<ReqProducto, ResProductoOfertado>();
    call.setUrl(MainEntry.getUrl() + UrlConstantes.CONSULTA_PRODUCTO);
    call.setEntrada(new ReqProducto());
    call.setSalida(ResProductoOfertado.class);
    call.setEncrypted(false);
    call.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
      @Override
      public void handle(final WorkerStateEvent eventParam) {
        final ResProductoOfertado respuesta = call.getValue();
        cmbProductos.setItemsS(FXCollections.observableArrayList(respuesta.getProductos()));
      };
    });
    call.setOnFailed(this.onFailed);
    call.reset();
    call.start();
  }

  private EventHandler<WorkerStateEvent> onFailed = new EventHandler<WorkerStateEvent>() {
    @Override
    public void handle(WorkerStateEvent event) {
    }
    
  };

  @FXML
  public final void cmbProductosListener() {
    log.info("Deshabilitando botones");
    disableButtons(false);
  }
  
  private final void disableButtons(final boolean isDisable){
    btnBuscar.setDisable(isDisable);
    btnCancelar.setDisable(isDisable);
  }
  

  @FXML
  private void abrePinPad(final ActionEvent event) {
    this.txtConsulta.setDisable(false);
    this.btnCancelar.setDisable(false);
    this.btnBuscar.setDisable(false);
    this.btnImprimir.setDisable(true);
    Callback callBackPinPad = new Callback() {

      @Override
      public void callback(Object object) {
        if (object != null) {
          RespuestaPerifericoBean respuesta = (RespuestaPerifericoBean) object;
          txtConsulta.setText(respuesta.getNumeroTarjeta());
          if (!recuperaRequestCuenta()) {
            return;
          }
          busquedaService = new BusquedaClientePersonaService();
          req.setIdSistema(getIdSistema());
          BusquedaClienteInterface controller = null;
          busquedaService.recuperarClientes(req, controller);
        } else {
        }
      }
    };
    modal.showParameters(MenuData.CONTROL_PINPAD, null, "Lectura de Tarjeta", callBackPinPad);
    if (callBackHabilitarBoton != null)
      callBackHabilitarBoton.callback(null);
    creaNavegacion();
  }
  private void addTextLimiter() {
    this.txtConsulta.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(final ObservableValue<? extends String> ov, final String oldValue,
          final String newValue) {
        if (newValue == null)
          return;
        if (txtConsulta.getText().length() > txtConsulta.getFormato().getFormato().length()) {
          txtConsulta.setText(oldValue);
        } else {
          if (!newValue.isEmpty()) {
            btnPinPad.setDisable(Boolean.TRUE);
          } else {
            btnPinPad.setDisable(Boolean.FALSE);
          }
          if (callBackHabilitarBoton != null) {
            callBackHabilitarBoton.callback(null);
          }
        }
        NavegacionControlesService.getInstance().creaNavegacion();
      }
    });
  }
      
  public SButton getBtnPinPad() {
    return this.btnPinPad;
  }

  private boolean recuperaRequestCuenta() {
    return false;
  }

  @FXML
  private void btnBuscarListener() {
    final String noCredito = this.txtConsulta.getText() != null ? this.txtConsulta.getText().trim()
        : Constantes.BLANK;
    if (noCredito.isEmpty()) {
      this.modal.showParameters(MenuData.MESSAGESSAMESTAGE,
          MensajesHardCoded.BUSQUEDA_CREDITO.getConfig());
    } else {
      this.consultaGeneraReporte(noCredito);
      ValidadorHuellaService.getInstance().validaHuellaCliente("", iniciaCallbackCliente());
      this.cmbProductos.setDisable(true);
      this.txtConsulta.setDisable(false);
      this.btnCancelar.setDisable(false);
      this.btnBuscar.setDisable(true);
      this.btnImprimir.setDisable(false);
      
    }
  }

  private Callback iniciaCallbackCliente() {
    Callback callback = new Callback() {
      public void callback(final Object object) {
      }
    };
    return callback;
  }

  private void consultaGeneraReporte(final String numCredito) {
    final RestCall<ReqProducto, ResImpresionCaratulaCred> call = new RestCall<ReqProducto, ResImpresionCaratulaCred>();
    call.setUrl(MainEntry.getUrl() + UrlConstantes.CONSULTA_CREDITO);
    call.setEntrada(this.getReqProducto(numCredito));
    call.setSalida(ResImpresionCaratulaCred.class);
    call.setEncrypted(false);
    call.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
      @Override
      public void handle(final WorkerStateEvent eventParam) {
        respuestaCaratula = call.getValue();
        txtNcliente.setText(respuestaCaratula.getNumeroCliente());
        txtNomcliente.setText(respuestaCaratula.getNombreCliente());
        txtimpAsignado.setText(respuestaCaratula.getImporteAsignado());
        txtDcorte.setText(respuestaCaratula.getDiaCorte());
        txtPlazo.setText(respuestaCaratula.getPlazo());
        txtDivisa.setText(respuestaCaratula.getDivisa());
        txtFecha.setText(respuestaCaratula.getFechOperacion());
      };
    });
    call.setOnFailed(this.onFailed);
    call.reset();
    call.start();
  }

  private ReqProducto getReqProducto(final String numCredito) {
    final ReqProducto req = new ReqProducto();
    req.setSucursal(SecurityUtils.getSession().getSucursalBean().getId_sucursal());
    req.setNumCredito(numCredito);
    req.setNumeroProducto(
        this.cmbProductos.getSelectionModel().getSelectedItem().getNumeroProducto());
    req.setTipo(String.valueOf(Constantes.TRES));
    return req;
  }

  @FXML
  public final void processConsultar(final ActionEvent event) {
  }

  @Override
  public final void initialize(final URL location, final ResourceBundle resources) {
    addTextLimiter();
    this.cmbProductos.setDisable(Boolean.FALSE);
    this.cmbProductos.clearSelection();
    this.txtConsulta.setFormato(Formato.FMT_CREDITOS);
    maxLength = Formato.FMT_CREDITOS.getFormato().length();
    this.cmbProductos.getPromptText();
    this.txtNcliente.setText("");
    this.txtNomcliente.setText("");
    this.txtimpAsignado.setText("");
    this.txtPlazo.setText("");
    this.txtDcorte.setText("");
    this.txtDivisa.setText("");
    this.txtFecha.setText("");
    this.btnCancelar.setDisable(true);
    this.btnBuscar.setDisable(true);
    this.btnImprimir.setDisable(true);
    initEvents();
  
  }

  private void initEvents(){
    this.cmbProductos.setOnAction(event -> {
      disableButtons(false);
    });
  }
  
  private ChangeListener<String> listener = (ObservableValue<? extends String> observable,
      String oldValue, String newValue) -> {
        
        if(txtConsulta.getText().length() == 0 && callBackCancelarBoton!=null && callBackHabilitarBoton!=null)
          callBackCancelarBoton.callback(null);
        else if(txtConsulta.getText().length() > 0 && callBackCancelarBoton!=null && callBackHabilitarBoton!=null)
          callBackHabilitarBoton.callback(null);
      creaNavegacion();
  };

  @FXML
  private void btnImprimirListener(final ActionEvent event) {
    final String noCredito = this.txtConsulta.getText() != null ? this.txtConsulta.getText().trim()
        : Constantes.BLANK;
    this.generaReporteCaratula(noCredito);
  }

  private void generaReporteCaratula(final String datosCliente) {
    final RestCall<ReqProducto, ResImpresionCaratulaCred> call = new RestCall<ReqProducto, ResImpresionCaratulaCred>();
    call.setUrl(MainEntry.getUrl() + UrlConstantes.REIMPRESION_CARAT);

    ReqProducto req = this.getReqProducto(datosCliente);
    req.setProducto(cmbProductos.getSelectionModel().getSelectedItem().getDescripcion());
    req.setNombreCliente(this.respuestaCaratula.getNombreCliente());
    req.setNumeroCliente(respuestaCaratula.getNumeroCliente());
    req.setFechaNacimiento(respuestaCaratula.getFechaNacimiento());
    req.setRfc(respuestaCaratula.getRfc());
    req.setMontCredito(respuestaCaratula.getImporteAsignado());
    req.setMontoTotal(respuestaCaratula.getMontoTotalCredito());
    req.setFormaPago(respuestaCaratula.getFormaPago());
    req.setFechaLimPago(respuestaCaratula.getDiaCorte() + " de cada mes");
    req.setPlazo(this.respuestaCaratula.getPlazo());
    req.setFechaOperacion(respuestaCaratula.getFechOperacion());
    req.setFolioOperacion(respuestaCaratula.getNumCredito());
    req.setCapacidadPrestamo(respuestaCaratula.getCapacidadPres());
    req.setTasaOrdinaria(respuestaCaratula.getInteresOrdinario());
    req.setCat(respuestaCaratula.getIva());
    req.setTasaMoratoria(respuestaCaratula.getInteresMoratorio());
    // subReporte
    // req.setFechaAbono(Utilerias.getStringDate(respuestaCaratula.getPlazoAbonos().get(0).getFechaCuota(),
    // "dd/MM/yyyy", Constantes.FORMATO_FECHA, false, false));
    // req.setNumMesesPago(Utilerias.getCurrency(respuestaCaratula.getPlazoAbonos().get(0).getNumMesesPago()));
    // req.setPagoMinimo(Utilerias.getCurrency(respuestaCaratula.getPlazoAbonos().get(0).getMensualidad()));
    // req.setSaldoInsoluto(Utilerias.getCurrency(respuestaCaratula.getPlazoAbonos().get(0).getSaldoFinal()));
    // req.setFechaAbono(respuestaCaratula.getPlazoAbonos().get(0).getFechaCuota());
    // req.setPagoMinimo(respuestaCaratula.getPlazoAbonos().get(0).getMensualidad());

    call.setEntrada(req);
    call.setSalida(ResImpresionCaratulaCred.class);
    call.setEncrypted(false);
    call.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
      @Override
      public void handle(final WorkerStateEvent eventParam) {
        final ResImpresionCaratulaCred respuesta = call.getValue();
        if (respuesta.getArchivo() != null) {
          modal.showParameters(MenuData.REPORTE_MODAL_SAMESTAGE,
              new Object[] { respuesta.getArchivo() },
              NavegacionService.getInstance().getSolserMenuActual().getNombre());
        }

      };
    });
    call.setOnFailed(this.onFailed);
    call.reset();
    call.start();

  }

  @FXML
  private void txtConsultaListener(KeyEvent event) {
    if (txtConsulta.getText().length() < 0)
      btnBuscar.setDisable(false);
  }

  @FXML
  private void btnCancelarListener() {

    cmbProductos.setDisable(Boolean.FALSE);
    cmbProductos.clearSelection();
    txtConsulta.setText(Constantes.BLANK);
    txtNcliente.setText("");
    txtNomcliente.setText("");
    txtimpAsignado.setText("");
    txtPlazo.setText("");
    txtDcorte.setText("");
    txtDivisa.setText("");
    txtFecha.setText("");
    btnCancelar.setDisable(true);
    btnBuscar.setDisable(true);
    btnImprimir.setDisable(true);
  }

  private void reiniciarPantalla() {
    btnCancelarListener();
    creaNavegacion();
  }

  @FXML
  private void btnSalirListener() {
    this.getSalir().callback(this.confirmacionSalir);
  }

  public ChangeListener<String> getListener() {
    return listener;
  }

  public void setListener(ChangeListener<String> listener) {
    this.listener = listener;
  }
}
