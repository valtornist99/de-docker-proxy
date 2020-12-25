package com.docker.extended.docker_proxy.repository.main;

import com.docker.extended.docker_proxy.domain.main.MEndpoint;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MEndpointRepository extends CrudRepository<MEndpoint, UUID> {
}