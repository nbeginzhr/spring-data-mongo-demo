package cn.haoyu.mongoTest.restController;

import cn.haoyu.mongoTest.manage.FieldBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by haoyu on 2019/1/7.
 */
@RestController
public class MongoTestController {
    private final Logger logger = LoggerFactory.getLogger(MongoTestController.class);

    @Autowired
    private FieldBiz fieldBiz;

    @GetMapping(value = "/api/mongo/test")
    public ResponseEntity<Object> testSave(){
        fieldBiz.saveListFields();
        return ResponseEntity.ok("success !!!");
    }

    @GetMapping(value = "/api/mongo/get")
    public ResponseEntity<Object> get(){
        return ResponseEntity.ok(fieldBiz.fetch());
    }

    @GetMapping(value = "/api/embed/test")
    public ResponseEntity<Object> embedTest(){
        fieldBiz.embedDocumentOperate();
        return ResponseEntity.ok("success");
    }

    @GetMapping(value = "/api/embed/update")
    public ResponseEntity<Object> epdateEmbedTest() throws Exception {
        fieldBiz.updateEmbedArray();
        return ResponseEntity.ok("success");
    }


}
