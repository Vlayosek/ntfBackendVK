package com.goit.notify.bo.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.goit.helper.FechasHelper;
import com.goit.helper.enums.Estado;
import com.goit.helper.enums.FormatoFecha;
import com.goit.helper.enums.NemonicoParametros;
import com.goit.notify.bo.INotImagenesUsuarioBO;
import com.goit.notify.dao.NotImagenesUsuarioDAO;
import com.goit.notify.dao.NotParametrosGeneralesDAO;
import com.goit.notify.dto.GeneralImagenesUsuarioDTO;
import com.goit.notify.dto.GetImagenesUsuarioDTO;
import com.goit.notify.dto.RequestImagenesUsuarioDTO;
import com.goit.notify.dto.UsuarioLogin;
import com.goit.notify.exceptions.BOException;
import com.goit.notify.model.NotImagenesUsuario;
import com.goit.notify.util.ValidacionUtil;


@Service
public class NotImagenesUsuarioBOImpl implements INotImagenesUsuarioBO {
    
    @Autowired
    private NotImagenesUsuarioDAO objNotImagenesUsuarioDAO;
    
    @Autowired
    private NotParametrosGeneralesDAO objNotParametrosGeneralesDAO;

    @Override
    public Map<String, Object> consultarImagenesUsuario() throws BOException , ClassNotFoundException, IOException{
    	
    	List<GeneralImagenesUsuarioDTO> lsGeneralImagenesUsuarioDTO= objNotImagenesUsuarioDAO.consultarImagenesUsuario();
  
    	Map<String, Object> mapResult = new HashMap<String, Object>();
    	mapResult.put("totalRows", lsGeneralImagenesUsuarioDTO.size());
    	
    	if(!ObjectUtils.isEmpty(lsGeneralImagenesUsuarioDTO)) {
    		mapResult.put("row", lsGeneralImagenesUsuarioDTO);
    	}else {
    		mapResult.put("row", "No hay registros");
    	}
    	return mapResult;
    	
    }
    
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Map<String, Object> postImagenUsuario(List<RequestImagenesUsuarioDTO> lsRequestImagenesUsuarioDTO,
			UsuarioLogin userLogin)
			throws BOException, ClassNotFoundException, IOException {
		
		NotImagenesUsuario objNotImagenesUsuario = null;
		
		for (RequestImagenesUsuarioDTO requestImagenesUsuarioDTO : lsRequestImagenesUsuarioDTO) {
			
			//Valida campos requeridos
          	ValidacionUtil.validarCampoRequeridoBO(requestImagenesUsuarioDTO.getNombre(),"not.campo.nombreImagen");
          	
          //Obtengo la extension 
     		String ext = FilenameUtils.getExtension(requestImagenesUsuarioDTO.getNombre().trim());
     		
     		String[] arrTipoImagen = { "JPG" , "PNG" , "JPGE" };
     		
     		if(!Arrays.stream(arrTipoImagen).anyMatch(StringUtils.upperCase(ext)::equals)) 
				 throw new BOException("not.warn.campoInvalido",new Object[]{"not.campo.extImagen"});
     		
     		//consulta carpeta donde se estan guardados los adjuntos
     		String uploadFilePath = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.PATH_UPLOAD_FILE).getValor();
     		
     		//se genera la ruta completa donde esta alojado el adjunto
     		String fullPath = uploadFilePath.concat(requestImagenesUsuarioDTO.getNombre());

			objNotImagenesUsuario = new NotImagenesUsuario();
			
			objNotImagenesUsuario.setIdEmpresa(userLogin.getIdEmpresa());
			objNotImagenesUsuario.setIdUsuario(userLogin.getIdUsuario());
			objNotImagenesUsuario.setNombre(requestImagenesUsuarioDTO.getNombre().trim());
			objNotImagenesUsuario.setRutaLocal(fullPath);
			objNotImagenesUsuario.setUrlPublica(fullPath);
			objNotImagenesUsuario.setEsPublica(Estado.ES_PUBLICA.getName());
			objNotImagenesUsuario.setFechaCrea(FechasHelper.dateToString(new Date(), FormatoFecha.YYYY_MM_DD));
			objNotImagenesUsuario.setEstado(Estado.ACTIVO.getName());
			objNotImagenesUsuario.setUsuarioCrea(userLogin.getUsuario());
			
			objNotImagenesUsuarioDAO.persist(objNotImagenesUsuario);
		}
		Map<String,Object> map=new HashMap<String,Object>() ;
        map.put("msg", "Datos Guardados Correctamente");
        return map;
	}


	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Map<String, Object> postImagenUsuarioFromCarga(List<String> lsRequestImagenesUsuarioDTO,
			UsuarioLogin userLogin)
			throws BOException, ClassNotFoundException, IOException {
		
		NotImagenesUsuario objNotImagenesUsuario = null;
		
		for (String string : lsRequestImagenesUsuarioDTO) {
			
			//Valida campos requeridos
          	ValidacionUtil.validarCampoRequeridoBO(string,"not.campo.nombreImagen");
          	
          //Obtengo la extension 
     		String ext = FilenameUtils.getExtension(string.trim());
     		
     		String[] arrTipoImagen = { "JPG" , "PNG" , "JPGE" };
     		
     		if(!Arrays.stream(arrTipoImagen).anyMatch(StringUtils.upperCase(ext)::equals)) 
				 throw new BOException("not.warn.campoInvalido",new Object[]{"not.campo.extImagen"});
     		
     		
     		String destinationUrl = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.DESTINATION_URL_ATTACH).getValor();
     		
     		//consulta carpeta donde se estan guardados los adjuntos
     		String uploadFilePath = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.PATH_USER_IMAGE).getValor();
     		
     		
     		String str = destinationUrl.substring(0, destinationUrl.length()-1);
     		
     		
     		//se genera la ruta completa donde esta alojado el adjunto
     		String rutaLocal = uploadFilePath.concat(userLogin.getIdUsuario()).concat("/").concat(string);
     		
     		String urlPublica = str.concat(uploadFilePath.concat(userLogin.getIdUsuario()).concat("/").concat(string));

			objNotImagenesUsuario = new NotImagenesUsuario();
			
			objNotImagenesUsuario.setIdEmpresa(userLogin.getIdEmpresa());
			objNotImagenesUsuario.setIdUsuario(userLogin.getIdUsuario());
			objNotImagenesUsuario.setNombre(string);
			objNotImagenesUsuario.setRutaLocal(rutaLocal);
			objNotImagenesUsuario.setUrlPublica(urlPublica);
			objNotImagenesUsuario.setEsPublica(Estado.ES_PUBLICA.getName());
			objNotImagenesUsuario.setFechaCrea(FechasHelper.dateToString(new Date(), FormatoFecha.YYYY_MM_DD_HH_MM_SS));
			objNotImagenesUsuario.setEstado(Estado.ACTIVO.getName());
			objNotImagenesUsuario.setUsuarioCrea(userLogin.getUsuario());
			
			objNotImagenesUsuarioDAO.persist(objNotImagenesUsuario);
		}
		Map<String,Object> map=new HashMap<String,Object>() ;
        map.put("msg", "Datos Guardados Correctamente");
        return map;
	}

	@Override
	public Map<String, Object> consultarImagenXId(Integer intImagen)
			throws BOException, ClassNotFoundException, IOException {
		//Valida que el ID Imagen sera requerido
        ValidacionUtil.validarCampoRequeridoBO(intImagen,"not.campo.idUsuario");

        Optional<NotImagenesUsuario> optNotImagenesUsuario= objNotImagenesUsuarioDAO.findWithValidacionCampo(intImagen);

        NotImagenesUsuario objNotImagenesUsuario = null;
        GetImagenesUsuarioDTO objGetImagenesUsuarioDTO = null;

        if(optNotImagenesUsuario.isPresent()){
        	objNotImagenesUsuario = optNotImagenesUsuario.get();
        	objGetImagenesUsuarioDTO = new GetImagenesUsuarioDTO();
        	objGetImagenesUsuarioDTO.setIdImagenUsuario(objNotImagenesUsuario.getIdImagenUsuario());
        	objGetImagenesUsuarioDTO.setIdEmpresa(objNotImagenesUsuario.getIdEmpresa());
        	objGetImagenesUsuarioDTO.setIdUsuario(objNotImagenesUsuario.getIdUsuario());
        	objGetImagenesUsuarioDTO.setNombre(objNotImagenesUsuario.getNombre());
        	objGetImagenesUsuarioDTO.setRutaLocal(objNotImagenesUsuario.getRutaLocal());
        	objGetImagenesUsuarioDTO.setUrlPublica(objNotImagenesUsuario.getUrlPublica());
        	objGetImagenesUsuarioDTO.setEsPublica(objNotImagenesUsuario.getEsPublica());
        	objGetImagenesUsuarioDTO.setEstado(objNotImagenesUsuario.getEstado());
        }
        
        Map<String, Object> mapResult = new HashMap<String, Object>();
    	mapResult.put("row", objGetImagenesUsuarioDTO);
    	
    	return mapResult;

	}

	@Override
	public Map<String, Object> consultarImagenesXUsuario(String strIdUsuario)
			throws BOException, ClassNotFoundException, IOException {
		//Valida que el ID Usuario sera requerido
        ValidacionUtil.validarCampoRequeridoBO(strIdUsuario,"not.campo.idUsuario");
        
        List<GeneralImagenesUsuarioDTO> lsGeneralImagenesUsuarioDTO = objNotImagenesUsuarioDAO.consultarImagenesXUsuario(strIdUsuario);
        
        //validar de que hayan imagenes por usuario
        
        Map<String, Object> mapResult = new HashMap<String, Object>();
        mapResult.put("idUsuario", strIdUsuario);
    	mapResult.put("totalRows", lsGeneralImagenesUsuarioDTO.size());
    	
    	if(!ObjectUtils.isEmpty(lsGeneralImagenesUsuarioDTO)) {
    		mapResult.put("row", lsGeneralImagenesUsuarioDTO);
    	}else {
    		mapResult.put("row", "No hay registros");
    	}
    	return mapResult;
        
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Map<String, Object> deleteImagen(Integer idImagen, UsuarioLogin userLogin) throws BOException, ClassNotFoundException, IOException {
				//Valido Campo requerido
				ValidacionUtil.validarCampoRequeridoBO(idImagen, "not.campo.idImagen");
			
				//Vlaido que exista y que este activo
				Optional<NotImagenesUsuario> optNotImagenesUsuario = objNotImagenesUsuarioDAO.findWithValidacionCampo(idImagen);
				
				//Le cambio por estado Inactivo (Eliminado Logico)
				optNotImagenesUsuario.get().setEstado(Estado.INACTIVO.getName());
				optNotImagenesUsuario.get().setUsuarioInactiva(userLogin.getUsuario());
				optNotImagenesUsuario.get().setFechaInactiva(FechasHelper.dateToString(new Date(), FormatoFecha.YYYY_MM_DD));
				
				//Envio a Actualizar
				objNotImagenesUsuarioDAO.update(optNotImagenesUsuario.get());
				
				
				Map<String,Object> map=new HashMap<String,Object>() ;
		        map.put("idImagen", optNotImagenesUsuario.get().getIdImagenUsuario());
		        map.put("row", "Imagen inactivada correctamente");
		        return map;
			
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Map<String, Object> putImagenUsuario(RequestImagenesUsuarioDTO objRequestImagenesUsuarioDTO,
			Integer intIdImagen, UsuarioLogin userLogin)
			throws BOException, ClassNotFoundException, IOException {
		
				//Valido Campo requerido
				ValidacionUtil.validarCampoRequeridoBO(intIdImagen, "not.campo.idImagen");
				
				Optional<NotImagenesUsuario> optNotImagenesUsuario = objNotImagenesUsuarioDAO.findWithValidacionCampo(intIdImagen);
				
				//Obtengo la extension 
	     		String ext = FilenameUtils.getExtension(objRequestImagenesUsuarioDTO.getNombre().trim());
	     		
	     		String[] arrTipoImagen = { "JPG" , "PNG" , "JPGE" };
	     		
	     		if(!Arrays.stream(arrTipoImagen).anyMatch(StringUtils.upperCase(ext)::equals)) 
					 throw new BOException("not.warn.campoInvalido",new Object[]{"not.campo.extImagen"});
	     		
	     		//consulta carpeta donde se estan guardados los adjuntos
	     		String uploadFilePath = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.PATH_UPLOAD_FILE).getValor();
	     		
	     		//se genera la ruta completa donde esta alojado el adjunto
	     		String fullPath = uploadFilePath.concat(objRequestImagenesUsuarioDTO.getNombre());
				
				optNotImagenesUsuario.get().setNombre(objRequestImagenesUsuarioDTO.getNombre().trim());
				optNotImagenesUsuario.get().setRutaLocal(fullPath);
				optNotImagenesUsuario.get().setUrlPublica(fullPath);
				optNotImagenesUsuario.get().setEsPublica(objRequestImagenesUsuarioDTO.getEsPublica());
				optNotImagenesUsuario.get().setUsuarioActualiza(userLogin.getUsuario());
				optNotImagenesUsuario.get().setFechaActualiza(FechasHelper.dateToString(new Date(), FormatoFecha.YYYY_MM_DD));
						
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("idImagen", optNotImagenesUsuario.get());
				
				return map;
	}
}
