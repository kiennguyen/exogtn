/**
 * Copyright (C) 2009 eXo Platform SAS.
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

package org.exoplatform.portal.webui.component;

import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.webui.portal.PageNodeEvent;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.config.model.PageNode;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.portal.webui.workspace.UIPortalApplication;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.application.portlet.PortletRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIBreadcumbs;
import org.exoplatform.webui.core.UIBreadcumbs.LocalPath;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

/**
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 30, 2006
 * @version:: $Id$
 */
@ComponentConfig(
   lifecycle = UIApplicationLifecycle.class
)
public class UIBreadcumbsPortlet extends UIPortletApplication
{

   private final String template;

   public UIBreadcumbsPortlet() throws Exception
   {
      PortletRequestContext context = (PortletRequestContext)WebuiRequestContext.getCurrentInstance();
      PortletRequest prequest = context.getRequest();
      PortletPreferences prefers = prequest.getPreferences();
      template = prefers.getValue("template", "app:/groovy/portal/webui/component/UIBreadcumbsPortlet.gtmpl");
   }

   private void loadSelectedPath() throws Exception
   {
      UserNode node = Util.getUIPortal().getSelectedUserNode();
      List<LocalPath> paths = new ArrayList<LocalPath>();
      
      do
      {
         if (node.getPageRef() == null)
         {
            paths.add(new LocalPath(null, node.getResolvedLabel()));
         }
         else
         {
            paths.add(new LocalPath(node.getURI(), node.getResolvedLabel()));
         }
         node = node.getParent();
      }
      while (node != null && node.getParent() != null);
      
      Collections.reverse(paths);
      
      UIBreadcumbs uiBreadCumbs = getChild(UIBreadcumbs.class);
      uiBreadCumbs.setPath(paths);
   }   

   public String getTemplate()
   {
      return template != null ? template : super.getTemplate();
   }
   
   public boolean isUseAjax() throws Exception
   {
      WebuiRequestContext context = WebuiRequestContext.getCurrentInstance();
      PortletRequest prequest = context.getRequest();
      PortletPreferences prefers = prequest.getPreferences();
      return Boolean.valueOf(prefers.getValue("useAJAX", "true"));        
   }
   
//   public List<PageNode> getSelectedPath()
//   {
//      return Util.getUIPortal().getSelectedPath();
//   }   
//
//   @Override
//   public void renderChildren() throws Exception
//   {
//      getSelectedPath();
//      super.renderChildren();
//   }
//
//   static public class SelectPathActionListener extends EventListener<UIBreadcumbs>
//   {
//      @Override
//      public void execute(Event<UIBreadcumbs> event) throws Exception
//      {
//         String uri = event.getRequestContext().getRequestParameter(OBJECTID);
//         UIPortal uiPortal = Util.getUIPortal();
//         PageNodeEvent<UIPortal> pnevent = new PageNodeEvent<UIPortal>(uiPortal, PageNodeEvent.CHANGE_PAGE_NODE, uri);
//         uiPortal.broadcast(pnevent, Event.Phase.PROCESS);
//      }
//   }

}
