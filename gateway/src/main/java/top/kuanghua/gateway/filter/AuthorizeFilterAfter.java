package top.kuanghua.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.reactivestreams.Publisher;
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
import top.kuanghua.authpom.service.TokenService;
import top.kuanghua.commonpom.utils.ObjSelfUtils;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.nio.charset.Charset;
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

    @Value("#{'${filter.afterNeedFilterPaths:}'.empty ? null : '${filter.afterNeedFilterPaths:}'.split(',')}")
    private List<String> afterNeedFilterPaths;

    @Resource
    private TokenService tokenService;

    @Value("${token-properties.renewTokenMinute}")
    private int renewTokenMinute;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory bufferFactory = response.bufferFactory();
        String path = request.getURI().getPath();

        //???????????????
        for (String allowPath : afterNeedFilterPaths) {
            if (!StringUtils.contains(path, allowPath)) {
                return chain.filter(exchange);
            }
        }
        //??????token????????????exp???????????????????????????
        ServerHttpResponseDecorator decoratedResponse = null;
        try {
            decoratedResponse = new ServerHttpResponseDecorator(response) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer join = dataBufferFactory.join(dataBuffers);
                            byte[] content = new byte[join.readableByteCount()];
                            join.read(content);
                            // ???????????????
                            DataBufferUtils.release(join);
                            String str = new String(content, Charset.forName("UTF-8"));
                            // log.info("????????????{}", str);
                            //todo ??????????????????????????????????????????????????????
                            //2.1.2 ???????????????????????? ??????token
                            //String jwtToken = request.getHeaders().getFirst("AUTHORIZE_TOKEN");
                            Long expValue = null;
                            Map dataMap = null;
                            try {
                                String tokenInfo = request.getHeaders().getFirst("TOKEN_INFO");
                                dataMap = JSON.parseObject(URLDecoder.decode(tokenInfo, "utf-8"), Map.class);
                                expValue = ObjSelfUtils.toLong(ObjSelfUtils.toString(dataMap.get("exp")) + "000");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Long currentData = DateTime.now().plusMinutes(renewTokenMinute).getMillis();
                            Map strMap = JSON.parseObject(str, Map.class);
                            if (ObjectUtils.isNotEmpty(expValue) && expValue < currentData) {
                                String generateToken = null;
                                try {
                                    //??????ty-auth????????????
                                    dataMap.remove("exp");
                                    dataMap.remove("iat");
                                    Object object = tokenService.updateToken(dataMap);
                                    if (ObjSelfUtils.isEmpty(object)) {
                                        return bufferFactory.wrap(str.getBytes());
                                    }
                                    Map map = JSON.parseObject(JSON.toJSONString(object), Map.class);
                                    generateToken = ObjSelfUtils.toString(map.get("data"));
                                } catch (Exception e) {
                                    log.error(e.toString());
                                }
                                strMap.put("isNeedUpdateToken", true);
                                strMap.put("updateToken", generateToken);
                            }
                            response.getHeaders().setContentLength(JSON.toJSONString(strMap).getBytes().length);
                            return bufferFactory.wrap(JSON.toJSONString(strMap).getBytes());
                        }));
                    }
                    // if body is not a flux. never got there.
                    return super.writeWith(body);
                }
            };
        } catch (Exception e) {
            return chain.filter(exchange);
        }
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    /*-1?????????????????????*/
    @Override
    public int getOrder() {
        return -1;
    }
}
