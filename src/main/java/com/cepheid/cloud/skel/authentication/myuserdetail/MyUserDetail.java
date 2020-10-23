package com.cepheid.cloud.skel.authentication.myuserdetail;

import com.cepheid.cloud.skel.model.Reader;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;


public class MyUserDetail implements UserDetails {

    private final Reader mReader;

    public MyUserDetail(Reader reader) {
        mReader=reader;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.
            createAuthorityList(mReader.getRole());
    }

    @Override
    public String getPassword() {
        return mReader.getPassword();
    }

    @Override
    public String getUsername() {
        return mReader.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
