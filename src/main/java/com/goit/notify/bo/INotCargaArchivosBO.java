package com.goit.notify.bo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.multipart.MultipartFile;

import com.goit.notify.dto.RequestCargaArchivosDTO;
import com.goit.notify.dto.UsuarioLogin;
import com.goit.notify.exceptions.BOException;

public interface INotCargaArchivosBO {
	
	/**
	 * Api para la carga de Archivos en base64
	 * 
	 * @author Vladimir Kozisck
	 * @param lsRequestCargaArchivosDTO
	 * @param usuario
	 * @return
	 * @throws BOException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	Map<String, Object> postCargaArchivos(List<RequestCargaArchivosDTO> lsRequestCargaArchivosDTO, UsuarioLogin usuario) throws BOException, ClassNotFoundException, IOException;
	
	/**
	 * Api para la carga de archivos 
	 * 
	 * @author Vladimir Kozisck
	 * @param archivoPlantilla
	 * @param usuario
	 * @param tipoAdjunto
	 * @return
	 * @throws BOException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	Map<String, Object> cargaArchivos(MultipartFile[] archivoPlantilla,String tipoAdjunto, Integer intIdPlantilla, UsuarioLogin usuario) throws BOException, ClassNotFoundException, IOException;
	
	
	/**
	 * Api para la carga de imagenes de usuario
	 * 
	 * @author Vladimir Kozisck
	 * @param archivoPlantilla
	 * @param usuario
	 * @return
	 * @throws BOException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	Map<String, Object> cargaArchivosImagenes(MultipartFile[] archivoPlantilla, UsuarioLogin usuario) throws BOException, ClassNotFoundException, IOException;

	
	/**
	 * Api para obtener un adjunto
	 * 
	 * @author Vladimir Kozisck
	 * @param strFullFilePath
	 * @param userLogin
	 * @return
	 * @throws BOException
	 * @throws JSONException 
	 */
	Map<String, Object> getCargaArchivos(String archivoAdjunto, String tipoAdjunto, UsuarioLogin userLogin) throws BOException, ClassNotFoundException, IOException;

	Map<String, Object> cargaArchivosAdjuntos(MultipartFile[] archivoPlantilla, String tipoAdjunto,
			UsuarioLogin usuario, Integer intIdPlantilla) throws BOException, ClassNotFoundException, IOException;

	Map<String, Object> obtenerArchivosAdjuntos(String archivoAdjunto, UsuarioLogin usuario)
			throws BOException, ClassNotFoundException, IOException;
	
	


}
