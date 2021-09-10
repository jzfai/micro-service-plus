package top.kuanghua.gatewaytwo.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.kuanghua.feign.tyauth.feign.TokenFeign;
import top.kuanghua.khcomomon.utils.ObjectUtilsSelf;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
/**
 * @Title: AuthorizeFilter
 * @Description:
 * @Auther: kuanghua
 * @create 2020/9/8 21:26
 */
@Slf4j
@Component
public class AuthorizeFilterAfter implements GlobalFilter, Ordered {

    @Value("#{'${filter.allowPaths:}'.empty ? null : '${filter.allowPaths:}'.split(',')}")
    private List<String> allowPaths;

    @Autowired
    private TokenFeign tokenFeign;
//    @Value("${jwt-properties.pubKeyPath}")
//    private String pubKeyPath;
    @Value("${token-properties.renewTokenMinute}")
    private int renewTokenMinute;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory bufferFactory = response.bufferFactory();
        String path = request.getURI().getPath();

        //白名单拦截
        for (String allowPath : allowPaths) {
            if(StringUtils.contains(path,allowPath)){
                return  chain.filter(exchange);
            }
        }
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                        DataBuffer join = dataBufferFactory.join(dataBuffers);
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);
                        // 释放掉内存
                        DataBufferUtils.release(join);
                        String str = new String(content, Charset.forName("UTF-8"));
//                        log.info("返回体：{}", str);
                        //todo 拦截到的返回体内容，可以随意去操作了
                        //2.1.2 如果小于一个小时 刷新token
                        //String jwtToken = request.getHeaders().getFirst("AUTHORIZE_TOKEN");
                        Long expValue = null;
                        Map dataMap=null;
                        try {
//                            Object reqData=tokenFeign.parseToken(jwtToken);
//                            Map map = JSON.parseObject(JSON.toJSONString(reqData), Map.class);
                            String tokenInfo = request.getHeaders().getFirst("TOKEN_INFO");
                            //System.out.println("获取到新的token"+tokenInfo);
                            dataMap = JSON.parseObject(URLDecoder.decode(tokenInfo, "utf-8"), Map.class);
                            expValue = ObjectUtilsSelf.toLong(ObjectUtilsSelf.toString(dataMap.get("exp"))+"000");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Long currentData= DateTime.now().plusMinutes(renewTokenMinute).getMillis();
                        Map strMap=JSON.parseObject(str, Map.class);
                        if(ObjectUtils.isNotEmpty(expValue)&&expValue < currentData){
                            String generateToken=null;
                            try {
                                //使用本地生成
                                //generateToken = JwtUtilsKh.updateToken(dataMap, RsaUtils.getPrivateKey(priKeyPath), 62);
                                //调用ty-auth进行生成
                                dataMap.remove("exp");
                                dataMap.remove("iat");
                                Object object = tokenFeign.updateToken(dataMap);
                                if(ObjectUtilsSelf.isEmpty(object)){
                                    return bufferFactory.wrap(str.getBytes());
                                }
                                Map map = JSON.parseObject(JSON.toJSONString(object), Map.class);
                                generateToken = ObjectUtilsSelf.toString(map.get("data"));
                            } catch (Exception e) {
                                //log.error(e.toString());
                            }
                            strMap.put("isNeedUpdateToken",true);
                            strMap.put("updateToken",generateToken);
                        }
                        response.getHeaders().setContentLength(JSON.toJSONString(strMap).getBytes().length);
                        return bufferFactory.wrap(JSON.toJSONString(strMap).getBytes());
                    }));
                }
                // if body is not a flux. never got there.
                return super.writeWith(body);
            }
        };
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    private DataBuffer getDataBuffer(ServerHttpResponse response, JSONObject  message) {
        byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        //response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        return buffer;
    }

    /*-1代表请求后触发*/
    @Override
    public int getOrder() {
        return -1;
    }
}
