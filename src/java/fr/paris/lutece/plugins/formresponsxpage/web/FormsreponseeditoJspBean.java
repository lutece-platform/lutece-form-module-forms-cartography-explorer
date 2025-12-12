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

import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.binding.BindingResult;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.ModelAttribute;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.cdi.mvc.Models;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import fr.paris.lutece.plugins.formresponsxpage.business.Formsreponseedito;
import fr.paris.lutece.plugins.formresponsxpage.business.FormsreponseeditoHome;

/**
 * This class provides the user interface to manage Formsreponseedito features ( manage, create, modify, remove )
 */
@RequestScoped
@Named
@Controller( controllerJsp = "ManageFormsreponseeditos.jsp", controllerPath = "jsp/admin/plugins/formresponsxpage/", right = "FORMRESPONSXPAGE_MANAGEMENT", securityTokenEnabled = true )
public class FormsreponseeditoJspBean extends MVCAdminJspBean
{

	// Rights
	public static final String RIGHT_MANAGEADMINRESPONSEEDITO = "FORMRESPONSXPAGE_MANAGEMENT";
		
    // Templates
    private static final String TEMPLATE_MODIFY_FORMSREPONSEEDITO = "/admin/plugins/forms/modules/formresponseexplorer/modify_formsreponseedito.html";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MODIFY_FORMSREPONSEEDITO = "formresponsxpage.modify_formsreponseedito.pageTitle";

    // Markers
    private static final String MARK_FORMSREPONSEEDITO = "formsreponseedito";

    // Views
    private static final String VIEW_MODIFY_FORMSREPONSEEDITO = "modifyFormsreponseedito";

    // Actions
    private static final String ACTION_MODIFY_FORMSREPONSEEDITO = "modifyFormsreponseedito";

    // Infos
    private static final String INFO_FORMSREPONSEEDITO_UPDATED = "formresponsxpage.info.formsreponseedito.updated";
    
    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    
    @Inject
    private Models _model;
    
    /**
     * Returns the form to update info about a formsreponseedito
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( value = VIEW_MODIFY_FORMSREPONSEEDITO, defaultView = true )
    public String getModifyFormsreponseedito( HttpServletRequest request )
    {
        Formsreponseedito formsreponseedito = FormsreponseeditoHome.findByPrimaryKey( 1 ).orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        _model.put( MARK_FORMSREPONSEEDITO, formsreponseedito );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_FORMSREPONSEEDITO, TEMPLATE_MODIFY_FORMSREPONSEEDITO, _model );
    }

    /**
     * Process the change form of a formsreponseedito
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( value = ACTION_MODIFY_FORMSREPONSEEDITO, securityTokenDisabled = true )
    public String doModifyFormsreponseedito( @Valid @ModelAttribute Formsreponseedito formsreponseedito, BindingResult bindingResult, HttpServletRequest request ) throws AccessDeniedException
    {   
    	if( bindingResult.isFailed( ) )
    	{
    		_model.put( MVCUtils.MARK_ERRORS, bindingResult.getAllErrors( ) );
    		_model.put( MARK_FORMSREPONSEEDITO, formsreponseedito );
    		return getPage( PROPERTY_PAGE_TITLE_MODIFY_FORMSREPONSEEDITO, TEMPLATE_MODIFY_FORMSREPONSEEDITO, _model );
    	}
    	
    	FormsreponseeditoHome.update( formsreponseedito );
        addInfo( INFO_FORMSREPONSEEDITO_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MODIFY_FORMSREPONSEEDITO );
    }
}