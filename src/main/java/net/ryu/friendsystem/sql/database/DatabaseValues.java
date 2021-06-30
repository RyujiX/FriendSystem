package net.ryu.friendsystem.sql.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseValues {

    public List<String> getAllColumnLabels() {
        return new ArrayList<>(Arrays.asList(
                getUUID() + " VARCHAR(48)",
                getDate() + " VARCHAR(24)",
                "PRIMARY KEY (" + getUUID() + ")")
        );
    }

    public String getUUID() {
        return "uuid";
    }

    public String getDate() {
        return "date";
    }
}
