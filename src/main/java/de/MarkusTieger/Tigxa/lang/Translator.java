package de.MarkusTieger.Tigxa.lang;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Translator {

    private static Properties language = new Properties();

    private static final List<Language> languages = new ArrayList<>();

    public static String translate(int id, Object... args){
        String data = language.getProperty(id + "", "translate." + id);
        data = String.format(data, args);
        return data;
    }

    public static Language loadLanguage(String path) throws IOException {
        InputStream in = Translator.class.getResourceAsStream(path);
        if(in == null) throw new IOException("Resource not found!");
        byte[] data = in.readAllBytes();
        String content = BFInterpreter.interpret(data);
        Properties prop = new Properties();
        prop.load(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));

        Language lang = new Language(prop);
        languages.add(lang);

        if(language.getProperty("name") == null){
            language = prop;
        }

        return lang;
    }

}
