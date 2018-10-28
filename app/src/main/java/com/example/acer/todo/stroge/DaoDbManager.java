package com.example.acer.todo.stroge;

import android.content.Context;

import com.example.acer.todo.model.DaoMaster;
import com.example.acer.todo.model.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * 管理 数据库，表
 */
public class DaoDbManager {

    private static final String DB_NAME="affair_data";
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    public static DaoDbManager get(){
        return SingleTonHolder.instance;
    }

    private static class SingleTonHolder{
        private static DaoDbManager instance=new DaoDbManager();
    }
    private DaoDbManager(){
    }

    public void init(Context context){
        DaoMaster.OpenHelper helper=new DaoMaster.DevOpenHelper(context,DB_NAME,null);
        Database db=helper.getWritableDb();
        daoMaster =new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoMaster getDaoMaster() {
        return daoMaster;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
