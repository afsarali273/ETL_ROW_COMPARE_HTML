// Example usage class
public class ETLHtmlGeneratorExample {
    
    public static void main(String[] args) {
        // Create sample data
        ETLComparisonResult result = createSampleComparisonResult();
        
        // Generate HTML
        String html = ETLHtmlGenerator.generateETLComparisonHtml(result);
        
        // Save to file or serve via web endpoint
        System.out.println("Generated HTML length: " + html.length() + " characters");
        
        // You can write to file like this:
        // Files.write(Paths.get("etl-report.html"), html.getBytes());
    }
    
    private static ETLComparisonResult createSampleComparisonResult() {
        ETLComparisonResult result = new ETLComparisonResult("source_customers", "target_customers");
        
        // Create summary
        ComparisonSummary summary = new ComparisonSummary();
        summary.setTotalSourceRecords(1000000);
        summary.setTotalTargetRecords(999950);
        summary.setMatchingRecords(999800);
        summary.setRecordsWithDifferences(150);
        summary.setMissingInTarget(50);
        summary.setTotalFieldsCompared(50000000);
        summary.setTotalFieldDifferences(750);
        result.setSummary(summary);
        
        // Create sample discrepant records
        DiscrepantRecord record1 = new DiscrepantRecord("CUST_001", "customers");
        record1.setStatus(RecordStatus.HAS_DIFFERENCES);
        record1.setTotalFields(50);
        
        FieldDifference diff1 = new FieldDifference("email", "john@oldomain.com", "john@newdomain.com", DifferenceType.VALUE_DIFFERENT);
        FieldDifference diff2 = new FieldDifference("phone", "123-456-7890", "1234567890", DifferenceType.FORMAT_DIFFERENT);
        
        record1.addFieldDifference(diff1);
        record1.addFieldDifference(diff2);
        
        result.addDiscrepantRecord(record1);
        
        return result;
    }
}
