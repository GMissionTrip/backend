// Principal (DB hit 없이 memberId만 보유)
package com.gangchu.gangchutrip.global.security.jwt;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class MemberPrincipal implements UserDetails {
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public MemberPrincipal(String username, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.authorities = authorities;
    }
    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return ""; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
