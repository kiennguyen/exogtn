/**
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.portal.gadget.core;

import org.apache.shindig.common.xml.DomUtil;
import org.apache.shindig.gadgets.Gadget;
import org.apache.shindig.gadgets.http.HttpResponse;
import org.apache.shindig.gadgets.rewrite.GadgetRewriter;
import org.apache.shindig.gadgets.rewrite.MutableContent;
import org.apache.shindig.gadgets.rewrite.RewritingException;
import org.apache.shindig.gadgets.spec.Feature;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.controller.resource.ResourceId;
import org.exoplatform.portal.controller.resource.ResourceScope;
import org.exoplatform.portal.controller.resource.script.FetchMap;
import org.exoplatform.portal.controller.resource.script.FetchMode;
import org.exoplatform.portal.controller.resource.script.Module;
import org.exoplatform.portal.controller.resource.script.ScriptGraph;
import org.exoplatform.portal.controller.resource.script.ScriptResource;
import org.exoplatform.web.WebAppController;
import org.exoplatform.web.application.javascript.JavascriptConfigService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * Look up GateIn resource with given Id and inject resource URL into html header of gadget's content
 * 
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */
public class GateInResourcesRewriter implements GadgetRewriter
{
   private static String GATEIN_RESOURCES_FEATURE = "gatein-resources";

   private static String FEATURE_ID = "id";

   public void rewrite(Gadget gadget, MutableContent content) throws RewritingException
   {
      if (gadget.getAllFeatures().contains(GATEIN_RESOURCES_FEATURE))
      {
         Feature grFeature = gadget.getSpec().getModulePrefs().getFeatures().get(GATEIN_RESOURCES_FEATURE);
         Collection<String> resourceIds = grFeature.getParamCollection(FEATURE_ID);

         if (resourceIds.size() > 0)
         {
            JavascriptConfigService service =
               (JavascriptConfigService)PortalContainer.getInstance().getComponentInstanceOfType(
                  JavascriptConfigService.class);
            FetchMap<ResourceId> resourcesMap = new FetchMap<ResourceId>();
            String resourceUrl = null;

            for (String id : resourceIds)
            {
               resourcesMap.add(new ResourceId(ResourceScope.SHARED, id), FetchMode.IMMEDIATE);
            }

            try
            {
               Map<String, FetchMode> resources =
                  service.resolveURLs(GadgetRequestHandler.getControllerContext(), resourcesMap, !PropertyManager.isDevelopping(),
                     !PropertyManager.isDevelopping(), new Locale("en"));
   
               for (Map.Entry<String, FetchMode> entry : resources.entrySet())
               {
                  Document doc = content.getDocument();
                  Element head = (Element)DomUtil.getFirstNamedChildNode(doc.getDocumentElement(), "head");
                  Element script = head.getOwnerDocument().createElement("script");
                  script.setAttribute("src", entry.getKey());
                  head.appendChild(script);
               }
            }
            catch (IOException e) 
            {
               throw new RewritingException("EEEEEEEEEEEEEEEEEErrrorroror",
                  HttpResponse.SC_INTERNAL_SERVER_ERROR);
            }
         }
         else
         {
            throw new RewritingException(GATEIN_RESOURCES_FEATURE + " required Param: " + FEATURE_ID,
               HttpResponse.SC_INTERNAL_SERVER_ERROR);
         }
      }
   }
}
