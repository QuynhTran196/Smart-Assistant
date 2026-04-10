*** Settings ***
Documentation    Common setup, teardown, and utility keywords
Library          AppiumLibrary
Library          String
Library          Collections
Resource         ../Config/config.resource


*** Keywords ***
# =============================================================================
# APP LIFECYCLE
# =============================================================================
Start Chat Application
    [Documentation]    Launches the Smart Assistant app
    Open Application    ${APPIUM_SERVER}
    ...    automationName=${ANDROID_AUTOMATION_NAME}
    ...    platformName=${ANDROID_PLATFORM_NAME}
    ...    deviceName=${DEVICE_NAME}
    ...    appPackage=${APP_PACKAGE}
    ...    appActivity=${APP_ACTIVITY}
    Sleep    2s

Close Chat Application
    [Documentation]    Closes the app and captures screenshot on failure
    Run Keyword If Test Failed    Capture Page Screenshot    filename=FAIL_${TEST_NAME}.png
    Close Application

Restart Application
    [Documentation]    Closes and reopens the app
    Close Application
    Start Chat Application

# =============================================================================
# SMART INTERACTION KEYWORDS
# =============================================================================
Smart Click
    [Documentation]    Waits for element to be visible and enabled before clicking
    [Arguments]    ${locator}    ${timeout}=${DEFAULT_TIMEOUT}
    Wait Until Element Is Visible    ${locator}    timeout=${timeout}
    Click Element    ${locator}

Smart Input
    [Documentation]    Clears field, types text, and hides keyboard
    [Arguments]    ${locator}    ${text}    ${timeout}=${DEFAULT_TIMEOUT}
    Wait Until Element Is Visible    ${locator}    timeout=${timeout}
    Click Element    ${locator}
    Clear Text    ${locator}
    Input Text    ${locator}    ${text}
    Hide Keyboard

Smart Long Press
    [Documentation]    Long press on an element
    [Arguments]    ${locator}    ${duration}=1000    ${timeout}=${DEFAULT_TIMEOUT}
    Wait Until Element Is Visible    ${locator}    timeout=${timeout}
    Long Press    ${locator}    duration=${duration}

# =============================================================================
# VERIFICATION KEYWORDS
# =============================================================================
Element Should Be Visible With Timeout
    [Documentation]    Verifies element is visible within timeout
    [Arguments]    ${locator}    ${timeout}=${DEFAULT_TIMEOUT}
    Wait Until Element Is Visible    ${locator}    timeout=${timeout}

Element Should Not Be Visible With Timeout
    [Documentation]    Verifies element is not visible within timeout
    [Arguments]    ${locator}    ${timeout}=${DEFAULT_TIMEOUT}
    Wait Until Page Does Not Contain Element    ${locator}    timeout=${timeout}

Verify Text Is Displayed
    [Documentation]    Verifies specific text is visible on screen
    [Arguments]    ${text}    ${timeout}=${DEFAULT_TIMEOUT}
    Wait Until Page Contains    ${text}    timeout=${timeout}

Verify Text Is Not Displayed
    [Documentation]    Verifies specific text is not visible on screen
    [Arguments]    ${text}    ${timeout}=${DEFAULT_TIMEOUT}
    Wait Until Page Does Not Contain    ${text}    timeout=${timeout}

# =============================================================================
# UTILITY KEYWORDS
# =============================================================================
Generate Unique String
    [Documentation]    Generates a unique string for test data
    [Arguments]    ${prefix}=test
    ${timestamp}=    Get Time    epoch
    ${unique}=    Set Variable    ${prefix}_${timestamp}
    RETURN    ${unique}

Wait For App To Stabilize
    [Documentation]    Waits for app animations to complete
    [Arguments]    ${seconds}=1
    Sleep    ${seconds}s

Scroll Down
    [Documentation]    Scrolls down on the screen
    Execute Script    mobile: swipeGesture    {"left": 100, "top": 500, "width": 800, "height": 1000, "direction": "up", "percent": 0.75}

Scroll Up
    [Documentation]    Scrolls up on the screen
    Execute Script    mobile: swipeGesture    {"left": 100, "top": 500, "width": 800, "height": 1000, "direction": "down", "percent": 0.75}

Take Debug Screenshot
    [Documentation]    Captures screenshot for debugging
    [Arguments]    ${name}=debug
    Capture Page Screenshot    filename=${name}_${TEST_NAME}.png
