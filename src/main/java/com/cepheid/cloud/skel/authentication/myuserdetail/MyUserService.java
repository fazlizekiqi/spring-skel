package com.cepheid.cloud.skel.authentication.myuserdetail;



import com.cepheid.cloud.skel.repository.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component

public class MyUserService implements UserDetailsService {

    private final ReaderRepository readerRepository;

    @Autowired
    public MyUserService(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return readerRepository.findByUsername(username)
            .map(MyUserDetail::new)
            .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username : %s not found", username)));
    }


}
