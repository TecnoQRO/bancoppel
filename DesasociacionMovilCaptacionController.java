package mx.com.solser.fx.controller.core.mantoservicios.desasociacionmovilcaptacion;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import lombok.extern.log4j.Log4j2;
import mx.com.solser.controls.SButton;
import mx.com.solser.controls.STableViewPaginacion;
import mx.com.solser.controls.api.FormatosCampos.Formato;
import mx.com.solser.controls.table.AlingEnum;
import mx.com.solser.cplasu.beans.dto.Cliente;
import mx.com.solser.cplasu.beans.dto.ConsCtasClienteBean;
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
public class DesasociacionMovilCaptacionController extends AbstractPanelFactory {
  
  @FXML 
  private HBox  panelBusqueda;
  private Boolean  vieneOficinaDigital;
  private BusquedaClientesController clienteController;
  
  @FXML 
  private SButton btnCancelar;
  
  @FXML
  private SButton btnSalir;
  
  @FXML
  private Label  lblNomCliente;
  
  @FXML
  private AnchorPane panelNavegable;

  @FXML
  private STableViewPaginacion<ConsCtasClienteBean> tblConsulta;

  private Cliente cliente;
 
  private SolserModalSameStage modal  = SolserApplicationConfig.getSolserModalSameStage();
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    vieneOficinaDigital = Boolean.FALSE;
    tblConsulta.setSize(897, 220);
    tblConsulta.setLimitWidth(897);
    tblConsulta.showPagination(Boolean.TRUE, Boolean.FALSE);
    tblConsulta.addColumnTextField("cta", resources.getString("ascnomovilcap.colProducto"), 300, AlingEnum.LEFT, AlingEnum.RIGHT, Formato.JUST_NUMBER, false);
    tblConsulta.addColumnTextField("cta", resources.getString("ascnomovilcap.colCuenta"), 200, AlingEnum.LEFT, AlingEnum.RIGHT, Formato.JUST_NUMBER, false);
    tblConsulta.addColumnTextField("cta", resources.getString("ascnomovilcap.colEstatus"), 100, AlingEnum.LEFT, AlingEnum.RIGHT, Formato.JUST_NUMBER, false);
    tblConsulta.addColumnTextField("cta", resources.getString("ascnomovilcap.colNumMovil"), 200, AlingEnum.LEFT, AlingEnum.RIGHT, Formato.JUST_NUMBER, false);

  }
 
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
      lblNomCliente.setText(cliente.getNombreCompleto() == null ? Constantes.BLANK : cliente.getNombreCompleto());
    }
  private void reiniciarPantalla() {
    btnCancelarListener();
    creaNavegacion();
  }
  
  @FXML 
  private void btnCancelarListener(){
//    registroInico = 0;
    if (!vieneOficinaDigital) {
      lblNomCliente.setText(Constantes.BLANK);
      clienteController.resetView();
      creaNavegacion();
    
    }
    
  }
  
  @FXML
  private void btnSalirListener() {
    this.getSalir().callback(this.confirmacionSalir);
  }

  @Override
  public void resetView() {
   log.info("Load Busqueda de Clientes");
   this.loadSearchClientView();
   log.info("Initialize DesasociacionMovilCaptacionController ");
   if (!vieneOficinaDigital) {
    panelBusqueda.setVisible(Boolean.TRUE);
    clienteController.resetView();
  }
  
  }
  
}
