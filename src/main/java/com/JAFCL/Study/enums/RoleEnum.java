package com.JAFCL.Study.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RoleEnum {
    
    ADMIN(1L),
    EMPLEADO(2L),
    CLIENTE(3L);

    private final Long id;

    public Long getId() {
        return id;
    }

}
