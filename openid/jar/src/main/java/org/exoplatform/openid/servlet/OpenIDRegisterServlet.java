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
package org.exoplatform.openid.servlet;

import org.exoplatform.container.web.AbstractHttpServlet;
import org.exoplatform.openid.OpenIDUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.jdbc.UserImpl;
import org.exoplatform.web.security.Credentials;
import org.exoplatform.web.security.security.AbstractTokenService;
import org.exoplatform.web.security.security.TransientTokenService;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="kien.nguyen@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */

public class OpenIDRegisterServlet extends AbstractHttpServlet
{
   private static final long serialVersionUID = 1745889612084935901L;
   private final Log log = ExoLogger.getLogger("openid:OpenIDRegisterServlet");

   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      doPost(req, resp);
   }

   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      String token = (String)req.getSession().getAttribute("openid.token");
      TransientTokenService tokenService = AbstractTokenService.getInstance(TransientTokenService.class);
      Credentials tCredentials = tokenService.validateToken(token, false);

      if (tCredentials == null)
      {
         PrintWriter out = resp.getWriter();
         out.println("You don't have permission to view this servlet");
         out.close();
         return;
      }
      
      //Submit from register.jsp
      User userData = new UserImpl(req.getParameter("username"));
      userData.setPassword(req.getParameter("password"));
      userData.setEmail(req.getParameter("email"));
      userData.setFirstName(req.getParameter("firstName"));
      userData.setLastName(req.getParameter("lastName"));

      try
      {
         User user = OpenIDUtils.getOpenIDService().createUser(userData, req.getParameter("identifier"));
         if(user != null)
         {
            //Auto Login
            log.info("Make auto login");
            user.setPassword(token);
            OpenIDUtils.autoLogin(user, req, resp);
         }
         log.info("Create successfully user: " + user.getUserName());
      }
      catch (Exception e)
      {
         log.error("Cannot create new user: ");
         e.printStackTrace();
      }
      return;
   }
}
