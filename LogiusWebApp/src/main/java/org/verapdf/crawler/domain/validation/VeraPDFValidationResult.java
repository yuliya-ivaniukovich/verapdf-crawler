package org.verapdf.crawler.domain.validation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VeraPDFValidationResult {
    private boolean isValid = false;
    private List<ValidationError> validationErrors = new ArrayList<>();
    private Map<String, String> properties = new HashMap<>();
    private String processingError = null;

    public VeraPDFValidationResult() {
    }

    @JsonProperty
    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    @JsonProperty
    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    @JsonProperty
    public boolean isValid() {
        return isValid;
    }

    @JsonProperty
    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    @JsonProperty
    public Map<String, String> getProperties() {
        return properties;
    }

    @JsonProperty
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @JsonProperty
    public String getProcessingError() {
        return processingError;
    }

    @JsonProperty
    public void setProcessingError(String processingError) {
        this.processingError = processingError;
    }

    public void addProperty(String name, String value) {
        properties.put(name, value);
    }
}
