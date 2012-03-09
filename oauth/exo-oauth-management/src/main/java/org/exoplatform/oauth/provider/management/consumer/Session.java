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
package org.exoplatform.oauth.provider.management.consumer;

import org.exoplatform.oauth.provider.Consumer;
import org.exoplatform.oauth.provider.OAuthToken;
import org.juzu.SessionScoped;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */
@Named("session")
@SessionScoped
public class Session implements Serializable
{
   private static final long serialVersionUID = 1L;

   private Consumer consumer;

   private OAuthToken accessToken;

   private String message;

   private String currentUser;
   
   private List<String> propertyNames;
   
   private Map<String, String> parameters;
   
   private Map<String, String> errors;

   public void setConsumer(Consumer consumer)
   {
      this.consumer = consumer;
   }

   public Consumer getConsumer()
   {
      return consumer;
   }

   public void setAccessToken(OAuthToken accessToken)
   {
      this.accessToken = accessToken;
   }

   public OAuthToken getAccessToken()
   {
      return accessToken;
   }

   public void setMessage(String message)
   {
      this.message = message;
   }

   public String getMessage()
   {
      return message;
   }

   public void setCurrentUser(String currentUser)
   {
      this.currentUser = currentUser;
   }

   public String getCurrentUser()
   {
      return currentUser;
   }

   public void setPropertyNames(List<String> propertyNames)
   {
      this.propertyNames = propertyNames;
   }

   public List<String> getPropertyNames()
   {
      propertyNames = new ArrayList<String>();
      propertyNames.add("name");
      propertyNames.add("description");
      propertyNames.add("website");
      return propertyNames;
   }

   public Map<String, String> getParameters()
   {
      return parameters;
   }

   public void setParameters(Map<String, String> parameters)
   {
      this.parameters = parameters;
   }

   public Map<String, String> getErrors()
   {
      return errors;
   }

   public void setErrors(Map<String, String> errors)
   {
      this.errors = errors;
   }

}
