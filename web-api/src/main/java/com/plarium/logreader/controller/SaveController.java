package com.plarium.logreader.controller;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.plarium.logreader.dto.LogRequestDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/save")
@Api(value = "save", description = "controller for saving of log batches")
public class SaveController {

    @PostMapping(value = "/{log-type}")
    @ApiOperation(value = "Получение стратегии заправок на маршруте", response = ResponseEntity.class)
    public ResponseEntity saveBatch(@PathVariable("log-type") String logType, @RequestBody @Valid ArrayNode logBatch) {
        System.out.println("LOG TYPE IS " + logType);
        System.out.println("LOG BATCH IS " + logBatch);
        return ResponseEntity.ok().build();
    }
}
