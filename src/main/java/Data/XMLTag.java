package Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Assumes the data is reasonably well-defined.
 * Created by laelcosta on 6/11/16.
 */
public class XMLTag {

    private final String type;
    private Map<String, String> data;
    private List<XMLTag> children;
    private int numChildren;

    public XMLTag(String type) {
        this.type = type;
        data = new HashMap<String, String>();
        children = new ArrayList<XMLTag>();
    }

    public String getType() {
        return type;
    }

    public void addDatum(String key, String value) {
        data.put(key, value);
    }

    public void addData(Map<String, String> map) {
        data.putAll(map);
    }

    public void addChild(XMLTag child) {
        children.add(child);
        numChildren ++;
    }

    public void addChildren(List<XMLTag> children) {
        this.children.addAll(children);
        numChildren += children.size();
    }

    public String getDatum(String key) {
        return data.get(key);
    }

    public XMLTag getChild(int index) {
        if (index < 0 || index >= numChildren) {
            return null;
        }

        return children.get(index);
    }
}
