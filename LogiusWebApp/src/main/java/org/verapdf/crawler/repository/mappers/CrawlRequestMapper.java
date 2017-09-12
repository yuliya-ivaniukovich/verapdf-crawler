package org.verapdf.crawler.repository.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.verapdf.crawler.domain.crawling.CrawlRequest;
import org.verapdf.crawler.repository.jobs.CrawlRequestDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CrawlRequestMapper implements RowMapper<CrawlRequest> {

    @Override
    public CrawlRequest mapRow(ResultSet resultSet, int i) throws SQLException {
        CrawlRequest result = new CrawlRequest(resultSet.getString(CrawlRequestDao.FIELD_ID),
                                resultSet.getString(CrawlRequestDao.FIELD_REPORT_EMAIL),
                resultSet.getDate(CrawlRequestDao.FIELD_CRAWL_SINCE));
        result.setFinished(resultSet.getBoolean(CrawlRequestDao.FIELD_IS_FINISHED));
        return result;
    }
}