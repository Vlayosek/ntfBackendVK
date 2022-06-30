package com.goit.notify.bo;

import java.io.IOException;
import java.util.Map;

import com.goit.notify.exceptions.BOException;

public interface INotParametroGeneralBO {

	/**
	 * Metodo Para consultar Parametro por nombre
	 * 
	 * @param strParametroGeneral
	 * @return
	 * @throws BOException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	/*
	 * Map<String, Object> getParametroGeneralXName(String strParametroGeneral)
	 * throws BOException, ClassNotFoundException, IOException;
	 * 
	 *//**
		 * Metood para consultar todos los Parametros Generales
		 * 
		 * @return
		 * @throws BOException
		 * @throws ClassNotFoundException
		 * @throws IOException
		 *//*
			 * Map<String, Object> consultarParametrosGenerales() throws BOException,
			 * ClassNotFoundException, IOException;
			 */

	/**
	 * Method for get General Parameter x Id
	 * 
	 * @param intIdParametroGeneral
	 * @return
	 * @throws BOException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	Map<String, Object> getParametroGeneralXId(Integer intIdParametroGeneral)
			throws BOException, ClassNotFoundException, IOException;
	
	/**
	 * Metodo Para consultar Parametro por nombre
	 * @param strParametroGeneral
	 * @return
	 * @throws BOException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	Map<String, Object> getParametroGeneralXName(String strParametroGeneral) 
			throws BOException, ClassNotFoundException, IOException;

}
