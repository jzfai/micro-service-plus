package top.kuanghua.integrationfront.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.ClassUtils;
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
}
