package mx.com.solser.fx.controller.core.mantoservicios;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoContratoEnum {
//  FIXME Se requiere que se verifique si estos datos se consultan en un servicio
  CONTRATO("1","CONTRATO DE SERVICIO DE INTERNET"),CARATULA("2","CARÁTULA DE SERVICIO DE INTERNET");
  
  private String clave;
  private String descripcion;
  @Override
  public String toString() {
    return this.clave +"-"+this.descripcion;
  }
  
  
  
  
  

}
