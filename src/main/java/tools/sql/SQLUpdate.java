package tools.sql;

import tools.db.DBColumn;
import tools.db.DBTable;

import java.util.Map;

public class SQLUpdate extends SQL {
    private Map<String, Object> rowNew;
    private Map<String, Object> rowOld;

    public SQLUpdate(DBTable table, Map<String, Object> rowNew,
                     Map<String, Object> rowOld) {
        super(table);
        this.rowNew = rowNew;
        this.rowOld = rowOld;
    }

    public String getDDL() {
        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE ").append(table.getQualifiedName());
        sb.append(" SET ");
        sb.append(updateStr());
        sb.append("WHERE ");
        for (DBColumn c : table.getPrimaryKey().getColumns()) {
            String cName = c.getColumnName();
            sb.append(cName).append("=");
            sb.append(getSQLString(c, rowNew.get(cName))).append(" AND ");
        }
        sb.delete(sb.lastIndexOf(" AND "), sb.length());
        sb.append(";\n");
        return new String(sb);
    }

    private boolean fieldEqual(String columnName) {
        Object o1 = rowNew.get(columnName);
        Object o2 = rowOld.get(columnName);
        if (o1 == null && o2 == null)
            return true;
        if (o1 == null && o2 != null)
            return false;
        if (o1 != null && o2 == null)
            return false;
        return o1.equals(o2);
    }

    private String updateStr() {
        StringBuffer sb = new StringBuffer();
        for (DBColumn c : table.getColumns()) {
            String cName = c.getColumnName();
            if (!fieldEqual(cName)) {
                sb.append(cName).append("=");
                // sb.append(getSQLString(c, rowNew.get(cName)));
                sb.append(getSQLString(c, rowOld.get(cName) + "->"
                        + rowNew.get(cName)));
                sb.append(", ");
            }
        }
        if (sb.indexOf(",") >= 0)
            sb.deleteCharAt(sb.lastIndexOf(","));
        return new String(sb);
    }

    public boolean hasChange() {
        String s = updateStr();
        return s != null && s.trim().length() > 0;
    }
}
