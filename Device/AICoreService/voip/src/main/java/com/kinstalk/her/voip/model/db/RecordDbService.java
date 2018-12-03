package com.kinstalk.her.voip.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kinstalk.her.voip.model.entity.RecordEntity;
import com.kinstalk.her.voip.ui.utils.LogUtils;
import com.kinstalk.her.voip.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siqing on 17/6/8.
 */

public class RecordDbService implements RecordMetaDate {

    private Context mContext;
    private RecordSQLiteOpenHelper mDbHelper;
    private SQLiteDatabase database;

    public RecordDbService(Context context) {
        this.mContext = context;
        mDbHelper = new RecordSQLiteOpenHelper(context);
    }

    public RecordEntity insertRecord(RecordEntity recordEntity) {
        if (recordEntity != null) {
            ContentValues values = new ContentValues();
            values.put(Column.PEER_UID, recordEntity.getPeerUid());
            values.put(Column.PEER_NAME, recordEntity.getPeerName());
            values.put(Column.CALL_TYPE, recordEntity.getCallType());
            values.put(Column.IS_ACCEPT, recordEntity.isAccept());
            values.put(Column.CREATE_TIME, recordEntity.getCreateTime());
            values.put(Column.END_TIME, recordEntity.getEndTime());
            values.put(Column.ACCEPT_TIME, recordEntity.getAcceptTime());
            values.put(Column.IS_READ, recordEntity.isRead());
            try {
                getDatabase().beginTransaction();
                long id = getDatabase().insert(TABLE_NAME, null, values);
                LogUtils.e("insertRecord id : " + id);
                recordEntity.setId(id);
                getDatabase().setTransactionSuccessful();
            } catch (Exception e) {
                LogUtils.i("[updateRecord]" + e.toString());
            } finally {
                getDatabase().endTransaction();
            }
        }
        return recordEntity;
    }

    public int deleteRecord(long id) {
        int result = 0;
        try {
            getDatabase().beginTransaction();
            result = getDatabase().delete(TABLE_NAME, Column._ID + " = ?", new String[]{String.valueOf(id)});
            LogUtils.e("deleteRecord id : " + id);
            getDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            LogUtils.i("[deleteRecord]" + e.toString());
        } finally {
            getDatabase().endTransaction();
        }
        return result;
    }

    public void deleteRecords(RecordEntity recordEntity) {
        if (recordEntity == null) {
            return;
        }
        boolean result = true;
        try {
            getDatabase().beginTransaction();
            for (RecordEntity entity : recordEntity.getMergeRecords()) {
                int count = getDatabase().delete(TABLE_NAME, Column._ID + " = ?", new String[]{String.valueOf(entity.getId())});
                if (count <= 0) {
                    result = false;
                    break;
                }
            }
            if (result) {
                getDatabase().setTransactionSuccessful();
            }
        } catch (Exception e) {

        } finally {
            getDatabase().endTransaction();
        }
    }

    public int updateRecord(RecordEntity recordEntity) {
        if (recordEntity == null || recordEntity.getId() == 0) {
            return 0;
        }
        int result = 0;
        try {
            getDatabase().beginTransaction();
            result = getDatabase().update(TABLE_NAME, parseValues(recordEntity), Column._ID + " = ?", new String[]{String.valueOf(recordEntity.getId())});
            getDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            LogUtils.i("[updateRecord]" + e.toString());
        } finally {
            getDatabase().endTransaction();
        }

        return result;
    }

    private ContentValues parseValues(RecordEntity recordEntity) {
        ContentValues values = new ContentValues();
        values.put(Column.PEER_UID, recordEntity.getPeerUid());
        values.put(Column.PEER_NAME, recordEntity.getPeerName());
        values.put(Column.CALL_TYPE, recordEntity.getCallType());
        values.put(Column.IS_ACCEPT, recordEntity.isAccept());
        values.put(Column.CREATE_TIME, recordEntity.getCreateTime());
        values.put(Column.END_TIME, recordEntity.getEndTime());
        values.put(Column.ACCEPT_TIME, recordEntity.getAcceptTime());
        values.put(Column.IS_READ, recordEntity.isRead());
        return values;
    }

