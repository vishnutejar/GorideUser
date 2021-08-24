package com.rider.database;

/**
 * Created by ubuntu on 18/11/16.
 */

public interface OnMigrationListener {

    void onMigrate(int oldVersion);
}
