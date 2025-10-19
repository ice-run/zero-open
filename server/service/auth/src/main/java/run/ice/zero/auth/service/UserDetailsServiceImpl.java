package run.ice.zero.auth.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.ice.zero.api.auth.error.AuthError;
import run.ice.zero.auth.entity.Perm;
import run.ice.zero.auth.entity.User;
import run.ice.zero.auth.repository.PermRepository;
import run.ice.zero.auth.repository.UserRepository;
import run.ice.zero.common.error.AppException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author DaoDao
 */
@Slf4j
@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private PermRepository permRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) {
            throw new UsernameNotFoundException(AuthError.USERNAME_NOT_EXIST + username);
        }
        User user = optional.get();
        Set<GrantedAuthority> grantedAuthorities = getGrantedAuthoritiesByUserId(user.getId());
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }


    /**
     * 根据 id 获取用户
     *
     * @param id Long
     * @return UserDetails
     */
    public UserDetails loadUserById(Long id) throws AppException {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppException(AuthError.USER_ID_NOT_EXIST, String.valueOf(id));
        }
        User user = optional.get();
        Set<GrantedAuthority> grantedAuthorities = getGrantedAuthoritiesByUserId(user.getId());
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }


    /**
     * 获取用户授权
     *
     * @param userId Long
     * @return Set
     */
    private Set<GrantedAuthority> getGrantedAuthoritiesByUserId(Long userId) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        /*
         * 获取用户授权
         */
        List<Perm> perms = permRepository.findAllByUserId(userId);
        /*
         * 声明用户授权
         */
        perms.forEach(perm -> {
            if (perm != null && perm.getCode() != null && Boolean.TRUE.equals(perm.getValid())) {
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(perm.getCode());
                grantedAuthorities.add(grantedAuthority);
            }
        });
        perms.clear();
        return grantedAuthorities;
    }

}
