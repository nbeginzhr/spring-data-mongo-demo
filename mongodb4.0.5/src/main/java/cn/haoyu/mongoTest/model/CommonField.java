package cn.haoyu.mongoTest.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by haoyu on 2018/10/18.
 */
public class CommonField implements Serializable {
    private static final long serialVersionUID = 5871704247558223636L;

    private String cid;
    private String field;
    private String name;
    private String defValue;
    private String fieldType;
    private String descrition;
    private List<CommonField> embedFields;  // 内部字段
    private String title;      // 标题
    private Date createTime;

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

    public String getDefValue() {
        return defValue;
    }

    public void setDefValue(String defValue) {
        this.defValue = defValue;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getDescrition() {
        return descrition;
    }

    public void setDescrition(String descrition) {
        this.descrition = descrition;
    }

    public List<CommonField> getEmbedFields() {
        return embedFields;
    }

    public void setEmbedFields(List<CommonField> embedFields) {
        this.embedFields = embedFields;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
