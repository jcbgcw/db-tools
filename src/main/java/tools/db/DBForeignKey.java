package tools.db;

public class DBForeignKey {

    private String keyName;
    private DBColumn pkColumn;
    private DBColumn fkColumn;

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public DBColumn getPkColumn() {
        return pkColumn;
    }

    public void setPkColumn(DBColumn pkColumn) {
        this.pkColumn = pkColumn;
    }

    public DBColumn getFkColumn() {
        return fkColumn;
    }

    public void setFkColumn(DBColumn fkColumn) {
        this.fkColumn = fkColumn;
    }

    public String getForeignKeyDDL() {
        return fkColumn.getTableName() + "." + fkColumn.getColumnName()
                + " REFERENCES " + fkColumn.getTableName() + "."
                + fkColumn.getColumnName();
    }
}
