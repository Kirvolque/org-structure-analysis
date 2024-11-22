package com.bigcompany.reporting;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a report as a collection of report entries. This record ensures that each report
 * is immutable and maintains a consistent state through the lifecycle of its use.
 * <p>
 * This class provides methods to encapsulate a list of {@link ReportEntry} and output a
 * formatted string that summarizes the contained entries according to their respective formats.
 * </p>
 *
 * @param entries A list of {@link ReportEntry} objects that make up the report; should not be null
 */
public record Report(List<ReportEntry> entries) {
    public Report(List<ReportEntry> entries) {
        this.entries = Collections.unmodifiableList(entries);
    }

    /**
     * Returns a formatted string representation of the entire report using entries' own format methods.
     *
     * @return Formatted report string.
     */
    public String format() {
        return entries.stream()
                .map(ReportEntry::formatEntry) // Using method reference to use formatEntry of each ReportEntry
                .collect(Collectors.joining("\n"));
    }
}
