package top.kuanghua.velocitygenerator.velocitydemo;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import top.kuanghua.velocitygenerator.utils.FrontVmsUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Title: Demo1
 * @Description:
 * @Auther: kuanghua
 * @create 2022-05-16 14:44
 */
public class Demo1 {
    public static void main(String[] args) throws IOException {

        Context context = FrontVmsUtils.getVelocityContext();

        // 设置变量
        context.put("name", "Velocity");
        List list = new ArrayList();
        list.add("1");
        list.add("2");
        context.put("list", list);
        Template template = FrontVmsUtils.getVelocityTemplate("test.vm");
        FileWriter fileWriter = new FileWriter(FrontVmsUtils.getResourceFilePath("vue", "test111.vue"));
        template.merge(context, fileWriter);
        fileWriter.close();
        //生成zip包
        FrontVmsUtils.createZipFile(FrontVmsUtils.getResourceDirPath("front-vms"), "filetest.zip", FrontVmsUtils.getResourceDirPath("zgj"));


    }
}
