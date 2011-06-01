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

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.message.sreg.SRegMessage;
import org.openid4java.message.sreg.SRegResponse;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */

public class ConsumerServlet extends HttpServlet
{
   private static final long serialVersionUID = -5998885243419513055L;

   private final Log log = ExoLogger.getLogger("openid:ConsumerServlet");

   private ServletContext context;

   private ConsumerManager manager;

   /*
    * (non-Javadoc)
    * 
    * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
    */
   public void init(ServletConfig config) throws ServletException
   {
      super.init(config);

      context = config.getServletContext();

      log.debug("context: " + context);

      try
      {
         this.manager = new ConsumerManager();
         manager.setAssociations(new InMemoryConsumerAssociationStore());
         manager.setNonceVerifier(new InMemoryNonceVerifier(5000));
      }
      catch (ConsumerException e)
      {
         throw new ServletException(e);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
    *      javax.servlet.http.HttpServletResponse)
    */
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      doPost(req, resp);
   }

   /*
    * (non-Javadoc)
    * 
    * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
    *      javax.servlet.http.HttpServletResponse)
    */
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      if ("true".equals(req.getParameter("is_return")))
      {
         processReturn(req, resp);
      }
      else
      {
         String identifier = req.getParameter("openid_identifier");
         if (identifier != null)
         {
            this.authRequest(identifier, req, resp);
         }
         else
         {
            this.getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
         }
      }
   }

   private void processReturn(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      Identifier identifier = this.verifyResponse(req);
      log.debug("identifier: " + identifier);
      if (identifier == null)
      {
         this.getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
      }
      else
      {
         req.setAttribute("identifier", identifier.getIdentifier());
         this.getServletContext().getRequestDispatcher("/return.jsp").forward(req, resp);
      }
   }

   // --- placing the authentication request ---
   public String authRequest(String userSuppliedString, HttpServletRequest httpReq, HttpServletResponse httpResp)
      throws IOException, ServletException
   {
      try
      {
         // configure the return_to URL where your application will receive
         // the authentication responses from the OpenID provider
         // String returnToUrl = "http://example.com/openid";
         String returnToUrl = httpReq.getRequestURL().toString() + "?is_return=true";

         // perform discovery on the user-supplied identifier
         List discoveries = manager.discover(userSuppliedString);

         // attempt to associate with the OpenID provider
         // and retrieve one service endpoint for authentication
         DiscoveryInformation discovered = manager.associate(discoveries);

         // store the discovery information in the user's session
         httpReq.getSession().setAttribute("openid-disc", discovered);

         // obtain a AuthRequest message to be sent to the OpenID provider
         AuthRequest authReq = manager.authenticate(discovered, returnToUrl);

         if (!discovered.isVersion2())
         {
            // Option 1: GET HTTP-redirect to the OpenID Provider endpoint
            // The only method supported in OpenID 1.x
            // redirect-URL usually limited ~2048 bytes
            httpResp.sendRedirect(authReq.getDestinationUrl(true));
            return null;
         }
         else
         {
            // Option 2: HTML FORM Redirection (Allows payloads >2048 bytes)

            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/formredirection.jsp");
            httpReq.setAttribute("prameterMap", httpReq.getParameterMap());
            httpReq.setAttribute("message", authReq);
            // httpReq.setAttribute("destinationUrl", httpResp
            // .getDestinationUrl(false));
            dispatcher.forward(httpReq, httpResp);
         }
      }
      catch (OpenIDException e)
      {
         // present error to the user
      }

      return null;
   }

   // --- processing the authentication response ---
   public Identifier verifyResponse(HttpServletRequest httpReq)
   {
      try
      {
         // extract the parameters from the authentication response
         // (which comes in as a HTTP request from the OpenID provider)
         ParameterList response = new ParameterList(httpReq.getParameterMap());

         // retrieve the previously stored discovery information
         DiscoveryInformation discovered = (DiscoveryInformation)httpReq.getSession().getAttribute("openid-disc");

         // extract the receiving URL from the HTTP request
         StringBuffer receivingURL = httpReq.getRequestURL();
         String queryString = httpReq.getQueryString();
         if (queryString != null && queryString.length() > 0)
            receivingURL.append("?").append(httpReq.getQueryString());

         // verify the response; ConsumerManager needs to be the same
         // (static) instance used to place the authentication request
         VerificationResult verification = manager.verify(receivingURL.toString(), response, discovered);

         // examine the verification result and extract the verified
         // identifier
         Identifier verified = verification.getVerifiedId();
         if (verified != null)
         {
            AuthSuccess authSuccess = (AuthSuccess)verification.getAuthResponse();

            if (authSuccess.hasExtension(SRegMessage.OPENID_NS_SREG))
            {
               MessageExtension ext = authSuccess.getExtension(SRegMessage.OPENID_NS_SREG);
               if (ext instanceof SRegResponse)
               {
                  SRegResponse sregResp = (SRegResponse)ext;
                  for (Iterator iter = sregResp.getAttributeNames().iterator(); iter.hasNext();)
                  {
                     String name = (String)iter.next();
                     String value = sregResp.getParameterValue(name);
                     httpReq.setAttribute(name, value);
                  }
               }
            }
            if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX))
            {
               FetchResponse fetchResp = (FetchResponse)authSuccess.getExtension(AxMessage.OPENID_NS_AX);

               // List emails = fetchResp.getAttributeValues("email");
               // String email = (String) emails.get(0);

               List aliases = fetchResp.getAttributeAliases();
               for (Iterator iter = aliases.iterator(); iter.hasNext();)
               {
                  String alias = (String)iter.next();
                  List values = fetchResp.getAttributeValues(alias);
                  if (values.size() > 0)
                  {
                     log.debug(alias + " : " + values.get(0));
                     httpReq.setAttribute(alias, values.get(0));
                  }
               }
            }

            return verified; // success
         }
      }
      catch (OpenIDException e)
      {
         // present error to the user
      }

      return null;
   }
}
