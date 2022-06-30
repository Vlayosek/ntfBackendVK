package com.goit.notify.bo;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.goit.notify.dto.RequestPlantillasDTO;
import com.goit.notify.exceptions.BOException;
import com.goit.notify.exceptions.RestClientException;
import com.goit.notify.exceptions.UnauthorizedException;
import com.goit.notify.model.NotPlantilla;

public interface INotPlantillasBO {

	/**
	 * Se crea los destinatarios
	 * 
	 * @author Scarlet Carvaca
	 * @param lsRequestPlantillasDTO
	 * @param nombre
	 * @param descripcion
	 * @return
	 * @throws BOException
	 * @throws UnauthorizedException 
	 * @throws RestClientException 
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	
	public Map<String, Object> postCrearPlantillas(RequestPlantillasDTO objPlantillasDTO, String strIdUsuario,
			String strIdEmpresa, String strUsuario) throws BOException, IOException, RestClientException, UnauthorizedException;
	
	public Optional<NotPlantilla> getPlantilla(Integer strIdPlantilla) throws BOException;
	
	public Map<String, Object> actualizarPlantillas(RequestPlantillasDTO objPlantillasDTO, Integer strIdPlantilla, String strIdUsuario,
			String strIdEmpresa, String strUsuario) throws BOException, IOException, RestClientException, UnauthorizedException;
	
	public Map<String, Object> eliminarPlantilla(Integer strIdPlantilla, String strIdUsuario,
			String strIdEmpresa, String strUsuario) throws BOException, IOException, RestClientException, UnauthorizedException;
	
	public Map<String, Object> getPlantillas() throws BOException;

}
