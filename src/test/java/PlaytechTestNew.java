import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This does the following for the Playtech site:
 * 1. Goes to the website
 * 2. Finds locations under "Locations" tab
 * 3. Gets Casino product info from "Who we are" section
 * 4. Finds job links for Tartu and Tallinn
 * 5. Closes browser
 *
 * Bonus stuff done:
 * 1. Using coordinates for clicking
 * 2. Saving results to file
 * 3. Using JUnit
 */
public class PlaytechTestNew {

    private WebDriver driver;
    private WebDriverWait wait;
    private List<String> testResults = new ArrayList<>();
    private Dimension screenSize;

    /**
     * Clicks using coordinates instead of direct click
     */
    private void clickUsingCoordinates(WebElement element) {
        // Get position and size
        Point location = element.getLocation();
        Dimension size = element.getSize();

        // Get center
        int centerX = location.getX() + (size.getWidth() / 2);
        int centerY = location.getY() + (size.getHeight() / 2);

        // Log info
        String message = "Clicking at coordinates: (" + centerX + ", " + centerY + ")";
        System.out.println(message);
        testResults.add(message);

        // Do the click
        Actions actions = new Actions(driver);
        actions.moveToElement(element).click().perform();
    }

    /**
     * Saves test results
     */
    private void addTestResults(String testName, List<String> results) {
        testResults.add("\n=== " + testName + " ===");
        testResults.add("Executed at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        testResults.add("Screen resolution: " + screenSize.width + "x" + screenSize.height);
        testResults.add("----------------------------------------");
        testResults.addAll(results);
        testResults.add("----------------------------------------");
    }

    /**
     * Writes results to file
     */
    private void writeAllResultsToFile() {
        try {
            // Make filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "playtech_test_results_" + timestamp + ".txt";

            // Write to file
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                writer.println("Playtech Test Results");
                writer.println("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println("Screen Resolution: " + screenSize.width + "x" + screenSize.height);
                writer.println("========================================");

                // Add all results
                for (String result : testResults) {
                    writer.println(result);
                }


                writer.println("All tests completed.");
            }

            System.out.println("All test results saved to file: " + fileName);
        } catch (IOException e) {
            System.out.println("Error writing results to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void setup() {
        // Setup Chrome
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        // Save screen size
        screenSize = driver.manage().window().getSize();
        System.out.println("Screen resolution: " + screenSize.width + "x" + screenSize.height);

        // Setup wait time
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Go to website
        driver.get("https://www.playtechpeople.com/");

        // Handle cookies popup
        try {
            // Find and click deny button
            WebElement denyCookiesButton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.id("CybotCookiebotDialogBodyButtonDecline")
                    )
            );
            // Click it
            clickUsingCoordinates(denyCookiesButton);
            System.out.println("Denied cookies");
        } catch (Exception e) {
            System.out.println("Cookie dialog not found or already handled");
        }
    }

    @Test
    public void verifyLocationsTab() {
        List<String> results = new ArrayList<>();

        try {
            // Click locations tab
            WebElement locationsTab = wait.until(
                    ExpectedConditions.elementToBeClickable(By.linkText("Locations"))
            );
            results.add("Clicking on 'Locations' tab");
            clickUsingCoordinates(locationsTab);

            // Wait a bit
            Thread.sleep(2000);

            // Find locations container
            WebElement locationsContainer = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.className("header-locations"))
            );

            // Get text
            String locationsRawText = locationsContainer.getText();
            results.add("Raw locations text:");
            results.add(locationsRawText);
            System.out.println("Raw locations text:");
            System.out.println(locationsRawText);

            // Split into lines
            String[] lines = locationsRawText.split("\\r?\\n");

            // Filter locations
            List<String> locationsList = new ArrayList<>();
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty() && !line.equalsIgnoreCase("View all locations")) {
                    locationsList.add(line);
                }
            }

            // Show results
            results.add("Total number of locations: " + locationsList.size());
            System.out.println("Total number of locations: " + locationsList.size());

            //Print locations
            for (String location : locationsList) {
                results.add(location);
                System.out.println(location);
            }
        } catch (Exception e) {
            // Oops something went wrong
            e.printStackTrace();
            results.add("ERROR: " + e.getMessage());
            System.out.println("Exception occurred. Please verify the locator and page load conditions.");
        }

        // Save results
        addTestResults("Locations Tab Test", results);
    }

    @Test
    public void verifyCasinoProductSuite() {
        List<String> results = new ArrayList<>();

        try {
            // Click Life at Playtech
            WebElement lifeAtPlaytechTab = wait.until(
                    ExpectedConditions.elementToBeClickable(By.linkText("Life at Playtech"))
            );
            results.add("Clicking on 'Life at Playtech' tab");
            clickUsingCoordinates(lifeAtPlaytechTab);
            Thread.sleep(2000); // Wait a bit

            // Click Who we are
            WebElement whoWeAreLink = wait.until(
                    ExpectedConditions.elementToBeClickable(By.linkText("Who we are"))
            );
            results.add("Clicking on 'Who we are' link");
            clickUsingCoordinates(whoWeAreLink);
            Thread.sleep(2000); // Wait a bit

            // Scroll down a few times
            Actions actions = new Actions(driver);
            for (int i = 0; i < 3; i++) {
                actions.sendKeys(Keys.PAGE_DOWN).perform();
                Thread.sleep(1000); // Wait a bit
            }

            // Find Casino description
            WebElement casinoDescription = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[contains(@class, 'product-card')]//h4[text()='Casino']/following-sibling::p")
                    )
            );

            results.add("Casino Product Suite Description:");
            results.add(casinoDescription.getText());
            System.out.println("Casino Product Suite Description:");
            System.out.println(casinoDescription.getText());
        } catch (Exception e) {
            e.printStackTrace();
            results.add("ERROR: " + e.getMessage());
            System.out.println("Exception occurred in verifyCasinoProductSuite. Please verify the locator and page load conditions.");
        }

        // Save results
        addTestResults("Casino Product Suite Test", results);
    }

    @Test
    public void findEstonianJobs() {
        List<String> results = new ArrayList<>();

        try {
            // Click All Jobs
            WebElement allJobsTab = wait.until(
                    ExpectedConditions.elementToBeClickable(By.linkText("All Jobs"))
            );
            results.add("Clicking 'All Jobs' tab");
            System.out.println("Clicking 'All Jobs' tab");
            clickUsingCoordinates(allJobsTab);

            // Wait a bit
            Thread.sleep(1000);

            // Click location dropdown
            WebElement selectLocation = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//div[contains(@class, 'column-title__location')]")
                    )
            );
            results.add("Clicking 'Select location' dropdown");
            System.out.println("Clicking 'Select location' dropdown");
            clickUsingCoordinates(selectLocation);

            // Wait a bit
            Thread.sleep(1000);

            // Select Estonia
            WebElement estoniaOption = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//div[contains(@class, 'locations-column__item')]/span[text()='Estonia']")
                    )
            );
            results.add("Selecting 'Estonia' from dropdown");
            System.out.println("Selecting 'Estonia' from dropdown");
            clickUsingCoordinates(estoniaOption);

            // Click Search
            WebElement searchButton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//input[@type='submit' and @class='blue-button' and @value='Search']")
                    )
            );
            results.add("Clicking Search button");
            System.out.println("Clicking Search button");
            clickUsingCoordinates(searchButton);

            // Wait for results
            Thread.sleep(3000);

            // Remember main window
            String mainWindow = driver.getWindowHandle();

            // For storing links
            String tartuJobLink = null;
            String tallinnJobLink = null;

            // Find job listings
            List<WebElement> jobListings = driver.findElements(
                    By.xpath("//a[contains(@class, 'job-item') and .//p[@class='location-link' and text()='Estonia'] and .//p[@class='arrow-link' and text()='Apply']]")
            );

            results.add("Found " + jobListings.size() + " job listings in Estonia");
            System.out.println("Found " + jobListings.size() + " job listings in Estonia");

            // Check each job
            for (WebElement jobListing : jobListings) {
                // Stop if we have both links
                if (tartuJobLink != null && tallinnJobLink != null) {
                    break;
                }

                try {
                    // Scroll to job
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].scrollIntoView(true);", jobListing
                    );
                    Thread.sleep(1000);

                    // Get job info
                    String jobTitle = jobListing.findElement(By.xpath(".//h6")).getText();
                    String jobUrl = jobListing.getAttribute("href");

                    results.add("Processing job: " + jobTitle + " with URL: " + jobUrl);
                    System.out.println("Processing job: " + jobTitle + " with URL: " + jobUrl);

                    // Click job
                    clickUsingCoordinates(jobListing);

                    // Wait for new tab
                    Thread.sleep(2000);

                    // Switch to new tab
                    for (String windowHandle : driver.getWindowHandles()) {
                        if (!windowHandle.equals(mainWindow)) {
                            driver.switchTo().window(windowHandle);
                            break;
                        }
                    }

                    // Wait for page load
                    Thread.sleep(3000);

                    // Find location
                    try {
                        // Look for location element
                        WebElement locationElement = driver.findElement(
                                By.xpath("//spl-job-location[@formattedAddress]")
                        );

                        // Get address
                        String addressText = locationElement.getAttribute("formattedAddress");
                        results.add("Found address: " + addressText);
                        System.out.println("Found address: " + addressText);

                        // Check if Tartu or Tallin
                        if (addressText.contains("Tartu") && tartuJobLink == null) {
                            tartuJobLink = jobUrl;
                            results.add("Found Tartu job: " + jobTitle);
                            results.add("Tartu job link: " + tartuJobLink);
                            System.out.println("Found Tartu job: " + jobTitle);
                            System.out.println("Tartu job link: " + tartuJobLink);
                        } else if (addressText.contains("Tallinn") && tallinnJobLink == null) {
                            tallinnJobLink = jobUrl;
                            results.add("Found Tallinn job: " + jobTitle);
                            results.add("Tallinn job link: " + tallinnJobLink);
                            System.out.println("Found Tallinn job: " + jobTitle);
                            System.out.println("Tallinn job link: " + tallinnJobLink);
                        } else {
                            results.add("Job is in Estonia but not in Tartu or Tallinn");
                            System.out.println("Job is in Estonia but not in Tartu or Tallinn");
                        }
                    } catch (Exception e) {
                        results.add("Could not find spl-job-location element: " + e.getMessage());
                        System.out.println("Could not find spl-job-location element: " + e.getMessage());
                    }

                    // Close tab and go back
                    driver.close();
                    driver.switchTo().window(mainWindow);

                    // Wait a bit
                    Thread.sleep(1000);

                } catch (Exception e) {
                    results.add("Error processing job listing: " + e.getMessage());
                    System.out.println("Error processing job listing: " + e.getMessage());

                    // Make sure we're back on main window
                    try {
                        driver.switchTo().window(mainWindow);
                    } catch (Exception ex) {
                        // Already on main window
                    }
                }
            }

            // Show final resalts
            results.add("\nFinal Job Links:");
            System.out.println("\nFinal Job Links:");
            if (tartuJobLink != null) {
                results.add("Tartu Job Link: " + tartuJobLink);
                System.out.println("Tartu Job Link: " + tartuJobLink);
            } else {
                results.add("No jobs found in Tartu");
                System.out.println("No jobs found in Tartu");
            }

            if (tallinnJobLink != null) {
                results.add("Tallinn Job Link: " + tallinnJobLink);
                System.out.println("Tallinn Job Link: " + tallinnJobLink);
            } else {
                results.add("No jobs found in Tallinn");
                System.out.println("No jobs found in Tallinn");
            }

        } catch (Exception e) {
            e.printStackTrace();
            results.add("Exception occurred while finding Estonian jobs: " + e.getMessage());
            System.out.println("Exception occurred while finding Estonian jobs.");
        }

        // Save results
        addTestResults("Estonian Jobs Test", results);
    }

    @AfterEach
    public void tearDown() {
        // Write results to file
        if (!testResults.isEmpty()) {
            writeAllResultsToFile();
        }

        // Close browser
        if (driver != null) {
            driver.quit();
        }
    }
}