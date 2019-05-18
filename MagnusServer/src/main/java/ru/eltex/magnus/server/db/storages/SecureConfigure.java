package ru.eltex.magnus.server.db.storages;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

public interface SecureConfigure {
    void setSqlQuery(AuthenticationManagerBuilder authenticationMgr);
}
