package top.kuanghua.velocitygenerator.utils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.ToolManager;
import org.springframework.util.ClassUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @Title: FrontVmsUtils
 * @Description: 前端模板生成工具类
 * @Auther: kuanghua
 * @create 2022-05-17 13:57
 */
public class FrontVmsUtils {
    //velocity tools配置文件路径
    public static final String ToolManagerConfigPath = "front-vms" + File.separator + "tools.xml";
    //velocity 模板路径
    public static final String VelocityTemplateDoc = "front-vms";
    //velocity 模板路径
    public static final String ZipDir = "front-vms" + File.separator + "tools.xml";

    /**
     * 获取Resurce下的目录路径
     *
     * @param dirName 第一级目录名称
     * @return 得到的目录名称
     */
    public static String getResourceUnderPath(String dirName) {
        return getResourceFilePath(dirName);
    }

    /**
     * 获取Resurce下的目录路径
     *
     * @param dirName    第一级目录名称
     * @param towDirName 第二级目录名称
     * @return 得到的目录名称
     */
    public static String getResourceUnderPath(String dirName, String towDirName) {
        return getResourceFilePath(dirName + File.separator + towDirName);
    }


    /**
     * @param firstLevelDirName 第一级目录名称
     * @param templateName      模板名称
     * @return Template
     */
    public static Template getVelocityTemplate(String firstLevelDirName, String templateName) {
        // 设置velocity资源和创建velocity模板引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, getResourceUnderPath(VelocityTemplateDoc));
        ve.init();
        return ve.getTemplate(firstLevelDirName + File.separator + templateName);
    }

    /**
     * @param templateName 模板名称
     * @return Template
     */
    public static Template getVelocityTemplate(String templateName) {
        // 设置velocity资源和创建velocity模板引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, getResourceUnderPath(VelocityTemplateDoc));
        ve.init();
        return ve.getTemplate(templateName);
    }

    /**
     * @return velocity Context
     */
    public static Context getVelocityContext() {
        // 加载toolbox
        ToolManager manager = new ToolManager();
        manager.configure(ToolManagerConfigPath);
        return manager.createContext();
    }

    /**
     * @param configPath Context 配置文件位置
     * @return velocity Context
     */
    public static Context getVelocityContext(String configPath) {
        // 加载toolbox
        ToolManager manager = new ToolManager();
        manager.configure(configPath);
        return manager.createContext();

    }

    /**
     * 获取Resurce下的目录路径
     *
     * @param dirName 第一级目录名称
     * @return 得到的目录名称
     */
    public static String getResourceDirPath(String dirName) {
        return getResourceFilePath(dirName);
    }


    /**
     * 获取Resurce下的文件路径
     *
     * @param dirName  第一级目录名称
     * @param fileName 第一级目录下文件的名称
     * @return 得到的目录名称
     */
    public static String getResourceFilePath(String dirName, String fileName) {
        return getResourceFilePath(dirName + File.separator + fileName);
    }


    /**
     * 获取Resurce下的目录路径
     *
     * @param dirName    第一级目录名称
     * @param towDirName 第二级目录名称
     * @param fileName   第二级目录下文件的名称
     * @return 得到的目录名称
     */
    public static String getResourceFilePath(String dirName, String towDirName, String fileName) {
        return getResourceFilePath(dirName + File.separator + towDirName + File.separator + fileName);
    }

    /**
     * @param path 获取classPath下目录的路径，没有则会创建
     * @return
     */
    private static String getResourceFilePath(String path) {

        String url = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        File file = new File(url + path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return ClassUtils.getDefaultClassLoader().getResource(path).getPath();
    }

    /**
     * @param response
     * @param path     导出文件的路径
     */
    public static void downloadZip(HttpServletResponse response, String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }

        try {
            ZipFile zipFile = new ZipFile(file);
            InputStream fis = new FileInputStream(zipFile.getFile());
            IOUtils.copy(fis, response.getOutputStream());
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //    if (file.exists()) {
        //        file.delete();
        //    }
    }

    /**
     * @param outputDirPath 导出zip包的路径 如  file://xxx.zip
     * @param inputDirPath  需要压缩的目录名字 如 file://doc
     */
    public static void createZipFile(String outputDirPath, String zipName, String inputDirPath) {
        try {
            new ZipFile(outputDirPath + File.separator + zipName).addFolder(new File(inputDirPath));
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }
}
