package tools;

import java.util.ArrayList;
import java.util.List;

public class PrimaryKey {
    private List<Object> keys = new ArrayList<Object>();

    public boolean equals(Object o) {
        if (o instanceof PrimaryKey) {
            PrimaryKey key = (PrimaryKey) o;
            return key.hashCode() == hashCode();
        }
        return false;
    }

    public int hashCode() {
        StringBuffer sb = new StringBuffer();
        for (Object key : getKeys()) {
            sb.append("'").append(key).append("',");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        return new String(sb).hashCode();
    }

    public void addKeyValue(Object value) {
        getKeys().add(value);
    }

    public List<Object> getKeys() {
        return keys;
    }

    public void setKeys(List<Object> keys) {
        this.keys = keys;
    }

}
