package tools.sql;

import tools.db.DBTable;

public class SQLDropTable extends SQL {
    public SQLDropTable(DBTable table) {
        super(table);
    }

    public String getDDL() {
        StringBuffer sb = new StringBuffer();
        sb.append("DROP TABLE ").append(table.getQualifiedName()).append(";\n");
        return new String(sb);
    }

}
