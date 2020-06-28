package com.plarium.logreader.dao;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;

public interface LogDao {

    void save(String logType, ArrayNode logs) throws IOException;

}
