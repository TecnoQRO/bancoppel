package mx.com.solser.fx.controller.core.mantoservicios;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import mx.com.solser.controls.SButton;
import mx.com.solser.controls.SChoiceBox;
import mx.com.solser.controls.SDatePicker;
import mx.com.solser.controls.SRadioButton;
import mx.com.solser.controls.STextField;
import mx.com.solser.fx.config.data.AbstractPanelFactory;

public class MantenimientoCambioPerfilController extends AbstractPanelFactory {

    @FXML
    private AnchorPane panelNavegable;

    @FXML
    private SButton btnAceptar;

    @FXML
    private SButton btnCancelar;

    @FXML
    private SButton btnSalir;

    @FXML
    private STextField txtNoEmpleado;

    @FXML
    private SChoiceBox<?> cmbPuesto;

    @FXML
    private STextField txtNomEmpleado;

    @FXML
    private CheckBox chbxEnLinea;

    @FXML
    private Label lblNoCliente;

    @FXML
    private Label lblNomCliente;

    @FXML
    private Label lblEstatus;

    @FXML
    private Label lblNoCliente1;

    @FXML
    private Label lblMenu1;

    @FXML
    private Label lblMenu2;

    @FXML
    private Label lblMenu3;

    @FXML
    private Label lblMenu4;

    @FXML
    private Label lblMenu5;

    @FXML
    private Label lblMenu6;

    @FXML
    private Label lblMenu7;

    @FXML
    private Label lblMenu8;

    @FXML
    private Label lblMenu9;

    @FXML
    private Label lblMenu10;

    @FXML
    private SRadioButton rdobtnLector;

    @FXML
    private SRadioButton rdobtnCertificadora;

    @FXML
    private SRadioButton rdobtnPinPad;

    @FXML
    private SRadioButton rdobtnNoPuerto;

    @FXML
    private SDatePicker dtVtoPass;

    @FXML
    private SDatePicker dtUltiCambio;

    @FXML
    void btnAceptarListener() {

    }

    @FXML
    void btnCancelarListener() {

    }

    @FXML
    void btnSalirListener() {
      this.getSalir().callback(this.confirmacionSalir);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
      // TODO Auto-generated method stub
      
    }

}
