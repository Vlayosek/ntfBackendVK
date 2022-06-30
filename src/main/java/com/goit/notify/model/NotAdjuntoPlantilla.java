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
@Table(name = "NOT_ADJUNTOS_PLANTILLA")
public class NotAdjuntoPlantilla implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ADJUNTO_PLANTILLA")
    private Integer idAdjuntoPlantilla;

    @Column(name = "ID_PLANTILLA")
    private Integer idPlantilla;
    
    @Size(max=100)
    @Column(name = "ID_EMPRESA")
    private String idEmpresa;
    
    @Size(max=100)
    @Column(name = "ID_USUARIO")
    private String idUsuario;

    @Size(max=100)
    @Column(name = "NOMBRE_ARCHIVO")
    private String nombreArchivo;

    @Size(max=500)
    @Column(name = "RUTA_LOCAL")
    private String rutaLocal;

    @Size(max=45)
    @Column(name = "EXTENSION")
    private String extension;
    
    @Column(name = "PESO")
    private Float peso;

    @Size(max=1)
    @Column(name = "ESTADO")
    private String estado;

    @Size(max=45)
    @Column(name = "USUARIO_CREA")
    private String usuarioCrea;

    @Size(max=45)
    @Column(name = "USUARIO_ACTUALIZA")
    private String usuarioActualiza;

    @Size(max=45)
    @Column(name = "USUARIO_INACTIVA")
    private String usuarioInactiva;

    //@JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "FECHA_CREA")
    private String fechaCrea;

    //@JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "FECHA_ACTUALIZA")
    private String fechaActualiza;

    //@JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "FECHA_INACTIVA")
    private String fechaInactiva;

}
