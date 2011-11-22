/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package net.oauth.example.provider.core;

import net.oauth.OAuthConsumer;
import net.oauth.OAuthProblemException;

import org.picocontainer.Startable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * An simple oauth consumer service which just persist in Memory
 * 
 * Created by The eXo Platform SAS
 * Author : Nguyen Anh Kien
 *          nguyenanhkien2a@gmail.com
 * Dec 9, 2010  
 */
public class SimpleOAuthConsumerService implements OAuthConsumerService, Startable
{
   /*store all consumers into map with consumer key as identifier*/
   private final Map<String, OAuthConsumer> ALL_CONSUMERS = Collections
      .synchronizedMap(new HashMap<String, OAuthConsumer>(10));

   public void start()
   {
      try
      {
         loadConsumers();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   public void stop()
   {
   }

   /**
    * Load all consumers from a file and store them into memory
    * 
    * @throws IOException
    */
   private void loadConsumers() throws IOException
   {
      Properties p = new Properties();
      String resourceName = "consumer.properties";
      URL resource = this.getClass().getResource(resourceName);
      if (resource == null)
      {
         throw new IOException("resource not found: " + resourceName);
      }
      InputStream stream = resource.openStream();
      try
      {
         p.load(stream);
      }
      finally
      {
         stream.close();
      }

      // for each entry in the properties file create a OAuthConsumer
      for (Map.Entry<Object, Object> prop : p.entrySet())
      {
         String consumer_key = (String)prop.getKey();
         // make sure it's key not additional properties
         if (!consumer_key.contains("."))
         {
            String consumer_secret = (String)prop.getValue();
            if (consumer_secret != null)
            {
               String consumer_description = (String)p.getProperty(consumer_key + ".description");
               String consumer_callback_url = (String)p.getProperty(consumer_key + ".callbackURL");
               // Create OAuthConsumer w/ key and secret
               OAuthConsumer consumer = new OAuthConsumer(consumer_callback_url, consumer_key, consumer_secret, null);
               consumer.setProperty("name", consumer_key);
               consumer.setProperty("description", consumer_description);
               addConsumer(consumer_key, consumer);
            }
         }
      }
   }

   /**
    * Get a consumer from consumer list
    * 
    * @param consumer_key key as identifier of consumer
    * @return OAuthConsumer object
    * @throws IOException
    * @throws OAuthProblemException
    */
   public OAuthConsumer getConsumer(String consumer_key)
   {
      try
      {
         // Try to reload consumers from the consumer.propreties.
         // It helps to be able to edit the file at runtime for testing
         loadConsumers();
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return ALL_CONSUMERS.get(consumer_key);
   }

   public void addConsumer(String consumer_key, OAuthConsumer consumer)
   {
      ALL_CONSUMERS.put(consumer_key, consumer);
   }

   public void removeConsumer(String consumerKey)
   {
      ALL_CONSUMERS.remove(consumerKey);
   }
}
