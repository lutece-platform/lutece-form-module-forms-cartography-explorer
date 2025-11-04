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
import fr.paris.lutece.plugins.search.solr.business.SolrFacetedResult;
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
    private static final String TEMPLATE_VIEW_LIST_FORM_RESPONSES = "skin/plugins/forms/modules/formresponseexplorer/view_list_form_responses.html";

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
    
    //NLG
    @View( value = VIEW_FORM_LIST_RESPONSES, defaultView = true )
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
        	SolrFacetedResult facetedSearchResults = engine.getFacetedSearchResults( PARAMETER_SOLR_GEOJSON + ":" + datalayer.getSolrTag( ), null, "DataLayer_text", "desc", 100, 1, 100, false);
        	
        	List<SolrSearchResult> solrSearchResults = facetedSearchResults.getSolrSearchResults();
        	
        	
            Optional<DataLayerMapTemplate> dataLayerMapTemplate = DataLayerMapTemplateHome.findByIdMapKeyIdDataLayerKey( xpageFrontOfficeMapTemplate.get().getId( ), datalayer.getId( ) );
        	points.addAll( CartographyService.getGeolocModel( solrSearchResults, datalayer, dataLayerMapTemplate.get( ) ) );
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
    
}
