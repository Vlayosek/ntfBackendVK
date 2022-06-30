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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.goit.helper.FechasHelper;
import com.goit.helper.enums.Estado;
import com.goit.helper.enums.FormatoFecha;
import com.goit.helper.enums.NemonicoParametros;
import com.goit.notify.bo.INotAdjuntoPlantillaBO;
import com.goit.notify.dao.NotAdjuntoPlantillaDAO;
import com.goit.notify.dao.NotParametrosGeneralesDAO;
import com.goit.notify.dao.NotPlantillasDAO;
import com.goit.notify.dto.GeneralAdjuntoPlantillaDTO;
import com.goit.notify.dto.GetAdjuntoPlantillaDTO;
import com.goit.notify.dto.RequestAdjuntoPlantillaDTO;
import com.goit.notify.dto.UsuarioLogin;
import com.goit.notify.exceptions.BOException;
import com.goit.notify.model.NotAdjuntoPlantilla;
import com.goit.notify.model.NotPlantilla;
import com.goit.notify.util.ValidacionUtil;


@Service
public class NotAdjuntoPlantillaBOImpl implements INotAdjuntoPlantillaBO {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(NotAdjuntoPlantillaBOImpl.class);


    @Autowired
    private NotAdjuntoPlantillaDAO objNotAdjuntoPlantillaDAO;
    
    @Autowired
    private NotPlantillasDAO objNotPlantillasDAO;
    
    @Autowired
    private NotParametrosGeneralesDAO objNotParametrosGeneralesDAO;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public Map<String, Object> postAdjuntoPlantilla(List<RequestAdjuntoPlantillaDTO> lsRequestAdjuntoPlantilla, Integer idPlantilla,UsuarioLogin usuario) throws BOException , ClassNotFoundException, IOException {
        ValidacionUtil.validarCampoRequeridoBO(idPlantilla,"not.campo.idPlantilla");

        //Busca la plantilla y valido que exista
        Optional<NotPlantilla> objNotPlantilla =objNotPlantillasDAO.ValidacionPlantilla(idPlantilla);
        
        NotAdjuntoPlantilla objNotAdjuntoPlantilla = null;

        //Recorro el objeto recibido en el request
        for (RequestAdjuntoPlantillaDTO objRequestAdjuntoPlantillaDTO:
             lsRequestAdjuntoPlantilla) {
        	
        	//Valida campos requeridos
          	ValidacionUtil.validarCampoRequeridoBO(objRequestAdjuntoPlantillaDTO.getNombreArchivo(),"not.campo.nombreArchivo");      	
     		
     		//Obtengo la extension 
     		String ext = FilenameUtils.getExtension(objRequestAdjuntoPlantillaDTO.getNombreArchivo().trim());
     		
     		//Consulta Extensiones Prohibidas
     		String extProhibidas = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.EXTENSIONES_PROHIBIDAS).getValor();
     		
     		String[] strExt = extProhibidas.toUpperCase().split(",");
     		
     		//Valida las estensiones
	   		 if (Arrays.stream(strExt).anyMatch(StringUtils.upperCase(ext)::equals))
				throw new BOException("not.warn.campoInvalido", new Object[]{"not.campo.extension"});
     		
     		//Consulta URL Base
     		//String urlAdjunto = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.DESTINATION_URL_ATTACH).getValor();
     		//consulta carpeta donde se estan guardados los adjuntos
     		String uploadFilePath = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.PATH_UPLOAD_FILE).getValor();
     		
     		//se genera la ruta completa donde esta alojado el adjunto
     		String fullPath = uploadFilePath.concat(objRequestAdjuntoPlantillaDTO.getNombreArchivo());
     		
