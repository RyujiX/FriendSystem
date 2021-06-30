package net.ryu.friendsystem.data;

import lombok.Getter;

import java.util.UUID;

public class Friends {
    @Getter public UUID uuid;
    @Getter public String date;

    public Friends setUUID(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public Friends setDate(String date) {
        this.date = date;
        return this;
    }
}
