package cn.haoyu.mongoTest.model;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by haoyu on 2018/6/26.
 */
public class Field implements Serializable {
    private static final long serialVersionUID = -9141181298551447894L;

    private String cid;
    private String field;
    private String name;

    private String useFor;
    private String title;
    private String subheading;
    private List<CommonField> embedFields;
    private Date createTime;

    @Id
    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUseFor() {
        return useFor;
    }

    public void setUseFor(String useFor) {
        this.useFor = useFor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubheading() {
        return subheading;
    }

    public void setSubheading(String subheading) {
        this.subheading = subheading;
    }

    public List<CommonField> getEmbedFields() {
        return embedFields;
    }

    public void setEmbedFields(List<CommonField> embedFields) {
        this.embedFields = embedFields;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
