package com.goit.notify.dto;

import lombok.Data;

@Data
public class GetParametrosGeneralesDTO {

    private Integer idParametroGeneral;
    private String descripcion;
    private String estado;
    private String nombre;
    private String tipoValor;
    private String valor;
}
