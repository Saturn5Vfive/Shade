package net.shade.plugin;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import net.shade.plugin.ChatPlugin;
import net.shade.plugin.SettingPlugin;
 
public class RequirePlugin {
     
    public static String require(String uri) {
         
        try {
             
            URL url = new URL(uri);
            BufferedReader returned = new BufferedReader(new InputStreamReader(url.openStream()));
             
            String l = "";
            String text = "";
            while ((l = returned.readLine()) != null) {
                text += l;
            }
            if(!text.startsWith(SettingPlugin.prefix))
                text = text + SettingPlugin.prefix;
            returned.close();
            return text;    
        }
        catch (Exception e) {
            ChatPlugin.sendChat("Error while fetching script: " + e.getMessage());
        }
        return null;
    }
}