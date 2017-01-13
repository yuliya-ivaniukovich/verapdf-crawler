package org.verapdf.crawler.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PDFValidationStatistics {
    private int numberOfInvalidPDFs;
    private int numberOfValidPDFs;
    private String invalidPDFReportURL;

    public PDFValidationStatistics() {}

    public PDFValidationStatistics(int numberOfInvalidPDFs, int numberOfValidPDFs, String invalidPDFReportURL) {
        this.numberOfInvalidPDFs = numberOfInvalidPDFs;
        this.numberOfValidPDFs = numberOfValidPDFs;
        this.invalidPDFReportURL = invalidPDFReportURL;
    }

    @JsonProperty
    public int getNumberOfInvalidPDFs() {
        return numberOfInvalidPDFs;
    }

    @JsonProperty
    public int getNumberOfValidPDFs() {
        return numberOfValidPDFs;
    }

    @JsonProperty
    public String getInvalidPDFReportURL() { return invalidPDFReportURL; }
}
