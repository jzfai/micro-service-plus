package top.kuanghua.tyauth.controller;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.kuanghua.khcomomon.entity.ResResult;
import top.kuanghua.tyauth.service.TokenService;

import java.util.Map;

/**
 * @Title: TokenController
 * @Description:
 * @Auther: kuanghua
 * @create 2021/1/31 16:31
 */

@RestController
@RequestMapping("token")
@Api(tags = "token相关")
@Slf4j
public class TokenController {

    @Autowired
    private TokenService tokenService;
    /*
     * 解析token
     * */
    @ApiOperation("获取token信息")
    @PostMapping("parseToken")
    public ResResult parseToken(@RequestParam("jwtToken") String jwtToken){
        System.out.println("解析出来token了");
        Claims claims = this.tokenService.parseToken(jwtToken);
        System.out.println(claims);
        return  new ResResult().success(claims);
    }

    /*
     * 生成token
     * */
    @ApiOperation("生成token")
    @PostMapping("generateToken")
    //@RequestHeader("authorize_token") String authorize_token
    public ResResult generateToken(@RequestBody Map tokenMap){
        System.out.println("生成的代码信息");
        System.out.println(tokenMap);


        // System.out.println("generateToken获得请求头的信息");
        // 获取一个参数
        // System.out.println(authorize_token);

        String generateToken = this.tokenService.generateToken(tokenMap);
        return  new ResResult().success(generateToken);
    }

    /*
     * 更新token
     * */
    @ApiOperation("更新token")
    @PostMapping("updateToken")
    public ResResult updateToken(@RequestBody Map map){
        String updateToken = this.tokenService.updateToken(map);
        System.out.println("生成信息的token"+updateToken);
        System.out.println("生成信息的token"+map.toString());
        return  new ResResult().success(updateToken);
    }
}


