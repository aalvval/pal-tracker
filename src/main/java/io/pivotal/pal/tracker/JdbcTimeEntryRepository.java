package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                creationStatementCreator(
                        timeEntry.getProjectId(),
                        timeEntry.getUserId(),
                        Date.valueOf(timeEntry.getDate()),
                        timeEntry.getHours()
                        ),
                generatedKeyHolder
        );

        return find(generatedKeyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(Long timeEntryId) {
        return jdbcTemplate.query(
                findStatementCreator(timeEntryId),
                extractor);
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query("SELECT id, project_id, user_id, date, hours FROM time_entries", mapper);
    }

    @Override
    public TimeEntry update(long timeLineId, TimeEntry timeEntry) {
        jdbcTemplate.update(
                updateStatementCreator(
                        timeLineId,
                        timeEntry.getProjectId(),
                        timeEntry.getUserId(),
                        Date.valueOf(timeEntry.getDate()),
                        timeEntry.getHours()
                )
        );
        return find(timeLineId);
    }

    @Override
    public void delete(Long timeEntryId) {
        jdbcTemplate.update(deleteStatementCreator(timeEntryId));
    }

    private PreparedStatementCreator creationStatementCreator(Long projectId, Long userId, Date date, Integer hours) {
        return con -> {
            PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO time_entries (project_id, user_id, date, hours) " +
                            "VALUES (?, ?, ?, ?)",
                    RETURN_GENERATED_KEYS
            );

            statement.setLong(1, projectId);
            statement.setLong(2, userId);
            statement.setDate(3, date);
            statement.setInt(4, hours);

            return statement;
        };

    }

    private PreparedStatementCreator updateStatementCreator(Long timeEntryId, Long projectId, Long userId, Date date, Integer hours) {
        return con -> {
            PreparedStatement statement = con.prepareStatement(
                    "UPDATE time_entries " +
                            "SET project_id = ?, user_id = ?, date = ?,  hours = ? " +
                            "WHERE id = ?"
            );

            statement.setLong(1, projectId);
            statement.setLong(2, userId);
            statement.setDate(3, date);
            statement.setInt(4, hours);
            statement.setLong(5, timeEntryId);

            return statement;
        };

    }

    private PreparedStatementCreator deleteStatementCreator(Long timeEntryId) {
        return con -> {
            PreparedStatement statement = con.prepareStatement(
                    "DELETE FROM time_entries WHERE id = ?"
            );

            statement.setLong(1, timeEntryId);

            return statement;
        };

    }

    private PreparedStatementCreator findStatementCreator(Long timeEntryId) {
        return con -> {
            PreparedStatement statement = con.prepareStatement(
                    "SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?"
            );

            statement.setLong(1, timeEntryId);

            return statement;
        };

    }

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

    private final ResultSetExtractor<TimeEntry> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;
}
