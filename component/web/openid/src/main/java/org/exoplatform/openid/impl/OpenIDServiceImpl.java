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
package org.exoplatform.openid.impl;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.openid.OpenIDService;
import org.exoplatform.openid.OpenIDUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;
import org.exoplatform.services.organization.impl.UserImpl;

/**
 * @author <a href="kien.nguyen@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */
public class OpenIDServiceImpl implements OpenIDService
{
   private final Log log = ExoLogger.getLogger("openid:OpenIDService");
   
   public OpenIDDAOImpl openIdDao = new OpenIDDAOImpl();
   
   public User findUserByOpenID(String openid)
   {
      try
      {
         String username = openIdDao.getUser(openid);
         PortalContainer container = OpenIDUtils.getContainer();
         OrganizationService orgService = (OrganizationService)container.getComponentInstanceOfType(OrganizationService.class);
         User user = null;
         if(username != null)
         {
            begin(orgService);
            user = orgService.getUserHandler().findUserByName(username);
            end(orgService);
            
            return user;
         }
      }
      catch (Exception e)
      {
         log.warn("Error during find user from database: " + e.getMessage());
      }
      return null;
   }
   
   public User createUser(User user, String openid) throws Exception {      
      //TODO Need implement validator for register input fields
      //Save account into database
      PortalContainer container = OpenIDUtils.getContainer();
      OrganizationService orgService = (OrganizationService)container.getComponentInstanceOfType(OrganizationService.class);
      
      begin(orgService);
      UserHandler userHandler = orgService.getUserHandler();
      userHandler.createUser(user, true);
      end(orgService);
      
      //Map openID with a user, temporarily saving into memory
      openIdDao.addOpenID(openid, user.getUserName());
      return new UserImpl(user.getUserName());
   }
   
   private void begin(OrganizationService orgService) throws Exception
   {
      if (orgService instanceof ComponentRequestLifecycle)
      {
          RequestLifeCycle.begin((ComponentRequestLifecycle)orgService);
      }
   }

   private void end(OrganizationService orgService) throws Exception
   {
      if (orgService instanceof ComponentRequestLifecycle)
      {
          RequestLifeCycle.end();
      }
   }
}
