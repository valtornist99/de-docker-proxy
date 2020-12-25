package com.docker.extended.docker_proxy.repository.main;

import com.docker.extended.docker_proxy.domain.main.MUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MUserRepository extends CrudRepository<MUser, UUID> {
    List<MUser> findByUserName(String userName);
}
