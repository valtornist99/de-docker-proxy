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

        log.info(request.getMethod());
        String url = request.getRequestURL().toString();
        log.info(url);
        String path = request.getRequestURI().substring(request.getContextPath().length());
        log.info(path);
        Map<String, List<String>> headersMap = Collections.list(request.getHeaderNames()).stream().collect(Collectors.toMap(Function.identity(), h -> Collections.list(request.getHeaders(h))));
        log.info(headersMap.toString());
        log.info(request.getHeader("docker-extended-user-name"));
        Map<String, List<String>> paramsMap = request.getParameterMap().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> Arrays.stream(entry.getValue()).collect(Collectors.toList())));
        log.info(paramsMap.toString());
        String contentType = request.getContentType();
        log.info(contentType);

        if(!userAuthorization.EndpointIsAllowed(request.getHeader("docker-extended-user-name"), path, BodyReader(request))) {
            ctx.unset();
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        return null;
    }

    private Map<String, String> BodyReader(HttpServletRequest request)
    {
        try {
            if(request.getContentType() == null) return new HashMap<>();

            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            log.info(body);

            if(!request.getContentType().equals("application/json")) return new HashMap<>();

            try {
                JSONObject json = (JSONObject) new JSONParser().parse(body);
                Set<String> keys = json.keySet();
                log.info(keys.toString());
                Map<String, String> params = keys.stream().collect(Collectors.toMap(key -> key, key -> json.get(key).toString()));
                return params;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }
}
