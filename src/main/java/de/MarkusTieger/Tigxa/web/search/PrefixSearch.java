package de.MarkusTieger.Tigxa.web.search;

import de.MarkusTieger.Tigxa.Browser;
import javafx.application.Platform;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

public class PrefixSearch {

    private static final HashMap<String, String> map = new HashMap<>();

    public static void load(Properties properties){

        Set<Map.Entry<Object, Object>> entries = properties.entrySet();

        synchronized (entries){
            for(Map.Entry<Object, Object> e : entries){
                if((e.getKey() + "").toLowerCase().startsWith((PrefixSearch.class.getName() + ".engines.").toLowerCase())){
                    String data = ((e.getKey() + "").substring((PrefixSearch.class.getName() + ".engines.").length()));
                    if(e.getValue().equals("-")) continue;
                    map.put(data, e.getValue() + "");
                }

            }
        }

        if(map.size() == 0){
            map.put("g", "https://google.com/search?q=%s");

            map.put("y", "https://youtube.com/search?q=%s");
            map.put("yt", map.get("y"));

            map.put("w", "https://www.wikipedia.org/search-redirect.php?search=%s");
            map.put("wiki", map.get("w"));

            map.put("z", "https://www.amazon.com/s/ref=nb_sb_noss?field-keywords=%s");
            map.put("az", map.get("z"));

            map.put("git", "https://github.com/search?q=%s");
        }
        Browser.saveConfig();

    }

    public static void save(Properties properties){
        for(Map.Entry<String, String> e : map.entrySet()){
            properties.setProperty(PrefixSearch.class.getName() + ".engines." + e.getKey(), e.getValue());
        }
    }

    public static void search(Consumer<String> location, String text) {
        final Consumer<String> locat = location;
        if(Browser.getMode() == Browser.Mode.JAVAFX) location = (loc) -> Platform.runLater(() -> locat.accept(loc));
        try {
            URI uri = new URI(text);
            if (uri.getScheme() == null) throw new URISyntaxException(text, "No Scheme set!");
            location.accept(uri.toString());
        } catch (URISyntaxException ex) {
            search0(location, text);
        }
    }

    private static final Logger LOGGER = LogManager.getLogger(PrefixSearch.class);

    private static void search0(Consumer<String> location, String text) {
        try {
            URI uri = new URI("https://" + text);
            try {
                InetAddress.getByName(uri.getHost());
                location.accept(uri.toString());
            } catch (UnknownHostException exc) {
                throw new URISyntaxException(text, "Unknown Host!");
            }
        } catch (URISyntaxException exc) {

            System.out.println("CHECKING ENTRIES...");

            for(Map.Entry<String, String> e : map.entrySet()){
                if(text.toLowerCase().startsWith(e.getKey().toLowerCase() + " ")){
                    try {
                        String query = text.substring(e.getKey().length() + 1);
                        URI uri = new URI(String.format(e.getValue(), URLEncoder.encode(query, StandardCharsets.UTF_8)));
                        location.accept(uri.toString());
                    } catch (Throwable ex) {
                    }
                    return;
                }
            }

            String query = text;
            try {

                URI uri = new URI(String.format(Browser.SEARCH, URLEncoder.encode(query, StandardCharsets.UTF_8)));
                location.accept(uri.toString());
            } catch (URISyntaxException uriSyntaxException) {
                uriSyntaxException.printStackTrace();
            }
        }
    }
}
