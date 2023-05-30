package com.sarang.codeforcesapi.services;

import com.sarang.codeforcesapi.models.CfUser;
import com.sarang.codeforcesapi.repositories.CodeforcesRepository;
import com.sarang.codeforcesapi.utils.CodeforcesApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
class CoderforcesServiceTest {

    @MockBean
    private CodeforcesRepository codeforcesRepository;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private CoderforcesService coderforcesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void fetchAndSaveUserSuccess() {
        String handle = "SARANG11";
        String apiUrl = "https://codeforces.com/api/user.info?handles=" + handle;
        CodeforcesApiResponse apiResponse = new CodeforcesApiResponse();
        CfUser user = new CfUser();
        user.setHandle(handle);
        apiResponse.setResult(new CfUser[]{user});
        when(restTemplate.getForObject(apiUrl, CodeforcesApiResponse.class)).thenReturn(apiResponse);

        when(codeforcesRepository.save(user)).thenReturn(user);

        CfUser result = coderforcesService.fetchAndSaveUser(handle);

        verify(restTemplate).getForObject(apiUrl, CodeforcesApiResponse.class);
        verify(codeforcesRepository).save(user);

        assertEquals(user, result);
    }

    @Test
    void getAllUsers() {
        CfUser user1 = new CfUser();
        user1.setHandle("user1");
        CfUser user2 = new CfUser();
        user2.setHandle("user2");
        List<CfUser> expectedUsers = Arrays.asList(user1, user2);
        when(codeforcesRepository.findAll()).thenReturn(expectedUsers);

        List<CfUser> actualUsers = coderforcesService.getAllUsers();

        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    void fetchAndSaveUserNull() {
        String handle = "john123";
        String apiUrl = "https://codeforces.com/api/user.info?handles=" + handle;
        when(restTemplate.getForObject(apiUrl, CodeforcesApiResponse.class)).thenReturn(null);

        CfUser result = coderforcesService.fetchAndSaveUser(handle);

        verify(restTemplate).getForObject(apiUrl, CodeforcesApiResponse.class);
        verifyNoInteractions(codeforcesRepository);

        assertEquals(null, result);
    }

}