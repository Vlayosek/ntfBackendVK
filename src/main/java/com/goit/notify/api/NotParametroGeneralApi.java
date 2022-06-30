package com.goit.notify.api;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.goit.notify.bo.INotParametroGeneralBO;
import com.goit.notify.dto.ResponseOk;
import com.goit.notify.exceptions.BOException;
import com.goit.notify.exceptions.CustomExceptionHandler;
import com.goit.notify.util.MensajesUtil;

@RestController
@RequestMapping("/parametro-general")
public class NotParametroGeneralApi {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(NotDestinatariosApi.class);

	@Autowired
	private INotParametroGeneralBO objINotParametroGeneralBO;
	
	/*
	 * @RequestMapping(method = RequestMethod.GET) public ResponseEntity<?>
	 * consultarParametrosGenerales(
	 * 
	 * @RequestHeader(value = "Accept-Language", required = false) String
	 * strLanguage) throws BOException, ClassNotFoundException, IOException { try {
	 * logger.info("Peticion de Parametros Generales entrante...."); return new
	 * ResponseEntity<>(new ResponseOk( MensajesUtil.getMensaje("not.response.ok",
	 * MensajesUtil.validateSupportedLocale(strLanguage)),
	 * objINotParametroGeneralBO.consultarParametrosGenerales()), HttpStatus.OK); }
	 * catch (BOException be) { logger.error(" ERROR => " +
	 * be.getTranslatedMessage(strLanguage)); throw new
	 * CustomExceptionHandler(be.getTranslatedMessage(strLanguage), be.getData()); }
	 * 
	 * }
	 */

	@RequestMapping(value = "/parametroXId", method = RequestMethod.GET)
	public ResponseEntity<?> consultarParametrosGeneralesXId(
			@RequestHeader(value = "Accept-Language", required = false) String strLanguage,
			@RequestParam(value = "IdParametroGeneral", required = false) Integer intIdParametroGeneral)
			throws BOException, ClassNotFoundException, IOException {
		try {
			logger.info("Peticion entrante de Parametros generales Por ID ....");
			return new ResponseEntity<>(
					new ResponseOk(
							MensajesUtil.getMensaje("not.response.ok",
									MensajesUtil.validateSupportedLocale(strLanguage)),
							objINotParametroGeneralBO.getParametroGeneralXId(intIdParametroGeneral)),
					HttpStatus.OK);
		} catch (BOException be) {
			logger.error(" ERROR => " + be.getTranslatedMessage(strLanguage));
			throw new CustomExceptionHandler(be.getTranslatedMessage(strLanguage), be.getData());
		}

	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> consultarParametrosGeneralesXNombre(
			@RequestHeader(value = "Accept-Language", required = false) String strLanguage,
			@RequestParam(value = "nombreParametroGeneral", required = false) String strNombreParametroGeneral)
			throws BOException, ClassNotFoundException, IOException {
		try {
			logger.info("Peticion entrante de Parametros generales Por ID ....");
			return new ResponseEntity<>(
					new ResponseOk(
							MensajesUtil.getMensaje("not.response.ok",
									MensajesUtil.validateSupportedLocale(strLanguage)),
							objINotParametroGeneralBO.getParametroGeneralXName(strNombreParametroGeneral)),
					HttpStatus.OK);
		} catch (BOException be) {
			logger.error(" ERROR => " + be.getTranslatedMessage(strLanguage));
			throw new CustomExceptionHandler(be.getTranslatedMessage(strLanguage), be.getData());
		}

	}
	

}
