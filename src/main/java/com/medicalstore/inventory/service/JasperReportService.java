package com.medicalstore.inventory.service;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Map;

public interface JasperReportService {
    ByteArrayInputStream exportToExcel(String templateName, Collection<?> data, Map<String, Object> parameters) throws Exception;
}
