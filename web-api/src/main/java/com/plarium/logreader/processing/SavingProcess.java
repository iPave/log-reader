package com.plarium.logreader.processing;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.plarium.logreader.dao.StorageLogDaoImpl;

import java.io.IOException;
import java.util.Map;

public class SavingProcess implements Process<Map<String, ArrayNode>, Void> {

    private StorageLogDaoImpl storageLogDao;

    public SavingProcess(StorageLogDaoImpl storageLogDao) {
        this.storageLogDao = storageLogDao;
    }

    @Override
    public Void process(Map<String, ArrayNode> input) throws IOException {
        for (Map.Entry<String, ArrayNode> logGroup : input.entrySet()) {
            String logType = logGroup.getKey();
            ArrayNode logsBatch = logGroup.getValue();
            storageLogDao.save(logType, logsBatch);
        }

        return null;
    }
}
