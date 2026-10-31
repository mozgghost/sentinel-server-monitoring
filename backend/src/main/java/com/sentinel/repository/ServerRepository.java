package com.sentinel.repository;

import com.sentinel.domain.ServerNode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ServerRepository extends JpaRepository<ServerNode, UUID> {
}
