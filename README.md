#Playtech Selenium Testing
Simple Selenium WebDriver project for testing different sections of the Playtech People website.
What It Does
This project runs automated tests on the Playtech People website to:

Check locations listed under the Locations tab
Find the Casino product description in the "Who we are" section
Get job links for positions in Tartu and Tallinn

Features

Clicks using screen coordinates instead of normal element clicks
Saves test results to a text file with timestamp
Uses JUnit 5 for running tests

Requirements

Java 11 or higher recommended (Java 8 might work but isn't ideal)
Selenium WebDriver
JUnit 5
WebDriverManager

Running the Tests

Clone this repo
Make sure you have the required libraries
Run PlaytechTestNew class as a JUnit test

When the tests finish running, check your project folder for a text file with the results.
Notes

The tests handle the cookie popup automatically
Each test runs in a separate browser session
