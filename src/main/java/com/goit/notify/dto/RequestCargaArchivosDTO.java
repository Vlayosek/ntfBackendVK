package com.goit.notify.dto;

import lombok.Data;

@Data
public class RequestCargaArchivosDTO {
	
	private String nombreArchivo;
    private String archivoBase64;
    //private String fullFilePath;

}
