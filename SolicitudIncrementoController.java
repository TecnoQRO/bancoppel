
package mx.com.solser.fx.controller.core.mantoservicios;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import lombok.extern.log4j.Log4j2;
import mx.com.solser.controls.SButton;
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
public class SolicitudIncrementoController extends AbstractPanelFactory{
  

  @FXML
  private AnchorPane panelNavegable;

  @FXML
  private HBox panelBusqueda;

  @FXML
  private AnchorPane pnBase;
  @FXML
  private SButton btnCancelar, btnSalir, btnAceptar;
  
  private BusquedaClientesController clienteController;
  private Cliente cliente;
  private Boolean bandera;
  @FXML
  private SolserModalSameStage modal = SolserApplicationConfig.getSolserModalSameStage();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    btnAceptar.setDisable(true);
    bandera = Boolean.FALSE;
  }

  private void loadSearchClientView() {
    final Object[] loader = SFxmlLoader.createLoaderSystem(MenuData.BUSQUEDA_CLIENTES);
    final AnchorPane anchor = (AnchorPane)loader[Constantes.CERO];
    panelBusqueda.getChildren().add(anchor);
    creaNavegacion();
    clienteController = (BusquedaClientesController) loader[Constantes.UNO];
    clienteController.searchBy(true, true , true, false , false);
    clienteController.setCallback(callback);
    clienteController.setCallBackCancelarBoton(callBackInhabilitarBotonCancelar);
    clienteController.setCallBackHabilitarBoton(callBackHabilitarBotonCancelar);
  }

  private Callback callBackInhabilitarBotonCancelar = new Callback() {

    @Override
    public void callback(Object object) {
      btnAceptar.setDisable(true);
      btnCancelar.setDisable(true);
      creaNavegacion();

    }
  };
  private Callback callBackHabilitarBotonCancelar = new Callback() {

    @Override
    public void callback(Object object) {
      btnCancelar.setDisable(false);
      creaNavegacion();
      

    }
  };
  private Callback callback = new Callback() {
    @Override
    public void callback(Object object) {
      cliente = (Cliente) object;
      clienteController.setDisabledPanel(Boolean.TRUE);
      if (cliente != null) {
        validaHuellas(cliente, cliente.getNumCliente(), MensajesHardCoded.CLIENTE_PRESENTE,
            Constantes.TITULO_MODAL_HUELLA_CLIENTE);
      } else {
        reiniciarPantalla();
      }
    }
  };
  
  
  
  

  private void validaHuellas(final Cliente cliente, final String numeroCliente,
      final MensajesHardCoded mensaje, final String tituloModal) {
    final String numGerente = SecurityUtils.getSession().getNumeroGerente();
    modal.showParameters(MenuData.MESSAGESSAMESTAGE, mensaje.getConfig(), null, new Callback() {
      @Override
      public void callback(Object object) {
        if ((Boolean) object) {
          if (!"0".equals(numeroCliente)) {
            ValidadorHuellaService.getInstance().validaHuellaCliente(numeroCliente,
                iniciaCallbackCliente(cliente));
          } else {
            ValidadorHuellaService.getInstance().validaHuellaEmpleado(numGerente,
                iniciaCallbackCliente(cliente), iniciaCallbackHuellaInvalida());
          }
        } else if (MensajesHardCoded.CLIENTE_PRESENTE.equals(mensaje)) {
          validaHuellas(cliente, "0", MensajesHardCoded.GERENTE_PRESENTE,
              Constantes.TITULO_MODAL_HUELLA_GERENTE);
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

  private Callback iniciaCallbackCliente(final Cliente c) {
    final Callback clienteCallback = new Callback() {
      @Override
      public void callback(final Object object) {
        if (object != null && object instanceof Boolean && (Boolean) object) {
          btnAceptar.setDisable(false);
          
        } else {
          reiniciarPantalla();
        }
      }
    };
    return clienteCallback;
  }


  private void reiniciarPantalla() {
    btnCancelarListener();
    creaNavegacion();
  }

  
  @FXML
  private void btnCancelarListener() {
    // registroInico = 0;
    if (!bandera) {
      clienteController.resetView();
      creaNavegacion();

    }

  }

  
  

  @FXML
  void btnAceptarListener(ActionEvent event) {
    if(this.cliente != null){
    }

  }



  @FXML
  void btnSalirListener() {
    this.getSalir().callback(this.confirmacionSalir);

  }
  

  @Override
  public void resetView() {
    log.info("Load Busqueda de Clientes");
    this.loadSearchClientView();
    log.info("Initialize BloqueoDesbloqueoCancelacionBancaController ");
    if (!bandera) {
      panelBusqueda.setVisible(Boolean.TRUE);
      clienteController.resetView();
    }

  }


}