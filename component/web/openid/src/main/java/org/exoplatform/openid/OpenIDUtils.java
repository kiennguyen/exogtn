/**
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.openid;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.organization.User;
import org.exoplatform.web.login.InitiateLoginServlet;
import org.exoplatform.web.security.Credentials;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */
public class OpenIDUtils
{
   public static PortalContainer getContainer()
   {
      return RootContainer.getInstance().getPortalContainer("portal");
   }
   
   public static OpenIDService getOpenIDService()
   {
      return (OpenIDService)getContainer().getComponentInstanceOfType(OpenIDService.class);
   }
   
   public static void autoLogin(User user, HttpServletRequest req, HttpServletResponse resp) throws IOException
   {
      Credentials credentials = new Credentials(user.getUserName(), user.getPassword(), Credentials.OPENID_AUTH);
      req.getSession().setAttribute(InitiateLoginServlet.CREDENTIALS, credentials);
      resp.sendRedirect("/portal/private/classic");
   }
}
