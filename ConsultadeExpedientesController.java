package mx.com.solser.fx.controller.core.mantoservicios;

import java.net.URL;



import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import mx.com.solser.controls.SButton;
import mx.com.solser.controls.STableViewPaginacion;
import mx.com.solser.controls.api.FormatosCampos.Formato;
import mx.com.solser.controls.table.AlingEnum;
import mx.com.solser.cplasu.beans.dto.ConsCtasClienteBean;
import mx.com.solser.fx.config.data.AbstractPanelFactory;

public class ConsultadeExpedientesController extends AbstractPanelFactory {

  
  @FXML
  private AnchorPane panelNavegable;

  @FXML
  private HBox paneBusqueda;

  @FXML
  private STableViewPaginacion<ConsCtasClienteBean> tblConsulta;
  
  @FXML
  private SButton btnAceptar;

  @FXML
  private SButton btnCancelar;

  @FXML
  private SButton btnSalir;

  
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    tblConsulta.setSize(897, 220);
    creaNavegacion();
    tblConsulta.showPagination(Boolean.TRUE, Boolean.FALSE);
    tblConsulta.addColumnTextField("cta", resources.getString("consexp.colNoCliente"), 100, AlingEnum.LEFT, AlingEnum.RIGHT, Formato.JUST_NUMBER, false);
    tblConsulta.addColumnTextField("cta", resources.getString("consexp.colNomCliente"), 100, AlingEnum.LEFT, AlingEnum.RIGHT, Formato.JUST_NUMBER, false);
    tblConsulta.addColumnTextField("cta", resources.getString("consexp.colUsuario"), 300, AlingEnum.LEFT, AlingEnum.RIGHT, Formato.JUST_NUMBER, false);
    tblConsulta.addColumnTextField("cta", resources.getString("consexp.colRevisado"), 100, AlingEnum.LEFT, AlingEnum.RIGHT, Formato.JUST_NUMBER, false);
    tblConsulta.addColumnTextField("cta", resources.getString("consexp.colEstatus"), 100, AlingEnum.LEFT, AlingEnum.RIGHT, Formato.JUST_NUMBER, false);
    tblConsulta.addColumnTextField("cta", resources.getString("consexp.colFechaAlta"), 100, AlingEnum.LEFT, AlingEnum.RIGHT, Formato.JUST_NUMBER, false);
    tblConsulta.addColumnTextField("cta", resources.getString("consexp.colConsExp"), 100, AlingEnum.LEFT, AlingEnum.RIGHT, Formato.JUST_NUMBER, false);
    
  }
 
  
  
  @FXML
  private void btnBuscarListener() {

  }

  @FXML
  private void btnCancelarListener() {

  }
  @FXML
  private void btnSalirListener() {
    this.getSalir().callback(this.confirmacionSalir);
  }
  
}
