package tools.db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBTable {
    private String schema;
    private String tableName;
    private List<DBColumn> columns = new ArrayList<DBColumn>();
    private DBPrimaryKey primaryKey;
    private List<DBIndex> indexes = new ArrayList<DBIndex>();
    private List<DBForeignKey> foreignKeys = new ArrayList<DBForeignKey>();

    public DBTable() {

    }

    public DBTable(DatabaseMetaData meta, String tableName) throws SQLException {
        this(meta, null, tableName);
    }

    public DBTable(DatabaseMetaData meta, String schema, String tableName)
            throws SQLException {
        this.setSchema(schema);
        this.setTableName(tableName);
        // set table columns
        ResultSet rs = meta.getColumns(null, schema, tableName, null);
        while (rs.next()) {
            getColumns().add(new DBColumn(rs));
        }
        // set table primary key
        DBPrimaryKey pk = new DBPrimaryKey();
        rs = meta.getPrimaryKeys(null, schema, tableName);
        while (rs.next()) {
            pk.setKeyName(rs.getString("PK_NAME"));
            pk.setTableName(tableName);
            pk.getColumns().add(getColumn(rs.getString("COLUMN_NAME")));
        }
        rs.close();
        setPrimaryKey(pk);
        // set table indexes
        rs = meta.getIndexInfo(null, schema, tableName, false, true);
        while (rs.next()) {
            String indexName = rs.getString("INDEX_NAME");
            if (!indexName.equals(getPrimaryKey().getKeyName())) {
                DBIndex index = getIndex(indexName);
                if (index == null) {
                    index = new DBIndex();
                    boolean nonUnique = rs.getBoolean("NON_UNIQUE");
                    index.setSchema(schema);
                    index.setIndexName(indexName);
                    index.setTableName(tableName);
                    index.setNonUnique(nonUnique);
                    getIndexes().add(index);
                }
                String columnName = rs.getString("COLUMN_NAME");
                index.getColumns().add(getColumn(columnName));
            }
        }
        rs.close();
    }

    public DBColumn getColumn(String columnName) {
        for (DBColumn column : getColumns()) {
            if (column.getColumnName().equals(columnName))
                return column;
        }
        return null;
    }

    public DBIndex getIndex(String indexName) {
        for (DBIndex index : getIndexes()) {
            if (index.getIndexName().equals(indexName))
                return index;
        }
        return null;
    }

    public DBForeignKey getForeignKey(String keyName) {
        for (DBForeignKey fk : getForeignKeys()) {
            if (fk.getKeyName().equals(keyName))
                return fk;
        }
        return null;
    }

    public String getQualifiedName() {
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

    public DBPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(DBPrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<DBIndex> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<DBIndex> indexes) {
        this.indexes = indexes;
    }

    public List<DBForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(List<DBForeignKey> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

}
