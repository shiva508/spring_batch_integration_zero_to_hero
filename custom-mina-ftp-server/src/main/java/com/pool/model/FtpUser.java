package com.pool.model;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.User;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FtpUser implements User {

    private final String userName;
    private final String password;
    private final boolean enabled;
    private final int maxIdleTime;
    private final List<Authority> authorities = new ArrayList<>();
    private final File homeDirectory;

    public FtpUser(String userName,
            String password,
            boolean enabled,
            List<? extends Authority> auths,
            int maxIdleTime,
            File homeDirectory) {
        this.userName = userName;
        this.maxIdleTime = maxIdleTime == -1 ?60_000 : maxIdleTime;
        this.homeDirectory = homeDirectory;
        this.password = password;
        this.enabled = enabled;
        if (auths != null) {
            this.authorities.addAll(auths);
        }
    }

    @Override
    public String getName() {
        return this.userName;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public List<? extends Authority> getAuthorities() {
        return authorities;
    }

    @Override
    public List<? extends Authority> getAuthorities(Class<? extends Authority> aClass) {
        return this.authorities.stream().filter(auth->auth.getClass().equals(aClass)).collect(Collectors.toList());
    }

    @Override
    public AuthorizationRequest authorize(AuthorizationRequest authorizationRequest) {
        return this.getAuthorities()
                .stream()
                .filter(authority -> authority.canAuthorize(authorizationRequest))
                .map(authority -> authority.authorize(authorizationRequest))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int getMaxIdleTime() {
        return this.maxIdleTime;
    }

    @Override
    public boolean getEnabled() {
        return this.enabled;
    }

    @Override
    public String getHomeDirectory() {
        return this.homeDirectory.getAbsolutePath();
    }
}
