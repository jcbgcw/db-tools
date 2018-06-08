package tools.sql;

import tools.db.DBColumn;
import tools.db.DBTable;

import java.util.Map;

public class SQLInsert extends SQL {
    private Map<String, Object> row;

    public SQLInsert(DBTable table, Map<String, Object> row) {
        super(table);
        this.row = row;
    }

    public String getDDL() {
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO ").append(table.getQualifiedName());
        sb.append(" (");
        for (DBColumn c : table.getColumns()) {
            sb.append(c.getColumnName()).append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append(") VALUES (");
        for (DBColumn c : table.getColumns()) {
            sb.append(getSQLString(c, row.get(c.getColumnName()))).append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append(");\n");
        return new String(sb);
    }
}
