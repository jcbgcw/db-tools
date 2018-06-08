package tools.sql;

import tools.db.DBColumn;
import tools.db.DBForeignKey;
import tools.db.DBPrimaryKey;

public class SQLAlterTable extends SQL {
    public enum AlterType {
        addColumn, dropColumn, alterColumn, primaryKey, foreignKey, renameConstraint, alterPrimaryKey
    }

    ;

    private DBColumn column;
    private DBForeignKey fk;
    private DBPrimaryKey pk;
    private AlterType type;
    private String tableName;
    private String oldName;
    private String newName;

    private SQLAlterTable() {
    }

    public static SQL createSQLAddColumn(DBColumn column) {
        SQLAlterTable sql = new SQLAlterTable();
        sql.tableName = column.getQualifiedTableName();
        sql.column = column;
        sql.type = AlterType.addColumn;
        return sql;
    }

    public static SQL createSQLDropColumn(DBColumn column) {
        SQLAlterTable sql = new SQLAlterTable();
        sql.tableName = column.getQualifiedTableName();
        sql.column = column;
        sql.type = AlterType.dropColumn;
        return sql;
    }

    public static SQL createSQLAlterColumn(DBColumn column) {
        SQLAlterTable sql = new SQLAlterTable();
        sql.tableName = column.getQualifiedTableName();
        sql.column = column;
        sql.type = AlterType.alterColumn;
        return sql;
    }

    public static SQL createSQLPrimaryKey(DBPrimaryKey pk) {
        SQLAlterTable sql = new SQLAlterTable();
        sql.pk = pk;
        sql.tableName = pk.getQualifiedTableName();
        sql.type = AlterType.alterPrimaryKey;
        return sql;
    }

    public static SQL createSQLForeignKey(DBForeignKey fk) {
        SQLAlterTable sql = new SQLAlterTable();
        sql.tableName = fk.getFkColumn().getQualifiedTableName();
        sql.fk = fk;
        sql.type = AlterType.foreignKey;
        return sql;
    }

    public static SQL createSQLRenameConstraint(String qualifiedTableName,
                                                String oldName, String newName) {
        SQLAlterTable sql = new SQLAlterTable();
        sql.tableName = qualifiedTableName;
        sql.oldName = oldName;
        sql.newName = newName;
        sql.type = AlterType.renameConstraint;
        return sql;
    }

    public String getDDL() {
        StringBuffer sb = new StringBuffer();
        sb.append("ALTER TABLE ").append(tableName);
        switch (type) {
            case addColumn:
                sb.append(" ADD COLUMN ").append(column.getColumnDDL());
                sb.append(";\n");
                break;
            case dropColumn:
                sb.append(" DROP COLUMN ").append(column.getColumnName());
                sb.append(";\n");
                break;
            case alterColumn:
                sb.append(" ALTER COLUMN ").append(column.getColumnDDL());
                sb.append(";\n");
                break;
            case foreignKey:
                sb.append(" ADD CONSTRAINT ").append(fk.getKeyName());
                sb.append(" FOREIGN KEY (");
                sb.append(fk.getFkColumn().getColumnName()).append(")");
                sb.append(" REFERENCES ").append(fk.getPkColumn().getTableName());
                sb.append("(").append(fk.getPkColumn().getColumnName());
                sb.append(");\n");
                break;
            case renameConstraint:
                sb.append(" RENAME CONSTRAINT ").append(oldName);
                sb.append(" TO ").append(newName);
                sb.append(";\n");
                break;
            case alterPrimaryKey:
                sb.append(" DROP PRIMARY KEY, ADD CONSTRAINT ");
                sb.append(fk.getKeyName()).append(" PRIMARY KEY ");
                sb.append(grpColumns(pk.getColumns()));
                sb.append(";\n");
                break;
        }
        return new String(sb);
    }
}
