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
 * SUBSTITUTE GOODS OR SERVICES LOSS OF USE, DATA, OR PROFITS OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */

package fr.paris.lutece.plugins.formresponsxpage.web;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import java.util.List;
import java.io.IOException;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.plugins.formresponsxpage.business.Formsreponseedito;
import fr.paris.lutece.plugins.formresponsxpage.business.FormsreponseeditoHome;
/**
 * This is the business class test for the object Formsreponseedito
 */
public class FormsreponseeditoJspBeanTest extends LuteceTestCase
{
    private static final String LABELRICHTEXTUN1 = "LabelrichtextUn1";
    private static final String LABELRICHTEXTUN2 = "LabelrichtextUn2";
    private static final String LABELRICHTEXTDEUX1 = "LabelrichtextDeux1";
    private static final String LABELRICHTEXTDEUX2 = "LabelrichtextDeux2";

public void testJspBeans(  ) throws AccessDeniedException, IOException
	{	
     	MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockServletConfig config = new MockServletConfig();

		//display admin Formsreponseedito management JSP
		FormsreponseeditoJspBean jspbean = new FormsreponseeditoJspBean();
		String html = jspbean.getManageFormsreponseeditos( request );
		assertNotNull(html);

		//action create Formsreponseedito
		request = new MockHttpServletRequest();

		response = new MockHttpServletResponse( );
		AdminUser adminUser = new AdminUser( );
		adminUser.setAccessCode( "admin" );
		
        
        request.addParameter( "labelrichtext_un" , LABELRICHTEXTUN1 );
        request.addParameter( "labelrichtext_deux" , LABELRICHTEXTDEUX1 );
		request.addParameter("action","createFormsreponseedito");
        request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "createFormsreponseedito" ));
		request.setMethod( "POST" );
        
		
		try 
		{
			AdminAuthenticationService.getInstance( ).registerUser(request, adminUser);
			html = jspbean.processController( request, response ); 
			
			
			// MockResponse object does not redirect, result is always null
			assertNull( html );
		}
		catch (AccessDeniedException e)
		{
			fail("access denied");
		}
		catch (UserNotSignedException e) 
		{
			fail("user not signed in");
		}

		//display modify Formsreponseedito JSP
		request = new MockHttpServletRequest();
        request.addParameter( "labelrichtext_un" , LABELRICHTEXTUN1 );
        request.addParameter( "labelrichtext_deux" , LABELRICHTEXTDEUX1 );
		//List<Integer> listIds = FormsreponseeditoHome.getIdFormsreponseeditosList();
        //assertTrue( !listIds.isEmpty( ) );
        //request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
		jspbean = new FormsreponseeditoJspBean();
		
		assertNotNull( jspbean.getModifyFormsreponseedito( request ) );	

		//action modify Formsreponseedito
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		
		adminUser = new AdminUser();
		adminUser.setAccessCode("admin");
		
        request.addParameter( "labelrichtext_un" , LABELRICHTEXTUN2 );
        request.addParameter( "labelrichtext_deux" , LABELRICHTEXTDEUX2 );
		request.setRequestURI("jsp/admin/plugins/example/ManageFormsreponseeditos.jsp");
		//important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createFormsreponseedito, qui est l'action par défaut
		request.addParameter("action","modifyFormsreponseedito");
		request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "modifyFormsreponseedito" ));

		try 
		{
			AdminAuthenticationService.getInstance( ).registerUser(request, adminUser);
			html = jspbean.processController( request, response );

			// MockResponse object does not redirect, result is always null
			assertNull( html );
		}
		catch (AccessDeniedException e)
		{
			fail("access denied");
		}
		catch (UserNotSignedException e) 
		{
			fail("user not signed in");
		}
		
		//get remove Formsreponseedito
		request = new MockHttpServletRequest();
        //request.setRequestURI("jsp/admin/plugins/example/ManageFormsreponseeditos.jsp");
        //request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
		jspbean = new FormsreponseeditoJspBean();
		request.addParameter("action","confirmRemoveFormsreponseedito");
		assertNotNull( jspbean.getModifyFormsreponseedito( request ) );
				
		//do remove Formsreponseedito
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		request.setRequestURI("jsp/admin/plugins/example/ManageFormsreponseeditots.jsp");
		//important pour que MVCController sache quelle action effectuer, sinon, il redirigera vers createFormsreponseedito, qui est l'action par défaut
		request.addParameter("action","removeFormsreponseedito");
		request.addParameter( "token", SecurityTokenService.getInstance( ).getToken( request, "removeFormsreponseedito" ));
		//request.addParameter( "id", String.valueOf( listIds.get( 0 ) ) );
		request.setMethod("POST");
		adminUser = new AdminUser();
		adminUser.setAccessCode("admin");

		try 
		{
			AdminAuthenticationService.getInstance( ).registerUser(request, adminUser);
			html = jspbean.processController( request, response ); 

			// MockResponse object does not redirect, result is always null
			assertNull( html );
		}
		catch (AccessDeniedException e)
		{
			fail("access denied");
		}
		catch (UserNotSignedException e) 
		{
			fail("user not signed in");
		}	
     
     }
}