            objNotAdjuntoPlantilla = new NotAdjuntoPlantilla();
            objNotAdjuntoPlantilla.setIdPlantilla(objNotPlantilla.get().getIdPlantilla());
            objNotAdjuntoPlantilla.setNombreArchivo(objRequestAdjuntoPlantillaDTO.getNombreArchivo().trim());
            objNotAdjuntoPlantilla.setRutaLocal(fullPath); //la obtengo del pusher?
            objNotAdjuntoPlantilla.setExtension(ext);
            objNotAdjuntoPlantilla.setEstado(Estado.ACTIVO.getName());
            objNotAdjuntoPlantilla.setUsuarioCrea(usuario.getUsuario());
            objNotAdjuntoPlantilla.setIdEmpresa(usuario.getIdEmpresa());
            objNotAdjuntoPlantilla.setIdUsuario(usuario.getIdUsuario());            
            objNotAdjuntoPlantilla.setFechaCrea(FechasHelper.dateToString(new Date(), FormatoFecha.YYYY_MM_DD_HH_MM_SS));

            objNotAdjuntoPlantillaDAO.persist(objNotAdjuntoPlantilla);
        }
        
        Map<String,Object> map=new HashMap<String,Object>() ;
        map.put("row", "Datos Guardados Correctamente");
        
        return map;
    }
    
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public Map<String, Object> postAdjuntoPlantillaFromCarga(List<String> lsRequestAdjuntoPlantilla, Integer idPlantilla,UsuarioLogin usuario, Float size) throws BOException , ClassNotFoundException, IOException {
        ValidacionUtil.validarCampoRequeridoBO(idPlantilla,"not.campo.idPlantilla");

        //Busca la plantilla y valido que exista
        Optional<NotPlantilla> objNotPlantilla =objNotPlantillasDAO.ValidacionPlantilla(idPlantilla);
        
        NotAdjuntoPlantilla objNotAdjuntoPlantilla = null;
        
        //Recorro el objeto recibido en el request
        for (String string :
             lsRequestAdjuntoPlantilla) {
        	
        	//Valida campos requeridos
          	ValidacionUtil.validarCampoRequeridoBO(string,"not.campo.nombreArchivo");      	
     		
     		//Obtengo la extension 
     		String ext = FilenameUtils.getExtension(string.trim());
     		
     		//Consulta Extensiones Prohibidas
     		String extProhibidas = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.EXTENSIONES_PROHIBIDAS).getValor();
     		
     		String[] strExt = extProhibidas.toUpperCase().split(",");
     		
     		//Valida las estensiones
	   		 if (Arrays.stream(strExt).anyMatch(StringUtils.upperCase(ext)::equals))
				throw new BOException("not.warn.campoInvalido", new Object[]{"not.campo.extension"});
     		
     		//Consulta URL Base
	   		//String destinationUrl = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.DESTINATION_URL_ATTACH).getValor();
     		//consulta carpeta donde se estan guardados los adjuntos
     		String uploadFilePath = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.PATH_UPLOAD_FILE).getValor();
     		
     		//se genera la ruta completa donde esta alojado el adjunto
     		String fullPath = uploadFilePath.concat(usuario.getIdUsuario().concat("/").concat(string));
     		
     		
            objNotAdjuntoPlantilla = new NotAdjuntoPlantilla();
            objNotAdjuntoPlantilla.setIdPlantilla(objNotPlantilla.get().getIdPlantilla());
            objNotAdjuntoPlantilla.setNombreArchivo(string.trim());
            objNotAdjuntoPlantilla.setRutaLocal(fullPath);
            objNotAdjuntoPlantilla.setExtension(ext);
            objNotAdjuntoPlantilla.setPeso(size);
            objNotAdjuntoPlantilla.setEstado(Estado.ACTIVO.getName());
            objNotAdjuntoPlantilla.setUsuarioCrea(usuario.getUsuario());
            objNotAdjuntoPlantilla.setIdEmpresa(usuario.getIdEmpresa());
            objNotAdjuntoPlantilla.setIdUsuario(usuario.getIdUsuario());            
            objNotAdjuntoPlantilla.setFechaCrea(FechasHelper.dateToString(new Date(), FormatoFecha.YYYY_MM_DD_HH_MM_SS));

            objNotAdjuntoPlantillaDAO.persist(objNotAdjuntoPlantilla);
        }
        
        Map<String,Object> map=new HashMap<String,Object>() ;
        map.put("msg", "Datos Guardados Correctamente");
        
        return map;
    }

    @Override
    public Map<String, Object> consultarAdjuntoPlantillaXId(Integer intAdjuntoPlantilla) throws BOException , ClassNotFoundException, IOException{
        //Valida que el ID Adjunto Plantilla sera requerido
        ValidacionUtil.validarCampoRequeridoBO(intAdjuntoPlantilla,"not.campo.idAdjuntoPlantilla");

		Optional<NotAdjuntoPlantilla> optNotAdjuntoPlantilla = objNotAdjuntoPlantillaDAO
				.findWithValidacionCampo(intAdjuntoPlantilla);

		NotAdjuntoPlantilla objNotAdjuntoPlantilla = null;
		GetAdjuntoPlantillaDTO objGeneralAdjuntoPlantillaDTO = null;

		objNotAdjuntoPlantilla = optNotAdjuntoPlantilla.get();
		objGeneralAdjuntoPlantillaDTO = new GetAdjuntoPlantillaDTO();
		objGeneralAdjuntoPlantillaDTO.setIdAdjuntoPlantilla(objNotAdjuntoPlantilla.getIdAdjuntoPlantilla());
		objGeneralAdjuntoPlantillaDTO.setIdPlantilla(objNotAdjuntoPlantilla.getIdPlantilla());
		objGeneralAdjuntoPlantillaDTO.setNombreArchivo(objNotAdjuntoPlantilla.getNombreArchivo());
		objGeneralAdjuntoPlantillaDTO.setRutaLocal(objNotAdjuntoPlantilla.getRutaLocal());
		objGeneralAdjuntoPlantillaDTO.setExtension(objNotAdjuntoPlantilla.getExtension());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("row", objGeneralAdjuntoPlantillaDTO);
		return map;
    }

    @Override
    public Map<String, Object> consultarAdjuntoPlantilla() throws BOException , ClassNotFoundException, IOException{
    	    	
    	List<GeneralAdjuntoPlantillaDTO> lsGeneralAdjuntoPlantillaDTO = objNotAdjuntoPlantillaDAO.consultarAdjuntosPlantillas();
    	
    	Map<String, Object> mapResult = new HashMap<String, Object>();
    	mapResult.put("totalRows", lsGeneralAdjuntoPlantillaDTO.size());
    	
    	if(!ObjectUtils.isEmpty(lsGeneralAdjuntoPlantillaDTO)) {
    		mapResult.put("row", lsGeneralAdjuntoPlantillaDTO);
    	}else {
    		mapResult.put("row", "No hay registros");
    	}
    	return mapResult;
    }

    @Override
    public Map<String, Object> consultarAdjuntoPlantillaXIdPlantilla(Integer idPlantilla) throws BOException, ClassNotFoundException, IOException {
        //Valida que el ID Plantilla sera requerido
        ValidacionUtil.validarCampoRequeridoBO(idPlantilla,"not.campo.idPlantilla");
        
      //Busca la plantilla y valido que exista
        Optional<NotPlantilla> objNotPlantilla =objNotPlantillasDAO.ValidacionPlantilla(idPlantilla);

        List<GeneralAdjuntoPlantillaDTO> lsGeneralAdjuntoPlantillaDTO = objNotAdjuntoPlantillaDAO.consultarAdjuntoPlantillaXIdPlantilla(idPlantilla);
        
        Map<String,Object> map=new HashMap<String,Object>() ;
        map.put("idPlantilla", idPlantilla);
        
        if(!ObjectUtils.isEmpty(lsGeneralAdjuntoPlantillaDTO)) {
    		map.put("row", lsGeneralAdjuntoPlantillaDTO);
    	}else {
    		map.put("row", "No hay Adjuntos para la plantilla "+objNotPlantilla.get().getIdPlantilla());
    	}
        return map;
    }

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Map<String, Object> deleteAdjuntoPlantilla(Integer idAdjuntoPlantilla,String usuario)
			throws BOException, ClassNotFoundException, IOException {
		//Valido Campo requerido
		ValidacionUtil.validarCampoRequeridoBO(idAdjuntoPlantilla, "not.campo.idAdjuntoPlantilla");
	
		//Vlaido que exista y que este activo
		Optional<NotAdjuntoPlantilla> optNotAdjuntoPlantilla = objNotAdjuntoPlantillaDAO.findWithValidacionCampo(idAdjuntoPlantilla);
		
		//Le cambio por estado Inactivo (Eliminado Logico)
		optNotAdjuntoPlantilla.get().setEstado(Estado.INACTIVO.getName());
		optNotAdjuntoPlantilla.get().setUsuarioInactiva(usuario);
		optNotAdjuntoPlantilla.get().setFechaInactiva(FechasHelper.dateToString(new Date(), FormatoFecha.YYYY_MM_DD_HH_MM_SS));
		
		//Envio a Actualizar
		objNotAdjuntoPlantillaDAO.update(optNotAdjuntoPlantilla.get());
		
		 Map<String,Object> map=new HashMap<String,Object>() ;
		
		 map.put("row", optNotAdjuntoPlantilla);
		
		return map;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Map<String, Object> putAdjuntoPlantilla(RequestAdjuntoPlantillaDTO objRequestAdjuntoPlantilla,
			Integer idPlantilla, String usuario)
			throws BOException, ClassNotFoundException, IOException {
		
		//Valido Campo requerido
		ValidacionUtil.validarCampoRequeridoBO(idPlantilla, "not.campo.idAdjuntoPlantilla");
		
		//Valida campos requeridos
      	ValidacionUtil.validarCampoRequeridoBO(objRequestAdjuntoPlantilla.getNombreArchivo(),"not.campo.nombreArchivo");
		
		//Valido que exista y que este activo
		Optional<NotAdjuntoPlantilla> optNotAdjuntoPlantilla = objNotAdjuntoPlantillaDAO.findWithValidacionCampo(idPlantilla);
		
		
		//Obtengo la extension 
 		String ext = FilenameUtils.getExtension(objRequestAdjuntoPlantilla.getNombreArchivo().trim());
 		
 		//Consulta Extensiones Prohibidas
 		String extProhibidas = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.EXTENSIONES_PROHIBIDAS).getValor();
 		
 		String[] strExt = extProhibidas.toUpperCase().split(",");
 		
 		//Valida las estensiones
   		 if (Arrays.stream(strExt).anyMatch(StringUtils.upperCase(ext)::equals))
			throw new BOException("not.warn.campoInvalido", new Object[]{"not.campo.extension"});
   		 
   		//Consulta URL Base
  		String urlAdjunto = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.DESTINATION_URL_ATTACH).getValor();
  		//consulta carpeta donde se estan guardados los adjuntos
  		String uploadFilePath = objNotParametrosGeneralesDAO.findYValidar(NemonicoParametros.PATH_UPLOAD_FILE).getValor();
  		
  		//se genera la ruta completa donde esta alojado el adjunto
  		String fullPath = uploadFilePath.concat(objRequestAdjuntoPlantilla.getNombreArchivo());
		
		if(optNotAdjuntoPlantilla.isPresent()) {
			optNotAdjuntoPlantilla.get().setNombreArchivo(objRequestAdjuntoPlantilla.getNombreArchivo().trim()); 
			optNotAdjuntoPlantilla.get().setRutaLocal(fullPath); 
			optNotAdjuntoPlantilla.get().setExtension(ext); 
			optNotAdjuntoPlantilla.get().setUsuarioActualiza(usuario);
			optNotAdjuntoPlantilla.get().setFechaActualiza(FechasHelper.dateToString(new Date(), FormatoFecha.YYYY_MM_DD_HH_MM_SS));
			
		}
		
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("nombreArchivo", optNotAdjuntoPlantilla);
		
		return map;
	}
}
