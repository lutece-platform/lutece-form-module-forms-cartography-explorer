/*
 * Copyright (c) 2002-2025, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */


package fr.paris.lutece.plugins.formresponsxpage.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

/**
 * This class provides Data Access methods for Formsreponseedito objects
 */
public final class FormsreponseeditoDAO extends AbstractFilterDao implements IFormsreponseeditoDAO
{
    // Constants
    private static final String SQL_QUERY_INSERT = "INSERT INTO formresponsxpage_formsreponseedito ( labelrichtext_un, labelrichtext_deux ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM formresponsxpage_formsreponseedito WHERE id_formsreponseedito = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE formresponsxpage_formsreponseedito SET labelrichtext_un = ?, labelrichtext_deux = ? WHERE id_formsreponseedito = ?";
   
	private static final String SQL_QUERY_SELECTALL = "SELECT id_formsreponseedito, labelrichtext_un, labelrichtext_deux FROM formresponsxpage_formsreponseedito";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_formsreponseedito FROM formresponsxpage_formsreponseedito";

    private static final String SQL_QUERY_SELECTALL_BY_IDS = SQL_QUERY_SELECTALL + " WHERE id_formsreponseedito IN (  ";
	private static final String SQL_QUERY_SELECT_BY_ID = SQL_QUERY_SELECTALL + " WHERE id_formsreponseedito = ?";


	/**
     * Constructor
     */
	public FormsreponseeditoDAO() {

		initMapSql(Formsreponseedito.class); //Maps with name and type of each databases column associated to the business class attributes 
	}

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Formsreponseedito formsreponseedito, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++ , formsreponseedito.getLabelrichtextUn( ) );
            daoUtil.setString( nIndex++ , formsreponseedito.getLabelrichtextDeux( ) );
            
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) ) 
            {
                formsreponseedito.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<Formsreponseedito> load( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeQuery( );
	        Formsreponseedito formsreponseedito = null;
	
	        if ( daoUtil.next( ) )
	        {
	            formsreponseedito = loadFromDaoUtil( daoUtil );
	        }
	
	        return Optional.ofNullable( formsreponseedito );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Formsreponseedito formsreponseedito, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
	        int nIndex = 1;
	        
            	daoUtil.setString( nIndex++ , formsreponseedito.getLabelrichtextUn( ) );
            	daoUtil.setString( nIndex++ , formsreponseedito.getLabelrichtextDeux( ) );
	        daoUtil.setInt( nIndex , formsreponseedito.getId( ) );
	
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Formsreponseedito> selectFormsreponseeditosList( Plugin plugin )
    {
        List<Formsreponseedito> formsreponseeditoList = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
				formsreponseeditoList.add( loadFromDaoUtil( daoUtil ) );
	        }
	
	        return formsreponseeditoList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdFormsreponseeditosList( Plugin plugin,  Map <String,String> mapFilterCriteria, String strColumnToOrder, String strSortMode )
    {
        List<Integer> formsreponseeditoList = new ArrayList<>( );
        
        String strSelectStatement =  prepareSelectStatement(SQL_QUERY_SELECTALL_ID, mapFilterCriteria, strColumnToOrder, strSortMode);  
        
        try( DAOUtil daoUtil = new DAOUtil( strSelectStatement, plugin ) )
        {
        
        	int nIndex = 1;
    	        
   	        for(Map.Entry<String, String> filter : mapFilterCriteria.entrySet()) {
   	        	
   	        	if(StringUtils.isNotBlank(filter.getValue())  && _mapSql.containsKey(filter.getKey())) {
   	        		daoUtil.setString( nIndex++ , filter.getValue() );
   	        	}
   	        }
    	        
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            formsreponseeditoList.add( daoUtil.getInt( 1 ) );
	        }
	
	        return formsreponseeditoList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectFormsreponseeditosReferenceList( Plugin plugin )
    {
        ReferenceList formsreponseeditoList = new ReferenceList();
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            formsreponseeditoList.addItem( daoUtil.getInt( 1 ) , daoUtil.getString( 2 ) );
	        }
	
	        return formsreponseeditoList;
    	}
    }
    
    /**
     * {@inheritDoc }
     */
	@Override
	public List<Formsreponseedito> selectFormsreponseeditosListByIds( Plugin plugin, List<Integer> listIds ) {
		List<Formsreponseedito> formsreponseeditoList = new ArrayList<>(  );
		
		StringBuilder builder = new StringBuilder( );

		if ( !listIds.isEmpty( ) )
		{
			for( int i = 0 ; i < listIds.size(); i++ ) {
			    builder.append( "?," );
			}
	
			String placeHolders =  builder.deleteCharAt( builder.length( ) -1 ).toString( );
			String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + ")";
			
			
	        try ( DAOUtil daoUtil = new DAOUtil( stmt, plugin ) )
	        {
	        	int index = 1;
				for( Integer n : listIds ) {
					daoUtil.setInt(  index++, n ); 
				}
	        	
	        	daoUtil.executeQuery(  );
	        	while ( daoUtil.next(  ) )
		        {
		            formsreponseeditoList.add( loadFromDaoUtil( daoUtil ) );
		        }
	        }
	    }
		return formsreponseeditoList;
		
	}


	private Formsreponseedito loadFromDaoUtil (DAOUtil daoUtil) {
		
		Formsreponseedito formsreponseedito = new Formsreponseedito(  );
		int nIndex = 1;
		
		formsreponseedito.setId( daoUtil.getInt( nIndex++ ) );
		formsreponseedito.setLabelrichtextUn( daoUtil.getString( nIndex++ ) );
		formsreponseedito.setLabelrichtextDeux( daoUtil.getString( nIndex ) );
		
		return formsreponseedito;
	}
}
