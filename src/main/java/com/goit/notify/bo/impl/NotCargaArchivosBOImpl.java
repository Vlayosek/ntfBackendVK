package com.goit.notify.bo.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.goit.helper.enums.NemonicoParametros;
import com.goit.notify.bo.INotCargaArchivosBO;
import com.goit.notify.dao.NotParametrosGeneralesDAO;
import com.goit.notify.dto.RequestCargaArchivosDTO;
import com.goit.notify.dto.UsuarioLogin;
import com.goit.notify.exceptions.BOException;
import com.goit.notify.util.ServiciosUtil;
import com.goit.notify.util.ValidacionUtil;

@Service
public class NotCargaArchivosBOImpl implements INotCargaArchivosBO{
	
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(NotCargaArchivosBOImpl.class);
	
	@Autowired
    private NotParametrosGeneralesDAO objNotParametrosGeneralesDAO;
	
	@Autowired
	private NotAdjuntoPlantillaBOImpl objNotAdjuntoPlantillaBOImpl;
	
	@Autowired
	private NotImagenesUsuarioBOImpl objNotImagenesUsuarioBOImpl;


	@Override
	public Map<String, Object> postCargaArchivos(List<RequestCargaArchivosDTO> lsRequestCargaArchivosDTO,
			UsuarioLogin usuario) throws BOException, ClassNotFoundException, IOException {

		
		String resSetDocumento = null;
		for (RequestCargaArchivosDTO requestCargaArchivosDTO : lsRequestCargaArchivosDTO) {
			// Valida campos requeridos
			ValidacionUtil.validarCampoRequeridoBO(requestCargaArchivosDTO.getNombreArchivo(),
					"not.campo.nombreArchivo");
			ValidacionUtil.validarCampoRequeridoBO(requestCargaArchivosDTO.getArchivoBase64(),
					"not.campo.archivoBase64");

			// obtengo el tamaño del archivo en bytes
			Integer size = tamañoArchivo(requestCargaArchivosDTO.getArchivoBase64());
			logger.info("Tamaño del achivo en bytes :" + size);

			// Convertir bytes a kb o mb
			String size2 = bytesToKB(size);
			logger.info("Tamaño del achivo en Kb :" + size2);

			String maxAttachFileSize = objNotParametrosGeneralesDAO
					.findYValidar(NemonicoParametros.MAX_ATTACH_FILE_SIZE).getValor();

			if (Float.parseFloat(size2) > Float.parseFloat(maxAttachFileSize)) {
				throw new BOException("not.warn.superaLimite",
						new Object[] { "not.campo.archivoBase64", maxAttachFileSize + " Kb" },
						"Tamaño del archivo: " + size2);
			}
			
			String extension = FilenameUtils
					.getExtension(requestCargaArchivosDTO.getNombreArchivo());
			
			
			//Consulta Extensiones Prohibidas
     		String extProhibidas = objNotParametrosGeneralesDAO
     				.findYValidar(NemonicoParametros.EXTENSIONES_PROHIBIDAS)
     				.getValor();
     		
     		String[] strExt = extProhibidas
     				.toUpperCase()
     				.split(",");
     		
     		//Valida las estensiones
	   		 if (Arrays.stream(strExt).anyMatch(StringUtils.upperCase(extension)::equals))
				throw new BOException("not.warn.campoInvalido", new Object[]{"not.campo.extension"});
			
			//String[] arrTipoExtensiones = {"HTML", "TXT", "XMLS"};
     		
    		 //Valida las estensiones
    		 //if (!Arrays.stream(arrTipoExtensiones).anyMatch(StringUtils.upperCase(requestCargaArchivosDTO.getExtension().toUpperCase())::equals))
				//throw new BOException("not.warn.campoInvalido", new Object[]{"not.campo.extension"});

			// Consulta base url
			String urlPusher = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.URL_PUSHER).getValor();
			// consulta path donde se cargaran los adjuntos
			String uploadFilePath = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.PATH_UPLOAD_FILE)
					.getValor();

