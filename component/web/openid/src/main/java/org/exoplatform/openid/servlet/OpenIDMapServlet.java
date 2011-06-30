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

import org.exoplatform.openid.OpenIDUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.impl.UserImpl;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.Credential;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="kien.nguyen@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */
public class OpenIDMapServlet extends HttpServlet
{
   private static final long serialVersionUID = -7588278665976675833L;
   private final Log log = ExoLogger.getLogger("openid:OpenIDMapServlet");

   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      doPost(req, resp);
   }

   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      //Submit from register.jsp
      String username = req.getParameter("username");
      String password = req.getParameter("password");
      String identifier = req.getParameter("identifier");
      if (username == null || password == null || identifier == null)
         throw new ServletException("UserName, Password and OpenID Identifier must be not empty");

      try
      {
         log.info("Mapping user servlet");
         //Verify username and password
         Authenticator authenticator = (Authenticator)OpenIDUtils.getContainer().getComponentInstanceOfType(Authenticator.class);

         if (authenticator == null) {
            log.error("No Authenticator component found, check your configuration");
            throw new ServletException("There is an internal error of Server");
         }

         Credential[] credentials =
            new Credential[]{new UsernameCredential(username), new PasswordCredential(password)};

         String userId = authenticator.validateUser(credentials);
         
         //Map openID and user
         OpenIDUtils.getOpenIDService().mapToUser(identifier, userId);
         
         //Auto login
         OpenIDUtils.autoLogin(new UserImpl(userId), req, resp);
      }
      catch (Exception e)
      {
         log.error("Username or Password is invalid: " + e.getMessage());
         
         //Go back to mapuser screen
         req.setAttribute("error", e.getMessage());
         this.getServletContext().getRequestDispatcher("/login/openid/mapuser.jsp").forward(req, resp);
      }
      return;
   }
}
