package cn.haoyu.mongoTest.manage;

import cn.haoyu.mongoTest.model.CommonField;
import cn.haoyu.mongoTest.model.Field;
import cn.haoyu.mongoTest.utils.BeanUtil;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
     * mongodb 版本为 4.0.5 ，3个副本集部署
     *
     * @return
     * @Date 2019/1/11
     **/
    @Transactional
    public void saveListFields() {
        Field fields = createFields();
        mongoTemplate.save(fields);
//        int error = 1/0;
    }


    /**
     * 1. 使用 spring-boot 2.1.1 支持的 Document 进行接近原生语言查询
     * 2. unwind 多重内嵌数组
     * 3. 比较同一个document 的2个不同字段的值
     *
     * @return
     * @Date 2019/1/31
     **/

    public List<Map> documentAndCompare2Field() {
        List<Document> aggs = new ArrayList<>();
        aggs.add(new Document("$match", new Document("_id", "testId")));
        // unwind array
        aggs.add(new Document("$unwind", "$embedFields"));
        aggs.add(new Document("$unwind", "$embedFields.embedFields"));
        aggs.add(new Document("$match", new Document("embedFields.name", "testName").append("embedFields.embedFields.fieldType", "testFieldType")));
        aggs.add(new Document("$unwind", "$bizs"));

        // compare two field in the same document
        List<String> eqs = new ArrayList<>();
        eqs.add("$embedFields.embedFields.field");
        eqs.add("$bizs.field");
        Document projects = new Document("_id", 1)
                .append("name", 1)
                .append("comment", 1)
                .append("commits", 1)
                .append("treeNodes", 1)
                .append("diff", new Document("$eq", eqs));

        aggs.add(new Document("$project", projects));
        // filter diff
        aggs.add(new Document("$match", new Document("diff", true)));
        aggs.add(new Document("$sort", new Document("commits.commitTime", -1)));
        // not include diff
        aggs.add(new Document("$project", new Document("_id", 1)
                .append("name", 1)
                .append("comment", 1)
                .append("commits", 1)
                .append("treeNodes", 1)));

        AggregateIterable<Map> maps = mongoTemplate.getCollection("field").aggregate(aggs, Map.class);
        MongoCursor<Map> iterator = maps.iterator();
        List<Map> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }

        return result;
    }

    // slice 保留数组的元素个数
    // 0: To update the array <field> to an empty array.
    // 负数: To update the array <field> to contain only the last <num> elements.
    // 正数:To update the array <field> contain only the first <num> elements.

    /**
     * 内嵌文档查询
     *
     * @return
     * @Date 2019/2/1
     **/
    public void embedDocumentOperate() {
        Map<String, Object> result = new HashMap<>();

        // 对象内嵌查询
        Criteria criteria1 = Criteria.where("_id").is("22");
        Query query = Query.query(criteria1);
        Field one = mongoTemplate.findOne(query, Field.class);
        result.put("1", one);
        System.out.println(one);

        Criteria criteria2 = Criteria.where("embedFields.name").is("name11");
        Query query2 = Query.query(criteria2);
        Field two = mongoTemplate.findOne(query2, Field.class);
        result.put("2", two);
        System.out.println(two);

        // 多个 . 点 适用于对象内嵌
        Criteria criteria3 = Criteria.where("embedFields.embedFields.cid").is("22");
        Query query3 = Query.query(criteria3);
        Field three = mongoTemplate.findOne(query3, Field.class);
        result.put("3", three);
        System.out.println(three);

        // list 内嵌查询 , 一层层 拆散
        // 管道查询数组
        List<AggregationOperation> aops = new ArrayList<>();
        aops.add(Aggregation.match(Criteria.where("_id").is("9")));

        // 拆分一级内嵌数组，只取其中的某一条
        aops.add(Aggregation.unwind("embedFields"));
        aops.add(Aggregation.match(Criteria.where("embedFields.cid").is("11")));

        // 拆分二级内嵌数组，只取其中的某一条
        aops.add(Aggregation.unwind("embedFields.embedFields"));
        aops.add(Aggregation.match(Criteria.where("embedFields.embedFields.cid").is("22")));

        // 拆分三级内嵌数组，只取其中的某一条
        aops.add(Aggregation.unwind("embedFields.embedFields.embedFields"));
        aops.add(Aggregation.match(Criteria.where("embedFields.embedFields.embedFields.name").is("name3")));

        List<Map> arbitratorField =
                mongoTemplate.aggregate(Aggregation.newAggregation(aops), "field", Map.class).getMappedResults();

        System.out.println(arbitratorField.size());

    }

    /**
     * 内嵌数组局部更新
     *
     * @return
     * @Date 2019/2/1
     **/
    public void updateEmbedArray() throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        Field field = new Field();
        field.setCid("4TEST4");
        field.setName("好好学习 22!");
        field.setField("天天向上 22!");
        field.setCreateTime(new Date());
        List fields = new ArrayList<>();
        Field field2 = new Field();
        field2.setCid("4test4");
        field2.setName("好哈学习 2222 !");
        field2.setField("天天向上 2222 !");
        field2.setCreateTime(new Date());
        fields.add(BeanUtil.convertBean(field));
        fields.add(BeanUtil.convertBean(field2));

