package com.example.spring_user_banking.controller;

import com.example.spring_user_banking.SpringUserBankingApplication;
import com.example.spring_user_banking.dto.TransferRequestDTO;
import com.example.spring_user_banking.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.math.BigDecimal;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = {SpringUserBankingApplication.class, AccountControllerIntegrationTest.TestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ExtendWith(SpringExtension.class)
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * Поднимаем контейнер с PostgreSQL.
     * <p>
     * testcontainers автоматически найдёт Docker-образ.
     */
    @Container
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:13-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    @BeforeAll
    static void initAll() {
        // Testcontainers сам запустит всё по аннотации @Container
    }

    @BeforeEach
    void setUp() {
        // Очищаем Redis
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("Интеграционный тест: успешный перевод денег через REST")
    void testTransferSuccess() throws Exception {
        // 1) Генерируем токен для отправителя (допустим, это userId=1)
        String token = "Bearer " + jwtTokenProvider.generateToken(1L);

        // 2) Вызываем POST /accounts/transfer
        mockMvc.perform(post("/accounts/transfer")
                        .contentType(APPLICATION_JSON)
                        .content(
                                "{ \"toUserId\": 2, \"amount\": 100.00 }"
                        )
                        .header("Authorization", token)
                )
                .andExpect(status().isOk());
    }

    @Configuration
    static class TestConfig {

        // Определяем DataSource, который ссылается на контейнер
        @Bean
        public DataSource dataSource() {
            return DataSourceBuilder.create()
                    .url(POSTGRES_CONTAINER.getJdbcUrl())
                    .username(POSTGRES_CONTAINER.getUsername())
                    .password(POSTGRES_CONTAINER.getPassword())
                    .driverClassName(POSTGRES_CONTAINER.getDriverClassName())
                    .build();
        }
    }
}
