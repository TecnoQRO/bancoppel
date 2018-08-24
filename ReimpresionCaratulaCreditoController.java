package mx.com.solser.fx.controller.core.mantoservicios;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import mx.com.solser.controls.SButton;
import mx.com.solser.controls.SChoiceBox;
import mx.com.solser.controls.STextField;
import mx.com.solser.controls.api.NavegacionControlesService;
import mx.com.solser.fx.config.data.AbstractPanelFactoryWizard;
import mx.com.solser.fx.enums.AltaUnicaAccionEnum;


public class ReimpresionCaratulaCreditoController extends AbstractPanelFactoryWizard{
  @FXML
  private AnchorPane panelNavegable;

  @FXML
  private SChoiceBox<String> cmbProducto;

  @FXML
  private STextField txtNumCredito;

  @FXML
  private STextField txtNumCliente;

  @FXML
  private STextField  txtNomCliente;

  @FXML
  private STextField txtImporteAsignado;

  @FXML
  private STextField txtPlazo;

  @FXML
  private STextField txtDiaCorte;

  @FXML
  private STextField txtDivisa;

  @FXML
  private STextField txtFechaOperacion;

  @FXML
  private SButton btnAceptar;

  @FXML
  private SButton btnCancelar;

  @FXML
  private SButton btnSalir;



    @FXML
    void btnAceptarListener() {

    }

    @FXML
    void btnCancelarListener() {

    }

    @FXML
    void btnSalirListener() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
      // TODO Auto-generated method stub
      cmbProducto.setItemsS(FXCollections.observableArrayList("Tarjeta de Credito VISA","Tarjeta de Credito VISA Electron"));
      
    }

    @Override
    public <T> T getBean() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public void resetView() {
      NavegacionControlesService.getInstance().creaNavegacion();
      
    }

    @Override
    public void guardaInformacion(AltaUnicaAccionEnum accion) {
      // TODO Auto-generated method stub
      
    }
}