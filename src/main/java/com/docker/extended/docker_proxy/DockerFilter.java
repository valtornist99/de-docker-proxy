package com.docker.extended.docker_proxy;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DockerFilter extends ZuulFilter {

    private final UserAuthorization userAuthorization;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        log.info("Method: " + request.getMethod());
        String url = request.getRequestURL().toString();
        log.info("Url: " + url);
        String path = request.getRequestURI().substring(request.getContextPath().length());
        log.info("Path: " + path);
        Map<String, List<String>> headersMap = Collections.list(request.getHeaderNames()).stream().collect(Collectors.toMap(Function.identity(), h -> Collections.list(request.getHeaders(h))));
        log.info("Headers: " + headersMap.toString());
        log.info("User: " + request.getHeader("docker-extended-user-name"));
        Map<String, List<String>> paramsMap = request.getParameterMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> Arrays.stream(entry.getValue()).collect(Collectors.toList())));
        log.info("Params: " + paramsMap.toString());
        String contentType = request.getContentType();
        log.info("Content type: " + contentType);

        if(!userAuthorization.EndpointIsAllowed(request.getHeader("docker-extended-user-name"), path, BodyReader(request))) {
            ctx.unset();
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        return null;
    }

    private Map<String, String> BodyReader(HttpServletRequest request)
    {
        log.info("OK1");
        try {
            log.info("OK1.0");
            log.info(request.toString());
            log.info("OK1.1");
            String ct = request.getContentType();
            log.info(ct);
            log.info("OK1.2");
            if(ct == null) {log.info("OK1.3"); return new HashMap<>();}
            log.info("OK2");

            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            log.info(body);
            log.info("OK3");

            if(!request.getContentType().equals("application/json")) return new HashMap<>();

            try {
                JSONObject json = (JSONObject) new JSONParser().parse(body);
                Set<String> keys = json.keySet();
                log.info("OK4");
                log.info(keys.toString());
                Map<String, String> params = keys.stream().collect(Collectors.toMap(key -> key, key -> json.get(key).toString()));
                log.info(params.toString());
                log.info("OK5");
                return params;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("OK6");
        return new HashMap<>();
    }
}
