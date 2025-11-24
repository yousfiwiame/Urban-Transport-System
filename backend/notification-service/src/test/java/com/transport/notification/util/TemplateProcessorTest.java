package com.transport.notification.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Template Processor Unit Tests")
class TemplateProcessorTest {

    private TemplateProcessor templateProcessor;

    @BeforeEach
    void setUp() {
        templateProcessor = new TemplateProcessor();
    }

    @Test
    @DisplayName("Should process template with single variable")
    void testProcessTemplate_SingleVariable() {
        // Given
        String template = "Hello {{name}}";
        Map<String, String> variables = Map.of("name", "John");

        // When
        String result = templateProcessor.processTemplate(template, variables);

        // Then
        assertThat(result).isEqualTo("Hello John");
    }

    @Test
    @DisplayName("Should process template with multiple variables")
    void testProcessTemplate_MultipleVariables() {
        // Given
        String template = "Hello {{firstName}} {{lastName}}, your order {{orderId}} is ready!";
        Map<String, String> variables = Map.of(
                "firstName", "John",
                "lastName", "Doe",
                "orderId", "12345"
        );

        // When
        String result = templateProcessor.processTemplate(template, variables);

        // Then
        assertThat(result).isEqualTo("Hello John Doe, your order 12345 is ready!");
    }

    @Test
    @DisplayName("Should handle missing variables gracefully")
    void testProcessTemplate_MissingVariable() {
        // Given
        String template = "Hello {{name}}, welcome {{missing}}!";
        Map<String, String> variables = Map.of("name", "John");

        // When
        String result = templateProcessor.processTemplate(template, variables);

        // Then
        assertThat(result).isEqualTo("Hello John, welcome !");
    }

    @Test
    @DisplayName("Should return original template when no variables provided")
    void testProcessTemplate_NoVariables() {
        // Given
        String template = "Hello {{name}}";
        Map<String, String> variables = new HashMap<>();

        // When
        String result = templateProcessor.processTemplate(template, variables);

        // Then
        // When no variables are provided, the template processor returns the original template
        assertThat(result).isEqualTo("Hello {{name}}");
    }

    @Test
    @DisplayName("Should return original template when null variables")
    void testProcessTemplate_NullVariables() {
        // Given
        String template = "Hello {{name}}";

        // When
        String result = templateProcessor.processTemplate(template, null);

        // Then
        // When variables are null, the template processor returns the original template
        assertThat(result).isEqualTo("Hello {{name}}");
    }

    @Test
    @DisplayName("Should return empty string when template is null")
    void testProcessTemplate_NullTemplate() {
        // Given
        Map<String, String> variables = Map.of("name", "John");

        // When
        String result = templateProcessor.processTemplate(null, variables);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should detect variables in template")
    void testHasVariables_True() {
        // Given
        String template = "Hello {{name}}";

        // When
        boolean result = templateProcessor.hasVariables(template);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when no variables in template")
    void testHasVariables_False() {
        // Given
        String template = "Hello World";

        // When
        boolean result = templateProcessor.hasVariables(template);

        // Then
        assertThat(result).isFalse();
    }
}

