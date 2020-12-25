package com.docker.extended.docker_proxy.repository.main;

import com.docker.extended.docker_proxy.domain.main.MParam;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MParamRepository extends CrudRepository<MParam, UUID> {
}
