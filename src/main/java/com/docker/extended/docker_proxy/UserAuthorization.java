package com.docker.extended.docker_proxy;

import com.docker.extended.docker_proxy.domain.main.MEndpoint;
import com.docker.extended.docker_proxy.domain.main.MParam;
import com.docker.extended.docker_proxy.domain.main.MUser;
import com.docker.extended.docker_proxy.repository.main.MUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserAuthorization {

    private final MUserRepository mUserRepository;

    public boolean EndpointIsAllowed(String userName, String endpoint, Map<String, String> params) {
        List<MUser> users = mUserRepository.findByUserName(userName);
        if(users.isEmpty()) return false;

        MUser user = users.get(0);
        List<MEndpoint> userEndpoints = user.getMEndpoints();
        List<MEndpoint> matchesEndpoints = userEndpoints.stream().filter(ep -> Pattern.matches(endpoint, ep.getContent())).collect(Collectors.toList());
        if(matchesEndpoints.isEmpty()) return false;

        MEndpoint curEndpoint = matchesEndpoints.get(0);
        List<String> curParams = curEndpoint.getMParams().stream().map(MParam::getContent).collect(Collectors.toList());
        List<String> notEmptyParams = params.entrySet().stream().filter(entry -> !IsEmptyParam(entry.getValue())).map(Map.Entry::getKey).collect(Collectors.toList());

        List<String> matchesParams = curParams.stream().filter(curParam -> notEmptyParams.contains(curParam)).collect(Collectors.toList());
        if(!matchesParams.isEmpty()) return false;

        return true;
    }

    private boolean IsEmptyParam(String param) {
        List<String> emptyVariants = Arrays.asList(new String[] {"","\"\"", "[]", "{}"});
        return emptyVariants.contains(param);
    }
}
