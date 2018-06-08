package tools.sql;

import tools.db.DBColumn;
import tools.db.DBTable;

import java.util.List;

public abstract class SQL {
    protected DBTable table;

    public SQL() {

    }

    public SQL(DBTable table) {
        this.table = table;
    }

    public DBTable getTable() {
        return table;
    }

    public void setTable(DBTable table) {
        this.table = table;
    }

    public abstract String getDDL();

    public String grpColumns(List<DBColumn> columns) {
        StringBuffer sb = new StringBuffer("(");
        for (DBColumn c : columns) {
            sb.append(c.getColumnName()).append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append(")");
        return new String(sb);
    }

    public static String getSQLString(DBColumn column, Object value) {
        if (value == null)
            return "NULL";
        String s = value.toString().replace("'", "''");
        if (column.getTypeName().equals("CHAR")
                || column.getTypeName().equals("VARCHAR")) {
            return "'" + s + "'";
        }
        if (column.getTypeName().equals("DATE")
                || column.getTypeName().equals("TIME")
                || column.getTypeName().equals("TIMESTAMP")) {
            return "'" + s + "'";
        }
        return s.toString();
    }
}
