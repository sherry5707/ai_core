package com.kinstalk.her.voip.model.db;

import android.provider.BaseColumns;

import static com.kinstalk.her.voip.model.db.RecordMetaDate.Column.ACCEPT_TIME;
import static com.kinstalk.her.voip.model.db.RecordMetaDate.Column.CALL_TYPE;
import static com.kinstalk.her.voip.model.db.RecordMetaDate.Column.CREATE_TIME;
import static com.kinstalk.her.voip.model.db.RecordMetaDate.Column.END_TIME;
import static com.kinstalk.her.voip.model.db.RecordMetaDate.Column.IS_ACCEPT;
import static com.kinstalk.her.voip.model.db.RecordMetaDate.Column.IS_READ;
import static com.kinstalk.her.voip.model.db.RecordMetaDate.Column.PEER_NAME;
import static com.kinstalk.her.voip.model.db.RecordMetaDate.Column.PEER_UID;

/**
 * Created by siqing on 17/6/8.
 */

public interface RecordMetaDate {
    String DB_NAME = "reocrd.db";

    String TABLE_NAME = "records";

    int DB_VERSION = 1;


    interface Column extends BaseColumns {
        String PEER_UID = "peer_uid";
        String PEER_NAME = "peer_name";
        String CALL_TYPE = "call_type";
        String IS_ACCEPT = "is_accept";
        String ACCEPT_TIME = "accept_time";
        String END_TIME = "end_time";
        String CREATE_TIME = "create_time";
        String IS_READ = "is_read";
    }


    String SQL_CREATE = "create table " + TABLE_NAME + "("
            + Column._ID + " integer primary key autoincrement,"
            + PEER_UID + " text,"
            + PEER_NAME + " text,"
            + CALL_TYPE + " integer,"
            + IS_ACCEPT + " integer,"
            + END_TIME + " long,"
            + ACCEPT_TIME + " long,"
            + IS_READ + " integer,"
            + CREATE_TIME + " long)";

    String SQL_DELETE = "drop table " + TABLE_NAME;
}
