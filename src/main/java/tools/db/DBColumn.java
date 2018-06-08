package tools.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DBColumn {
    private String schema;
    private String tableName;
    private String columnName;
    private String typeName;
    private int columnSize;
    private int decimalDigits;
    private String remarks;
    private String columnDefult;
    private int ordinalPosition;
    private boolean isNullable;

    public DBColumn(ResultSet rs) {
        try {
            this.setSchema(rs.getString("TABLE_SCHEM"));
            this.setTableName(rs.getString("TABLE_NAME"));
            this.setColumnName(rs.getString("COLUMN_NAME"));
            this.setTypeName(rs.getString("TYPE_NAME"));
            this.setColumnSize(rs.getInt("COLUMN_SIZE"));
            this.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
            this.setColumnDefult(rs.getString("COLUMN_DEF"));
            this.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));
            this.setNullable(!"NO".equals(rs.getString("IS_NULLABLE")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getQualifiedTableName() {
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

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getColumnDefult() {
        return columnDefult;
    }

    public void setColumnDefult(String columnDefult) {
        this.columnDefult = columnDefult;
    }

    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean isNullable) {
        this.isNullable = isNullable;
    }

    public String getDataTypeDDL() {
        String dataType = getTypeName();
        String s = dataType;
        if ("DATE".equals(dataType) || "TIME".equals(dataType)
                || "TIMESTAMP".equals(dataType) || "INTEGER".equals(dataType)
                || "SMALLINT".equals(dataType) || "BIGINT".equals(dataType)
                || "DOUBLE".equals(dataType)) {
        } else if ("CHAR".equals(dataType) || "VARCHAR".equals(dataType)) {
            s += "(" + getColumnSize() + ")";
        } else {
            s += "(" + getColumnSize() + "," + getDecimalDigits() + ")";
        }
        if (!this.isNullable()) {
            s += " NOT NULL";
        }
        String defaut = getColumnDefult();
        if (defaut != null && !defaut.equals("")) {
            s += " DEFAULT " + defaut;
        }
        return s;
    }

    public String getColumnDDL() {
        return getColumnName() + " " + getDataTypeDDL();
    }
}
