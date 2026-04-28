package pe.edu.vallegrande.slack.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HealthControllerTest {

    private HealthController healthController;

    @BeforeEach
    void setUp() {
        healthController = new HealthController();
    }

    @Test
    void testHealthEndpoint() {
        ResponseEntity<Map<String, Object>> response = healthController.health();
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("UP", body.get("status"));
        assertEquals("Slack Integration Demo", body.get("service"));
        assertEquals("1.0.0", body.get("version"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void testInfoEndpoint() {
        ResponseEntity<Map<String, String>> response = healthController.info();
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("Jenkins-Slack Integration", body.get("application"));
        assertEquals("Demo de integración CI/CD con notificaciones", body.get("description"));
        assertEquals("Valle Grande", body.get("author"));
    }
}
