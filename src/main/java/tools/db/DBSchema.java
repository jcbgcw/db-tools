package tools.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBSchema {
    private List<DBTable> tables = new ArrayList<DBTable>();

    public void importSchema(Connection conn, String schema)
            throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        List<String> tableNames = getTableNames(meta, schema);
        System.out.println(tableNames);
        for (String tableName : tableNames) {
            DBTable table = new DBTable(meta, schema, tableName);
            getTables().add(table);
        }
        // set table foreign keys
        for (DBTable table : getTables()) {
            ResultSet rs = meta.getImportedKeys(null, schema, table
                    .getTableName());
            while (rs.next()) {
                DBForeignKey fk = new DBForeignKey();
                fk.setKeyName(rs.getString("FK_NAME"));
                String fkColumnName = rs.getString("FKCOLUMN_NAME");
                String pkTableName = rs.getString("PKTABLE_NAME");
                String pkColumnName = rs.getString("PKCOLUMN_NAME");
                fk.setFkColumn(table.getColumn(fkColumnName));
                fk.setPkColumn(getTable(pkTableName).getColumn(pkColumnName));
                table.getForeignKeys().add(fk);
            }
            rs.close();
        }

    }

    private List<String> getTableNames(DatabaseMetaData meta, String schema)
            throws SQLException {
        List<String> tableNames = new ArrayList<String>();
        ResultSet rs = meta.getTables(null, schema, null, null);
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            addTableName(meta, schema, tableNames, tableName);
        }
        rs.close();
        return tableNames;
    }

    private void addTableName(DatabaseMetaData meta, String schema,
                              List<String> tableNames, String tableName) throws SQLException {
        ResultSet rs = meta.getImportedKeys(null, schema, tableName);
        while (rs.next()) {
            String pkTableName = rs.getString("PKTABLE_NAME");
            addTableName(meta, schema, tableNames, pkTableName);
        }
        rs.close();
        if (!tableNames.contains(tableName)) {
            tableNames.add(tableName);
        }
    }

    public DBTable getTable(String tableName) {
        for (DBTable table : getTables()) {
            if (table.getTableName().equals(tableName))
                return table;
        }
        return null;
    }

    public List<DBTable> getTables() {
        return tables;
    }

    public void setTables(List<DBTable> tables) {
        this.tables = tables;
    }

}
