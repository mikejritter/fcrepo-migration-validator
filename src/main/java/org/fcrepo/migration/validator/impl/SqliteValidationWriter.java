/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */
package org.fcrepo.migration.validator.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.fcrepo.migration.validator.api.SqliteInitializer;
import org.fcrepo.migration.validator.api.ValidationResult;
import org.fcrepo.migration.validator.api.ValidationResultWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shake
 */
public class SqliteValidationWriter implements ValidationResultWriter {

    private static final Logger logger = LoggerFactory.getLogger(SqliteValidationWriter.class);

    /*
    private static final String INSERT = "INSERT INTO " + SqliteInitializer.RESULT_TABLE +
                                         // 4 int, 5 text
                                         " values(%s, %s, %s, %s, '%s', '%s', '%s', '%s', '%s')";
     */

    private static final String PINSERT = "INSERT INTO " + SqliteInitializer.RESULT_TABLE +
                                          // 4 int, 5 text
                                          " values(?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private int count = 0;
    private final Connection connection;

    /**
     * @param connection
     */
    public SqliteValidationWriter(final Connection connection) {
        try {
            connection.setAutoCommit(false);
            this.connection = connection;
            SqliteInitializer.init(this.connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public synchronized void write(final List<ValidationResult> results) {
        try (final var statement = connection.prepareStatement(PINSERT)) {
            for (var result : results) {
                if (count % 1000 == 0) {
                    connection.commit();
                }
                final var status = result.getStatus();
                final var type = result.getValidationType();
                final var level = result.getValidationLevel();

                statement.setInt(1, result.getIndex());
                statement.setInt(2, status.ordinal());
                statement.setInt(3, level.ordinal());
                statement.setInt(4, type.ordinal());

                statement.setString(5, result.getDetails());
                statement.setString(6, result.getSourceObjectId());
                statement.setString(7, result.getTargetObjectId());
                statement.setString(8, result.getSourceResourceId());
                statement.setString(9, result.getTargetResourceId());

                statement.execute();
                count++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void finish() {
        try {
            connection.commit();
        } catch (SQLException e) {
            // ouch
        }
    }
}
