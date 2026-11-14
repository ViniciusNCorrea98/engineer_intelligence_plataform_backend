package io.eip.security;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCredentialRepository extends JpaRepository<ServiceCredential, UUID> {
}
