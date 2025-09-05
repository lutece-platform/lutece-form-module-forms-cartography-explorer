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
 	
 
package fr.paris.lutece.plugins.formresponsxpage.web;

import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.html.AbstractPaginator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;


import fr.paris.lutece.plugins.formresponsxpage.business.Formsreponseedito;
import fr.paris.lutece.plugins.formresponsxpage.business.FormsreponseeditoHome;

/**
 * This class provides the user interface to manage Formsreponseedito features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageFormsreponseeditos.jsp", controllerPath = "jsp/admin/plugins/formresponsxpage/", right = "FORMRESPONSXPAGE_MANAGEMENT" )
public class FormsreponseeditoJspBean extends AbstractJspBean <Integer, Formsreponseedito>
{

	// Rights
	public static final String RIGHT_MANAGEADMINRESPONSEEDITO = "FORMRESPONSXPAGE_MANAGEMENT";
		
    // Templates
    private static final String TEMPLATE_MANAGE_FORMSREPONSEEDITOS = "/admin/plugins/formresponsxpage/manage_formsreponseeditos.html";
    private static final String TEMPLATE_CREATE_FORMSREPONSEEDITO = "/admin/plugins/formresponsxpage/create_formsreponseedito.html";
    private static final String TEMPLATE_MODIFY_FORMSREPONSEEDITO = "/admin/plugins/formresponsxpage/modify_formsreponseedito.html";

    // Parameters
    private static final String PARAMETER_ID_FORMSREPONSEEDITO = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_FORMSREPONSEEDITOS = "formresponsxpage.manage_formsreponseeditos.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_FORMSREPONSEEDITO = "formresponsxpage.modify_formsreponseedito.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_FORMSREPONSEEDITO = "formresponsxpage.create_formsreponseedito.pageTitle";

    // Markers
    private static final String MARK_FORMSREPONSEEDITO_LIST = "formsreponseedito_list";
    private static final String MARK_FORMSREPONSEEDITO = "formsreponseedito";

    private static final String JSP_MANAGE_FORMSREPONSEEDITOS = "jsp/admin/plugins/formresponsxpage/ManageFormsreponseeditos.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_FORMSREPONSEEDITO = "formresponsxpage.message.confirmRemoveFormsreponseedito";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "formresponsxpage.model.entity.formsreponseedito.attribute.";

    // Views
    private static final String VIEW_MANAGE_FORMSREPONSEEDITOS = "manageFormsreponseeditos";
    private static final String VIEW_CREATE_FORMSREPONSEEDITO = "createFormsreponseedito";
    private static final String VIEW_MODIFY_FORMSREPONSEEDITO = "modifyFormsreponseedito";

    // Actions
    private static final String ACTION_CREATE_FORMSREPONSEEDITO = "createFormsreponseedito";
    private static final String ACTION_MODIFY_FORMSREPONSEEDITO = "modifyFormsreponseedito";
    private static final String ACTION_REMOVE_FORMSREPONSEEDITO = "removeFormsreponseedito";
    private static final String ACTION_CONFIRM_REMOVE_FORMSREPONSEEDITO = "confirmRemoveFormsreponseedito";

    // Infos
    private static final String INFO_FORMSREPONSEEDITO_CREATED = "formresponsxpage.info.formsreponseedito.created";
    private static final String INFO_FORMSREPONSEEDITO_UPDATED = "formresponsxpage.info.formsreponseedito.updated";
    private static final String INFO_FORMSREPONSEEDITO_REMOVED = "formresponsxpage.info.formsreponseedito.removed";
    
    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    
    // Session variable to store working values
    private Formsreponseedito _formsreponseedito;
    private List<Integer> _listIdFormsreponseeditos;
    private HashMap<String,String> _mapFilterCriteria = new HashMap<>();
    private String _optionOrderBy;
    
    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_FORMSREPONSEEDITOS )
    public String getManageFormsreponseeditos( HttpServletRequest request )
    {
        _formsreponseedito = null;
        
        // new search only if in pagination mode
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX) == null )
        {
        	// if sorting request : new search with the existing filter criteria, ordered 
        	// example of order by parameter : orderby=name
        	if ( StringUtils.isNotBlank( (String)request.getParameter(PARAMETER_SEARCH_ORDER_BY) ) )
        	{
        		
        		String strOrderByColumn =  (String)request.getParameter(PARAMETER_SEARCH_ORDER_BY);
        		String strSortMode = getSortMode(); 
        		
        		_listIdFormsreponseeditos = FormsreponseeditoHome.getIdFormsreponseeditosList( _mapFilterCriteria, strOrderByColumn, strSortMode );
               	
	       	}
	       	else
	       	{
	       		// reload the filter criteria and search
	       		_mapFilterCriteria = (HashMap<String, String>) getFilterCriteriaFromRequest( request );
	       		_listIdFormsreponseeditos = FormsreponseeditoHome.getIdFormsreponseeditosList( _mapFilterCriteria, null ,null);
	       	}
        	
        	//set CurrentPageIndex of Paginator to null in aim of displays the first page of results
        	resetCurrentPageIndexOfPaginator();
        }
       	
       	Map<String, Object> model = getPaginatedListModel( request, MARK_FORMSREPONSEEDITO_LIST, _listIdFormsreponseeditos, JSP_MANAGE_FORMSREPONSEEDITOS );
             
        addSearchParameters(model,_mapFilterCriteria); //allow the persistence of search values in inputs search bar inputs
                     
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_FORMSREPONSEEDITOS, TEMPLATE_MANAGE_FORMSREPONSEEDITOS, model );

    }

	/**
     * Get Items from Ids list
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
	@Override
	List<Formsreponseedito> getItemsFromIds( List<Integer> listIds ) 
	{
		List<Formsreponseedito> listFormsreponseedito = FormsreponseeditoHome.getFormsreponseeditosListByIds( listIds );
		
		// keep original order
        return listFormsreponseedito.stream()
                 .sorted(Comparator.comparingInt( notif -> listIds.indexOf( notif.getId())))
                 .collect(Collectors.toList());
	}
	
	@Override
	int getPluginDefaultNumberOfItemPerPage( ) {
		return AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
	}
    
    /**
    * reset the _listIdFormsreponseeditos list
    */
    public void resetListId( )
    {
    	_listIdFormsreponseeditos = new ArrayList<>( );
    }
    
    
    /**
     * Returns the form to update info about a formsreponseedito
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( value = VIEW_MODIFY_FORMSREPONSEEDITO, defaultView = true )
    public String getModifyFormsreponseedito( HttpServletRequest request )
    {
        //int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_FORMSREPONSEEDITO ) );
    	int nId = 1;

        if ( _formsreponseedito == null || ( _formsreponseedito.getId(  ) != nId ) )
        {
            Optional<Formsreponseedito> optFormsreponseedito = FormsreponseeditoHome.findByPrimaryKey( nId );
            _formsreponseedito = optFormsreponseedito.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }


        Map<String, Object> model = getModel(  );
        model.put( MARK_FORMSREPONSEEDITO, _formsreponseedito );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_FORMSREPONSEEDITO ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_FORMSREPONSEEDITO, TEMPLATE_MODIFY_FORMSREPONSEEDITO, model );
    }

    /**
     * Process the change form of a formsreponseedito
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_FORMSREPONSEEDITO )
    public String doModifyFormsreponseedito( HttpServletRequest request ) throws AccessDeniedException
    {   
        populate( _formsreponseedito, request, getLocale( ) );
		
		
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_FORMSREPONSEEDITO ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _formsreponseedito, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_FORMSREPONSEEDITO, PARAMETER_ID_FORMSREPONSEEDITO, _formsreponseedito.getId( ) );
        }

        FormsreponseeditoHome.update( _formsreponseedito );
        addInfo( INFO_FORMSREPONSEEDITO_UPDATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MODIFY_FORMSREPONSEEDITO );
    }
}
