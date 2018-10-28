package com.example.acer.todo.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 用了greendao之后，，不能使用序列化
 */
@Entity
public class Affair  {

    @Id(autoincrement = true)
    private Long id;

    private String title;
    @Property(nameInDb = "content")
    private String article;

    private int belong;

    @Generated(hash = 434007637)
    public Affair(Long id, String title, String article, int belong) {
        this.id = id;
        this.title = title;
        this.article = article;
        this.belong = belong;
    }

    @Generated(hash = 435208867)
    public Affair() {
    }

    public int getBelong() {
        return belong;
    }

    public void setBelong(int belong) {
        this.belong = belong;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
