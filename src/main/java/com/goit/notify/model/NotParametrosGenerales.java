package com.goit.notify.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name= "NOT_PARAMETROS_GENERALES")
public class NotParametrosGenerales  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PARAMETRO_GENERAL")
    private Integer idParametroGeneral;
	
	@Size(max=500)
	@Column(name = "DESCRIPCION")
	private String descripcion;

	@Size(max=1)
	@Column(name = "ESTADO")
	private String estado;
	
	@Column(name = "FECHA_CREACION")
	private String fechaCreacion;
	
	@Column(name = "FECHA_ACTUALIZACION")
	private String fechActualizacion;
	
	@Column(name = "FECHA_INACTIVACION")
	private String fechaInactivacion;
	
	@Size(max=100)
	@Column(name = "NOMBRE")
	private String nombre;
	
	@Size(max=10)
	@Column(name = "TIPO_VALOR")
	private String tipoValor;
	
	@Size(max=200)
	@Column(name = "VALOR")
	private String valor;
	
}
