package com.example.acer.todo.stroge;

import com.example.acer.todo.model.Affair;
import com.example.acer.todo.model.AffairDao;

/**
 * 提供 任务的  增删改查  “接口”
 */
public class AffairDaoDao {

    private static AffairDaoDao mAffairDao;
    private final DaoDbManager daoManager;


    public AffairDaoDao(){
        daoManager =DaoDbManager.get();
    }

    public static AffairDaoDao getInstance(){
        if (mAffairDao == null) {
            mAffairDao =new AffairDaoDao();
        }
        return mAffairDao;
    }

    /**
     * daoSession.getAffairDao   是自动daosession中生成的
     * @return
     */
    public AffairDao getAffairDao(){
        return daoManager.getDaoSession().getAffairDao();
    }


}
