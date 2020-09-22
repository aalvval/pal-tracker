package io.pivotal.pal.tracker;

import java.util.*;

public class InMemoryTimeEntryRepository implements TimeEntryRepository{

    private final Map<Long, TimeEntry> database = new HashMap<>();

    private long counter = 0L;

    public TimeEntry create(TimeEntry timeEntry) {
        this.counter++;
        //long newId = (long) database.values().size() + 1;
        timeEntry.setId(this.counter);
        this.database.put(this.counter, timeEntry);
        return timeEntry;
    }

    public TimeEntry find(Long timeEntryId) {
        return this.database.get(timeEntryId);
    }

    public List<TimeEntry> list() {
        return new ArrayList<>(this.database.values());
    }

    public TimeEntry update(long timelineId, TimeEntry timeEntry) {
        if (this.database.get(timelineId) == null) {
            return null;
        }
        timeEntry.setId(timelineId);
        this.database.put(timelineId, timeEntry);
        return timeEntry;
    }

    public void delete(Long timeEntryId) {
        this.database.remove(timeEntryId);
    }
}
