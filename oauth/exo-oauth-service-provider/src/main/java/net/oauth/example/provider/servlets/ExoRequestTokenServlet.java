/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package net.oauth.example.provider.servlets;

import net.oauth.example.provider.core.SimpleOAuthServiceProvider;

import net.oauth.OAuthProblemException;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthValidator;
import net.oauth.example.provider.core.ExoOAuth3LeggedProviderService;
import net.oauth.example.provider.core.OAuthServiceProvider;
import net.oauth.server.OAuthServlet;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.web.AbstractHttpServlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet process the first request, it validate information to avoid malform request or attacking
 * After validating completely, it create a token calls Request token for authorization step later
 * 
 * See OAuth 2.0 specification for more detail
 * 
 * @author <a href="trongtt@gmail.com">Trong Tran</a>
 * @version $Revision$
 */
public class ExoRequestTokenServlet extends AbstractHttpServlet
{
   private static final long serialVersionUID = 1L;
   
   @Override
   protected void onService(ExoContainer container, HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException
   {
      try
      {
         // generate request_token and secret
         OAuthServiceProvider consumerService =
            (OAuthServiceProvider)container.getComponentInstanceOfType(OAuthServiceProvider.class);

         OAuthMessage requestMessage = OAuthServlet.getMessage(req, null);

         OAuthConsumer consumer = SimpleOAuthServiceProvider.toOAuthConsumer(consumerService.getConsumer(requestMessage.getConsumerKey()));
         if (consumer == null)
         {
            OAuthProblemException problem =
               new OAuthProblemException("token_rejected, consumer hasn't yet registered with provider");
            throw problem;
         }

         OAuthAccessor accessor = new OAuthAccessor(consumer);

         OAuthValidator validator = (OAuthValidator)container.getComponentInstanceOfType(OAuthValidator.class);
         validator.validateMessage(requestMessage, accessor);

         // Support the 'Variable Accessor Secret' extension
         // described in http://oauth.pbwiki.com/AccessorSecret
         String secret = requestMessage.getParameter("oauth_accessor_secret");
         if (secret != null)
         {
            accessor.setProperty(OAuthConsumer.ACCESSOR_SECRET, secret);
         }

         // generate request_token and secret
         ExoOAuth3LeggedProviderService provider =
            (ExoOAuth3LeggedProviderService)container.getComponentInstanceOfType(ExoOAuth3LeggedProviderService.class);
         provider.generateRequestToken(accessor);

         res.setContentType("text/plain");
         OutputStream out = res.getOutputStream();
         OAuth.formEncode(
            OAuth.newList("oauth_token", accessor.requestToken, "oauth_token_secret", accessor.tokenSecret), out);
         out.close();

      }
      catch (Exception e)
      {
         ExoOAuth3LeggedProviderService.handleException(e, req, res, true);
      }

   }
}
