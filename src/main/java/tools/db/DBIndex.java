package tools.db;

import java.util.ArrayList;
import java.util.List;

public class DBIndex {
    private String indexName;
    private String schema;
    private String tableName;
    private List<DBColumn> columns = new ArrayList<DBColumn>();
    private boolean nonUnique;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
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

    public boolean isNonUnique() {
        return nonUnique;
    }

    public void setNonUnique(boolean nonUnique) {
        this.nonUnique = nonUnique;
    }

    public String getQualifiedIndexName() {
        if (schema == null || schema.trim().length() == 0)
            return indexName;
        return schema + "." + indexName;
    }

    public String getQualifiedTableName() {
        if (schema == null || schema.trim().length() == 0)
            return tableName;
        return schema + "." + tableName;
    }

    public String getIndexDDL() {
        String s = "(";
        for (DBColumn c : getColumns()) {
            if (!s.equals("(")) {
                s += ",";
            }
            s += c.getColumnName();
        }
        s += ")";
        if (!isNonUnique()) {
            s += "UNIQUE";
        }
        return s;
    }
}
