package com.Revolut.service.api;

import org.jooq.DSLContext;

public interface DatabaseManager {
    DSLContext getSqlDSL();
    void close();
}