package com.goit.notify.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;

import com.goit.notify.dto.ListaPlantillasDTO;
import com.goit.notify.exceptions.BOException;
import com.goit.notify.model.NotPlantilla;

import lombok.NonNull;

@Service
public class NotPlantillasDAO extends BaseDAO<NotPlantilla,Integer> {
	
	protected NotPlantillasDAO() {
		super(NotPlantilla.class);
	}
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
	
	@Override
	public void persist(NotPlantilla t) throws PersistenceException {
		super.persist(t);
	}

	@Override
	public void update(NotPlantilla t) throws PersistenceException {
		super.update(t);
	}

	@Override
	public Optional<NotPlantilla> find(@NonNull Integer id) {
		return super.find(id);
	}	
	
	public Optional<NotPlantilla> ValidacionPlantilla(Integer id) throws BOException {
		
		Optional<NotPlantilla> objPlantilla = find(id);
		
		// Valida que exista
		if (!objPlantilla.isPresent())
			throw new BOException("not.warn.idPlantillaNoExiste", new Object[] { id });

		// Valida este activo.
		if (objPlantilla.get().getEstado() == null || !"A".equalsIgnoreCase(objPlantilla.get().getEstado()))
			throw new BOException("not.warn.idPlantillaInactiva", new Object[] { id });
		
		return objPlantilla;
	}
	
	public List <ListaPlantillasDTO> listarPlantillas () throws BOException{
		
		StringBuilder strJPQL = new StringBuilder();
		strJPQL.append(" SELECT nombre as nombre, ");
		strJPQL.append(" 	    descripcion as descripcion, ");
		strJPQL.append("        tipo as tipo, ");
		strJPQL.append("        estado as estado ");
		strJPQL.append(" FROM   NotPlantilla ");
		//strJPQL.append(" where estado = 'A' ");
		//almaceno mi query en una tupla
		TypedQuery<Tuple> query = em.createQuery(strJPQL.toString(), Tuple.class);

		//listo mi query
		//List<Tuple> respPlantillas = query.getResultList();

		return query.getResultList().stream().map(tuple -> {
			return ListaPlantillasDTO.builder().nombre(tuple.get("nombre", String.class))
					.descripcion(tuple.get("descripcion", String.class))
					.tipo(tuple.get("tipo",String.class))
					.descripcion(tuple.get("descripcion", String.class))
					.estado(tuple.get("estado", String.class))
					.build();
		}).collect(Collectors.toList());
	}
	
	
	public List consultarPlantilla(String idUsuario,String idEmpresa) {

		StringBuilder strJPQLBase = new StringBuilder();

		try {

			strJPQLBase.append("SELECT sc.idPlantilla as idPlantilla, ");
			strJPQLBase.append("	   sc.idUsuario as idUsuario,  ");
			strJPQLBase.append("	   sc.idEmpresa as idEmpresa  ");
			strJPQLBase.append("FROM  NotPlantilla sc ");
			strJPQLBase.append(" WHERE	 sc.idEmpresa=:idEmpresa ");
			strJPQLBase.append(" AND	 sc.idUsuario=:idUsuario ");
			strJPQLBase.append(" AND	 sc.idPlantilla= 2 ");


			Query query = em.createQuery(strJPQLBase.toString());

			query.setParameter("idEmpresa", idUsuario);
			query.setParameter("idUsuario", idEmpresa);

			// CanalDTOGeneral objCanalDto = new CanalDTOGeneral();
			
			List optNotPlantilla = null;

			//NotPlantilla objNotPlantilla = (NotPlantilla) query.getResultList();
			
			optNotPlantilla = query.getResultList();
			// objCanalDto.setId(obj[0].toString());
			return optNotPlantilla;

//			return (CanalDTOGeneral) query.getSingleResult();

		} catch (NoResultException e) {
			return null;
		}
	}

	public Long consultarPlantillasCount(String idUsuario, String idEmpresa) {
    	StringBuilder strJPQL = new StringBuilder();


			strJPQL.append(" SELECT count(ap)");
			strJPQL.append(" FROM 	NotPlantilla ap");
			strJPQL.append(" WHERE	 ap.estado='A' ");
			strJPQL.append(" AND	 ap.idEmpresa=:empresa ");
			strJPQL.append(" AND	 ap.idUsuario=:usuario ");
			
			Query query = em.createQuery(strJPQL.toString());
			
	        query.setParameter("usuario", idUsuario);
	        query.setParameter("empresa", idEmpresa);

	        Long lonCantQuery = (Long) query.getSingleResult();
	        
	        return lonCantQuery;

    	
    }
	
}
