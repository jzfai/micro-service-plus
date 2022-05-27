package top.kuanghua.integrationfront.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.kuanghua.integrationfront.entity.Brand;
import top.kuanghua.integrationfront.service.BrandService;
import top.kuanghua.integrationfront.utils.FrontVmsUtils;
import top.kuanghua.khcomomon.entity.KHCommonParams;
import top.kuanghua.khcomomon.entity.ResResult;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.*;

@Api(tags = "品牌表(Brand)")
@RestController
@RequestMapping("brand")
public class BrandController {
    @Resource
    private BrandService brandService;
    /**
     * 分页查询所有数据
     * @param brand 查询实体
     * @return 所有数据
     */
    @GetMapping("selectPage")
    @ApiOperation(value = "分页查询所有数据")
    public ResResult selectPage(Brand brand, KHCommonParams commonParams) {
        QueryWrapper<Brand> queryWrapper = new QueryWrapper<>();
        if (ObjectUtils.isNotEmpty(brand.getId())) {
            queryWrapper.like("id", brand.getId());
        }
        if (ObjectUtils.isNotEmpty(brand.getName())) {
            queryWrapper.like("name", brand.getName());
        }
        if (ObjectUtils.isNotEmpty(brand.getImage())) {
            queryWrapper.like("image", brand.getImage());
        }

        if (ObjectUtils.isNotEmpty(brand.getLetter())) {
            queryWrapper.like("letter", brand.getLetter());
        }
        if (ObjectUtils.isNotEmpty(brand.getSeq())) {
            queryWrapper.like("seq", brand.getSeq());
        }
        queryWrapper.orderByDesc("create_time");
        if (StringUtils.isNotEmpty(commonParams.getStartTime())) {
            queryWrapper.between("create_time", commonParams.getStartTime(), commonParams.getEndTime());
        }
        queryWrapper.select("id,name,image,letter,seq,create_time,update_time");
        Page<Brand> brandPage = this.brandService.selectPage(commonParams.getPageNum(), commonParams.getPageSize(), queryWrapper);
        return new ResResult().success(brandPage);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectById")
    @ApiOperation(value = "通过id主键查询单条数据")
    public ResResult selectById(@RequestParam("id") Integer id) {
        return new ResResult().success(this.brandService.selectById(id));
    }





    /**
     * @Description: 根据id数组查询品牌列表
     * @Param: idList id数组
     * @return: ids列表数据
     */
    @ApiOperation(value = "根据id数组查询品牌列表")
    @PostMapping("selectBatchIds")
    public ResResult selectBatchIds(@RequestParam("idList") List<Integer> idList) {
        return new ResResult().success(this.brandService.selectBatchIds(idList));
    }


    /**
     * 新增数据
     *
     * @param brand 实体对象
     * @return 新增结果
     */
    @ApiOperation(value = "新增数据")
    @PostMapping("insert")
    public ResResult insert(@RequestBody Brand brand) {
        return new ResResult().success(this.brandService.insert(brand));
    }

    /**
     * 修改数据
     *
     * @param brand 实体对象
     * @return 修改结果
     */
    @ApiOperation(value = "根据id修改数据")
    @PutMapping("updateById")
    public ResResult updateById(@RequestBody Brand brand) {
        return new ResResult().success(this.brandService.updateById(brand));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @ApiOperation(value = "根据id数组删除数据")
    @DeleteMapping("deleteBatchIds")
    public ResResult deleteBatchIds(@RequestBody List<Long> idList) {
        return new ResResult().success(this.brandService.deleteBatchIds(idList));
    }


    @DeleteMapping("deleteById")
    @ApiOperation(value = "根据id数组删除数据")
    public ResResult deleteById(@RequestParam("id") Integer id) {
        return new ResResult().success(this.brandService.deleteById(id));
    }
}
