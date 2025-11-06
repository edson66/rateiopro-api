package com.rateiopro.api.domain.dadosUsuario;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByNome(AppRole appRole);
}
