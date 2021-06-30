package net.ryu.friendsystem.sql.database;

import net.ryu.friendsystem.FriendSystemPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DatabaseUser {

    private DatabaseValues databaseValues;
    private Database database;

    public DatabaseUser(FriendSystemPlugin plugin) {
        databaseValues = plugin.getDatabaseValues();
        database = plugin.getDatabase();
    }

    public void setValues(String uuid, String friend, String date) {
        Map<String, Object> values = new HashMap<>();

        values.put(databaseValues.getDate(), date);

        setValues(friend, values, uuid);
    }

    public boolean doesExist(String id, String table) {
        String uuid = databaseValues.getUUID();
        String sql = "SELECT * FROM " + table + " WHERE " + uuid + "=? LIMIT 1;";
        ResultSet resultSet = database.executeQuery(sql, id);

        try {
            return resultSet.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public Object getValue(String id, String key, String table) {
        String uuid = databaseValues.getUUID();
        String sql = "SELECT " + key + " FROM " + table + " WHERE " + uuid + "=? LIMIT 1;";
        ResultSet resultSet = database.executeQuery(sql, id);

        try {
            if(!resultSet.next()) {
                return null;
            }

            return resultSet.getObject(key);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void setValue(String id, String key, Object object, String table) {
        String uuid = databaseValues.getUUID();
        String sql = "INSERT INTO " + table + " (" + uuid + ", " + key + ") VALUES (?, ?) ON DUPLICATE KEY UPDATE " + key + " =?;";

        database.executeUpdate(sql, id, object, object);
    }

    public void setValues(String id, Map<String, Object> objectMap, String table) {
        String uuid = databaseValues.getUUID();

        StringBuilder columnBuilder = new StringBuilder("(");
        StringBuilder valuesBuilder = new StringBuilder("(");
        StringBuilder onDuplicateBuilder = new StringBuilder();

        columnBuilder.append(uuid);
        columnBuilder.append(", ");

        checkIfString(id, valuesBuilder);
        valuesBuilder.append(", ");

        Iterator<Map.Entry<String, Object>> iterator = objectMap.entrySet().iterator();

        while(iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String string = entry.getKey();
            Object value = entry.getValue();

            columnBuilder.append("`");
            columnBuilder.append(string);
            columnBuilder.append("`");
            checkIfString(value, valuesBuilder);
            onDuplicateBuilder.append("`");
            onDuplicateBuilder.append(string);
            onDuplicateBuilder.append("`");
            onDuplicateBuilder.append("=");
            checkIfString(value, onDuplicateBuilder);

            if(iterator.hasNext()) {
                columnBuilder.append(", ");
                valuesBuilder.append(", ");
                onDuplicateBuilder.append(", ");
            }
        }

        columnBuilder.append(")");
        valuesBuilder.append(")");

        String sql = "INSERT INTO " + table + " " + columnBuilder + " VALUES " + valuesBuilder + " ON DUPLICATE KEY UPDATE " + onDuplicateBuilder + ";";

        database.executeUpdate(sql);
    }

    private void checkIfString(Object value, StringBuilder stringBuilder) {
        if(value instanceof String) {
            stringBuilder.append("\"");
            stringBuilder.append(value);
            stringBuilder.append("\"");
        } else {
            stringBuilder.append(value);
        }
    }

}
