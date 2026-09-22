package com.BankingSystem.Banking_System.SecurityTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginEndpoint_shouldBePublic() throws Exception {

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "email": "",
                                "password": ""
                            }
                            """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserEndpoint_shouldBePublic() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "firstName": "",
                                "lastName": "",
                                "email": "",
                                "password": ""
                            }
                            """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsersEndpoint_shouldRequireAuthentication() throws Exception {

        mockMvc.perform(
                        get("/api/users")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void swaggerApiDocs_shouldBePublic() throws Exception {

        mockMvc.perform(
                        get("/v3/api-docs")
                )
                .andExpect(status().isOk());
    }
}