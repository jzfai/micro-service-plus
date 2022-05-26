package top.kuanghua.integrationfront.service;

import com.alibaba.fastjson.JSON;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.springframework.stereotype.Service;
import top.kuanghua.integrationfront.utils.FrontVmsUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @Title: FrontVmsService
 * @Description:
 * @Auther: kuanghua
 * @create 2022-05-25 11:34
 */
@Service
public class FrontVmsService {
    public  void GeneratorFrontVms() throws IOException {
        //模拟数据
        String string = FrontVmsUtils.readFileToString("front-vms/data.json");
        Map<String, Object> jsonData = JSON.parseObject(string, Map.class);

        Context context = FrontVmsUtils.getVelocityContext();
        // 设置变量
 //       Map<String, Object> editPageConfig = JSON.parseObject(jsonData.get("editPageConfig").toString(), Map.class);
        context.put("apiConfig", jsonData);
//        context.put("queryConfig", jsonData.get("searchConditionConfigs"));
//        context.put("tableConfig", jsonData.get("dataTablePageConfig"));
//        context.put("formConfig", editPageConfig.get("editFieldConfigs"));
//        context.put("commonConfig", jsonData.get("editPageConfig"));
        Template template = FrontVmsUtils.getAssetTemplate("index.vm");
        FileWriter fileWriter = new FileWriter(FrontVmsUtils.exportVmsPath() + "index.vue");
        template.merge(context, fileWriter);
        fileWriter.close();

        //第二个模板
        Template addModal = FrontVmsUtils.getAssetTemplate("add-model.vm");
        FileWriter addModalWriter = new FileWriter(FrontVmsUtils.exportVmsPath() + "add-model.vue");
        addModal.merge(context, addModalWriter);
        addModalWriter.close();

        //生成zip包
        FrontVmsUtils.createZipFile(FrontVmsUtils.exportZipPath(), FrontVmsUtils.exportVmsPath());
    }

    public static void main(String[] args) throws IOException {
        //模拟数据
        String string = FrontVmsUtils.readFileToString("front-vms/data.json");
        Map<String, Object> jsonData = JSON.parseObject(string, Map.class);

        Context context = FrontVmsUtils.getVelocityContext();
        // 设置变量
        // Map<String, Object> editPageConfig = JSON.parseObject(jsonData.get("editPageConfig").toString(), Map.class);
        context.put("apiConfig", jsonData.get("apiConfig"));
        context.put("queryConfig",jsonData.get("queryConfig"));
        context.put("tableConfig", jsonData.get("tableConfig"));
        context.put("formConfig", jsonData.get("formConfig"));
        context.put("commonConfig", jsonData.get("commonConfig"));
        Template template = FrontVmsUtils.getAssetTemplate("CRUD.vm");
        FileWriter fileWriter = new FileWriter(FrontVmsUtils.exportVmsPath() + "CRUD.vue");
        template.merge(context, fileWriter);
        fileWriter.close();

        //第二个模板
        Template addModal = FrontVmsUtils.getAssetTemplate("CRUDForm.vm");
        FileWriter addModalWriter = new FileWriter(FrontVmsUtils.exportVmsPath() + "CRUDForm.vue");
        addModal.merge(context, addModalWriter);
        addModalWriter.close();

        //生成zip包
        FrontVmsUtils.createZipFile(FrontVmsUtils.exportZipPath(), FrontVmsUtils.exportVmsPath());
    }
}
