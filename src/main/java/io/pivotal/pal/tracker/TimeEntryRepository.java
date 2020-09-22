package io.pivotal.pal.tracker;

import java.util.Collection;
import java.util.List;

public interface TimeEntryRepository {
    public TimeEntry create(TimeEntry timeEntry) ;

    public TimeEntry find(Long timeEntryId);

    public List<TimeEntry> list();

    public TimeEntry update(long timelineId, TimeEntry timeEntry);

    public void delete(Long timeEntryId);
}
