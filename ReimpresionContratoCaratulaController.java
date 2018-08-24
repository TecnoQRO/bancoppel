package mx.com.solser.fx.controller.core.mantoservicios.reimpresioncontratocaratula;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import lombok.extern.log4j.Log4j2;
import mx.com.solser.controls.SButton;
import mx.com.solser.controls.SChoiceBox;
import mx.com.solser.controls.SRadioButton;
import mx.com.solser.cplasu.beans.dto.Cliente;
import mx.com.solser.cplasu.interfaces.Callback;
import mx.com.solser.cplasu.util.Constantes;
import mx.com.solser.fx.config.data.AbstractPanelFactory;
import mx.com.solser.fx.config.data.MenuData;
import mx.com.solser.fx.controller.genericos.BusquedaClientesController;
import mx.com.solser.fx.entrypoint.config.SFxmlLoader;
import mx.com.solser.fx.entrypoint.config.SolserApplicationConfig;
import mx.com.solser.fx.security.SecurityUtils;
import mx.com.solser.fx.service.huellas.ValidadorHuellaService;
import mx.com.solser.model.ui.MensajesHardCoded;
import mx.com.solser.ui.controls.SolserModalSameStage;

@Log4j2
public class ReimpresionContratoCaratulaController extends AbstractPanelFactory {



  @FXML
  private AnchorPane panelNavegable;

  @FXML
  private SRadioButton rdobtnCliente;

  @FXML
  private SRadioButton rdobtnBanco;

  @FXML
  private AnchorPane panelBusqueda;

  @FXML
  private Label lblNoCliente;

  @FXML
  private Label lblNomCliente;

  @FXML
  private Label lblTipoServicioBPI;

  @FXML
  private SChoiceBox<?> cmbSeleccioneDoc;

  @FXML
  private SButton btnImprimir;

  @FXML
  private SButton btnCancelar;

  @FXML
  private SButton btnSalir;
  private Cliente cliente;
  private Boolean  vieneOficinaDigital;
  private BusquedaClientesController clienteController;
  private SolserModalSameStage modal  = SolserApplicationConfig.getSolserModalSameStage();
  
  private void loadSearchClientView(){
    final Object[] loader = SFxmlLoader.createLoaderSystem(MenuData.BUSQUEDA_CLIENTES);
    final AnchorPane anchor = (AnchorPane)loader[Constantes.CERO];
    panelBusqueda.getChildren().add(anchor);
    creaNavegacion();
    clienteController = (BusquedaClientesController) loader[Constantes.UNO];
    clienteController.setCallback(callback);
    clienteController.setCallBackCancelarBoton(callBackInhabilitarBotonCancelar);
    clienteController.setCallBackHabilitarBoton(callBackHabilitarBotonCancelar);
  }
  private Callback callBackInhabilitarBotonCancelar = new Callback() {
    
    @Override
    public void callback(Object object) {
      btnCancelar.setDisable(Boolean.TRUE);
      creaNavegacion();
      
    }
  };
  private Callback callBackHabilitarBotonCancelar = new Callback() {
    
    @Override
    public void callback(Object object) {
      btnCancelar.setDisable(Boolean.FALSE);
      creaNavegacion();
      
    }
  };
  private Callback callback = new Callback() {
    @Override
    public void callback(Object object) {
      cliente = (Cliente) object;
      clienteController.setDisabledPanel(Boolean.TRUE);
      if (cliente != null) {
        validaHuellas(cliente, cliente.getNumCliente(), MensajesHardCoded.CLIENTE_PRESENTE, Constantes.TITULO_MODAL_HUELLA_CLIENTE);
      } else {
        reiniciarPantalla();
      }
  }
  };
    
  private void validaHuellas(final Cliente cliente, final String numeroCliente, final MensajesHardCoded mensaje, final String tituloModal) {
    final String numGerente = SecurityUtils.getSession().getNumeroGerente();
    modal.showParameters(MenuData.MESSAGESSAMESTAGE, mensaje.getConfig(), null, new Callback() {
      @Override
      public void callback(Object object) {
        if ((Boolean) object) {
          if (!"0".equals(numeroCliente)) {
            ValidadorHuellaService.getInstance().validaHuellaCliente(numeroCliente, iniciaCallbackCliente(cliente));
          } else {
            ValidadorHuellaService.getInstance().validaHuellaEmpleado(numGerente, iniciaCallbackCliente(cliente), iniciaCallbackHuellaInvalida());
          }
        } else if (MensajesHardCoded.CLIENTE_PRESENTE.equals(mensaje)) {
          validaHuellas(cliente, "0", MensajesHardCoded.GERENTE_PRESENTE, Constantes.TITULO_MODAL_HUELLA_GERENTE);
        } else if (MensajesHardCoded.GERENTE_PRESENTE.equals(mensaje)) {
          reiniciarPantalla();
        }
      }
    });
  }
  private Callback iniciaCallbackHuellaInvalida() {
    return new Callback() {

      @Override
      public void callback(Object object) {
        reiniciarPantalla();
      }
    };
  }
  
    private Callback iniciaCallbackCliente(final Cliente cliente) {
      final Callback clienteCallback = new Callback() {
        @Override
        public void callback(final Object object) {
          if (object != null && object instanceof Boolean && (Boolean) object) {
            continuaConsultaCliente(cliente);
          } else {
            reiniciarPantalla();
          }
        }
      };
      return clienteCallback;
    }
    private void continuaConsultaCliente(final Cliente cliente) {
      lblNoCliente.setText(cliente.getNumCliente() == null ? Constantes.BLANK : cliente.getNumCliente());
      lblNomCliente.setText(cliente.getNombreCompleto() == null ? Constantes.BLANK : cliente.getNombreCompleto());
      //lblTipoServicioBPI.setText(cliente.get == null ? Constantes.BLANK : cliente.getTipoServicioBPI());
    }
    
  private void reiniciarPantalla() {
    btnCancelarListener();
    creaNavegacion();
  }
  
  @FXML 
  private void btnCancelarListener(){
//    registroInico = 0;
    if (!vieneOficinaDigital) {
      lblNoCliente.setText(Constantes.BLANK);
      lblNomCliente.setText(Constantes.BLANK);
      lblTipoServicioBPI.setText(Constantes.BLANK);
      clienteController.resetView();
      creaNavegacion();
     }
  }
  
  @FXML
  private void btnSalirListener() {
    this.getSalir().callback(this.confirmacionSalir);
  }
  
  @FXML
  void btnImprimirListener() {
    this.modal.showParameters(MenuData.MESSAGESSAMESTAGE,MensajesHardCoded.ERROR_CONSULTARINFOCLIENTE .getConfig());
  }
  
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    vieneOficinaDigital = Boolean.FALSE;
    
  }
  
  @Override
  public void resetView() {
   log.info("Load Busqueda de Clientes");
   this.loadSearchClientView();
   log.info("Initialize reimpresioncontratocaratulacontroller ");
   if (!vieneOficinaDigital) {
    panelBusqueda.setVisible(Boolean.TRUE);
    clienteController.resetView();
  }
  
  }
}
