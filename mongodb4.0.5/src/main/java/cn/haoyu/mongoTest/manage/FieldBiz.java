package cn.haoyu.mongoTest.manage;

import cn.haoyu.mongoTest.model.CommonField;
import cn.haoyu.mongoTest.model.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by haoyu on 2019/1/7.
 */
@Component("fieldBiz")
public class FieldBiz {

    @Autowired
    private MongoTemplate mongoTemplate;


    public Field createFields() {
        Field field = new Field();
        field.setCid(new Random().nextInt(10) + "");
        field.setField("test001");
        field.setName("name001");

        List<CommonField> commonFields = createCommonFieldList(1);

        field.setEmbedFields(commonFields);
        return field;
    }

    private List<CommonField> createCommonFieldList(int count) {
        List<CommonField> result = new ArrayList<>();
        CommonField commonField1 = new CommonField();
        commonField1.setCid(count + "");
        commonField1.setField("field" + count);
        commonField1.setName("name" + count);

        CommonField commonField2 = new CommonField();
        commonField2.setCid(count + "" + count);
        commonField2.setField("field" + count + count);
        commonField2.setName("name" + count + count);


        if (count < 3) {
            List<CommonField> arbCommonFieldList = createCommonFieldList(++count);
            commonField2.setEmbedFields(arbCommonFieldList);
            commonField2.setEmbedFields(arbCommonFieldList);

        }
        result.add(commonField1);
        result.add(commonField2);
        return result;
    }

    /**
     * 事务测试，方法异常时，事务回滚
     *  mongodb 版本为 4.0.5 ，3个副本集部署
     * @return
     * @Date 2019/1/11
     **/
    @Transactional
    public void saveListFields() {
        Field fields = createFields();
        mongoTemplate.save(fields);
//        int error = 1/0;
    }


    public Field fetch(){
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").is("1NzBN8a0Y3J6")),Field.class);
    }


    
}
