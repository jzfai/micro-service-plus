package top.kuanghua.velocitygenerator.velocitydemo;

import com.alibaba.fastjson.JSON;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import top.kuanghua.velocitygenerator.utils.FrontVmsUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * @Title: Demo1
 * @Description:
 * @Auther: kuanghua
 * @create 2022-05-16 14:44
 */
public class Demo2 {
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
        Template template = FrontVmsUtils.getAssetTemplate("test.vm");
        StringWriter stringWriter = new StringWriter();
        template.merge(context, stringWriter);

        System.out.println(stringWriter.toString());
        stringWriter.close();
        //生成zip包
        //FrontVmsUtils.createZipFile(FrontVmsUtils.exportZipPath(), FrontVmsUtils.exportVmsPath());

    }
}
