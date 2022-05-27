package top.kuanghua.integrationfront.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.kuanghua.integrationfront.service.FrontVmsService;
import top.kuanghua.integrationfront.utils.FrontVmsUtils;
import top.kuanghua.khcomomon.entity.ResResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;
@Api(tags = "前端模板生成")
@RestController
@RequestMapping("VmsTemplate")
public class VmsTemplateController {


    @Resource
    private FrontVmsService FrontVmsService;
    /**
     * 导入数据下载zip包
     * @return 所有数据
     */
    @PostMapping("exportFrontVms")
    @ApiOperation(value = "exportFrontVms")
    public ResResult generatorVms(HttpServletResponse response) throws IOException {
       //FrontVmsService.GeneratorFrontVms();
        response.setContentType("application/zip");
        response.setCharacterEncoding("utf-8");
        //xxx.zip是你压缩包文件名
//        response.setHeader("Content-Disposition", "template.zip");
        //你压缩包路径
        String path = ClassUtils.getDefaultClassLoader().getResource("front-vms").getPath();
        FrontVmsUtils.downloadZip(response, path + File.separator + "export-vms.zip");
        response.setHeader("Access-Control-Expose-Headers", "exportFileName");
        response.setHeader("exportFileName", "export-vms.zip");
        return new ResResult().success("导出成功");
    }

    /**
     * 模拟级联接口数据
     * @return
     */
    @GetMapping("cascadeApiDemo")
    @ApiOperation(value = "cascadeApiDemo")
    public ResResult cascadeApiDemo() throws IOException {
        String string = FrontVmsUtils.readFileToString("front-vms/cascadeData.json");
        ArrayList<Map> jsonData = JSON.parseObject(string, ArrayList.class);
        return new ResResult().success(jsonData);
    }
    /**
     * 模拟select接口数据
     * @return
     */
    @GetMapping("selectApiDemo")
    @ApiOperation(value = "selectApiDemo")
    public ResResult selectApiDemo() {
        ArrayList<Map> arrayList = new ArrayList<>();
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("id","1");
        map1.put("label","支付中");
        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("id","2");
        map2.put("label","支付成功");
        arrayList.add(map1);
        arrayList.add(map2);
        return new ResResult().success(arrayList);
    }
}
