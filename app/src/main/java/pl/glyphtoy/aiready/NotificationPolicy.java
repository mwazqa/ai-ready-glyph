package pl.glyphtoy.aiready;

final class NotificationPolicy {
    enum Result {
        ACCEPTED("regular notification"),
        ONGOING("ongoing notification"),
        GROUP_SUMMARY("notification group summary"),
        ACTIVE_PROGRESS("work still in progress");

        final String reason;
        Result(String reason) { this.reason = reason; }
    }

    static Result evaluate(boolean ongoing, boolean summary, boolean progress) {
        if (ongoing) return Result.ONGOING;
        if (summary) return Result.GROUP_SUMMARY;
        if (progress) return Result.ACTIVE_PROGRESS;
        return Result.ACCEPTED;
    }
}
