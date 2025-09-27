// Main DTO for discrepant records
public class DiscrepantRecord {
    private String recordId;
    private RecordStatus status;
    private int totalFields;
    private int fieldsWithDifferences;
    private List<FieldDifference> fieldDifferences;
    private LocalDateTime processedAt;
    private String tableName;
    
    // Constructors
    public DiscrepantRecord() {
        this.fieldDifferences = new ArrayList<>();
        this.processedAt = LocalDateTime.now();
    }
    
    public DiscrepantRecord(String recordId, String tableName) {
        this();
        this.recordId = recordId;
        this.tableName = tableName;
    }
    
    // Getters and Setters
    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    
    public int getTotalFields() { return totalFields; }
    public void setTotalFields(int totalFields) { this.totalFields = totalFields; }
    
    public int getFieldsWithDifferences() { return fieldsWithDifferences; }
    public void setFieldsWithDifferences(int fieldsWithDifferences) { 
        this.fieldsWithDifferences = fieldsWithDifferences; 
    }
    
    public List<FieldDifference> getFieldDifferences() { return fieldDifferences; }
    public void setFieldDifferences(List<FieldDifference> fieldDifferences) { 
        this.fieldDifferences = fieldDifferences; 
        this.fieldsWithDifferences = fieldDifferences.size();
    }
    
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    
    // Utility methods
    public void addFieldDifference(FieldDifference fieldDifference) {
        this.fieldDifferences.add(fieldDifference);
        this.fieldsWithDifferences = this.fieldDifferences.size();
    }
    
    public double getDifferencePercentage() {
        if (totalFields == 0) return 0.0;
        return (double) fieldsWithDifferences / totalFields * 100;
    }
}


// DTO for individual field differences
public class FieldDifference {
    private String fieldName;
    private String sourceValue;
    private String targetValue;
    private DifferenceType differenceType;
    private String description;
    private String dataType;
    
    // Constructors
    public FieldDifference() {}
    
    public FieldDifference(String fieldName, String sourceValue, String targetValue, DifferenceType differenceType) {
        this.fieldName = fieldName;
        this.sourceValue = sourceValue;
        this.targetValue = targetValue;
        this.differenceType = differenceType;
        this.description = generateDescription();
    }
    
    // Getters and Setters
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    
    public String getSourceValue() { return sourceValue; }
    public void setSourceValue(String sourceValue) { this.sourceValue = sourceValue; }
    
    public String getTargetValue() { return targetValue; }
    public void setTargetValue(String targetValue) { this.targetValue = targetValue; }
    
    public DifferenceType getDifferenceType() { return differenceType; }
    public void setDifferenceType(DifferenceType differenceType) { 
        this.differenceType = differenceType;
        this.description = generateDescription();
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    
    // Utility method to generate description based on difference type
    private String generateDescription() {
        if (differenceType == null) return "";
        
        switch (differenceType) {
            case VALUE_DIFFERENT:
                return String.format("Value changed from '%s' to '%s'", sourceValue, targetValue);
            case MISSING_IN_TARGET:
                return "Field exists in source but missing in target";
            case MISSING_IN_SOURCE:
                return "Field exists in target but missing in source";
            case FORMAT_DIFFERENT:
                return "Same value but different format";
            case CASE_SENSITIVE:
                return "Same value but different case";
            case PRECISION_DIFFERENT:
                return "Numeric precision difference";
            case WHITESPACE_DIFFERENT:
                return "Whitespace or trimming difference";
            default:
                return "Unknown difference type";
        }
    }
}


// Enum for different types of record status
public enum RecordStatus {
    HAS_DIFFERENCES("Has Differences"),
    MISSING_IN_TARGET("Missing in Target"),
    MISSING_IN_SOURCE("Missing in Source"),
    EXTRA_IN_TARGET("Extra in Target");
    
    private final String displayName;
    
    RecordStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}





// Enum for different types of field differences
public enum DifferenceType {
    VALUE_DIFFERENT("Value Different"),
    MISSING_IN_TARGET("Missing in Target"),
    MISSING_IN_SOURCE("Missing in Source"),
    FORMAT_DIFFERENT("Format Different"),
    CASE_SENSITIVE("Case Sensitive"),
    PRECISION_DIFFERENT("Precision Different"),
    WHITESPACE_DIFFERENT("Whitespace Different"),
    DATA_TYPE_DIFFERENT("Data Type Different"),
    NULL_VS_EMPTY("Null vs Empty"),
    DATE_FORMAT_DIFFERENT("Date Format Different"),
    ENCODING_DIFFERENT("Encoding Different");
    
    private final String displayName;
    
