package com.quickzetuser.database;

/**
 * Created by ubuntu on 18/11/16.
 */

public interface OnMigrationListener {

    void onMigrate(int oldVersion);
}
