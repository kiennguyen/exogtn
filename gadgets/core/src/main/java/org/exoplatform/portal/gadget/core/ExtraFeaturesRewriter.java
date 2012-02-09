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
import org.apache.shindig.gadgets.rewrite.GadgetRewriter;
import org.apache.shindig.gadgets.rewrite.MutableContent;
import org.apache.shindig.gadgets.rewrite.RewritingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */
public class ExtraFeaturesRewriter implements GadgetRewriter
{
   public void rewrite(Gadget gadget, MutableContent content) throws RewritingException
   {
      if(gadget.getAllFeatures().contains("exo-jquery"))
      {
         Document doc = content.getDocument();
         Element head = (Element) DomUtil.getFirstNamedChildNode(doc.getDocumentElement(), "head");
         Element script = head.getOwnerDocument().createElement("script");
         
         //TODO should use configurable mechanism and resource controller for URL of exo-jquery feature
         script.setAttribute("src", "http://localhost:8080/eXoResources/javascript/jquery.js");
         head.appendChild(script);
      }
      
   }
}
