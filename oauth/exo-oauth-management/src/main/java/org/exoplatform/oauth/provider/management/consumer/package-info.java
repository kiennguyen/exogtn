@Application(
   plugins = {
      AssetPlugin.class,
      AjaxPlugin.class
})
@Assets(
   scripts = {
      @Script(src = "javascripts/jquery-1.7.1.min.js"),
      @Script(src = "javascripts/jquery-ui-1.7.2.custom.min.js"),
      @Script(src = "javascripts/consumer.js")
   },
   stylesheets = {
      @Stylesheet(src = "stylesheets/consumer/style.css")
   }
)
package org.exoplatform.oauth.provider.management.consumer;

import org.juzu.Application;
import org.juzu.plugin.ajax.AjaxPlugin;
import org.juzu.plugin.asset.AssetPlugin;
import org.juzu.plugin.asset.Assets;
import org.juzu.plugin.asset.Script;
import org.juzu.plugin.asset.Stylesheet;