package tools.sql;

import tools.db.DBColumn;
import tools.db.DBForeignKey;
import tools.db.DBIndex;
import tools.db.DBTable;

public class SQLCreateTable extends SQL {

    public SQLCreateTable(DBTable table) {
        super(table);
    }

    public String getDDL() {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE ");
        sb.append(getTable().getQualifiedName()).append("\n");
        sb.append("(\n");
        for (DBColumn c : getTable().getColumns()) {
            sb.append("\t").append(c.getColumnDDL()).append(",\n");
        }
        // sb.append("\tCONSTRAINT ").append(table.getPrimaryKey().getKeyName());
        sb.append("\tPRIMARY KEY ");
        sb.append(grpColumns(table.getPrimaryKey().getColumns())).append("\n");
        sb.append(");\n");
        for (DBIndex idx : table.getIndexes()) {
            SQL sql = new SQLCreateIndex(idx);
            sb.append(sql.getDDL());
        }
        for (DBForeignKey fk : table.getForeignKeys()) {
            SQL sql = SQLAlterTable.createSQLForeignKey(fk);
            sb.append(sql.getDDL());
        }
        return new String(sb);
    }
}
