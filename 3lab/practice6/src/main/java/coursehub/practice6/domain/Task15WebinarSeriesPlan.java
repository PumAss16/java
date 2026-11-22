package coursehub.practice6.domain;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

record WebinarSeriesPlan(ZoneId organizerZone, List<Instant> sessions, Duration sessionDuration) {

    WebinarSeriesPlan {
        if (organizerZone == null || sessions == null || sessionDuration == null) {
            throw new IllegalArgumentException("plan fields are required");
        }
        if (sessions.isEmpty()) {
            throw new IllegalArgumentException("sessions are required");
        }
        List<Instant> copy = new ArrayList<>(sessions);
        if (copy.contains(null)) {
            throw new IllegalArgumentException("session is required");
        }
        sessions = List.copyOf(copy);
    }

    int lessonCount() {
        return sessions.size();
    }

    List<Instant> sessionsAfter(Instant moment) {
        if (moment == null) {
            throw new IllegalArgumentException("moment is required");
        }
        List<Instant> result = new ArrayList<>();
        for (Instant session : sessions) {
            if (session.isAfter(moment)) {
                result.add(session);
            }
        }
        return List.copyOf(result);
    }

    Instant lastSessionEnd() {
        Instant last = sessions.get(sessions.size() - 1);
        return last.plus(sessionDuration);
    }

    LocalDate localDateOf(Instant moment) {
        if (moment == null) {
            throw new IllegalArgumentException("moment is required");
        }
        return moment.atZone(organizerZone).toLocalDate();
    }
}
