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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.forms.web.entrytype.EntryTypeFileDisplayService;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDisplayService;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.leaflet.business.GeolocItem;
import fr.paris.lutece.plugins.leaflet.service.IconService;
import fr.paris.lutece.plugins.search.solr.business.SolrSearchEngine;
import fr.paris.lutece.plugins.search.solr.business.SolrSearchResult;
import fr.paris.lutece.plugins.search.solr.indexer.SolrItem;
import fr.paris.lutece.portal.business.file.File;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.carto.business.DataLayer;
import fr.paris.lutece.plugins.carto.business.DataLayerMapTemplate;
import fr.paris.lutece.plugins.carto.business.DataLayerMapTemplateHome;
import fr.paris.lutece.plugins.carto.business.MapTemplate;
import fr.paris.lutece.plugins.carto.business.MapTemplateHome;
import fr.paris.lutece.plugins.cartography.modules.solr.service.CartographyService;
import fr.paris.lutece.plugins.formresponsxpage.business.Formsreponseedito;
import fr.paris.lutece.plugins.formresponsxpage.business.FormsreponseeditoHome;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.service.upload.FormsAsynchronousUploadHandler;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsResponseUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.html.DelegatePaginator;
import fr.paris.lutece.util.html.IPaginator;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

/**
 *
 * Controller for formResponse display
 *
 */
@Controller( xpageName = FormsResponseEditoXPage.XPAGE_NAME, pageTitleI18nKey = FormsResponseEditoXPage.MESSAGE_PAGE_TITLE, pagePathI18nKey = FormsResponseEditoXPage.MESSAGE_PATH )
public class FormsResponseEditoXPage extends MVCApplication
{
    public static final String XPAGE_NAME = "formsResponseEdito";

    /**
     * Generated serial id
     */
    private static final long serialVersionUID = 8146530527615651620L;

    // Messages
    protected static final String MESSAGE_PAGE_TITLE = "forms.response.xpage.form.view.pageTitle";
    protected static final String MESSAGE_PATH = "forms.response.xpage.form.view.pagePathLabel";
    protected static final String MESSAGE_ERROR_NOT_PUBLISHED_FORM_RESPONSE = "forms.xpage.response.error.inactive";
    protected static final String MESSAGE_ERROR_NOT_FOUND_FORM_RESPONSE = "forms.xpage.response.error.notfound";
    protected static final String MESSAGE_FORM_RESPONSE_PAGETITLE = "forms.xpage.response.pagetitle";
    protected static final String MESSAGE_FORM_RESPONSE_PATHLABEL = "forms.xpage.response.pathlabel";
    private static final String MESSAGE_ACTION_ERROR = "forms.xpage.response.action.error";
    private static final String MESSAGE_ERROR_TOKEN = "Invalid security token";
    private static final String MESSAGE_ACTION_SUCCESS = "forms.xpage.response.action.success";

    public static final String PARAMETER_SOLR_GEOJSON = "DataLayer_text";
    
    private static final String PROPERTY_RESULTS_PER_PAGE = "search.nb.docs.per.page";
    private static final int DEFAULT_RESULTS_PER_PAGE = 10;
    private static final String PARAMETER_NB_ITEMS_PER_PAGE = "items_per_page";
    private static final String DEFAULT_PAGE_INDEX = "1";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    
    // Views
    public static final String VIEW_FORM_RESPONSE = "formResponseView";
    public static final String VIEW_FORM_FILE = "formFileView";
    public static final String VIEW_FORM_LIST_RESPONSES = "formListResponsesView";

    // Actions
    private static final String ACTION_PROCESS_ACTION = "doProcessAction";
    private static final String ACTION_SAVE_TASK_FORM = "doSaveTaskForm";

    // Templates
    private static final String TEMPLATE_VIEW_FORM_RESPONSE = "/skin/plugins/forms/view_form_response.html";
    private static final String TEMPLATE_VIEW_FORM_FILE = "/skin/plugins/forms/view_form_file.html";
    private static final String TEMPLATE_TASK_FORM_RESPONSE = "/skin/plugins/forms/task_form_workflow.html";
    private static final String TEMPLATE_VIEW_LIST_FORM_RESPONSES = "/skin/formresponsesedito/view_list_form_responses.html";

    // Marks
    private static final String MARK_WORKFLOW_ACTION_LIST = "workflow_action_list";
    private static final String MARK_ID_FORM_RESPONSE = "id_form_response";
    private static final String MARK_ID_ACTION = "id_action";
    private static final String MARK_TASK_FORM = "tasks_form";
    private static final String MARK_LIST_FILE = "listFiles";
    private static final String MARK_LIST_FORMRESPONSE = "listFormResponses";
    private static final String MARK_FORMRESPONSES_LIST = "listFormResponsePaginator";
    private static final String MARK_FORMSREPONSEEDITO = "formsreponseedito";
    private static final String MARK_URL_PAGINATOR = "urlPaginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    
    private static final String FULL_URL = "fullUrl";
    
