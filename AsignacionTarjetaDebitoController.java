package mx.com.solser.fx.controller.core.mantoservicios.asignaciontarjetadebito;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableRow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import lombok.extern.log4j.Log4j2;
import mx.com.solser.controls.SButton;
import mx.com.solser.controls.SCheckBox;
import mx.com.solser.controls.STableView;
import mx.com.solser.controls.api.FormatosCampos.Formato;
import mx.com.solser.controls.api.NavegacionControlesService;
import mx.com.solser.controls.table.AlingEnum;
import mx.com.solser.cplasu.beans.dto.Cliente;
import mx.com.solser.cplasu.beans.dto.altaunica.altaproductos.captacion.CuentasCliente;
import mx.com.solser.cplasu.beans.dto.tarjetas.ConsultaClienteTarjetaBean;
import mx.com.solser.cplasu.beans.req.tarjetas.ReqCancelaTarjeta;
import mx.com.solser.cplasu.beans.res.altaunica.productocaptacion.ResCuentasCliente;
import mx.com.solser.cplasu.beans.res.tarjetas.ResConsCtesTarWeb;
import mx.com.solser.cplasu.interfaces.Callback;
import mx.com.solser.cplasu.util.Constantes;
import mx.com.solser.fx.config.data.AbstractPanelFactory;
import mx.com.solser.fx.config.data.MenuData;
import mx.com.solser.fx.controller.genericos.BusquedaClientesController;
import mx.com.solser.fx.entrypoint.config.SFxmlLoader;
import mx.com.solser.fx.entrypoint.config.SolserApplicationConfig;
import mx.com.solser.fx.security.SecurityUtils;
import mx.com.solser.fx.service.huellas.ValidadorHuellaService;
import mx.com.solser.fx.service.mantoservicios.AsignacionTarjetaDebitoService;
import mx.com.solser.fx.service.mantoservicios.MantoServiciosService;
import mx.com.solser.model.ui.MensajesHardCoded;
import mx.com.solser.ui.controls.SolserModalSameStage;

@Log4j2
public class AsignacionTarjetaDebitoController extends AbstractPanelFactory {

  private SolserModalSameStage modal = SolserApplicationConfig.getSolserModalSameStage();
  private BusquedaClientesController controllerClientes;
  private Boolean vieneOficinaDigital;
  private Cliente cliente;
  private MantoServiciosService callService = new MantoServiciosService();
  private AsignacionTarjetaDebitoService service = new AsignacionTarjetaDebitoService();
  private ResourceBundle properties;
  
  @FXML
  private AnchorPane panelNavegable;

  @FXML
  private HBox panelBusqueda;

  @FXML
  private Label lblNomCliente;

  @FXML
  private Label lblRFC;

  @FXML
  private STableView<CuentasCliente> tblCuentas;

  @FXML
  private STableView<ConsultaClienteTarjetaBean> tblAsigDeb;

  @FXML
  private SCheckBox chkCobraComision;

  @FXML
  private SButton btnAsignar;

  @FXML
  private SButton btnAceptar;

  @FXML
  private SButton btnCancelar;

  @FXML
  private SButton btnSalir;

  @FXML
  private SButton btnLeerTarjeta;

  @FXML
  void btnAceptarListener() {

  }

  private void loadClientesView() {
    final Object[] loader = SFxmlLoader.createLoaderSystem(MenuData.BUSQUEDA_CLIENTES);
    final AnchorPane panel = (AnchorPane)loader[Constantes.CERO];
    panelBusqueda.getChildren().clear();
    panelBusqueda.getChildren().add(panel);
    controllerClientes = (BusquedaClientesController) loader[Constantes.UNO];
    controllerClientes.setCallback(callback);
    controllerClientes.setCallBackCancelarBoton(callbackCancelarBotonCancelar);
    controllerClientes.setCallBackHabilitarBoton(callbackhabilitarBotonCancelar);
    controllerClientes.setLoadView(loadViewCallback);
  }

  private Callback loadViewCallback = new Callback() {
    @Override
    public void callback(final Object object) {
      controllerClientes.searchBy(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE,
          Boolean.FALSE);
      NavegacionControlesService.getInstance().creaNavegacion();
    }
  };

