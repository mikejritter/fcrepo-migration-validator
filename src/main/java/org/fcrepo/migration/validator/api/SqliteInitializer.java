package org.fcrepo.migration.validator.api;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class SqliteInitializer {

    private static final Logger logger = LoggerFactory.getLogger(SqliteInitializer.class);

    public static final String STATUS_TABLE = "status";
    public static final String VALIDATION_TYPE_TABLE = "validation_type";
    public static final String VALIDATION_LEVEL_TABLE = "validation_level";
    public static final String RESULT_TABLE = "result";

    private static final String STATUS_TABLE_CREATE =
        "CREATE TABLE " + STATUS_TABLE + "(ordinal INT PRIMARY KEY, status TEXT)";

    private static final String LEVEL_TABLE_CREATE =
        "CREATE TABLE " + VALIDATION_LEVEL_TABLE + "(ordinal INT PRIMARY KEY, validation_level TEXT)";

    private static final String TYPE_TABLE_CREATE =
        "CREATE TABLE " + VALIDATION_TYPE_TABLE + "(ordinal INT PRIMARY KEY, validation_type TEXT)";

    private static final String RESULT_TABLE_CREATE =
        "CREATE TABLE " + RESULT_TABLE +
        "(id INT, status INT, validation_level INT," +
        "validation_type INT, details TEXT, source_obj_id TEXT," +
        "target_obj_id TEXT, source_resource_id TEXT, target_resource_id TEXT)";

    private static final String STATUS_INSERT = "INSERT INTO " + STATUS_TABLE + " values(%s, '%s')";
    private static final String VALIDATION_TYPE_INSERT = "INSERT INTO " + VALIDATION_TYPE_TABLE + " values(%s, '%s')";
    private static final String VALIDATION_LEVEL_INSERT = "INSERT INTO " + VALIDATION_LEVEL_TABLE + " values(%s, '%s')";

    /**
     *
     * @param connection
     * @throws SQLException
     */
    public static void init(final Connection connection) throws SQLException {
        logger.info("Creating database");
        try (/*var connection = dataSource.getConnection();*/
             var statement = connection.createStatement()) {
            statement.executeUpdate(STATUS_TABLE_CREATE);
            statement.executeUpdate(LEVEL_TABLE_CREATE);
            statement.executeUpdate(TYPE_TABLE_CREATE);
            statement.executeUpdate(RESULT_TABLE_CREATE);

            for (var status : ValidationResult.Status.values()) {
                statement.executeUpdate(String.format(STATUS_INSERT, status.ordinal(), status.name()));
            }

            for (var type : ValidationResult.ValidationType.values()) {
                statement.executeUpdate(String.format(VALIDATION_TYPE_INSERT, type.ordinal(), type.name()));
            }

            for (var level : ValidationResult.ValidationLevel.values()) {
                statement.executeUpdate(String.format(VALIDATION_LEVEL_INSERT, level.ordinal(), level.name()));
            }

            connection.commit();
        }
        logger.info("Done");
    }
}