    public List<RecordEntity> queryRecords() {
        List<RecordEntity> recordEntityList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getDatabase().query(TABLE_NAME, null, null, null, null, null, Column.CREATE_TIME + " DESC");
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(Column._ID));
                String peerId = cursor.getString(cursor.getColumnIndex(Column.PEER_UID));
                String peerName = cursor.getString(cursor.getColumnIndex(Column.PEER_NAME));
                int callType = cursor.getInt(cursor.getColumnIndex(Column.CALL_TYPE));
                int isAccept = cursor.getInt(cursor.getColumnIndex(Column.IS_ACCEPT));
                long createTime = cursor.getLong(cursor.getColumnIndex(Column.CREATE_TIME));
                long acceptTime = cursor.getLong(cursor.getColumnIndex(Column.ACCEPT_TIME));
                long endTime = cursor.getLong(cursor.getColumnIndex(Column.END_TIME));
                int isRead = cursor.getInt(cursor.getColumnIndex(Column.IS_READ));
                RecordEntity recordEntity = new RecordEntity();
                recordEntity.setId(id);
                recordEntity.setAccept(isAccept == RecordConstant.ACCEPT ? true : false);
                recordEntity.setPeerUid(peerId);
                recordEntity.setPeerName(peerName);
                recordEntity.setAcceptTime(acceptTime);
                recordEntity.setCreateTime(createTime);
                recordEntity.setEndTime(endTime);
                recordEntity.setCallType(callType);
                recordEntity.setRead(isRead);
                recordEntityList.add(recordEntity);
            }
        } catch (Exception e) {
            LogUtils.i("[updateRecord]" + e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return recordEntityList;
    }

    /**
     * 经过未接合并处理的通话记录
     *
     * @return
     */
    public List<RecordEntity> queryRecordsWithMissCallMerge() {
        List<RecordEntity> recordEntityList = new ArrayList<>();
        List<RecordEntity> originRecordList = queryRecords();
        while (!originRecordList.isEmpty()) {
            RecordEntity mergeRecord = getOneMergeRecord(originRecordList);
            recordEntityList.add(mergeRecord);
        }
        return recordEntityList;
    }

    private RecordEntity getOneMergeRecord(List<RecordEntity> originRecordList) {
        List<RecordEntity> records = new ArrayList<RecordEntity>();
        long lastMissTime = 0;
        String lastUid = null;
        for (int i = 0; i < originRecordList.size(); i++) {
            RecordEntity entity = originRecordList.get(i);
            boolean isMiss = entity.getCallType() == RecordConstant.CALL_TYPE_IN && entity.isAccept() != RecordConstant.ACCEPT;
            if (!isMiss && lastMissTime == 0) {
                records.add(entity);
                break;
            } else {
                if (isMiss) {
                    if (lastMissTime == 0) {
                        records.add(entity);
                        lastMissTime = entity.getCreateTime();
                        lastUid = entity.getPeerUid();
                    } else {
                        if (DateUtils.isSameDay(lastMissTime, entity.getCreateTime()) ) {
                            if (entity.getPeerUid().equals(lastUid)) {
                                //如果是一天并且是同一个人合并记录继续
                                records.add(entity);
                            } else {
                                continue;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        originRecordList.removeAll(records);
        RecordEntity resultEntity = records.get(0);
        resultEntity.setMergeRecords(records);
        return resultEntity;
    }

    public List<RecordEntity> queryUnReadRecords() {
        List<RecordEntity> recordEntityList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getDatabase().query(TABLE_NAME, null, Column.IS_READ + " = ? and " + Column.CALL_TYPE + " = ? and " + Column.IS_ACCEPT + " = ?", new String[]{"0", String.valueOf(RecordConstant.CALL_TYPE_IN), "0"}, null, null, Column.CREATE_TIME + " DESC");
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(Column._ID));
                String peerId = cursor.getString(cursor.getColumnIndex(Column.PEER_UID));
                String peerName = cursor.getString(cursor.getColumnIndex(Column.PEER_NAME));
                int callType = cursor.getInt(cursor.getColumnIndex(Column.CALL_TYPE));
                int isAccept = cursor.getInt(cursor.getColumnIndex(Column.IS_ACCEPT));
                long createTime = cursor.getLong(cursor.getColumnIndex(Column.CREATE_TIME));
                long acceptTime = cursor.getLong(cursor.getColumnIndex(Column.ACCEPT_TIME));
                long endTime = cursor.getLong(cursor.getColumnIndex(Column.END_TIME));
                int isRead = cursor.getInt(cursor.getColumnIndex(Column.IS_READ));
                RecordEntity recordEntity = new RecordEntity();
                recordEntity.setId(id);
                recordEntity.setAccept(isAccept == RecordConstant.ACCEPT ? true : false);
                recordEntity.setPeerUid(peerId);
                recordEntity.setPeerName(peerName);
                recordEntity.setAcceptTime(acceptTime);
                recordEntity.setCreateTime(createTime);
                recordEntity.setEndTime(endTime);
                recordEntity.setCallType(callType);
                recordEntity.setRead(isRead);
                recordEntityList.add(recordEntity);
            }
        } catch (Exception e) {
            LogUtils.i("[updateRecord]" + e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return recordEntityList;
    }

    /**
     * 批量标记已读用事务更快
     *
     * @param recordEntityList
     */
    public void signRead(List<RecordEntity> recordEntityList) {
        if (recordEntityList == null || recordEntityList.isEmpty()) {
            return;
        }
        boolean result = true;
        try {
            for (RecordEntity recordEntity : recordEntityList) {
                recordEntity.setRead(1);
            }
            getDatabase().beginTransaction();
            for (RecordEntity recordEntity : recordEntityList) {
                int count = getDatabase().update(TABLE_NAME, parseValues(recordEntity), Column._ID + " = ?", new String[]{String.valueOf(recordEntity.getId())});
                if (count < 0) {
                    result = false;
                    break;
                }
            }
            if (result) {
                getDatabase().setTransactionSuccessful();
            }
        } catch (Exception e) {

        } finally {
            try {
                getDatabase().endTransaction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 删除超过500条的数据
     */
    public void deleteLimitRecords(Integer offset) throws Exception{
        long startTime = System.currentTimeMillis();
        Cursor cursorCount = getDatabase().rawQuery("select count(*) from " + TABLE_NAME , null);
        int resultCount = 0;
        while (cursorCount.moveToNext()) {
            resultCount = cursorCount.getInt(0);
            break;
        }
        cursorCount.close();
        int deleteCount = 0;
        if (resultCount > offset) {
            Cursor cursor = getDatabase().rawQuery("select * from " + TABLE_NAME + " order by " + Column.CREATE_TIME + " desc limit " + offset + ", 1", null);
            int id = 0;
            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex(Column._ID));
                if (id != 0) {
                    break;
                }
            }
            cursor.close();
            if (id != 0) {
                deleteCount = getDatabase().delete(TABLE_NAME, Column._ID + "< ?" , new String[]{String.valueOf(id)});
            }
        }
        LogUtils.e("delete > " + offset + ",delete count " + deleteCount + ", user time " + (System.currentTimeMillis() - startTime));
    }

    public SQLiteDatabase getDatabase() {
        if (database == null) {
            database = mDbHelper.getWritableDatabase();
        }
        return database;
    }

    public void destory() {
        if (database != null) {
            database.close();
        }
        if (database != null) {
            mDbHelper.close();
        }
    }
}
