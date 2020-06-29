package com.plarium.logreader.controller;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.plarium.logreader.dao.StorageLogDaoImpl;
import com.plarium.logreader.services.LogProcessingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/save")
@Api(value = "save", description = "controller for saving of log batches")
public class SaveController {

    @PostMapping
    @ApiOperation(value = "Starts a process for saving batch of logs", response = ResponseEntity.class)
    public ResponseEntity saveBatch(@RequestBody @NotEmpty ArrayNode logBatch) throws Exception {
        StorageLogDaoImpl storageLogDao = new StorageLogDaoImpl();
        LogProcessingService logProcessingService = new LogProcessingService(storageLogDao);
        logProcessingService.run(logBatch);
        return ResponseEntity.ok().build();
    }
}
