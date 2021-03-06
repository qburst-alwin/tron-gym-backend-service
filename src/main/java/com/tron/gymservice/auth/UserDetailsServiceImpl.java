package com.tron.gymservice.auth;

import com.tron.gymservice.entity.UserMasterInfo;
import com.tron.gymservice.repository.UserMasterInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service   // It has to be annotated with @Service.
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserMasterInfoRepository userMasterInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserMasterInfo appUser = userMasterInfoRepository.findByUserName(username);


        String password=encoder.encode(appUser.getPassword());

        if (appUser.getUserName().equals(username)) {

            // Remember that Spring needs roles to be in this format: "ROLE_" + userRole (i.e. "ROLE_ADMIN")
            // So, we need to set it to that format, so we can verify and compare roles (i.e. hasRole("ADMIN")).
            List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                    .commaSeparatedStringToAuthorityList("ROLE_" + appUser.getRole());

            // The "User" class is provided by Spring and represents a model class for user to be returned by UserDetailsService
            // And used by auth manager to verify and check user authentication.
            return new User(appUser.getUserName(), appUser.getPassword(), grantedAuthorities);
        }
        throw new UsernameNotFoundException("Username: " + username + " not found");
    }


}