package org.verapdf.crawler.report;

import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.verapdf.crawler.domain.crawling.CurrentJob;
import org.verapdf.crawler.domain.report.CrawlJobReport;
import org.verapdf.crawler.app.engine.HeritrixClient;
import org.verapdf.crawler.repository.document.ReportDocumentDao;
import org.verapdf.crawler.repository.jobs.CrawlJobDao;
import org.xml.sax.SAXException;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HeritrixReporter {

    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");

    private final HeritrixClient client;
    private final ReportDocumentDao reportDocumentDao;
    private final CrawlJobDao crawlJobDao;

    public HeritrixReporter(HeritrixClient client, DataSource dataSource, CrawlJobDao crawlJobDao) {
        this.client = client;
        this.crawlJobDao = crawlJobDao;
        reportDocumentDao = new ReportDocumentDao(dataSource);
    }

    // If time is null settings were not provided
    public CrawlJobReport getReport(String job, LocalDateTime time) throws IOException, ParserConfigurationException, SAXException {
        CrawlJobReport result = new CrawlJobReport(job,
                client.getListOfCrawlUrls(job).get(0),
                client.getCurrentJobStatus(job).replaceAll("\\s+",""),
                client.getDownloadedCount(job));
        setFields(result, job, time);
        return result;
    }

    public CrawlJobReport getReport(String job, String jobURL, LocalDateTime time) throws IOException {
        String config = client.getConfig(jobURL);
        CrawlJobReport result = new CrawlJobReport(job,
                HeritrixClient.getListOfCrawlUrlsFromXml(config).get(0),
                "finished",0);
        setFields(result, job, time);
        return result;
    }

    private void setFields(CrawlJobReport report, String job, LocalDateTime time) {
        report.setPdfStatistics(reportDocumentDao.getValidationStatistics(job, time));
        report.setNumberOfODFDocuments(reportDocumentDao.getNumberOfOdfFilesForJob(job, time));
        report.setNumberOfOfficeDocuments(reportDocumentDao.getNumberOfMicrosoftFilesForJob(job, time));
        report.setNumberOfOoxmlDocuments(reportDocumentDao.getNumberOfOoxmlFilesForJob(job, time));
        CurrentJob crawlJob = crawlJobDao.getCrawlJob(job);
        report.setStartTime(crawlJob.getStartTime().format(FORMATTER));
        if(crawlJob.getFinishTime() != null) {
            report.setFinishTime(crawlJob.getFinishTime().format(FORMATTER));
        }
    }

    private File buildODSReport(CrawlJobReport reportData, LocalDateTime time) throws IOException {
        File file = new File(HeritrixClient.baseDirectory + "sample_report.ods");
        final Sheet totalSheet = SpreadSheet.createFromFile(file).getSheet(0);
        totalSheet.ensureColumnCount(2);
        if(time != null) {
            totalSheet.setValueAt(time.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")) + " GMT", 1, 0);
        }
        else {
            totalSheet.setValueAt("", 0, 0);
        }
        totalSheet.setValueAt(reportData.getPdfStatistics().getNumberOfValidPdfDocuments(),1, 1);
        totalSheet.setValueAt(reportData.getNumberOfODFDocuments(),1, 2);
        totalSheet.setValueAt(reportData.getPdfStatistics().getNumberOfValidPdfDocuments() +
                reportData.getNumberOfODFDocuments(), 1, 3);
        totalSheet.setValueAt(reportData.getPdfStatistics().getNumberOfInvalidPdfDocuments(),1, 4);
        totalSheet.setValueAt(reportData.getNumberOfOfficeDocuments(),1, 5);
        totalSheet.setValueAt(reportData.getNumberOfOoxmlDocuments(),1, 6);
        totalSheet.setValueAt(reportData.getNumberOfOfficeDocuments() +
                reportData.getPdfStatistics().getNumberOfInvalidPdfDocuments() +
                reportData.getNumberOfOoxmlDocuments(), 1, 7);

        SpreadSheet spreadSheet = totalSheet.getSpreadSheet();
        setStringsInSheet(spreadSheet.getSheet(1), reportDocumentDao.getMicrosoftOfficeFiles(reportData.getId(), time));
        setStringsInSheet(spreadSheet.getSheet(2), reportDocumentDao.getInvalidPdfFiles(reportData.getId(), time));
        setStringsInSheet(spreadSheet.getSheet(3), reportDocumentDao.getOoxmlFiles(reportData.getId(), time));

        File ODSReport = new File(HeritrixClient.baseDirectory + "report.ods");
        totalSheet.getSpreadSheet().saveAs(ODSReport);
        return ODSReport;
    }

    public File buildODSReport(String job, LocalDateTime time) throws IOException, ParserConfigurationException, SAXException {
        CrawlJobReport reportData = getReport(job, time);
        return buildODSReport(reportData, time);
    }

    public File buildODSReport(String job, String jobURL, LocalDateTime time) throws IOException {
        CrawlJobReport reportData = getReport(job, jobURL, time);
        return buildODSReport(reportData,time);
    }

    public List<String> getOfficeReport(String job, LocalDateTime time) {
        return reportDocumentDao.getMicrosoftOfficeFiles(job, time);
    }

    public List<String> getOoxmlReport(String job, LocalDateTime time) {
        return reportDocumentDao.getOoxmlFiles(job, time);
    }

    public List<String> getInvalidPdfReport(String job, LocalDateTime time) {
        return reportDocumentDao.getInvalidPdfFiles(job, time);
    }

    private void setStringsInSheet(Sheet sheet, List<String> list) {
        int i = 1;
        sheet.ensureColumnCount(1);
        for(String line: list) {
            sheet.ensureRowCount(i + 1);
            sheet.setValueAt(line, 0, i);
            i++;
        }
    }
}
