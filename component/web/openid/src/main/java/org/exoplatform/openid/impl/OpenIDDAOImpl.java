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

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="kienna@exoplatform.com">Kien Nguyen</a>
 * @version $Revision$
 */
public class OpenIDDAOImpl
{
   //TODO Should get from database. instead of caching this list
   //Map<String, String> means Map<openid, username>
   private static Map<String, String> openidDatabase = new HashMap<String, String>();
   
   public String getUser(String openid)
   {
      return openidDatabase.get(openid);
   }
   
   /**
    * Add to OpenID table, map with existing user by username
    * @return
    */
   public void addOpenID(String openid, String username)
   {
      openidDatabase.put(openid, username);
   }
}
