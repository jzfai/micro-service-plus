package top.kuanghua.velocitygenerator.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.kuanghua.khcomomon.entity.ResResult;
import top.kuanghua.velocitygenerator.utils.FrontVmsUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Api(tags = "(VmsTemplate)")
@RestController
@RequestMapping("VmsTemplate")
public class VmsTemplateController {

    //    @Resource
    //    private BrandService brandService;

    /**
     * 导入数据下载zip包
     *
     * @return 所有数据
     */
    @PostMapping("generatorVms")
    @ApiOperation(value = "generatorVms")
    public ResResult generatorVms(HttpServletResponse response) {
        response.setContentType("application/zip");
        response.setCharacterEncoding("utf-8");
        //xxx.zip是你压缩包文件名
        response.setHeader("Content-Disposition", "attachment;filename=template.zip");
        //你压缩包路径
        String path = ClassUtils.getDefaultClassLoader().getResource("front-vms").getPath();
        FrontVmsUtils.downloadZip(response, path + File.separator + "template.zip");

        return new ResResult().success("导出成功");
    }
}
