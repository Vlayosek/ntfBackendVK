package com.goit.notify.bo.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.goit.notify.bo.INotParametroGeneralBO;
import com.goit.notify.dao.NotParametrosGeneralesDAO;
import com.goit.notify.dao.NotPlantillasDAO;
import com.goit.notify.dto.GeneralParametrosGeneralesDTO;
import com.goit.notify.dto.GetParametrosGeneralesDTO;
import com.goit.notify.exceptions.BOException;
import com.goit.notify.model.NotParametrosGenerales;
import com.goit.notify.util.ValidacionUtil;

@Service
public class NotParametroGeneralBOImpl implements INotParametroGeneralBO {

	@Autowired
	private NotParametrosGeneralesDAO objNotParametrosGeneralesDAO;
	
	@Autowired
	private NotPlantillasDAO objNotPlantillasDAO;

	@Override

	public Map<String, Object> getParametroGeneralXId(Integer intParametroGeneral)
			throws BOException, ClassNotFoundException, IOException {
		// Valida que el ID Adjunto Plantilla sera requerido
		ValidacionUtil.validarCampoRequeridoBO(intParametroGeneral, "not.campo.idAdjuntoPlantilla");

		Optional<NotParametrosGenerales> optNotParametrosGenerales = objNotParametrosGeneralesDAO
				.findWithValidacionCampo(intParametroGeneral);

		NotParametrosGenerales objNotAdjuntoPlantilla = null;
		GetParametrosGeneralesDTO objGetParametrosGeneralesDTO = null;

		if (optNotParametrosGenerales.isPresent()) {
			objNotAdjuntoPlantilla = optNotParametrosGenerales.get();
			objGetParametrosGeneralesDTO = new GetParametrosGeneralesDTO();
			objGetParametrosGeneralesDTO.setIdParametroGeneral(objNotAdjuntoPlantilla.getIdParametroGeneral());
			objGetParametrosGeneralesDTO.setDescripcion(objNotAdjuntoPlantilla.getDescripcion());
			objGetParametrosGeneralesDTO.setEstado(objNotAdjuntoPlantilla.getEstado());
			objGetParametrosGeneralesDTO.setNombre(objNotAdjuntoPlantilla.getNombre());
			objGetParametrosGeneralesDTO.setTipoValor(objNotAdjuntoPlantilla.getTipoValor());
			objGetParametrosGeneralesDTO.setValor(objNotAdjuntoPlantilla.getValor());
		}
	
		/*
		 * Long longConsultarPlantillas = objNotPlantillasDAO.consultarPlantillasCount(
		 * "365fd312-7fc8-408a-8157-62d327599011","2");
		 * 
		 * 
		 * if (longConsultarPlantillas == 0) throw new
		 * BOException("Usuario y Empresa no existen");
		 */
    	
    	
    	Map<String, Object> map = new HashMap<String, Object>();
		map.put("row", objGetParametrosGeneralesDTO);
		//map.put("countPlantilla", longConsultarPlantillas);
		return map;
	}

	@Override
	public Map<String, Object> getParametroGeneralXName(String strParametroGeneral)
			throws BOException, ClassNotFoundException, IOException {
		// Valida que el ID Adjunto Plantilla sera requerido
		ValidacionUtil.validarCampoRequeridoBO(strParametroGeneral, "not.campo.stringAdjuntoPlantilla");
		
		Optional<GeneralParametrosGeneralesDTO> optGeneralParametrosGeneralesDTO = objNotParametrosGeneralesDAO.findGroup(strParametroGeneral);
				
		return null;
	}

	/*
	 * @Override public Map<String, Object> consultarParametrosGenerales() throws
	 * BOException, ClassNotFoundException, IOException {
	 * 
	 * String strParametroGeneral = "uploadFilePath";
	 * 
	 * Optional<GetParametrosGeneralesDTO> optGetParametrosGeneralesDTO = null;
	 * 
	 * optGetParametrosGeneralesDTO =
	 * NotParametrosGeneralesDAO.findGroup(strParametroGeneral);
	 * 
	 * Long longConsultarAdjuntosPlantillas =
	 * NotParametrosGeneralesDAO.getCountParametrosGenerales();
	 * List<GeneralParametrosGeneralesDTO> lsGeneralParametrosGeneralesDTO =
	 * NotParametrosGeneralesDAO.getParametrosGenerales(); Map<String, Object>
	 * mapResult = new HashMap<String, Object>(); mapResult.put("totalRows",
	 * longConsultarAdjuntosPlantillas); mapResult.put("row",
	 * lsGeneralParametrosGeneralesDTO);
	 * 
	 * return mapResult; }
	 * 
	 * 
	 * @Override public Map<String, Object> getParametroGeneralXName(String
	 * strParametroGeneral) throws BOException, ClassNotFoundException, IOException
	 * { // TODO Auto-generated method stub
	 * ValidacionUtil.validarCampoRequeridoBO(strParametroGeneral,
	 * "not.campo.idAdjuntoPlantilla");
	 * 
	 * Optional<GetParametrosGeneralesDTO> optGetParametrosGeneralesDTO =
	 * NotParametrosGeneralesDAO.findGroup(strParametroGeneral);
	 * 
	 * NotParametrosGenerales objNotParametrosGenerales = null;
	 * GeneralParametrosGeneralesDTO objGeneralParametrosGeneralesDTO = null;
	 * 
	 * 
	 * if (optGetParametrosGeneralesDTO.isPresent()) { objNotParametrosGenerales =
	 * optGetParametrosGeneralesDTO.get(); objGeneralAdjuntoPlantillaDTO = new
	 * GetAdjuntoPlantillaDTO();
	 * objGeneralAdjuntoPlantillaDTO.setIdAdjuntoPlantilla(objNotParametrosGenerales
	 * .getIdAdjuntoPlantilla());
	 * objGeneralAdjuntoPlantillaDTO.setIdPlantilla(objNotParametrosGenerales.
	 * getIdPlantilla());
	 * objGeneralAdjuntoPlantillaDTO.setNombreArchivo(objNotParametrosGenerales.
	 * getNombreArchivo());
	 * objGeneralAdjuntoPlantillaDTO.setRutaLocal(objNotParametrosGenerales.
	 * getRutaLocal());
	 * objGeneralAdjuntoPlantillaDTO.setExtension(objNotParametrosGenerales.
	 * getExtension()); }
	 * 
	 * 
	 * Map<String, Object> map = new HashMap<String, Object>(); map.put("row",
	 * optGetParametrosGeneralesDTO); return map; }
	 */

}
