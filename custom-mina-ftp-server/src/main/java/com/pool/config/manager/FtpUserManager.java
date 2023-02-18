package com.pool.config.manager;

import com.pool.model.FtpUser;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FtpUserManager implements UserManager {

    private File root ;
    private final JdbcTemplate jdbcTemplate;

    public FtpUserManager(File root, JdbcTemplate jdbcTemplate) {
        this.root = root;
        this.jdbcTemplate = jdbcTemplate;
    }

    // AUTHORITIES
    private final List<Authority> adminAuthorities = List.of(new WritePermission());
    private final List<Authority> anonAuthorities = List.of(
            new ConcurrentLoginPermission(20, 2),
            new TransferRatePermission(4800, 4800));

    private final String insertSql = "insert into ftp_user (username, password, enabled, admin) values (?,?,?,?)";
    private final String selectUsernamesSql = "select distinct username from ftp_user";
    private final String deleteByNameSql = "delete from ftp_user where username = ? ";
    private final String selectByNameSql = "select * from ftp_user where username = ?";

    private final RowMapper<String> usernameRowMapper = (resultSet, i) -> resultSet.getString("username");

    private final RowMapper<User> userRowMapper =(resultSet, rowNum) -> {
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        boolean enabled = resultSet.getBoolean("enabled");
        boolean admin = resultSet.getBoolean("admin");
        int id = resultSet.getInt("id");
        File home = new File(new File(root, Integer.toString(id)), "home");
        Assert.isTrue(home.exists() || home.mkdirs(), "the home directory " + home.getAbsolutePath() + " must exist");
        List<Authority> authorities = new ArrayList<>(anonAuthorities);
        if (admin) {
            authorities.addAll(adminAuthorities);
        }
        return new FtpUser(username, password, enabled, authorities, -1, home);
    };
    @Override
    public User getUserByName(String name) throws FtpException {
        List<User> users = this.jdbcTemplate.query(this.selectByNameSql,
                new Object[]{name}, this.userRowMapper);
        Assert.isTrue(users.size() > 0, "there must be a user by this name");
        return users.get(0);
    }

    @Override
    public String[] getAllUserNames() throws FtpException {
        List<String> userNames = this.jdbcTemplate.query(this.selectUsernamesSql, this.usernameRowMapper);
        return userNames.toArray(new String[0]);
    }

    @Override
    public void delete(String name) throws FtpException {
        int update = this.jdbcTemplate.update(this.deleteByNameSql, name);
        Assert.isTrue(update > -1, "there must be some acknowledgment");
    }

    @Override
    public void save(User user) throws FtpException {
        int update = this.jdbcTemplate.update(this.insertSql,
                user.getName(), user.getPassword(), user.getEnabled(), user.getAuthorities().equals(this.adminAuthorities));
        Assert.isTrue(update > 0, "there must be some acknowledgment of the write");
    }

    @Override
    public boolean doesExist(String username) throws FtpException {
        return this.getUserByName(username) != null;
    }

    @Override
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        Assert.isTrue(authentication instanceof UsernamePasswordAuthentication, "the given authentication must support username and password authentication");
        UsernamePasswordAuthentication upw = (UsernamePasswordAuthentication) authentication;
        String user = upw.getUsername();
        try {
            return Optional
                    .ofNullable(this.getUserByName(user))
                    .filter(u -> {
                        String incomingPw = u.getPassword();
                        return encode(incomingPw).equalsIgnoreCase(u.getPassword());
                    })
                    .orElseThrow(() -> new AuthenticationFailedException("Authentication has failed! Try your username and password."));
        } catch (FtpException e) {
            throw new RuntimeException(e);
        }
    }

    private String encode(String pw) {
        return pw;
    }
    @Override
    public String getAdminName() throws FtpException {
        return "admin";
    }

    @Override
    public boolean isAdmin(String s) throws FtpException {
        return getAdminName().equalsIgnoreCase(s);
    }
}
