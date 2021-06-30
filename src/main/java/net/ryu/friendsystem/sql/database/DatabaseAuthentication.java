package net.ryu.friendsystem.sql.database;

import lombok.Data;

@Data
public class DatabaseAuthentication {
    private String host, port, username, password, database;
}