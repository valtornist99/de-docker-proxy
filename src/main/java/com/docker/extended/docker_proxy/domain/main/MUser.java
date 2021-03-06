package com.docker.extended.docker_proxy.domain.main;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class MUser {
    @org.hibernate.annotations.Type(type="org.hibernate.type.UUIDCharType")
    @Id
    private final UUID id = UUID.randomUUID();
    private final String userName;
    private final UUID permissionId;
    @OneToMany(fetch = FetchType.EAGER)
    private final List<MEndpoint> mEndpoints;
}