    private static final String MARK_POINTS = "points";
    private static final String MARK_POINTS_GEOJSON = "geojson";
    private static final String MARK_POINTS_ID = "id";
    private static final String MARK_POINTS_FIELDCODE = "code";
    private static final String MARK_POINTS_TYPE = "type";
    private static final String MARK_PAGINATOR = "paginator";
    
    private int _nItemsPerPage;
    
 // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Parameters
    private static final String PARAMETER_ID_ACTION = "id_action";

    @View( value = VIEW_FORM_RESPONSE, defaultView = true )
    public XPage getFormResponseView( HttpServletRequest request ) throws SiteMessageException
    {
        Locale locale = getLocale( request );
        FormResponse formResponse = findFormResponseFrom( request );

        Collection<Action> actionsList = getActionsForUser( request, formResponse );
        if("true".equals(request.getParameter(FormsConstants.PARAMETER_ACTION_SUCCESS)))
        {
            addInfo( MESSAGE_ACTION_SUCCESS,getLocale(request) );
        }
        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_FORM_RESPONSE, formResponse );
        model.put( MARK_WORKFLOW_ACTION_LIST, actionsList );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_PROCESS_ACTION ) );

        XPage xPage = getXPage( TEMPLATE_VIEW_FORM_RESPONSE, getLocale( request ), model );
        xPage.setTitle( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PAGETITLE, locale ) );
        xPage.setPathLabel( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PATHLABEL, locale ) );

        return xPage;
    }
    
    //NLG
    @View( value = VIEW_FORM_LIST_RESPONSES, defaultView = false )
    public XPage getFormListeResponsesView( HttpServletRequest request ) throws SiteMessageException
    {
        Locale locale = getLocale( request );
        List<FormResponse> lstFormResponse = FormResponseHome.selectAllFormResponses();

        Map<String, Object> model = getModel( );
        
        SolrSearchEngine engine = SolrSearchEngine.getInstance( );
        
        List<HashMap<String, Object>> points = new ArrayList<HashMap<String, Object>>( );
        
        Optional<MapTemplate> xpageFrontOfficeMapTemplate = MapTemplateHome.findXpageFrontOffice();
        List<DataLayer> lstDatalayers = DataLayerMapTemplateHome.getDataLayerListByMapTemplateId( xpageFrontOfficeMapTemplate.get().getId( ) );
        
        for ( DataLayer datalayer : lstDatalayers )
        {
        	List<SolrSearchResult> listResultsGeoloc = null;
        	listResultsGeoloc = engine.getGeolocSearchResults( PARAMETER_SOLR_GEOJSON + ":" + datalayer.getSolrTag( ), null, 100 );
            Optional<DataLayerMapTemplate> dataLayerMapTemplate = DataLayerMapTemplateHome.findByIdMapKeyIdDataLayerKey( xpageFrontOfficeMapTemplate.get().getId( ), datalayer.getId( ) );
        	points.addAll( CartographyService.getGeolocModel( listResultsGeoloc, datalayer, dataLayerMapTemplate.get( ) ) );
        }
                
        // paginator & session related elements
        String strCurrentPageIndex = request.getParameter( PARAMETER_PAGE_INDEX );
        int nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_RESULTS_PER_PAGE, DEFAULT_RESULTS_PER_PAGE );
        String strCurrentItemsPerPage = request.getParameter( PARAMETER_NB_ITEMS_PER_PAGE );
        int nCurrentItemsPerPage = strCurrentItemsPerPage != null ? Integer.parseInt( strCurrentItemsPerPage ) : 0;

        strCurrentPageIndex = ( strCurrentPageIndex != null ) ? strCurrentPageIndex : DEFAULT_PAGE_INDEX;
        
        UrlItem url = new UrlItem( "jsp/site/Portal.jsp?page=formsResponseEdito&view=formListResponsesView" );
        String strUrl = url.getUrl( );
        
        _nItemsPerPage = AbstractPaginator.getItemsPerPage( request, AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, nDefaultItemsPerPage );
        
        Paginator<FormResponse> paginatorList = new LocalizedPaginator<>( lstFormResponse, _nItemsPerPage, strUrl, AbstractPaginator.PARAMETER_PAGE_INDEX,
       		strCurrentPageIndex, getLocale(request ) );
        
        int nId = 1;
        Formsreponseedito formsreponseedito;
        Optional<Formsreponseedito> optFormsreponseedito = FormsreponseeditoHome.findByPrimaryKey( nId );
        formsreponseedito = optFormsreponseedito.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );

        
        // Récupère la page courante depuis la requête
        Paginator.getPageIndex(request, strUrl, strCurrentPageIndex);

        model.put( MARK_FORMSREPONSEEDITO, formsreponseedito );
        
        model.put( MARK_POINTS, points );
        model.put( MARK_PAGINATOR, paginatorList );
        model.put( MARK_FORMRESPONSES_LIST, paginatorList.getPageItems( ) );
        model.put( MARK_LIST_FORMRESPONSE, lstFormResponse);
        model.put( MARK_URL_PAGINATOR, strUrl );
        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_PROCESS_ACTION ) );
        
        String strRequestUrl = request.getRequestURL( ).toString( );
        model.put( FULL_URL, strRequestUrl );

        XPage xPage = getXPage( TEMPLATE_VIEW_LIST_FORM_RESPONSES, getLocale( request ), model );
        xPage.setTitle( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PAGETITLE, locale ) );
        xPage.setPathLabel( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PATHLABEL, locale ) );

        return xPage;
    }
    

    @View( value = VIEW_FORM_FILE, defaultView = false )
    public XPage getFormFileView( HttpServletRequest request ) throws SiteMessageException
    {
        Locale locale = getLocale( request );
        FormResponse formResponse = findFormResponseFrom( request );
        List<Response> listResponse = new ArrayList<Response>();
        if("true".equals(request.getParameter(FormsConstants.PARAMETER_ACTION_SUCCESS)))
        {
            addInfo( MESSAGE_ACTION_SUCCESS,getLocale(request) );
        }
        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_FORM_RESPONSE, formResponse );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_PROCESS_ACTION ) );
        for (FormResponseStep step: formResponse.getSteps() ){
            listResponse.addAll(findResponses(step.getQuestions()));
        }
        model.put( FormsConstants.MARK_QUESTION_LIST_RESPONSES, listResponse );

        formResponse.getSteps().stream()
                .flatMap(step -> step.getQuestions().stream())
                .forEach(fqr -> {
                    IEntryDisplayService displayService = EntryServiceManager.getInstance()
                            .getEntryDisplayService(fqr.getQuestion().getEntry().getEntryType());
                    if (displayService instanceof EntryTypeFileDisplayService) {
                        displayService.getEntryTemplateDisplay(request, fqr.getQuestion().getEntry(), locale, model, DisplayType.READONLY_FRONTOFFICE);
                    }
                });

        List<File> listFiles = formResponse.getSteps().stream()
                .flatMap(step -> step.getQuestions().stream())
                .filter(fqr -> fqr.getQuestion().getEntry().isOnlyDisplayInBack())
                .flatMap(fqr -> fqr.getEntryResponse().stream())
                .map(Response::getFile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        model.put( MARK_LIST_FILE, listFiles );
        XPage xPage = getXPage( TEMPLATE_VIEW_FORM_FILE, getLocale( request ), model );
        xPage.setTitle( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PAGETITLE, locale ) );
        xPage.setPathLabel( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PATHLABEL, locale ) );

        return xPage;
    }

    @fr.paris.lutece.portal.util.mvc.commons.annotations.Action( value = ACTION_PROCESS_ACTION )
    public XPage doProcessAction( HttpServletRequest request ) throws AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_PROCESS_ACTION ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
        // Get parameters from request
        int nIdFormResponse = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_RESPONSE ), NumberUtils.INTEGER_MINUS_ONE );
        int nIdAction = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ACTION ), NumberUtils.INTEGER_MINUS_ONE );

        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
        FormResponse formResponse = FormResponseHome.findByPrimaryKey( nIdFormResponse );

        if (  formResponse == null || !FormsResponseUtils.isAuthorized(formResponse, user) )
        {
            return redirect( request, VIEW_FORM_RESPONSE, FormsConstants.PARAMETER_ID_RESPONSE, nIdFormResponse );
        }

        Locale locale = getLocale( request );
        WorkflowService workflowService = WorkflowService.getInstance( );
        if ( workflowService.isDisplayTasksForm( nIdAction, locale ) )
        {
            FormsAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ) );

            String strHtmlTasksForm = WorkflowService.getInstance( ).getDisplayTasksForm( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, request,
                    locale, null );

            Map<String, Object> model = new LinkedHashMap<>( );
            model.put( MARK_ID_FORM_RESPONSE, String.valueOf( nIdFormResponse ) );
            model.put( MARK_ID_ACTION, String.valueOf( nIdAction ) );
            model.put( MARK_TASK_FORM, strHtmlTasksForm );
            model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_SAVE_TASK_FORM ) );

            XPage xPage = getXPage( TEMPLATE_TASK_FORM_RESPONSE, locale, model );
            xPage.setTitle( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PAGETITLE, locale ) );
            xPage.setPathLabel( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PATHLABEL, locale ) );

            return xPage;
        }

        try
        {
            workflowService.doProcessAction( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, formResponse.getFormId( ), request, locale, false, user );
        }
        catch( AppException e )
        {
            AppLogService.error( "Error processing action for id response '" + nIdFormResponse + "' - cause : " + e.getMessage( ), e );
        }
        // Redirect to the correct view
        return redirect( request, VIEW_FORM_RESPONSE, FormsConstants.PARAMETER_ID_RESPONSE, nIdFormResponse );
    }

    /**
     * Process workflow action
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @fr.paris.lutece.portal.util.mvc.commons.annotations.Action( value = ACTION_SAVE_TASK_FORM )
    public XPage doSaveTaskForm( HttpServletRequest request ) throws AccessDeniedException
    {
        int nIdFormResponse = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_RESPONSE ), NumberUtils.INTEGER_MINUS_ONE );
        int nIdAction = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ACTION ), NumberUtils.INTEGER_MINUS_ONE );

        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
        FormResponse formResponse = FormResponseHome.findByPrimaryKey( nIdFormResponse );

        if ( formResponse == null || !FormsResponseUtils.isAuthorized(formResponse, user) )
        {
            return redirect( request, VIEW_FORM_RESPONSE, FormsConstants.PARAMETER_ID_RESPONSE, nIdFormResponse );
        }

        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_SAVE_TASK_FORM ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }

        int nIdForm = formResponse.getFormId( );
        Locale locale = getLocale( request );
        WorkflowService workflowService = WorkflowService.getInstance( );

        if ( workflowService.canProcessAction( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, nIdForm, request, false, user ) )
        {
            try
            {
                String strError = workflowService.doSaveTasksForm( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, nIdForm, request, locale, user );
                if ( strError != null )
                {
                    return redirect( request, strError );
                }
            }
            catch( AppException e )
            {
                AppLogService.error( "Error processing action for record " + nIdFormResponse, e );
            }
        }
        else
        {
            addError( MESSAGE_ACTION_ERROR, locale );
        }
        return redirect( request, VIEW_FORM_RESPONSE, FormsConstants.PARAMETER_ID_RESPONSE, nIdFormResponse );
    }

    private Collection<Action> getActionsForUser( HttpServletRequest request, FormResponse formResponse )
    {
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
        if (formResponse != null)
        {
            Form form = FormHome.findByPrimaryKey( formResponse.getFormId( ) );
            if (FormsResponseUtils.isAuthorized(formResponse, SecurityService.getInstance( ).getRegisteredUser( request ), form ))
            {
                WorkflowService workflowService = WorkflowService.getInstance( );
                boolean workflowEnabled = workflowService.isAvailable( ) && ( form.getIdWorkflow( ) != FormsConstants.DEFAULT_ID_VALUE );

                if ( workflowEnabled )
                {
                    return workflowService.getActions( formResponse.getId( ), FormResponse.RESOURCE_TYPE, form.getIdWorkflow( ), (User) user );
                }
            }
        }
        return new ArrayList<>( );
    }

    /**
     * Finds the formResponse from the specified request
     *
     * @param request
     *            the request
     * @return the found formResponse, or {@code null} if not found
     * @throws FormResponseNotFoundException
     *             if the form is not found
     * @throws SiteMessageException
     *             if the formResponse is not accessible
     */
    private FormResponse findFormResponseFrom( HttpServletRequest request ) throws SiteMessageException
    {
        FormResponse formResponse = null;
        int nIdFormResponse = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_RESPONSE ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdFormResponse != FormsConstants.DEFAULT_ID_VALUE )
        {
            formResponse = FormResponseHome.findByPrimaryKey( nIdFormResponse );
        }
        else
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_NOT_FOUND_FORM_RESPONSE, SiteMessage.TYPE_ERROR );
        }

        if ( formResponse == null )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_NOT_FOUND_FORM_RESPONSE, SiteMessage.TYPE_ERROR );
        }
        else if ( !formResponse.isPublished( ) && !FormsResponseUtils.isAuthorized(formResponse, SecurityService.getInstance( ).getRegisteredUser( request ) ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_NOT_PUBLISHED_FORM_RESPONSE, SiteMessage.TYPE_ERROR );
        }

        return formResponse;
    }

    private List<Response> findResponses(List<FormQuestionResponse> listFormQuestionResponse )
    {
        List<Response> listResponse = new ArrayList<>( );

        if ( listFormQuestionResponse != null )
        {
            for ( FormQuestionResponse formQuestionResponse : listFormQuestionResponse )
            {
                listResponse.addAll(formQuestionResponse.getEntryResponse( ));
            }
        }

        return listResponse;
    }
}
