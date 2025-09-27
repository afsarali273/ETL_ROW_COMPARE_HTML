import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ETLHtmlGenerator {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Generates complete HTML page for ETL comparison results
     */
    public static String generateETLComparisonHtml(ETLComparisonResult comparisonResult) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>ETL Data Validation - ").append(escapeHtml(comparisonResult.getSourceTableName())).append(" vs ").append(escapeHtml(comparisonResult.getTargetTableName())).append("</title>\n");
        html.append(generateCSS());
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class=\"dashboard\">\n");
        html.append(generateHeader(comparisonResult));
        html.append(generateSummaryCards(comparisonResult.getSummary()));
        html.append(generateFiltersAndSearch());
        html.append(generateDiscrepantRecordsTable(comparisonResult.getDiscrepantRecords()));
        html.append("    </div>\n");
        html.append(generateJavaScript(comparisonResult.getDiscrepantRecords()));
        html.append("</body>\n");
        html.append("</html>");
        
        return html.toString();
    }
    
    /**
     * Generates the CSS styles
     */
    private static String generateCSS() {
        return """
            <style>
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }
                
                body {
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    background: #0a0a0a;
                    color: #e5e5e5;
                    line-height: 1.6;
                }
                
                .dashboard {
                    max-width: 1400px;
                    margin: 0 auto;
                    padding: 2rem;
                }
                
                .header {
                    margin-bottom: 2rem;
                }
                
                .header h1 {
                    font-size: 2rem;
                    font-weight: 600;
                    color: #ffffff;
                    margin-bottom: 0.5rem;
                }
                
                .header-meta {
                    color: #888;
                    font-size: 0.9rem;
                }
                
                .summary-grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                    gap: 1rem;
                    margin-bottom: 2rem;
                }
                
                .summary-card {
                    background: #111;
                    border: 1px solid #333;
                    border-radius: 8px;
                    padding: 1.5rem;
                }
                
                .summary-card h3 {
                    font-size: 0.9rem;
                    color: #888;
                    margin-bottom: 0.5rem;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                }
                
                .summary-value {
                    font-size: 2rem;
                    font-weight: 600;
                    color: #ffffff;
                }
                
                .summary-percentage {
                    font-size: 0.9rem;
                    margin-top: 0.25rem;
                }
                
                .text-green { color: #22c55e; }
                .text-yellow { color: #eab308; }
                .text-red { color: #ef4444; }
                .text-blue { color: #3b82f6; }
                
                .controls {
                    display: flex;
                    gap: 1rem;
                    margin-bottom: 2rem;
                    flex-wrap: wrap;
                }
                
                .search-input, .filter-select {
                    background: #111;
                    border: 1px solid #333;
                    border-radius: 6px;
                    padding: 0.5rem 1rem;
                    color: #e5e5e5;
                    font-size: 0.9rem;
                }
                
                .search-input {
                    flex: 1;
                    min-width: 300px;
                }
                
                .records-table {
                    background: #111;
                    border: 1px solid #333;
                    border-radius: 8px;
                    overflow: hidden;
                }
                
                .table-header {
                    background: #1a1a1a;
                    padding: 1rem;
                    border-bottom: 1px solid #333;
                    display: grid;
                    grid-template-columns: 1fr 120px 120px 100px 60px;
                    gap: 1rem;
                    align-items: center;
                    font-weight: 600;
                    font-size: 0.9rem;
                    color: #888;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                }
                
                .record-row {
                    border-bottom: 1px solid #222;
                    cursor: pointer;
                    transition: background-color 0.2s;
                }
                
                .record-row:hover {
                    background: #1a1a1a;
                }
                
                .record-summary {
                    padding: 1rem;
                    display: grid;
                    grid-template-columns: 1fr 120px 120px 100px 60px;
                    gap: 1rem;
                    align-items: center;
                }
                
                .record-id {
                    font-family: 'Monaco', 'Menlo', monospace;
                    font-size: 0.9rem;
                }
                
                .status-badge {
                    padding: 0.25rem 0.75rem;
                    border-radius: 12px;
                    font-size: 0.8rem;
                    font-weight: 500;
                    text-align: center;
                }
                
                .status-differences {
                    background: rgba(234, 179, 8, 0.2);
                    color: #eab308;
                    border: 1px solid rgba(234, 179, 8, 0.3);
                }
                
                .status-missing-target {
                    background: rgba(239, 68, 68, 0.2);
                    color: #ef4444;
                    border: 1px solid rgba(239, 68, 68, 0.3);
                }
                
                .status-missing-source {
                    background: rgba(59, 130, 246, 0.2);
                    color: #3b82f6;
                    border: 1px solid rgba(59, 130, 246, 0.3);
                }
                
                .progress-bar {
                    background: #333;
                    border-radius: 4px;
                    height: 8px;
                    overflow: hidden;
                }
                
                .progress-fill {
                    height: 100%;
                    background: #eab308;
                    transition: width 0.3s;
                }
                
                .expand-icon {
                    width: 16px;
                    height: 16px;
                    transition: transform 0.2s;
                    color: #888;
                }
                
                .record-row.expanded .expand-icon {
                    transform: rotate(90deg);
                }
                
                .record-details {
                    display: none;
                    padding: 1.5rem;
                    background: #0a0a0a;
                    border-top: 1px solid #222;
                }
                
                .record-row.expanded .record-details {
                    display: block;
                }
                
                .details-header {
                    margin-bottom: 1rem;
                    padding-bottom: 0.5rem;
                    border-bottom: 1px solid #333;
                }
                
                .details-title {
                    font-size: 1.1rem;
                    font-weight: 600;
                    color: #ffffff;
                }
                
                .field-differences {
                    display: grid;
                    gap: 1rem;
                }
                
                .field-difference {
                    background: #111;
                    border: 1px solid #333;
                    border-radius: 6px;
                    padding: 1rem;
                }
                
                .field-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 0.75rem;
                }
                
                .field-name {
                    font-family: 'Monaco', 'Menlo', monospace;
                    font-weight: 600;
                    color: #ffffff;
                }
                
                .difference-type {
                    padding: 0.2rem 0.5rem;
                    border-radius: 4px;
                    font-size: 0.75rem;
                    font-weight: 500;
                    background: rgba(234, 179, 8, 0.2);
                    color: #eab308;
                    border: 1px solid rgba(234, 179, 8, 0.3);
                }
                
                .value-comparison {
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 1rem;
                }
                
                .value-section {
                    background: #0a0a0a;
                    border: 1px solid #333;
                    border-radius: 4px;
                    padding: 0.75rem;
                }
                
                .value-label {
                    font-size: 0.8rem;
                    color: #888;
                    margin-bottom: 0.25rem;
                    text-transform: uppercase;
                    letter-spacing: 0.5px;
                }
                
                .value-content {
                    font-family: 'Monaco', 'Menlo', monospace;
                    font-size: 0.9rem;
                    color: #e5e5e5;
                    word-break: break-all;
                }
                
                .difference-description {
                    margin-top: 0.75rem;
                    padding-top: 0.75rem;
                    border-top: 1px solid #333;
                    font-size: 0.9rem;
                    color: #888;
                }
                
                .no-records {
                    text-align: center;
                    padding: 3rem;
                    color: #888;
                }
                
                @media (max-width: 768px) {
                    .dashboard {
                        padding: 1rem;
                    }
                    
                    .summary-grid {
                        grid-template-columns: 1fr;
                    }
                    
                    .controls {
                        flex-direction: column;
                    }
                    
                    .search-input {
                        min-width: auto;
                    }
                    
                    .table-header,
                    .record-summary {
                        grid-template-columns: 1fr 80px 40px;
                        gap: 0.5rem;
                    }
                    
                    .table-header .hide-mobile,
                    .record-summary .hide-mobile {
                        display: none;
                    }
                    
                    .value-comparison {
                        grid-template-columns: 1fr;
                    }
                }
            </style>
            """;
    }
    
    /**
     * Generates the header section
     */
    private static String generateHeader(ETLComparisonResult result) {
        StringBuilder header = new StringBuilder();
        header.append("        <div class=\"header\">\n");
        header.append("            <h1>ETL Data Validation Dashboard</h1>\n");
        header.append("            <div class=\"header-meta\">\n");
        header.append("                <span>").append(escapeHtml(result.getSourceTableName())).append(" → ").append(escapeHtml(result.getTargetTableName())).append("</span>\n");
        if (result.getComparisonStartTime() != null) {
            header.append("                <span> • Processed: ").append(result.getComparisonStartTime().format(DATE_FORMATTER)).append("</span>\n");
        }
        if (result.getComparisonEndTime() != null) {
            header.append("                <span> • Duration: ").append(formatDuration(result.getComparisonDuration().toMillis())).append("</span>\n");
        }
        header.append("            </div>\n");
        header.append("        </div>\n");
        return header.toString();
    }
    
    /**
     * Generates summary cards
     */
    private static String generateSummaryCards(ComparisonSummary summary) {
        if (summary == null) {
            return "";
        }
        
        StringBuilder cards = new StringBuilder();
        cards.append("        <div class=\"summary-grid\">\n");
        
        // Total Records
        cards.append("            <div class=\"summary-card\">\n");
        cards.append("                <h3>Total Records</h3>\n");
        cards.append("                <div class=\"summary-value\">").append(String.format("%,d", summary.getTotalSourceRecords())).append("</div>\n");
        cards.append("            </div>\n");
        
        // Matching Records
        cards.append("            <div class=\"summary-card\">\n");
        cards.append("                <h3>Perfect Matches</h3>\n");
        cards.append("                <div class=\"summary-value text-green\">").append(String.format("%,d", summary.getMatchingRecords())).append("</div>\n");
        cards.append("                <div class=\"summary-percentage text-green\">").append(String.format("%.1f%%", summary.getMatchPercentage())).append("</div>\n");
        cards.append("            </div>\n");
        
        // Records with Differences
        cards.append("            <div class=\"summary-card\">\n");
        cards.append("                <h3>Records with Issues</h3>\n");
        cards.append("                <div class=\"summary-value text-yellow\">").append(String.format("%,d", summary.getRecordsWithDifferences())).append("</div>\n");
        cards.append("            </div>\n");
        
        // Field Accuracy
        cards.append("            <div class=\"summary-card\">\n");
        cards.append("                <h3>Field Accuracy</h3>\n");
        cards.append("                <div class=\"summary-value text-blue\">").append(String.format("%.1f%%", summary.getFieldAccuracyPercentage())).append("</div>\n");
        cards.append("                <div class=\"summary-percentage\">").append(String.format("%,d / %,d fields", summary.getTotalFieldsCompared() - summary.getTotalFieldDifferences(), summary.getTotalFieldsCompared())).append("</div>\n");
        cards.append("            </div>\n");
        
        cards.append("        </div>\n");
        return cards.toString();
    }
    
    /**
     * Generates filters and search controls
     */
    private static String generateFiltersAndSearch() {
        return """
                <div class="controls">
                    <input type="text" class="search-input" id="searchInput" placeholder="Search by Record ID or Field Name...">
                    <select class="filter-select" id="statusFilter">
                        <option value="all">All Records</option>
                        <option value="HAS_DIFFERENCES">Has Differences</option>
                        <option value="MISSING_IN_TARGET">Missing in Target</option>
                        <option value="MISSING_IN_SOURCE">Missing in Source</option>
                    </select>
                </div>
                """;
    }
    
    /**
     * Generates the discrepant records table
     */
    private static String generateDiscrepantRecordsTable(List<DiscrepantRecord> records) {
        StringBuilder table = new StringBuilder();
        table.append("        <div class=\"records-table\">\n");
        table.append("            <div class=\"table-header\">\n");
        table.append("                <div>Record ID</div>\n");
        table.append("                <div>Status</div>\n");
        table.append("                <div class=\"hide-mobile\">Field Issues</div>\n");
        table.append("                <div class=\"hide-mobile\">Progress</div>\n");
        table.append("                <div></div>\n");
        table.append("            </div>\n");
        
        if (records == null || records.isEmpty()) {
            table.append("            <div class=\"no-records\">\n");
            table.append("                <p>No discrepant records found. All data matches perfectly!</p>\n");
            table.append("            </div>\n");
        } else {
            for (DiscrepantRecord record : records) {
                table.append(generateRecordRow(record));
            }
        }
        
        table.append("        </div>\n");
        return table.toString();
    }
    
    /**
     * Generates a single record row
     */
    private static String generateRecordRow(DiscrepantRecord record) {
        StringBuilder row = new StringBuilder();
        row.append("            <div class=\"record-row\" data-record-id=\"").append(escapeHtml(record.getRecordId())).append("\" data-status=\"").append(record.getStatus().name()).append("\">\n");
        row.append("                <div class=\"record-summary\">\n");
        row.append("                    <div class=\"record-id\">").append(escapeHtml(record.getRecordId())).append("</div>\n");
        row.append("                    <div><span class=\"status-badge status-").append(record.getStatus().name().toLowerCase().replace("_", "-")).append("\">").append(escapeHtml(record.getStatus().getDisplayName())).append("</span></div>\n");
        row.append("                    <div class=\"hide-mobile\">").append(record.getFieldsWithDifferences()).append(" / ").append(record.getTotalFields()).append("</div>\n");
        row.append("                    <div class=\"hide-mobile\">\n");
        row.append("                        <div class=\"progress-bar\">\n");
        row.append("                            <div class=\"progress-fill\" style=\"width: ").append(String.format("%.1f", record.getDifferencePercentage())).append("%\"></div>\n");
        row.append("                        </div>\n");
        row.append("                    </div>\n");
        row.append("                    <div class=\"expand-icon\">▶</div>\n");
        row.append("                </div>\n");
        
        // Record details (initially hidden)
        row.append("                <div class=\"record-details\">\n");
        row.append("                    <div class=\"details-header\">\n");
        row.append("                        <div class=\"details-title\">Field-by-Field Comparison</div>\n");
        row.append("                    </div>\n");
        row.append("                    <div class=\"field-differences\">\n");
        
        if (record.getFieldDifferences() != null) {
            for (FieldDifference fieldDiff : record.getFieldDifferences()) {
                row.append(generateFieldDifference(fieldDiff));
            }
        }
        
        row.append("                    </div>\n");
        row.append("                </div>\n");
        row.append("            </div>\n");
        
        return row.toString();
    }
    
    /**
     * Generates a field difference section
     */
    private static String generateFieldDifference(FieldDifference fieldDiff) {
        StringBuilder field = new StringBuilder();
        field.append("                        <div class=\"field-difference\">\n");
        field.append("                            <div class=\"field-header\">\n");
        field.append("                                <span class=\"field-name\">").append(escapeHtml(fieldDiff.getFieldName())).append("</span>\n");
        field.append("                                <span class=\"difference-type\">").append(escapeHtml(fieldDiff.getDifferenceType().getDisplayName())).append("</span>\n");
        field.append("                            </div>\n");
        field.append("                            <div class=\"value-comparison\">\n");
        field.append("                                <div class=\"value-section\">\n");
        field.append("                                    <div class=\"value-label\">Source Value</div>\n");
        field.append("                                    <div class=\"value-content\">").append(escapeHtml(fieldDiff.getSourceValue() != null ? fieldDiff.getSourceValue() : "NULL")).append("</div>\n");
        field.append("                                </div>\n");
        field.append("                                <div class=\"value-section\">\n");
        field.append("                                    <div class=\"value-label\">Target Value</div>\n");
        field.append("                                    <div class=\"value-content\">").append(escapeHtml(fieldDiff.getTargetValue() != null ? fieldDiff.getTargetValue() : "NULL")).append("</div>\n");
        field.append("                                </div>\n");
        field.append("                            </div>\n");
        if (fieldDiff.getDescription() != null && !fieldDiff.getDescription().isEmpty()) {
            field.append("                            <div class=\"difference-description\">").append(escapeHtml(fieldDiff.getDescription())).append("</div>\n");
        }
        field.append("                        </div>\n");
        return field.toString();
    }
    
    /**
     * Generates JavaScript for interactivity
     */
    private static String generateJavaScript(List<DiscrepantRecord> records) {
        return """
            <script>
                document.addEventListener('DOMContentLoaded', function() {
                    // Toggle record details
                    document.querySelectorAll('.record-row').forEach(row => {
                        row.addEventListener('click', function() {
                            this.classList.toggle('expanded');
                        });
                    });
                    
                    // Search functionality
                    const searchInput = document.getElementById('searchInput');
                    const statusFilter = document.getElementById('statusFilter');
                    
                    function filterRecords() {
                        const searchTerm = searchInput.value.toLowerCase();
                        const statusValue = statusFilter.value;
                        
                        document.querySelectorAll('.record-row').forEach(row => {
                            const recordId = row.dataset.recordId.toLowerCase();
                            const status = row.dataset.status;
                            const fieldNames = Array.from(row.querySelectorAll('.field-name'))
                                .map(el => el.textContent.toLowerCase()).join(' ');
                            
                            const matchesSearch = recordId.includes(searchTerm) || fieldNames.includes(searchTerm);
                            const matchesStatus = statusValue === 'all' || status === statusValue;
                            
                            row.style.display = matchesSearch && matchesStatus ? 'block' : 'none';
                        });
                    }
                    
                    searchInput.addEventListener('input', filterRecords);
                    statusFilter.addEventListener('change', filterRecords);
                });
            </script>
            """;
    }
    
    /**
     * Escapes HTML to prevent XSS attacks
     */
    private static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }
    
    /**
     * Formats duration in milliseconds to human readable format
     */
    private static String formatDuration(long millis) {
        if (millis < 1000) return millis + "ms";
        if (millis < 60000) return String.format("%.1fs", millis / 1000.0);
        return String.format("%.1fm", millis / 60000.0);
    }
}
