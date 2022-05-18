package top.kuanghua.velocitygenerator.velocitydemo;

import com.alibaba.fastjson.JSON;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import top.kuanghua.velocitygenerator.utils.FrontVmsUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @Title: Demo1
 * @Description:
 * @Auther: kuanghua
 * @create 2022-05-16 14:44
 */
public class Demo1 {
    public static void main(String[] args) throws IOException {
        //模拟数据
        String string = FrontVmsUtils.readFileToString("front-vms/data.json");
        Map<String, Object> jsonData = JSON.parseObject(string, Map.class);

        Context context = FrontVmsUtils.getVelocityContext();
        // 设置变量
        context.put("apiConfig", jsonData.get("apiConfig"));
        context.put("queryConfig", jsonData.get("queryConfig"));
        context.put("tableConfig", jsonData.get("tableConfig"));
        context.put("formConfig", jsonData.get("formConfig"));
        context.put("commonConfig", jsonData.get("commonConfig"));
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
}