  private Callback callback = new Callback() {
    @Override
    public void callback(Object object) {
      cliente = (Cliente) object;
      controllerClientes.setDisabledPanel(Boolean.TRUE);
      if (cliente != null) {
        validaHuellas(cliente, cliente.getNumCliente(), MensajesHardCoded.CLIENTE_PRESENTE,
            Constantes.TITULO_MODAL_HUELLA_CLIENTE);
      } else {
        cleanScreen();
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
          cleanScreen();
        }
      }
    });
  }

  private Callback iniciaCallbackCliente(final Cliente cliente) {
    return new Callback() {

      @Override
      public void callback(Object object) {
        if (object != null && object instanceof Boolean && (Boolean) object) {
          continuaConsultaCliente(cliente);
          callService.getAccounts(cliente, retornoCuentas());

        } else {
          cleanScreen();
        }
      }
    };

  }

  private Callback retornoCuentas() {
    return new Callback() {
      @Override
      public void callback(Object object) {
        if (object != null && object instanceof ResCuentasCliente) {
          fillAccountsTableView(((ResCuentasCliente) object).getCuentas());
        }
      }
    };
  }

  private final void fillAccountsTableView(final List<CuentasCliente> listAccounts) {
    if (listAccounts != null && !listAccounts.isEmpty()) {
      if (listAccounts.size() > 20) {
        for (int i = 0; i < 20; i++) {
          tblCuentas.getItems().add(listAccounts.get(i));
        }
      } else
        tblCuentas.getItems().addAll(listAccounts);
    }
  }

  private final void catchComponentEvents() {
    tblCuentas.setRowFactory(tv -> {
      final TableRow<CuentasCliente> row = new TableRow<>();
      row.setOnMouseClicked(event -> {
        if (event.getClickCount() == 1 && (!row.isEmpty())) {
          CuentasCliente accountDTO = row.getItem();
          final ReqCancelaTarjeta request = new ReqCancelaTarjeta(accountDTO.getCuenta(),
              cliente.getNumCliente());
          service.consultaTarjetaCliente(request, callbackRetorno());
        
        }
      });
      return row;
    });
  }

  private Callback callbackRetorno() {
    return new Callback() {
      @Override
      public void callback(Object object) {
        if (object != null && object instanceof ResConsCtesTarWeb) {
          ConsultaClienteTarjetaBean dto = ((ResConsCtesTarWeb) object).getTarjetaCliente();
          dto.setNombreCompleto(dto.getNombre1() + " " + dto.getNombre2() + " "
              + dto.getApellidoPaterno() + " " + dto.getApellidoMaterno());
          tblAsigDeb.clearTable();
          tblAsigDeb.getItems().add(dto);
          if(!tblAsigDeb.getItems().isEmpty()){
            ponFoco(tblAsigDeb);
            tblAsigDeb.getSelectionModel().selectFirst();
          }
        }
      }
    };
  }

  private Callback iniciaCallbackHuellaInvalida() {
    return new Callback() {

      @Override
      public void callback(Object object) {
        cleanScreen();
      }
    };
  }

  private void continuaConsultaCliente(final Cliente cliente) {
    lblNomCliente.setText(
        cliente.getNombreCompleto() == null ? Constantes.BLANK : cliente.getNombreCompleto());
    lblRFC.setText(cliente.getRfc() == null ? Constantes.BLANK : cliente.getRfc());
  }

  private Callback callbackCancelarBotonCancelar = new Callback() {
    @Override
    public void callback(Object object) {
      btnCancelar.setDisable(true);
      NavegacionControlesService.getInstance().creaNavegacion();
    }
  };

  private Callback callbackhabilitarBotonCancelar = new Callback() {
    @Override
    public void callback(Object object) {
      btnCancelar.setDisable(false);
      NavegacionControlesService.getInstance().creaNavegacion();
    }
  };

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    vieneOficinaDigital = Boolean.FALSE;
//    this.properties = resources;
    initTableViewAccounts(resources);
    initTableViewAccountInformation(resources);
    catchComponentEvents();
  }

  private final void initTableViewAccounts(final ResourceBundle properties) {
    tblCuentas.setLimitWidth(960);
    tblCuentas.setPrefHeight(185);
    tblCuentas.activateAutoNumeration();
    tblCuentas.addColumnTextField("cuenta", properties.getString("busquedagralcte.colNumCuenta"),
        913, AlingEnum.LEFT, Formato.NONE);
  }

  private final void initTableViewAccountInformation(final ResourceBundle properties) {
    tblAsigDeb.setLimitWidth(960);
    tblAsigDeb.activateAutoNumeration();
    tblAsigDeb.addColumnCheck("check", " ", 29, AlingEnum.CENTER);
    tblAsigDeb.addColumnTextField("nombreCompleto", properties.getString("asigtardeb.colNom"), 319,
        AlingEnum.LEFT, AlingEnum.CENTER, Formato.JUST_CHAR, false);
    tblAsigDeb.addColumnTextField("rfc", properties.getString("asigtardeb.colRfc"), 100,
        AlingEnum.LEFT, AlingEnum.RIGHT, Formato.JUST_ALPHA_NUM, false);
    tblAsigDeb.addColumnTextField("numTarjeta", properties.getString("asigtardeb.colNoTar"), 150,
        AlingEnum.LEFT, AlingEnum.RIGHT, Formato.JUST_NUMBER, false);
    tblAsigDeb.addColumnTextField("fechaVencimiento", properties.getString("asigtardeb.colExp"), 80,
        AlingEnum.RIGHT, AlingEnum.LEFT, Formato.JUST_ALPHA_NUM, false);
    tblAsigDeb.addColumnTextField("limiteRetiro", properties.getString("asigtardeb.colMonMax"), 150,
        AlingEnum.LEFT, AlingEnum.CENTER, Formato.JUST_ALPHA_NUM, false);
    tblAsigDeb.addColumnTextField("tipoCliente", properties.getString("asigtardeb.colTipoCliente"),
        100, AlingEnum.LEFT, AlingEnum.CENTER, Formato.JUST_ALPHA_NUM, false);
    tblAsigDeb.getTable().getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

  }

  @FXML
  void btnAsignarListener() {

  }

  @FXML
  public final void btnCancelarListener() {
    cleanScreen();
  }

  @FXML
  private void btnSalirListener() {
    this.getSalir().callback(this.confirmacionSalir);
  }

  private void cleanScreen() {
    this.loadClientesView();
    controllerClientes.resetView();
    lblNomCliente.setText(Constantes.BLANK);
    lblRFC.setText(Constantes.BLANK);
    cliente = new Cliente();
    btnCancelar.setDisable(true);
    tblCuentas.getItems().clear();
    tblAsigDeb.getItems().clear();
    NavegacionControlesService.getInstance().creaNavegacion();
  }

  @FXML
  void chkCobraComisionListener() {

  }

  @Override
  public void resetView() {
    this.cleanScreen();
  }

  public boolean isVieneOficinaDigital() {
    return this.vieneOficinaDigital;
  }

  public void actualizaNavegacion() {
    NavegacionControlesService.getInstance().creaNavegacion();
  }

}
