package com.cepheid.cloud.skel.authentication.myuserdetail;

import com.cepheid.cloud.skel.model.Reader;
import com.cepheid.cloud.skel.repository.ReaderRepository;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

public class MyUserServiceTest {

    @Mock
    ReaderRepository readerRepository;
    @InjectMocks
    MyUserService myUserService;

    @Captor
    ArgumentCaptor<String> stringCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void user_service_should_load_user_by_username() throws Exception {
        //GIVEN
        String username = "fazli";
        Reader reader = Reader.builder().username(username).password("123321").role("ADMIN").build();
        given(readerRepository.findByUsername(anyString())).willReturn(Optional.of(reader));

        //WHEN
        UserDetails userDetails = myUserService.loadUserByUsername(reader.getUsername());

        //THEN
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(reader.getUsername());
        then(readerRepository).should(times(1)).findByUsername(stringCaptor.capture());
        String capturedUserName = stringCaptor.getValue();
        assertThat(capturedUserName).isEqualTo(username);
    }

    @Test
    public void user_service_should_not_load_user_by_username() {
        //GIVEN
        String username = "unknown";
        String errMessage=String.format("User with username : %s not found", username);
        given(readerRepository.findByUsername(anyString())).willReturn(Optional.empty());

        //WHEN
        assertThatThrownBy(()->myUserService.loadUserByUsername(username))
            .isInstanceOfAny(UsernameNotFoundException.class)
            .hasMessage(errMessage);
        //THEN
        then(readerRepository).should(times(1)).findByUsername(anyString());
    }
}

