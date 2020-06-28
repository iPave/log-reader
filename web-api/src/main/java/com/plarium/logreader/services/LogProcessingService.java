package com.plarium.logreader.services;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.plarium.logreader.dao.StorageLogDaoImpl;
import com.plarium.logreader.processing.SavingProcess;
import com.plarium.logreader.processing.TypeGroupingProcess;

import java.io.IOException;
import java.util.Map;


public class LogProcessingService {

    private final StorageLogDaoImpl storageLogDao;

    public LogProcessingService(StorageLogDaoImpl storageLogDao) {
        this.storageLogDao = storageLogDao;
    }

    public void run(ArrayNode input) throws IOException {
        Map<String, ArrayNode> groupedLogs = new TypeGroupingProcess().process(input);
        new SavingProcess(storageLogDao).process(groupedLogs);
    }
}
