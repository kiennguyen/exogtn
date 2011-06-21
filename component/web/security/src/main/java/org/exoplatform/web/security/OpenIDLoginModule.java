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
package org.exoplatform.web.security;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.UsernameCredential;
import org.exoplatform.services.security.jaas.DefaultLoginModule;
import javax.security.auth.login.LoginException;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */
public class OpenIDLoginModule extends DefaultLoginModule
{
   private static final Log log = ExoLogger.getLogger(OpenIDLoginModule.class);
   
   @Override
   protected Log getLogger()
   {
      return log;
   }
   
   @SuppressWarnings("unchecked")
   @Override
   public boolean login() throws LoginException
   {
      try
      {
         String auth_type = (String)sharedState.get("javax.security.auth.login.auth_type");
         if(auth_type == null || !auth_type.equals(Credentials.OPENID_AUTH))
         {
            return false;
         }
         
         String username = (String)sharedState.get("javax.security.auth.login.name");
      
         if (username == null)
         {
              log.error("OpenID Login Failed. Credential Not Found!!");
              return false;
         }
            
         Authenticator authenticator = (Authenticator) getContainer()
               .getComponentInstanceOfType(Authenticator.class);
   
         if (authenticator == null)
         {
               throw new LoginException("No Authenticator component found, check your configuration");
         }
   
         identity = authenticator.createIdentity(username);
         sharedState.put("exo.security.identity", identity);
         sharedState.put("javax.security.auth.login.name", username);
         subject.getPublicCredentials().add(new UsernameCredential(username));
         
         return true;
      }
      catch (final Throwable e)
      {
         log.error(e.getLocalizedMessage());
         throw new LoginException(e.getMessage());
      }
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public boolean commit() throws LoginException
   {
      return true;
   }
   
   /**
    * @see javax.security.auth.spi.LoginModule#abort()
    */
   public boolean abort() throws LoginException
   {
      return true;
   }
   /**
    * @see javax.security.auth.spi.LoginModule#logout()
    */
   public boolean logout() throws LoginException
   {
      return true;
   }
}
