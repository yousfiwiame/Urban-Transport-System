package com.transport.notification.exception;

import com.transport.notification.dto.response.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({GlobalExceptionHandlerTest.TestController.class, GlobalExceptionHandler.class})
@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    @RequestMapping("/test")
    static class TestController {
        @GetMapping("/notification-not-found")
        public void throwNotificationNotFound() {
            throw new NotificationNotFoundException("Notification not found");
        }

        @GetMapping("/template-not-found")
        public void throwTemplateNotFound() {
            throw new TemplateNotFoundException("Template not found");
        }

        @GetMapping("/notification-send-exception")
        public void throwNotificationSendException() {
            throw new NotificationSendException("Failed to send notification");
        }

        @GetMapping("/invalid-recipient")
        public void throwInvalidRecipient() {
            throw new InvalidRecipientException("Invalid recipient");
        }

        @GetMapping("/generic-exception")
        public void throwGenericException() {
            throw new RuntimeException("Generic error");
        }
    }

    @Test
    @DisplayName("Should handle NotificationNotFoundException")
    void testHandleNotificationNotFound() throws Exception {
        mockMvc.perform(get("/test/notification-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Notification Not Found"))
                .andExpect(jsonPath("$.message").value("Notification not found"));
    }

    @Test
    @DisplayName("Should handle TemplateNotFoundException")
    void testHandleTemplateNotFound() throws Exception {
        mockMvc.perform(get("/test/template-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Template Not Found"));
    }

    @Test
    @DisplayName("Should handle NotificationSendException")
    void testHandleNotificationSendException() throws Exception {
        mockMvc.perform(get("/test/notification-send-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Notification Send Failed"));
    }

    @Test
    @DisplayName("Should handle InvalidRecipientException")
    void testHandleInvalidRecipient() throws Exception {
        mockMvc.perform(get("/test/invalid-recipient"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Recipient"));
    }

    @Test
    @DisplayName("Should handle generic exceptions")
    void testHandleGenericException() throws Exception {
        mockMvc.perform(get("/test/generic-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }
}