			ServiciosUtil objServiciosUtil = new ServiciosUtil();
			resSetDocumento =  objServiciosUtil.guardarAdjunto(urlPusher, requestCargaArchivosDTO.getNombreArchivo(),
					requestCargaArchivosDTO.getArchivoBase64(), uploadFilePath);
			
		}
		
		HashMap<String,Object> mapper=new HashMap<String,Object>() ;
		mapper.put("msg", resSetDocumento);
		mapper.values();

        
        return mapper;
	}
	
	/**
	 * Convierte el tamaño de Bytes a Kb
     * @author Vladimir Kozisck
     * @param bytes
     * @return
     */
    public static String bytesToKB(long bytes) {  
		BigDecimal filesize = new BigDecimal(bytes);  		
		BigDecimal kilobyte = new BigDecimal(1024);  
		float returnValue = filesize.divide(kilobyte, 1, BigDecimal.ROUND_DOWN).floatValue();  
		return (String.valueOf(returnValue));
	} 
    
    /**
     * Obtiene el tamaño del archivo en bytes
     * @author Vladimir Kozisck
     * @param imageBase64Str
     * @return
     */
    public static Integer tamañoArchivo(String imageBase64Str){

		 // 1. Encuentra el signo igual y elimina el signo igual (= usado para llenar la longitud de la cadena base64)
		Integer equalIndex= imageBase64Str.indexOf("=");
		if(imageBase64Str.indexOf("=")>0) {
			imageBase64Str=imageBase64Str.substring(0, equalIndex);
		}
		 // 2. El tamaño del flujo de caracteres original en bytes
		Integer strLength=imageBase64Str.length();
		logger.info("imageBase64Str Length :" +  strLength);
		 // 3. El tamaño del flujo de archivos obtenido después del cálculo, en bytes
		Integer size=strLength-(strLength/8)*2;
		return size;
	}

	@Override
	public Map<String, Object> getCargaArchivos(String archivoAdjunto, String tipoAdjunto, UsuarioLogin userLogin)
			throws BOException, ClassNotFoundException, IOException{
		
		ValidacionUtil.validarCampoRequeridoBO(tipoAdjunto ,"not.campo.tipoAdjuntos");    
		ValidacionUtil.validarCampoRequeridoBO(archivoAdjunto, "not.campo.archivosAdjuntos");
		
	
			switch (tipoAdjunto) {
			case "A": 
				
				return obtenerArchivosAdjuntos(archivoAdjunto, userLogin);


			case "I":
				
				return null;
				//return obtenerArchivosImagenes(archivoAdjunto, userLogin);

			default:
				throw new BOException("not.warn.campoInvalido", new Object[] { "not.campo.tipoAdjuntos" });
			}
		
	}

	@Override
	public Map<String, Object> cargaArchivos(MultipartFile [] archivoPlantilla,String tipoAdjunto, Integer intIdPlantilla, UsuarioLogin usuario)
			throws BOException, ClassNotFoundException, IOException {
		
		//Valida campos requeridos
		ValidacionUtil.validarCampoRequeridoBO(tipoAdjunto ,"not.campo.tipoAdjuntos");    
		ValidacionUtil.validarCampoRequeridoBO(archivoPlantilla, "not.campo.archivosAdjuntos");
		
		if ( archivoPlantilla.equals(null))
			throw new BOException("not.warn.campoObligatorio", new Object[] { archivoPlantilla });
	
		
		switch (tipoAdjunto) {
		case "A": 
			// Valida campos requeridos
			ValidacionUtil.validarCampoRequeridoBO(intIdPlantilla, "not.campo.idPlantilla");
			
			return cargaArchivosAdjuntos(archivoPlantilla,tipoAdjunto, usuario,intIdPlantilla);


		case "I":
			
			
			return cargaArchivosImagenes(archivoPlantilla, usuario);

		default:
			throw new BOException("not.warn.campoInvalido", new Object[] { "not.campo.tipoAdjuntos" });
		}
		
		
	}


	@Override
	public Map<String, Object> cargaArchivosImagenes(MultipartFile[] archivoPlantilla, UsuarioLogin usuario)
			throws BOException, ClassNotFoundException, IOException {
		ValidacionUtil.validarCampoRequeridoBO(archivoPlantilla, "not.campo.archivosAdjuntos");

		if (!(archivoPlantilla.length > 0)) {
			throw new BOException("not.warn.campoObligatorio", new Object[] { archivoPlantilla });
		}

		String resSetDocumento = null;
		ServiciosUtil objServiciosUtil = null;
		List<String> lsStrAdjuntosNames = new ArrayList<String>();
		List<String> lsAdjuntos = new ArrayList<String>();
		String destinationUrl = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.DESTINATION_URL_ATTACH)
				.getValor();
		String str = destinationUrl.substring(0, destinationUrl.length() - 1);

		for (MultipartFile multipartFile : archivoPlantilla) {
			// String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String fileName = FilenameUtils.getName(multipartFile.getOriginalFilename().trim());

			System.out.println(fileName);

			String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());

			String[] arrTipoImagen = { "JPG", "PNG", "JPGE" };

			if (!Arrays.stream(arrTipoImagen).anyMatch(StringUtils.upperCase(extension)::equals))
				throw new BOException("not.warn.campoInvalido", new Object[] { "not.campo.extImagen" });

			// Obtengo el contenido del adjunto
			byte[] fileContent = multipartFile.getBytes();

			String encodedString = Base64.getEncoder().encodeToString(fileContent);

			// obtengo el tamaño del archivo en bytes
			Integer size = tamañoArchivo(encodedString);
			logger.info("Tamaño del achivo en bytes :" + size);

			// Convertir bytes a kb o mb
			String size2 = bytesToKB(size);
			logger.info("Tamaño del achivo en Kb :" + size2);

			String maxAttachFileSize = objNotParametrosGeneralesDAO
					.findYValidar(NemonicoParametros.MAX_ATTACH_FILE_SIZE).getValor();

			if (Float.parseFloat(size2) > Float.parseFloat(maxAttachFileSize)) {
				throw new BOException("not.warn.superaLimite",
						new Object[] { "not.campo.archivoBase64", maxAttachFileSize + " Kb" },
						"Tamaño del archivo: " + size2);
			}

			// Consulta base url
			String urlPusher = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.URL_PUSHER).getValor();

			// consulta path donde se cargaran los adjuntos
			String uploadFilePath = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.PATH_USER_IMAGE)
					.getValor();

			String finalPath = uploadFilePath.concat(usuario.getIdUsuario());

			objServiciosUtil = new ServiciosUtil();
			resSetDocumento = objServiciosUtil.guardarAdjunto(urlPusher, fileName, encodedString, finalPath);

			lsStrAdjuntosNames.add(fileName);

			String urlPublica = str.concat(uploadFilePath.concat(usuario.getIdUsuario()).concat("/").concat(fileName));

			lsAdjuntos.add(urlPublica);

		}

		HashMap<String, Object> mapper = new HashMap<String, Object>();

		if (resSetDocumento.equalsIgnoreCase("Documento escrito con exito.")) {
			objNotImagenesUsuarioBOImpl.postImagenUsuarioFromCarga(lsStrAdjuntosNames, usuario);
			mapper.put("row", lsAdjuntos);
		} else {
			mapper.put("msg", resSetDocumento);
			throw new BOException(resSetDocumento);

		}
		return mapper;
	}
	
	
	@Override
	public Map<String, Object> cargaArchivosAdjuntos(MultipartFile[] archivoPlantilla,String tipoAdjunto, UsuarioLogin usuario,Integer intIdPlantilla)
			throws BOException, ClassNotFoundException, IOException {

		
		String resSetDocumento = null;
		ServiciosUtil objServiciosUtil = null;
		List<String> lsStrAdjuntosNames = new ArrayList<String>();
		List<String> lsAdjuntos = new ArrayList<String>();
		Float size2 = null;
 		String destinationUrl = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.DESTINATION_URL_ATTACH).getValor();
 		
 		String str = destinationUrl.substring(0, destinationUrl.length()-1);
		
		for (MultipartFile multipartFile : archivoPlantilla) {
			
			//String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String fileName = FilenameUtils
					.getName(multipartFile
							.getOriginalFilename()
							.trim());
			
			System.out.println(fileName);
			
			String extension = FilenameUtils
					.getExtension(multipartFile
							.getOriginalFilename());	
			
			//Consulta Extensiones Prohibidas
     		String extProhibidas = objNotParametrosGeneralesDAO
     				.findYValidar(NemonicoParametros.EXTENSIONES_PROHIBIDAS)
     				.getValor();
     		
     		String[] strExt = extProhibidas
     				.toUpperCase()
     				.split(",");
     		
     		//Valida las estensiones
	   		 if (Arrays.stream(strExt).anyMatch(StringUtils.upperCase(extension)::equals))
				throw new BOException("not.warn.campoInvalido", new Object[]{"not.campo.extension"});
			
	   		 
			//Obtengo el contenido del adjunto
			byte[] fileContent = multipartFile
					.getBytes();
			
			String encodedString = Base64
					.getEncoder()
					.encodeToString(fileContent);
			

			// obtengo el tamaño del archivo en bytes
			Integer size = tamañoArchivo(encodedString);
			logger.info("Tamaño del achivo en bytes :" + size);

			// Convertir bytes a kb o mb
			size2 = Float.parseFloat(bytesToKB(size));
			logger.info("Tamaño del achivo en Kb :" + size2);

			String maxAttachFileSize = objNotParametrosGeneralesDAO
					.findYValidar(NemonicoParametros.MAX_IMAGES_FILE_SIZE)
					.getValor();

			if (size2 > Float.parseFloat(maxAttachFileSize)) {
				throw new BOException("not.warn.superaLimite",
						new Object[] { "not.campo.archivosAdjuntos", maxAttachFileSize + " Kb" },
						"Tamaño del archivo: " + size2);
			}
			
			// Consulta base url
			String urlPusher = objNotParametrosGeneralesDAO
					.findYValidar(NemonicoParametros.URL_PUSHER)
					.getValor();
			
			// consulta path donde se cargaran los adjuntos
			String uploadFilePath = objNotParametrosGeneralesDAO
					.findYValidar(NemonicoParametros.PATH_UPLOAD_FILE)
					.getValor();
			
			//a la ruta le concateno el id del usuario
			String finalPath = uploadFilePath.concat("/").concat(usuario.getIdUsuario());


			objServiciosUtil = new ServiciosUtil();
			resSetDocumento =  objServiciosUtil
					.guardarAdjunto(urlPusher, fileName,encodedString, finalPath);
			
					
			lsStrAdjuntosNames.add(fileName);
			
     		String urlPublica = str.concat(uploadFilePath.concat(usuario.getIdUsuario()).concat("/").concat(fileName));

			lsAdjuntos.add(urlPublica);
			
		}
		
		HashMap<String,Object> mapper=new HashMap<String,Object>() ;
		
		if(resSetDocumento.equalsIgnoreCase("Documento escrito con exito.")) {
			objNotAdjuntoPlantillaBOImpl.postAdjuntoPlantillaFromCarga(lsStrAdjuntosNames, intIdPlantilla, usuario, size2);
			mapper.put("row", lsAdjuntos);
		} else {
			mapper.put("msg", resSetDocumento);
			throw new BOException(resSetDocumento);

		}        
        return mapper;
	}
	
	@Override
	public Map<String, Object> obtenerArchivosAdjuntos(String archivoAdjunto, UsuarioLogin usuario)
			throws BOException, ClassNotFoundException, IOException {

		
		String resSetDocumento = null;
		ServiciosUtil objServiciosUtil = null;
		List<String> lsStrAdjuntosNames = new ArrayList<String>();
		
		
		//Obtengo la extension 
		//String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
 		String ext = FilenameUtils.getExtension(archivoAdjunto.trim());
		
		
		//Consulta Extensiones Prohibidas
 		String extProhibidas = objNotParametrosGeneralesDAO
 				.findYValidar(NemonicoParametros.EXTENSIONES_PROHIBIDAS)
 				.getValor();
 		
 		String[] strExt = extProhibidas
 				.toUpperCase()
 				.split(",");
 		
 		//Valida las estensiones
   		 if (Arrays.stream(strExt).anyMatch(StringUtils.upperCase(ext)::equals))
			throw new BOException("not.warn.campoInvalido", new Object[]{"not.campo.extension"});
				
		// Consulta base url
		String urlPusher = objNotParametrosGeneralesDAO
				.findYValidar(NemonicoParametros.URL_PUSHER_GET)
				.getValor();
		
		// consulta path donde se cargaran los adjuntos
		String uploadFilePath = objNotParametrosGeneralesDAO
				.findYValidar(NemonicoParametros.PATH_UPLOAD_FILE)
				.getValor();
		
		//a la ruta le concateno el id del usuario
		String finalPath = uploadFilePath.concat("/").concat(usuario.getIdUsuario()).concat("/").concat(archivoAdjunto);


		objServiciosUtil = new ServiciosUtil();
		resSetDocumento =  objServiciosUtil
				.obtenerAdjunto(urlPusher, finalPath);
		
		HashMap<String,Object> mapper=new HashMap<String,Object>() ;
		mapper.put("row", resSetDocumento);

        
        return mapper;
	}

}
