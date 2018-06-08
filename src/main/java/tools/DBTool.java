package tools;

import tools.db.*;
import tools.sql.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBTool {

    public static void getSnapShotDDL(Connection conn, String schem)
            throws SQLException {
        DBSchema schema = new DBSchema();
        schema.importSchema(conn, schem);
        compareSchema(new DBSchema(), schema);
    }

    public static void getSnapShotDML(Connection conn, String schem)
            throws SQLException {
        DBSchema schema = new DBSchema();
        schema.importSchema(conn, schem);
        compareData(new DBSchema(), schema, null, conn);
    }

    public static void compareSchema(DBSchema schema1, DBSchema schema2) {
        for (DBTable t2 : schema2.getTables()) {
            DBTable t1 = schema1.getTable(t2.getTableName());
            if (t1 == null) {
                SQL sql = new SQLCreateTable(t2);
                System.out.println(sql.getDDL());
            } else {
                compareTable(t1, t2);
            }
        }
        for (DBTable t1 : schema1.getTables()) {
            if (schema2.getTable(t1.getTableName()) == null) {
                SQL sql = new SQLDropTable(t1);
                System.out.println(sql.getDDL());
            }
        }
    }

    public static void compareTable(DBTable t1, DBTable t2) {
        // compare columns
        for (DBColumn c2 : t2.getColumns()) {
            DBColumn c1 = t1.getColumn(c2.getColumnName());
            if (c1 == null) {
                SQL sql = SQLAlterTable.createSQLAddColumn(c2);
                System.out.println(sql.getDDL());
            } else if (!c1.getDataTypeDDL().equals(c2.getDataTypeDDL())) {
                SQL sql = SQLAlterTable.createSQLAlterColumn(c2);
                System.out.println(sql.getDDL());
            }
        }
        for (DBColumn c1 : t1.getColumns()) {
            if (t2.getColumn(c1.getColumnName()) == null) {
                SQL sql = SQLAlterTable.createSQLDropColumn(c1);
                System.out.println(sql.getDDL());
            }
        }
        // compare primary key
        DBPrimaryKey pk1 = t1.getPrimaryKey();
        DBPrimaryKey pk2 = t2.getPrimaryKey();
        if (pk2.getPrimaryKeyDDL().equals(pk1.getPrimaryKeyDDL())) {
            if (!pk1.getKeyName().equals(pk2.getKeyName())) {
                // TODO different key names, same key columns
                System.out
                        .println("-- TODO different primary key names, same primary key columns");
            }
        } else {
            if (pk1.getKeyName().equals(pk2.getKeyName())) {
                // TODO same key name, different key columns
                System.out
                        .println("-- TODO same primary key name, different primary key columns");
            } else {
                // TODO different in both key name and key columns
                System.out
                        .println("-- TODO different in both primary key name and primary key columns");
            }
        }
        // compare indexes
        for (DBIndex idx2 : t2.getIndexes()) {
            DBIndex idx1 = t1.getIndex(idx2.getIndexName());
            if (idx1 == null) {
                // TODO create new index
                System.out.println("-- TODO create new index");
                SQL sql = new SQLCreateIndex(idx2);
                System.out.println(sql.getDDL());
            } else {
                if (!idx1.getIndexDDL().equals(idx2.getIndexDDL())) {
                    // TODO change existing index
                    System.out.println("-- TODO change existing index");
                }
            }
        }
        for (DBIndex idx1 : t1.getIndexes()) {
            if (t2.getIndex(idx1.getIndexName()) == null) {
                // TODO drop existing index
                System.out.println("-- TODO drop existing index");
            }
        }
        // compare foreign keys
        for (DBForeignKey fk2 : t2.getForeignKeys()) {
            DBForeignKey fx1 = t1.getForeignKey(fk2.getKeyName());
            if (fx1 == null) {
                // TODO create new foreign key
                System.out.println("-- TODO create new foreign key");
            } else {
                if (!fk2.getForeignKeyDDL().equals(fx1.getForeignKeyDDL())) {
                    // TODO change existing foreign key
                    System.out.println("-- TODO change existing foreign key");
                }
            }
        }
        for (DBForeignKey fk1 : t1.getForeignKeys()) {
            if (t2.getForeignKey(fk1.getKeyName()) == null) {
                // TODO drop existing foreign key
                System.out.println("-- TODO drop existing foreign key");
            }
        }
    }

    public static void compareData(DBSchema schema1, DBSchema schema2,
                                   Connection c1, Connection c2) throws SQLException {
        for (DBTable t2 : schema2.getTables()) {
            List<Map<String, Object>> data2 = read(t2, c2);
            DBTable t1 = schema1.getTable(t2.getTableName());
            if (t1 == null) {
                if (!data2.isEmpty()) {
                    System.out.println(insertEntireTable(t2, data2));
                }
            } else {
                List<Map<String, Object>> data1 = read(t1, c1);
                String s = compareTableData(t2, data1, data2);
                if (!s.isEmpty()) {
                    System.out.println(compareTableData(t2, data1, data2));
                }
            }
        }
    }

    public static List<Map<String, Object>> read(DBTable t, Connection conn)
            throws SQLException {
        String sql = "select * from " + t.getQualifiedName() + " ORDER BY ";
        DBPrimaryKey pk = t.getPrimaryKey();
        for (DBColumn c : pk.getColumns()) {
            if (sql.endsWith(" ORDER BY ")) {
                sql += c.getColumnName();
            } else {
                sql += ", " + c.getColumnName();
            }
        }
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<Map<String, Object>> data = read(rs, t);
        rs.close();
        ps.close();
        return data;
    }

    public static String compareTableData(DBTable t,
                                          List<Map<String, Object>> data1, List<Map<String, Object>> data2) {
        StringBuffer sb = new StringBuffer();
        Map<PrimaryKey, Map<String, Object>> d1 = convertData(data1, t);
        Map<PrimaryKey, Map<String, Object>> d2 = convertData(data2, t);
        for (PrimaryKey key : d1.keySet()) {
            if (!d2.containsKey(key)) {
                SQL sql = new SQLDelete(t, d1.get(key));
                sb.append(sql.getDDL());
            }
        }
        for (PrimaryKey key : d2.keySet()) {
            if (d1.containsKey(key)) {
                SQLUpdate sql = new SQLUpdate(t, d2.get(key), d1.get(key));
                if (sql.hasChange()) {
                    sb.append(sql.getDDL());
                }
            } else {
                SQL sql = new SQLInsert(t, d2.get(key));
                sb.append(sql.getDDL());
            }
        }
        return new String(sb);
    }

    public static Map<PrimaryKey, Map<String, Object>> convertData(
            List<Map<String, Object>> data, DBTable t) {
        Map<PrimaryKey, Map<String, Object>> result = new HashMap<PrimaryKey, Map<String, Object>>();
        for (Map<String, Object> map : data) {
            PrimaryKey key = new PrimaryKey();
            for (DBColumn c : t.getPrimaryKey().getColumns()) {
                key.addKeyValue(map.get(c.getColumnName()));
            }
            result.put(key, map);
        }
        return result;
    }

    public static String insertEntireTable(DBTable t,
                                           List<Map<String, Object>> data) {
        StringBuffer sb = new StringBuffer();
        for (Map<String, Object> map : data) {
            SQL sql = new SQLInsert(t, map);
            sb.append(sql.getDDL());
        }
        return new String(sb);
    }

    public static List<Map<String, Object>> read(ResultSet rs, DBTable t)
            throws SQLException {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        List<DBColumn> pkc = t.getPrimaryKey().getColumns();
        while (rs.next()) {
            PrimaryKey key = new PrimaryKey();
            for (DBColumn c : pkc) {
                key.addKeyValue(rs.getObject(c.getColumnName()));
            }
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            for (DBColumn c : t.getColumns()) {
                fieldValues.put(c.getColumnName(), rs.getObject(c
                        .getColumnName()));
            }
            data.add(fieldValues);
        }
        return data;
    }

}