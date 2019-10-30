package com.spark.bitrade.service;

import com.spark.bitrade.dao.WebsiteInformationDao;
import com.spark.bitrade.entity.QWebsiteInformation;
import com.spark.bitrade.entity.WebsiteInformation;
import com.spark.bitrade.service.Base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author rongyu
 * @description
 * @date 2018/1/26 10:18
 */
@Service
public class WebsiteInformationService extends BaseService {
    @Autowired
    private WebsiteInformationDao websiteInformationDao;

    @Transactional(readOnly = true)
    public WebsiteInformation fetchOne() {
        QWebsiteInformation qEntity = QWebsiteInformation.websiteInformation;
        return queryFactory.selectFrom(qEntity).fetchOne();
    }

    public WebsiteInformation save(WebsiteInformation websiteInformation) {
        return websiteInformationDao.save(websiteInformation);
    }

}
