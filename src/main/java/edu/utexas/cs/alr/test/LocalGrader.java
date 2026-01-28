package edu.utexas.cs.alr.test;

import edu.utexas.cs.alr.ast.Expr;
import edu.utexas.cs.alr.util.ExprUtils;
import edu.utexas.cs.alr.util.SatUtil;

import java.io.*;
import java.nio.file.*;
import java.util.*;


public class LocalGrader {
    private static final String TEST_CASES_DIR = "resources/test-cases";
    private static final String SAT_DIR = TEST_CASES_DIR + "/sat";
    private static final String UNSAT_DIR = TEST_CASES_DIR + "/unsat";
    
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_BOLD = "\u001B[1m";
    
    private int passed = 0;
    private int failed = 0;
    private List<String> failedTests = new ArrayList<>();
    private long totalTime = 0;
    
    public static void main(String[] args) {
        LocalGrader grader = new LocalGrader();
        
        if (args.length > 0) {
            // Run specific test file
            String testFile = args[0];
            String expected = args.length > 1 ? args[1] : guessExpectedResult(testFile);
            grader.runSingleTest(testFile, expected);
        } else {
            // Run all tests
            grader.runAllTests();
        }
    }
    
    private static String guessExpectedResult(String testFile) {
        if (testFile.contains("sat")) {
            return "SAT";
        } else if (testFile.contains("unsat")) {
            return "UNSAT";
        }
        return "UNKNOWN";
    }
    
    public void runAllTests() {
        printHeader();
        
        // Collect test files
        List<TestCase> satTests = collectTestFiles(SAT_DIR, "SAT");
        List<TestCase> unsatTests = collectTestFiles(UNSAT_DIR, "UNSAT");
        List<TestCase> allTests = new ArrayList<>();
        allTests.addAll(satTests);
        allTests.addAll(unsatTests);
        
        if (allTests.isEmpty()) {
            printColored("No test cases found!", ANSI_RED);
            return;
        }
        
        printColored(String.format("Found %d SAT test cases and %d UNSAT test cases", 
            satTests.size(), unsatTests.size()), ANSI_BLUE);
        System.out.println();
        
        // Run tests
        int total = allTests.size();
        for (int i = 0; i < allTests.size(); i++) {
            TestCase test = allTests.get(i);
            String testName = test.file.toString().replace(TEST_CASES_DIR + "/", "");
            System.out.printf("[%3d/%3d] Testing %-50s ", i + 1, total, testName);
            
            long startTime = System.currentTimeMillis();
            boolean success = runTest(test.file, test.expected);
            long elapsed = System.currentTimeMillis() - startTime;
            totalTime += elapsed;
            
            if (success) {
                printColored(String.format("✓ PASSED (%.2fs)", elapsed / 1000.0), ANSI_GREEN);
                passed++;
            } else {
                printColored(String.format("✗ FAILED (%.2fs)", elapsed / 1000.0), ANSI_RED);
                failed++;
                failedTests.add(String.format("%s (expected %s)", testName, test.expected));
            }
        }
        
        printSummary();
    }
    
    public void runSingleTest(String testFile, String expected) {
        Path path = Paths.get(testFile);
        if (!Files.exists(path)) {
            printColored("Error: Test file not found: " + testFile, ANSI_RED);
            return;
        }
        
        printColored("Running test: " + path.getFileName(), ANSI_BLUE);
        printColored("Expected result: " + expected, ANSI_BLUE);
        System.out.println();
        
        long startTime = System.currentTimeMillis();
        boolean success = runTest(path, expected);
        long elapsed = System.currentTimeMillis() - startTime;
        
        if (success) {
            printColored(String.format("✓ PASSED (%.2fs)", elapsed / 1000.0), ANSI_GREEN);
        } else {
            printColored(String.format("✗ FAILED (%.2fs)", elapsed / 1000.0), ANSI_RED);
        }
    }
    
    private boolean runTest(Path testFile, String expected) {
        try {
            // Read the test file
            String input = new String(Files.readAllBytes(testFile));
            
            // Parse and check SAT
            Expr expr = ExprUtils.parseFrom(new ByteArrayInputStream(input.getBytes()));
            Expr cnfExpr = ExprUtils.toTseitin(expr);
            boolean result = SatUtil.checkSAT(cnfExpr);
            
            // Check result
            String resultStr = result ? "SAT" : "UNSAT";
            return resultStr.equals(expected);
            
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private List<TestCase> collectTestFiles(String dir, String expected) {
        List<TestCase> tests = new ArrayList<>();
        try {
            Path dirPath = Paths.get(dir);
            if (Files.exists(dirPath)) {
                Files.walk(dirPath)
                    .filter(Files::isRegularFile)
                    .forEach(path -> tests.add(new TestCase(path, expected)));
            }
        } catch (IOException e) {
            System.err.println("Error collecting test files from " + dir + ": " + e.getMessage());
        }
        return tests;
    }
    
    private void printHeader() {
        printColored("=".repeat(70), ANSI_BLUE);
        printColored("CDCL SAT Solver - Local Grader", ANSI_BOLD + ANSI_BLUE);
        printColored("=".repeat(70), ANSI_BLUE);
        System.out.println();
    }
    
    private void printSummary() {
        System.out.println();
        printColored("=".repeat(70), ANSI_BLUE);
        printColored("Summary", ANSI_BOLD + ANSI_BLUE);
        printColored("=".repeat(70), ANSI_BLUE);
        System.out.println("Total tests: " + (passed + failed));
        printColored("Passed: " + passed, ANSI_GREEN);
        printColored("Failed: " + failed, ANSI_RED);
        System.out.printf("Total time: %.2fs\n", totalTime / 1000.0);
        if (passed + failed > 0) {
            System.out.printf("Average time per test: %.2fs\n", totalTime / 1000.0 / (passed + failed));
        }
        
        if (!failedTests.isEmpty()) {
            System.out.println();
            printColored("Failed Tests:", ANSI_RED);
            for (String test : failedTests) {
                System.out.println("  - " + test);
            }
        }
        
        System.out.println();
        if (failed == 0) {
            printColored("🎉 All tests passed!", ANSI_GREEN + ANSI_BOLD);
        } else {
            printColored("❌ " + failed + " test(s) failed", ANSI_RED + ANSI_BOLD);
        }
    }
    
    private void printColored(String text, String color) {
        System.out.println(color + text + ANSI_RESET);
    }
    
    private static class TestCase {
        Path file;
        String expected;
        
        TestCase(Path file, String expected) {
            this.file = file;
            this.expected = expected;
        }
    }
}