    DifferenceType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}


// Wrapper DTO for the complete comparison result
public class ETLComparisonResult {
    private String comparisonId;
    private String sourceTableName;
    private String targetTableName;
    private LocalDateTime comparisonStartTime;
    private LocalDateTime comparisonEndTime;
    private ComparisonSummary summary;
    private List<DiscrepantRecord> discrepantRecords;
    
    // Constructors
    public ETLComparisonResult() {
        this.discrepantRecords = new ArrayList<>();
        this.comparisonStartTime = LocalDateTime.now();
    }
    
    public ETLComparisonResult(String sourceTableName, String targetTableName) {
        this();
        this.sourceTableName = sourceTableName;
        this.targetTableName = targetTableName;
        this.comparisonId = generateComparisonId();
    }
    
    // Getters and Setters
    public String getComparisonId() { return comparisonId; }
    public void setComparisonId(String comparisonId) { this.comparisonId = comparisonId; }
    
    public String getSourceTableName() { return sourceTableName; }
    public void setSourceTableName(String sourceTableName) { this.sourceTableName = sourceTableName; }
    
    public String getTargetTableName() { return targetTableName; }
    public void setTargetTableName(String targetTableName) { this.targetTableName = targetTableName; }
    
    public LocalDateTime getComparisonStartTime() { return comparisonStartTime; }
    public void setComparisonStartTime(LocalDateTime comparisonStartTime) { 
        this.comparisonStartTime = comparisonStartTime; 
    }
    
    public LocalDateTime getComparisonEndTime() { return comparisonEndTime; }
    public void setComparisonEndTime(LocalDateTime comparisonEndTime) { 
        this.comparisonEndTime = comparisonEndTime; 
    }
    
    public ComparisonSummary getSummary() { return summary; }
    public void setSummary(ComparisonSummary summary) { this.summary = summary; }
    
    public List<DiscrepantRecord> getDiscrepantRecords() { return discrepantRecords; }
    public void setDiscrepantRecords(List<DiscrepantRecord> discrepantRecords) { 
        this.discrepantRecords = discrepantRecords; 
    }
    
    // Utility methods
    public void addDiscrepantRecord(DiscrepantRecord record) {
        this.discrepantRecords.add(record);
    }
    
    private String generateComparisonId() {
        return "CMP_" + System.currentTimeMillis();
    }
    
    public Duration getComparisonDuration() {
        if (comparisonStartTime != null && comparisonEndTime != null) {
            return Duration.between(comparisonStartTime, comparisonEndTime);
        }
        return Duration.ZERO;
    }
}


// Summary statistics DTO
public class ComparisonSummary {
    private long totalSourceRecords;
    private long totalTargetRecords;
    private long matchingRecords;
    private long recordsWithDifferences;
    private long missingInTarget;
    private long missingInSource;
    private long totalFieldsCompared;
    private long totalFieldDifferences;
    
    // Constructors
    public ComparisonSummary() {}
    
    // Getters and Setters
    public long getTotalSourceRecords() { return totalSourceRecords; }
    public void setTotalSourceRecords(long totalSourceRecords) { this.totalSourceRecords = totalSourceRecords; }
    
    public long getTotalTargetRecords() { return totalTargetRecords; }
    public void setTotalTargetRecords(long totalTargetRecords) { this.totalTargetRecords = totalTargetRecords; }
    
    public long getMatchingRecords() { return matchingRecords; }
    public void setMatchingRecords(long matchingRecords) { this.matchingRecords = matchingRecords; }
    
    public long getRecordsWithDifferences() { return recordsWithDifferences; }
    public void setRecordsWithDifferences(long recordsWithDifferences) { 
        this.recordsWithDifferences = recordsWithDifferences; 
    }
    
    public long getMissingInTarget() { return missingInTarget; }
    public void setMissingInTarget(long missingInTarget) { this.missingInTarget = missingInTarget; }
    
    public long getMissingInSource() { return missingInSource; }
    public void setMissingInSource(long missingInSource) { this.missingInSource = missingInSource; }
    
    public long getTotalFieldsCompared() { return totalFieldsCompared; }
    public void setTotalFieldsCompared(long totalFieldsCompared) { this.totalFieldsCompared = totalFieldsCompared; }
    
    public long getTotalFieldDifferences() { return totalFieldDifferences; }
    public void setTotalFieldDifferences(long totalFieldDifferences) { 
        this.totalFieldDifferences = totalFieldDifferences; 
    }
    
    // Utility methods
    public double getMatchPercentage() {
        if (totalSourceRecords == 0) return 0.0;
        return (double) matchingRecords / totalSourceRecords * 100;
    }
    
    public double getFieldAccuracyPercentage() {
        if (totalFieldsCompared == 0) return 0.0;
        return (double) (totalFieldsCompared - totalFieldDifferences) / totalFieldsCompared * 100;
    }
}


