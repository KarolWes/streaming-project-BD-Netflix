package utils;

import models.EtlAgg;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Connector {

    public static SinkFunction<EtlAgg> getMySQLSink(ParameterTool properties) {
        JdbcStatementBuilder<EtlAgg> statementBuilder =
                new JdbcStatementBuilder<EtlAgg>() {
                    @Override
                    public void accept(PreparedStatement ps, EtlAgg data) throws SQLException {
                        ps.setLong(1, data.getFilmId());
                        ps.setString(2, data.getTitle());
                        ps.setString(3, data.getDate());
                        ps.setLong(4, data.getRateCount());
                        ps.setLong(5, data.getRateSum());
                        ps.setLong(6, data.getReviewerCount());
                    }
                };
        JdbcConnectionOptions connectionOptions = new
                JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withUrl(properties.getRequired("mysql.url"))
                .withDriverName("com.mysql.jdbc.Driver")
                .withUsername(properties.getRequired("mysql.username"))
                .withPassword(properties.getRequired("mysql.password"))
                .build();
        JdbcExecutionOptions executionOptions = JdbcExecutionOptions.builder()
                .withBatchSize(100)
                .withBatchIntervalMs(200)
                .withMaxRetries(5)
                .build();
        SinkFunction<EtlAgg> jdbcSink =
                JdbcSink.sink("insert into netflix_sink" +
                                "(movie_id, title, date, " +
                                "rate_count, rate_sum, reviewer_count) \n" +
                                "values (?, ?, ?, ?, ?, ?)",
                        statementBuilder,
                        executionOptions,
                        connectionOptions);
        return jdbcSink;
    }

}
