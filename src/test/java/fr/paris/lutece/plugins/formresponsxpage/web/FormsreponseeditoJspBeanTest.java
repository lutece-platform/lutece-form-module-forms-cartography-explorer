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

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;


import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.inject.Inject;
import fr.paris.lutece.portal.business.right.Right;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.test.LuteceTestCase;
/**
 * This is the business class test for the object Formsreponseedito
 */
public class FormsreponseeditoJspBeanTest extends LuteceTestCase
{
    private static final String NEW_LABELRICHTEXTUN = "NewLabelrichtextUn";
    private static final String NEW_LABELRICHTEXTDEUX = "NewLabelrichtextDeux2";

    private static final String ATTRIBUTE_ADMIN_USER = "lutece_admin_user";
    
    @Inject
    private FormsreponseeditoJspBean _jspbean;
    
    @Test
    public void testJspBean(  ) throws AccessDeniedException, IOException
	{
    	MockHttpServletRequest request = new MockHttpServletRequest( );
    	MockHttpServletResponse response = new MockHttpServletResponse();

    	//display modify Formsreponseedito JSP
		AdminUser user = AdminUserHome.findUserByLogin( "admin" );
		user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId( ) ) );
        Map<String, Right> mapRights = new HashMap<>( );
        Right right = new Right( );
        right.setId( FormsreponseeditoJspBean.RIGHT_MANAGEADMINRESPONSEEDITO );
        mapRights.put( FormsreponseeditoJspBean.RIGHT_MANAGEADMINRESPONSEEDITO, right );
        user.setRights( mapRights );
        user.setLocale( new Locale("fr", "FR", "") );
        request.getSession( true ).setAttribute( ATTRIBUTE_ADMIN_USER, user );

        _jspbean.init( request, FormsreponseeditoJspBean.RIGHT_MANAGEADMINRESPONSEEDITO );

		assertNotNull( _jspbean.getModifyFormsreponseedito( request ) );
    	
    	//action modify Formsreponseedito JSP
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();		
		
        request.addParameter( "labelrichtext_un" , NEW_LABELRICHTEXTUN );
        request.addParameter( "labelrichtext_deux" , NEW_LABELRICHTEXTDEUX );
		request.addParameter("action","modifyFormsreponseedito");

		try 
		{
			AdminUser adminUser = new AdminUser( );
			adminUser.setAccessCode( "admin" );
			AdminAuthenticationService.getInstance( ).registerUser(request, adminUser);
			String html = _jspbean.processController( request, response );

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