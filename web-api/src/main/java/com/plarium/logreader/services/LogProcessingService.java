package com.plarium.logreader.services;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.plarium.logreader.dao.StorageLogDaoImpl;
import com.plarium.logreader.processing.Chain;
import com.plarium.logreader.processing.SavingProcess;
import com.plarium.logreader.processing.TypeGroupingProcess;


public class LogProcessingService {

    private final StorageLogDaoImpl storageLogDao;

    public LogProcessingService(StorageLogDaoImpl storageLogDao) {
        this.storageLogDao = storageLogDao;
    }

    public void run(ArrayNode input) throws Exception {
        Chain.createStart(new TypeGroupingProcess())
                .append(new SavingProcess(storageLogDao))
                .start(input);
    }
}
