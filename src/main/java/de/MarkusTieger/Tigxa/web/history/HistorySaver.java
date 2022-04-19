package de.MarkusTieger.Tigxa.web.history;

import de.MarkusTieger.Tigxa.Browser;
import org.gjt.sp.jedit.gui.HistoryModel;
import org.gjt.sp.jedit.gui.HistoryModelSaver;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class HistorySaver implements HistoryModelSaver {

    private final Properties config;

    public HistorySaver(Properties config){
        this.config = config;
    }

    @Override
    public Map<String, HistoryModel> load(Map<String, HistoryModel> map) {
        HistoryModel model = new HistoryModel(Browser.NAME.toLowerCase());

        model.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                update();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                update();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                update();
            }
            
            public void update(){
                Browser.saveConfig();
            }
        });

        for(Map.Entry<Object, Object> e : config.entrySet()){
            if((e + "").toLowerCase().startsWith((HistorySaver.class.getName() + ".history.").toLowerCase())){
                if((e.getValue() + "").equalsIgnoreCase("-")) continue;
                System.out.println("ADD: " + e.getValue());
                model.addItem(e.getValue() + "");
            }
        }

        return Collections.singletonMap(Browser.NAME.toLowerCase(), model);
    }

    @Override
    public boolean save(Map<String, HistoryModel> map) {
        boolean changed = false;

        for(Map.Entry<Object, Object> e : config.entrySet()){
            if((e + "").toLowerCase().startsWith((HistorySaver.class.getName() + ".history.").toLowerCase())){
                config.setProperty(e.getKey() + "", "-");
            }
        }

        if(map == null) return true;
        if(map.containsKey(Browser.NAME.toLowerCase())){
            HistoryModel model = map.get(Browser.NAME.toLowerCase());
            if(model == null) return false;
            for(int i = 0; i < model.size(); i++){
                config.setProperty(HistorySaver.class.getName() + ".history." + i, model.elementAt(i));
            }
        }

        return changed;
    }

}
