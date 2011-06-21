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

import org.exoplatform.openid.OpenIDService;
import org.exoplatform.openid.impl.OpenIDServiceImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.jdbc.UserImpl;
import org.exoplatform.web.login.InitiateLoginServlet;
import org.exoplatform.web.security.Credentials;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */
public class OpenIDAccountServlet extends HttpServlet
{
   private static final long serialVersionUID = -631150770085187794L;

   private final Log log = ExoLogger.getLogger("openid:OpenIDAccountServlet");

   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      doPost(req, resp);
   }

   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      if(req.getRemoteUser() != null)
      {
         //Authenticated
         resp.sendRedirect("/portal/private/classic");
      }
      
      if (req.getParameter("username") != null)
      {
         //Submit from register.jsp
         User userData = new UserImpl(req.getParameter("username"));
         userData.setPassword(req.getParameter("password"));
         userData.setEmail(req.getParameter("email"));
         userData.setFirstName(req.getParameter("firstName"));
         userData.setLastName(req.getParameter("lastName"));

         try
         {
            User user = new OpenIDServiceImpl().createUser(userData, req.getParameter("identifier"));
            if(user != null)
            {
               //Auto Login
               log.info("Make auto login");
               this.autoLogin(user, req, resp);
            }
            log.info("Create successfully user: " + user.getUserName());
         }
         catch (Exception e)
         {
            log.error("Cannot create new user");
            e.printStackTrace();
         }
         return;
      }

      String identifier = (String)req.getAttribute("identifier");
      String queryString = req.getQueryString();
      log.info("Your OpenID: " + identifier + "\nQuery String:" + queryString);
      
      if (identifier != null)
      {
         OpenIDService service = new OpenIDServiceImpl();
         User user = service.findUserByOpenID(identifier);
         if (user != null)
         {
            try
            {
               //Auto Login
               log.info("Make auto login");
               this.autoLogin(user, req, resp);               
            }
            catch (Exception e)
            {
               log.error("authentication unsuccessful");
               e.printStackTrace();
            }
         }
         else
         {
            //ask user create account
            log.info("Go to register new account");
            req.setAttribute("identifier", identifier);
            req.setAttribute("user", user);
            this.getServletContext().getRequestDispatcher("/login/openid/register.jsp").forward(req, resp);
         }
      }
   }
   
   private void autoLogin(User user, HttpServletRequest req, HttpServletResponse resp) throws IOException
   {
      Credentials credentials = new Credentials(user.getUserName(), user.getPassword(), Credentials.OPENID_AUTH);
      req.getSession().setAttribute(InitiateLoginServlet.CREDENTIALS, credentials);
      resp.sendRedirect("/portal/private/classic");
   }
}
