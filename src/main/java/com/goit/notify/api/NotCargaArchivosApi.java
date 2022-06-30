package com.goit.notify.api;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.goit.notify.bo.INotCargaArchivosBO;
import com.goit.notify.dto.ResponseOk;
import com.goit.notify.dto.UsuarioLogin;
import com.goit.notify.exceptions.BOException;
import com.goit.notify.exceptions.CustomExceptionHandler;
import com.goit.notify.util.MensajesUtil;

@RestController
@RequestMapping("/carga-archivo")
public class NotCargaArchivosApi {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(NotCargaArchivosApi.class);
	
	@Autowired
	private INotCargaArchivosBO objINotCargaArchivosBO;
	
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> postCargaArchivo(
			@RequestHeader(value = "Accept-Language", required = false) String strLanguage,
			// @RequestBody List<RequestCargaArchivosDTO> lsRequestCargaArchivosDTO,
			@RequestPart(name = "archivoAdjunto", required = true) MultipartFile[] archivoPlantilla,
			@RequestPart(name = "tipoAdjunto", required = true) String tipoAdjunto,
			@RequestParam(value = "idPlantilla", required = false) Integer intIdPlantilla)
			throws BOException, ClassNotFoundException, IOException {
		
		try {
			logger.info("Peticion entrante de Guardado de Archivos....");
			UsuarioLogin userLogin = (UsuarioLogin) SecurityContextHolder.getContext()
					.getAuthentication()
					.getPrincipal();

			return new ResponseEntity<>(new ResponseOk(
					MensajesUtil.getMensaje("not.response.ok", MensajesUtil.validateSupportedLocale(strLanguage)), objINotCargaArchivosBO.cargaArchivos(archivoPlantilla,tipoAdjunto, intIdPlantilla, userLogin)),
					HttpStatus.OK);
		} catch (BOException e) {
			logger.error(" ERROR => " + e.getTranslatedMessage(strLanguage));
			throw new CustomExceptionHandler(e.getTranslatedMessage(strLanguage), e.getData());
		}
		
		
	
      	
		
	}
	
	@RequestMapping(value = "/obtener-archivo",method = RequestMethod.POST)
	public ResponseEntity<?> getCargaArchivo(
			@RequestHeader(value = "Accept-Language", required = false) String strLanguage,
			@RequestPart(name = "archivoAdjunto", required = true) String archivoAdjunto,
			@RequestPart(name = "tipoAdjunto", required = true) String tipoAdjunto)
			throws BOException, ClassNotFoundException, IOException {
		try {
			logger.info("Peticion entrante de Cargado de Archivos....");
			UsuarioLogin userLogin = (UsuarioLogin) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();

			return new ResponseEntity<>(new ResponseOk(
					MensajesUtil.getMensaje("not.response.ok", MensajesUtil.validateSupportedLocale(strLanguage)),
					objINotCargaArchivosBO.getCargaArchivos(archivoAdjunto, tipoAdjunto, userLogin)), HttpStatus.OK);
		} catch (BOException e) {
			logger.error(" ERROR => " + e.getTranslatedMessage(strLanguage));
			throw new CustomExceptionHandler(e.getTranslatedMessage(strLanguage), e.getData());
		}
	}

}