//        String command = "{update: 'arbitratorField',updates: [{ q: {_id:\"3KNAM390aENt\"}, u: {$set : {\"embedFields.$[e1].embedFields.$[e2].name\" : \"好雨 加油！！！\"}},arrayFilters: [{'e1._id':'Wx3uzi'},{'e2._id':'vti7KT'}]}]}";
        // ======================== 基于 spring-data-mongo 2.1.1  start=====================
//        mongoTemplate.executeCommand(command);
        Document collection = new Document("update", "field");
        Document updateDoucment = new Document("q", new Document("_id", "9"));

        // 1. update string 更新字符串元素
        // { "update" : "field", "updates" : [{ "q" : { "_id" : "9" }, "u" : { "$set" : { "embedFields.$[e1].embedFields.$[e2].name" : "奔跑吧 !!! 兄弟 !" } }, "arrayFilters" : [{ "e1.cid" : "11" }, { "e2.cid" : "2" }] }] }
//        Document set = new Document("$set", new Document("embedFields.$[e1].embedFields.$[e2].name", "奔跑吧 !!! 好雨 !"));
//        updateDoucment.put("u", set);

        // 2. update array elem 更新整个内嵌数组元素
        // 对象的更新需要转换成Map or DBObject ,如果直接将对象通过下面2行代码转换成DBObject 还是报错 CodecConfigurationException: Can't find a codec ，
        // 得先换成map
//        Document set = new Document("$set", new Document("embedFields.$[e1].embedFields.$[e2].embedFields", fields));
//        updateDoucment.put("u", set);

        //3. update array add single elem 添加内嵌数组元素
//        Document set = new Document("$push", new Document("embedFields.$[e1].embedFields.$[e2].embedFields", BeanUtil.convertBean(field2)));
//        updateDoucment.put("u", set);
        // 3.1 update array add mutil elems 添加内嵌数组元素，一次添加多个
        // { "update" : "field", "updates" : [{ "q" : { "_id" : "9" }, "u" : { "$push" : { "embedFields.$[e1].embedFields.$[e2].embedFields.$[e3].embedFields" : { "$each" : [{ "field" : "天天向上 22!", "createTime" : { "$date" : 1548990556101 }, "name" : "好好学习 22!", "cid" : "4TEST4" }, { "field" : "天天向上 2222 !", "createTime" : { "$date" : 1548990556101 }, "name" : "好哈学习 2222 !", "cid" : "4test4" }] } } }, "arrayFilters" : [{ "e1.cid" : "11" }, { "e2.cid" : "2" }, { "e3.cid" : "3TEST1" }] }] }
//        Document each = new Document("$each", fields);
//        Document set = new Document("$push", new Document("embedFields.$[e1].embedFields.$[e2].embedFields.$[e3].embedFields", each));
//        updateDoucment.put("u", set);

        // 4. update delete  array elem 删除数组内嵌元素 , $pop删除第一个或最后一个元素 ,$pull删除指定元素
        // { "update" : "field", "updates" : [{ "q" : { "_id" : "9" }, "u" : { "$pull" : { "embedFields.$[e1].embedFields.$[e2].embedFields.$[e3].embedFields" : { "name" : "好好学习 22!" } } }, "arrayFilters" : [{ "e1.cid" : "11" }, { "e2.cid" : "2" }, { "e3.cid" : "3TEST1" }] }] }
        Document pullElem = new Document("name", "好好学习 22!");
        Document pull = new Document("embedFields.$[e1].embedFields.$[e2].embedFields.$[e3].embedFields", pullElem);
        Document set = new Document("$pull", pull);
        updateDoucment.put("u", set);

        ArrayList<Document> filterList = new ArrayList<>();
        filterList.add(new Document("e1.cid", "11"));
        filterList.add(new Document("e2.cid", "2"));
        filterList.add(new Document("e3.cid", "3TEST1"));
        updateDoucment.put("arrayFilters", filterList);

        ArrayList<Document> updateList = new ArrayList<>();
        updateList.add(updateDoucment);
        collection.put("updates", updateList);
        System.out.println(collection.toJson());
        mongoTemplate.executeCommand(collection);
    }


    public Field fetch() {
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").is("22")), Field.class);
    }


}
