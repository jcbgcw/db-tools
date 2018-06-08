package tools.db;

import java.util.ArrayList;
import java.util.List;

public class DBPrimaryKey {
    private String keyName;
    private String schema;
    private String tableName;
    private List<DBColumn> columns = new ArrayList<DBColumn>();

    public String getQualifiedTableName() {
        if (schema == null || schema.trim().length() == 0)
            return tableName;
        return schema + "." + tableName;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<DBColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<DBColumn> columns) {
        this.columns = columns;
    }

    public String getPrimaryKeyDDL() {
        String s = "(";
        for (DBColumn c : getColumns()) {
            if (!s.equals("(")) {
                s += ",";
            }
            s += c.getColumnName();
        }
        s += ")";
        return s;
    }
}
