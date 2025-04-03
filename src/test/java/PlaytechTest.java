

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * This test performs the following:
 * 1. Navigates to the Playtech People website.
 * 2. Clicks the "Locations" tab.
 * 3. Retrieves the list of locations from the header-locations element.
 * 4. Filters out any non-location text (e.g., “View all locations”) and prints the results.
 */
public class PlaytechTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setup() {
        // Set up ChromeDriver using WebDriverManager.
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        // Increase wait time to handle dynamic content.
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Navigate to the Playtech People website.
        driver.get("https://www.playtechpeople.com/");
    }

    @Test
    public void verifyLocationsTab() {
        try {
            // Click on the "Locations" tab.
            WebElement locationsTab = wait.until(
                    ExpectedConditions.elementToBeClickable(By.linkText("Locations"))
            );
            locationsTab.click();

            // Optional: Pause briefly to allow for animation or dynamic content.
            Thread.sleep(2000); // For debugging only. Replace with an appropriate wait if possible.

            // Debug: Print a snippet of the page source if needed.
            // System.out.println(driver.getPageSource().substring(0, 1000));

            // Locate the container that holds the location data.
            // Based on your DOM dump, use the class "header-locations".
            WebElement locationsContainer = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.className("header-locations"))
            );

            // Retrieve the inner text of the container.
            String locationsRawText = locationsContainer.getText();
            System.out.println("Raw locations text:");
            System.out.println(locationsRawText);

            // Split the text into lines. Each valid line should represent a location.
            String[] lines = locationsRawText.split("\\r?\\n");

            // Filter out any lines that are empty or not an actual location (e.g., "View all locations").
            List<String> locationsList = new ArrayList<>();
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty() && !line.equalsIgnoreCase("View all locations")) {
                    locationsList.add(line);
                }
            }

            // Print the total number of locations.
            System.out.println("Total number of locations: " + locationsList.size());
            // Print each location.
            for (String location : locationsList) {
                System.out.println(location);
            }
        } catch (Exception e) {
            // Print any exceptions encountered and prompt to verify locators and page conditions.
            e.printStackTrace();
            System.out.println("Exception occurred. Please verify the locator and page load conditions.");
        }
    }

    @Test
    public void verifyCasinoProductSuite() {
        try {
            // Click on the "Life at Playtech" tab.
            WebElement lifeAtPlaytechTab = wait.until(
                    ExpectedConditions.elementToBeClickable(By.linkText("Life at Playtech"))
            );
            lifeAtPlaytechTab.click();
            Thread.sleep(2000); // Allow time for the Life at Playtech section to load.

            // Click on the "Who we are" link within the Life at Playtech section.
            WebElement whoWeAreLink = wait.until(
                    ExpectedConditions.elementToBeClickable(By.linkText("Who we are"))
            );
            whoWeAreLink.click();
            Thread.sleep(2000); // Give some time for the Who we are section to show.

            // Scroll down gradually using PAGE_DOWN keystrokes (simulate three scroll actions).
            Actions actions = new Actions(driver);
            for (int i = 0; i < 3; i++) {
                actions.sendKeys(Keys.PAGE_DOWN).perform();
                Thread.sleep(1000); // Wait to allow lazy-loaded content to render.
            }

            // Now locate the Casino product suite description.
            // This uses the relative XPath: find a product-card where <h4> equals "Casino" and then get the adjacent <p>.
            WebElement casinoDescription = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[contains(@class, 'product-card')]//h4[text()='Casino']/following-sibling::p")
                    )
            );

            System.out.println("Casino Product Suite Description:");
            System.out.println(casinoDescription.getText());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception occurred in verifyCasinoProductSuite. Please verify the locator and page load conditions.");
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
