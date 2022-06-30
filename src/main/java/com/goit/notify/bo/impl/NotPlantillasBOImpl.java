package com.goit.notify.bo.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.goit.helper.FechasHelper;
import com.goit.helper.enums.FormatoFecha;
import com.goit.notify.bo.INotPlantillasBO;
import com.goit.notify.dao.NotPlantillasDAO;
import com.goit.notify.dto.ListaPlantillasDTO;
import com.goit.notify.dto.RequestPlantillasDTO;
import com.goit.notify.exceptions.BOException;
import com.goit.notify.exceptions.RestClientException;
import com.goit.notify.exceptions.UnauthorizedException;
import com.goit.notify.model.NotPlantilla;
import com.goit.notify.util.ValidacionUtil;

@Service
public class NotPlantillasBOImpl implements INotPlantillasBO {

	@Autowired
	private NotPlantillasDAO objPlantillasDAO;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class})
	public Map<String, Object> postCrearPlantillas(RequestPlantillasDTO objPlantillasDTO, String strIdUsuario,
			String strIdEmpresa, String strUsuario) throws BOException {
		
		ValidacionUtil.validarCampoRequeridoBO(objPlantillasDTO.getNombre(), "not.campo.nombre");
		ValidacionUtil.validarCampoRequeridoBO(objPlantillasDTO.getTipo(), "not.campo.tipo");


	
	   //*************************************************************************************
	   //Valida el tipo Plantilla
		String[] arrTipoPlantillas = {"H", "T"};
		 
		 if (!Arrays.stream(arrTipoPlantillas).anyMatch(StringUtils.upperCase(objPlantillasDTO.getTipo())::equals)) 
				throw new BOException("not.warn.paramNoValidTipo", new Object[]{"not.campo.tipo"});
		 
		Date datFechaActual = new Date();
		
		NotPlantilla objPlantilla = new NotPlantilla();
		
		objPlantilla.setNombre(objPlantillasDTO.getNombre());
		objPlantilla.setDescripcion(objPlantillasDTO.getDescripcion());
		objPlantilla.setTipo(objPlantillasDTO.getTipo());
		objPlantilla.setEstado("A");
		objPlantilla.setUsuarioCreacion(strUsuario);
		objPlantilla.setFechaCreacion(FechasHelper.dateToString(datFechaActual, FormatoFecha.YYYY_MM_DD_HH_MM_SS));
		objPlantillasDAO.persist(objPlantilla);
	
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("idPlantilla", objPlantilla.getIdPlantilla());

		return map;
	}

	@Override
	//@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class})
	public Optional<NotPlantilla> getPlantilla(Integer strIdPlantilla) throws BOException {		
		ValidacionUtil.validarCampoRequeridoBO(strIdPlantilla, "not.campo.idPlantilla");
		//Valida que la plantilla exista y este en estado activo
		Optional<NotPlantilla> objPlantilla = objPlantillasDAO.ValidacionPlantilla(strIdPlantilla);
		//Optional<NotPlantilla> optPlantilla = objPlantillasDAO.find(strIdPlantilla);
		
		return objPlantilla;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class})
	public Map<String, Object> actualizarPlantillas(RequestPlantillasDTO objPlantillasDTO, Integer strIdPlantilla,
			String strIdUsuario, String strIdEmpresa, String strUsuario)
			throws BOException, IOException, RestClientException, UnauthorizedException {
		NotPlantilla objPlantilla = new NotPlantilla();

		//Validar Campo Requerido ID_PLANTILLA
		ValidacionUtil.validarCampoRequeridoBO(strIdPlantilla, "not.campo.idPlantilla");

		//Busca la plantilla
		Optional<NotPlantilla> optPlantilla = objPlantillasDAO.find(strIdPlantilla);

		//Actualiza los campos
		if(optPlantilla.isPresent()) {
			optPlantilla.get().setNombre(objPlantillasDTO.getNombre());
			optPlantilla.get().setDescripcion(objPlantillasDTO.getDescripcion());
			optPlantilla.get().setTipo(objPlantillasDTO.getTipo());
			optPlantilla.get().setUsuarioActualiza(strUsuario);
			optPlantilla.get().setFechActualiza(FechasHelper.dateToString(new Date(), FormatoFecha.YYYY_MM_DD_HH_MM_SS));
			objPlantillasDAO.update(optPlantilla.get());
			//valida que la plantilla exista
		}else if(objPlantilla.getIdPlantilla() != strIdPlantilla) {
			throw new BOException("not.warn.idPlantillaNoExiste", new Object[]{"not.campo.idPlantilla"}); 
		}
		
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("idPlantilla", strIdPlantilla);
		
		return map;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class})
	public Map<String, Object> eliminarPlantilla(Integer strIdPlantilla,
			String strIdUsuario, String strIdEmpresa, String strUsuario)
			throws BOException, IOException, RestClientException, UnauthorizedException {
		//Valido Campo requerido
		ValidacionUtil.validarCampoRequeridoBO(strIdPlantilla, "not.campo.idPlantilla");
		//Busco la plantilla
		Optional<NotPlantilla> optPlantilla = objPlantillasDAO.find(strIdPlantilla);
		//Valida que la plantilla este en estado activo activo	
		Optional<NotPlantilla> objPlantilla = objPlantillasDAO.ValidacionPlantilla(strIdPlantilla);
		objPlantilla.get().setEstado("I");
		objPlantilla.get().setUsuarioInactiva(strUsuario);
		objPlantilla.get().setFechaInactiva(FechasHelper.dateToString(new Date(), FormatoFecha.YYYY_MM_DD_HH_MM_SS));
		objPlantillasDAO.update(optPlantilla.get());
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("Plantilla Inactiva", strIdPlantilla);
		return map;
	}

	@Override
	public Map<String, Object> getPlantillas() throws BOException {
		//llamamos al metodo ListarPlantillas
		List<ListaPlantillasDTO> lsConsultarPlantillas = objPlantillasDAO.listarPlantillas();

		Map<String,Object> map = new HashMap<String,Object>();
		//map.put("totalRows", longConsultarGrupos);
		map.put("Fila", lsConsultarPlantillas);

		return map;
	}
}
