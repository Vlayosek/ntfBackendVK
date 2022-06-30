package com.goit.notify.util;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goit.helper.enums.NemonicoParametros;
import com.goit.notify.dao.NotParametrosGeneralesDAO;
import com.goit.notify.dto.RequestDestinatariosDTO;
import com.goit.notify.dto.ResponsesAutenticacionDTO;
import com.goit.notify.exceptions.BOException;
import com.goit.notify.exceptions.RestClientException;
import com.goit.notify.exceptions.UnauthorizedException;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;

@Service
public class ServiciosUtil {
	
	// Variables para Tiempo de Conexion
	private static Integer READ_TIME_OUT = 30000; // 30 segundos
	private static Integer CONNECT_TIME_OUT = 5000; // 5 Segundos
	
    @Autowired
    private NotParametrosGeneralesDAO objNotParametrosGeneralesDAO;
	
	@Autowired
	private Gson gson;
	
	/**
	 * Guarda los destinatario
	 * 
	 * @author Bryan Zamora
	 * @param strAuthorization
	 * @param strLanguage
	 * @param strLanguaje 
	 * @param intIdGrupo
	 * @param lsRequestDestinatariosDTO
	 * @return
	 * @throws RestClientException
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 * @throws BOException
	 * @throws UnauthorizedException
	 */
	public ResponsesAutenticacionDTO guardarDestinatario(
			String strCanal, 
			String strAuthorization,
			String strLanguaje,
			Integer intIdGrupo,
			List<RequestDestinatariosDTO> lsRequestDestinatariosDTO)
			throws RestClientException, JsonMappingException, JsonProcessingException, BOException, UnauthorizedException {
		
		ObjectMapper objectMapper = new ObjectMapper();

		// Consulta base url
		String strBaseUrl = NemonicoParametros.GOIT_REDISMANAGER_API.getName();

		ClientConfig clientConfig = new DefaultClientConfig();
		Client client = Client.create(clientConfig);
		client.setReadTimeout(READ_TIME_OUT);
		client.setConnectTimeout(CONNECT_TIME_OUT);
		WebResource webResource = client.resource(strBaseUrl + "/destinatario")
				.queryParam("idGrupo",intIdGrupo.toString());
		
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON)
				.header("content-type", MediaType.APPLICATION_JSON)
				.header("Accept-Language", strLanguaje)
				.header("Authorization",strAuthorization)
				.header("Canal",strCanal)
				.post(ClientResponse.class,objectMapper.writeValueAsString(lsRequestDestinatariosDTO));

		String strResponseEntity = response.getEntity(String.class);
		JsonNode jsonNode = objectMapper.readTree(strResponseEntity);
		ResponsesAutenticacionDTO objResponsesAutenticacionDTO;
		if (response.getStatus() == 200) {
			objResponsesAutenticacionDTO = gson.fromJson(strResponseEntity, ResponsesAutenticacionDTO.class);
			return objResponsesAutenticacionDTO;
		} else if (response.getStatus() == 400) {
			throw new BOException(jsonNode.get("message").asText());
		} else if (response.getStatus() == 401) {
			throw new UnauthorizedException(jsonNode.get("message").asText(),
					jsonNode.get("errorData") != null ? jsonNode.get("errorData") : new Object[] {});
		} else {
			throw new RestClientException(jsonNode.get("message").asText(),
					jsonNode.get("errorData") != null ? jsonNode.get("errorData") : new Object[] {});
		}

	}
	
	/**
	 * Guarda un adjunto de plantilla
	 * 
	 * @author Vladimir Kozisck
	 * @param urlPusher
	 * @param name
	 * @param base64doc
	 * @param path
	 * @return
	 * @throws BOException
	 */
	public String guardarAdjunto(
			String urlPusher,
			String name, 
			String base64doc,
			String path) throws BOException {

		ClientConfig clientConfig = new DefaultClientConfig();
		Client client = Client.create(clientConfig);
		client.setReadTimeout(READ_TIME_OUT);
		client.setConnectTimeout(CONNECT_TIME_OUT);
		Form form = new Form();
		form.add("name", name);
		form.add("base64doc",base64doc);
		form.add("path", path);
		WebResource webResource = client.resource(urlPusher);
		
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_FORM_URLENCODED)
				.post(ClientResponse.class,form);

		String strResponseEntity = response.getEntity(String.class);
		if (response.getStatus() == 200) {
			return strResponseEntity;
		} else if (response.getStatus() == 400) {
			throw new BOException(strResponseEntity);
		}
		return strResponseEntity;

	}
	
	
	/**
	 * Guarda un adjunto de plantilla
	 * 
	 * @author Vladimir Kozisck
	 * @param urlPusher
	 * @param name
	 * @param base64doc
	 * @param path
	 * @return
	 * @throws BOException
	 */
	public String obtenerAdjunto(
			String urlPusher,
			String path) throws BOException {

		ClientConfig clientConfig = new DefaultClientConfig();
		Client client = Client.create(clientConfig);
		client.setReadTimeout(READ_TIME_OUT);
		client.setConnectTimeout(CONNECT_TIME_OUT);
		Form form = new Form();
		form.add("fullFilePath", path);
		WebResource webResource = client.resource(urlPusher);
		
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_FORM_URLENCODED)
				.post(ClientResponse.class,form);

		String strResponseEntity = response.getEntity(String.class);
		if (response.getStatus() == 200) {
			return strResponseEntity;
		} else if (response.getStatus() == 400) {
			throw new BOException(strResponseEntity);
		}
		return strResponseEntity;

	}
}
