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
package org.exoplatform.oauth.provider.portlet;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.oauth.provider.Consumer;
import org.exoplatform.oauth.provider.OAuthException;
import org.exoplatform.oauth.provider.OAuthServiceProvider;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.ProcessAction;
import javax.portlet.RenderMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */

public class UIConsumerPortlet extends GenericPortlet
{
   private final Logger log = LoggerFactory.getLogger(UIConsumerPortlet.class);
   
   @Override
   protected void doHeaders(RenderRequest request, RenderResponse response)
   {
      super.doHeaders(request, response);
      Element cssElement = response.createElement("link");
      cssElement.setAttribute("href", response.encodeURL(request.getContextPath() + "/skin/DefaultStylesheet.css"));
      cssElement.setAttribute("rel", "stylesheet");
      cssElement.setAttribute("type", "text/css");

      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, cssElement);
   }

   @RenderMode(name = "view")
   public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      OAuthServiceProvider oauthProvider =
         (OAuthServiceProvider)container.getComponentInstanceOfType(OAuthServiceProvider.class);

      List<Consumer> consumers = oauthProvider.getAllConsumers();
      request.setAttribute("consumers", consumers);
      getPortletContext().getRequestDispatcher("/jsp/consumer.jsp").include(request, response);
   }

   @ProcessAction(name = "add")
   public void addConsumer(ActionRequest request, ActionResponse response)
   {
      String consumerKey = request.getParameter("consumerKey");
      String consumerSecret = request.getParameter("consumerSecret");
      String callbackURL = request.getParameter("callbackURL");
      String consumerName = request.getParameter("consumerName");
      String consumerDescription = request.getParameter("consumerDescription");
      String consumerWebsite = request.getParameter("consumerDescription");

      Map<String, String> errorMsg = new HashMap<String, String>();
      if (consumerKey == null || consumerKey.trim().length() == 0)
      {
         errorMsg.put("consumerKey", "Please input consumer key");
      }
      if (consumerSecret == null || consumerSecret.trim().length() == 0)
      {
         errorMsg.put("consumerSecret", "Please input consumer secret");
      }
      if (callbackURL == null || callbackURL.trim().length() == 0)
      {
         errorMsg.put("callbackURL", "Please input callback Url");
      }

      if (errorMsg.size() == 0)
      {
         OAuthServiceProvider provider =
            (OAuthServiceProvider)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
               OAuthServiceProvider.class);
         Map<String, String> properties = new HashMap<String, String>();
         properties.put("name", consumerName);
         properties.put("description", consumerDescription);
         properties.put("website", consumerWebsite);
         try
         {
            provider.registerConsumer(consumerKey, consumerSecret, callbackURL, properties);
         }
         catch (OAuthException e)
         {
            log.error("Error during register consumer into system", e);
         }
      }
      else
      {
         Map<String, String> aNewConsumer = new HashMap<String, String>();
         aNewConsumer.put("consumerKey", consumerKey);
         aNewConsumer.put("consumerSecret", consumerSecret);
         aNewConsumer.put("callbackURL", callbackURL);
         request.setAttribute("errorMsg", errorMsg);
         request.setAttribute("aNewConsumer", aNewConsumer);
      }
   }

   @ProcessAction(name = "deleteConsumer")
   public void deleteConsumer(ActionRequest request, ActionResponse response)
   {
      String consumerKey = request.getParameter("consumerKey");
      if (consumerKey != null)
      {
         OAuthServiceProvider provider =
            (OAuthServiceProvider)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
               OAuthServiceProvider.class);
         provider.removeConsumer(consumerKey);
      }
   }
}
