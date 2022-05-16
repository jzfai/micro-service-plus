package top.kuanghua.integrationfront.velocitydemo;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.ToolManager;

import java.io.IOException;
import java.io.StringWriter;
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
        // 设置velocity资源和创建velocity模板引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        ve.init();

        // 加载toolbox
        ToolManager manager = new ToolManager();
        manager.configure("tools.xml");
        Context ctx = manager.createContext();
        // 设置变量
        ctx.put("name", "Velocity");
        List list = new ArrayList();
        list.add("1");
        list.add("2");
        ctx.put("list", list);

        //加载模板
        Template tpl = ve.getTemplate("front-vms/test.vm");

        //合并数据到模板（stringWriter或fileWriter）
        StringWriter sw = new StringWriter();
        tpl.merge(ctx, sw);
        System.out.println(sw.toString());

        //关闭流
        sw.close();
    }
}
