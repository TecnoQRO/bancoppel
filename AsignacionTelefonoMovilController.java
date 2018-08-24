package mx.com.solser.fx.controller.core.mantoservicios;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import mx.com.solser.controls.SButton;
import mx.com.solser.controls.SChoiceBox;
import mx.com.solser.controls.STextField;
import mx.com.solser.cplasu.beans.dto.Cliente;
import mx.com.solser.cplasu.beans.dto.ConsCtasClienteBean;
import mx.com.solser.cplasu.util.Constantes;
import mx.com.solser.ui.controls.ModalBodyInOutControllerSameStage;

public class AsignacionTelefonoMovilController  extends ModalBodyInOutControllerSameStage<Cliente, Boolean>{

    @FXML
    private AnchorPane panelNavegable;

    @FXML
    private Label lblCliente;

    @FXML
    private STextField txtMovil;

    @FXML
    private SChoiceBox<ConsCtasClienteBean> cmbCompania;

    @FXML
    private SButton btnAceptar, btnSalir, btnCancelar;

    @FXML
    void btnAceptarListener() {

    }

    @FXML
    private void btnCancelarListener() {

    }

    @FXML
    private void btnSalirListener() {
      close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
      
    }

    @Override
    public void setEntrada(Cliente entrada) {
      if(entrada!=null)
        lblCliente.setText(entrada.getNumCliente() == null ? Constantes.BLANK : entrada.getNumCliente());
      
    }

    @Override
    public Optional<Boolean> getResponse() {
      return null;
    }

}
