package cn.haoyu.mongoTest.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by haoyu on 2019/1/31.
 */
public class Biz implements Serializable {
    private static final long serialVersionUID = 3491015511783691447L;

    private String cid;
    private String bizName;
    private String useFor;
    private String field;
    private Date createTime;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public String getUseFor() {
        return useFor;
    }

    public void setUseFor(String useFor) {
        this.useFor = useFor;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
