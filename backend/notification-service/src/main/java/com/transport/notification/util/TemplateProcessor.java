package com.transport.notification.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for processing notification templates with variable substitution.
 * 
 * <p>Supports template variables in the format {{variableName}} which are
 * replaced with actual values from a provided map.
 */
@Component
@Slf4j
public class TemplateProcessor {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    /**
     * Processes a template string by replacing variables with values from the provided map.
     * 
     * @param template the template string with variables in {{variableName}} format
     * @param variables map of variable names to their values
     * @return processed template with variables replaced
     */
    public String processTemplate(String template, Map<String, String> variables) {
        if (template == null || template.isEmpty()) {
            return template;
        }

        if (variables == null || variables.isEmpty()) {
            log.warn("Template has variables but no variable map provided");
            return template;
        }

        StringBuffer result = new StringBuffer();
        Matcher matcher = VARIABLE_PATTERN.matcher(template);

        while (matcher.find()) {
            String variableName = matcher.group(1).trim();
            String value = variables.getOrDefault(variableName, "");
            
            if (value.isEmpty()) {
                log.warn("Variable '{}' not found in variable map, using empty string", variableName);
            }
            
            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Checks if a template contains any variables.
     * 
     * @param template the template string to check
     * @return true if template contains variables, false otherwise
     */
    public boolean hasVariables(String template) {
        if (template == null || template.isEmpty()) {
            return false;
        }
        return VARIABLE_PATTERN.matcher(template).find();
    }
}

