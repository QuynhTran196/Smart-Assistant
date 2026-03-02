*** Settings ***
Documentation     Smoke tests - Quick sanity checks for Smart Assistant
...               These tests should run in under 5 minutes

Library           AppiumLibrary
Resource          ../../Resources/Common.resource
Resource          ../../Resources/PageObjects/ChatPage.resource
Resource          ../../Resources/PageObjects/HistoryPage.resource
Resource          ../../Resources/PageObjects/SettingsPage.resource

Test Setup        Start Chat Application
Test Teardown     Close Chat Application

*** Test Cases ***
# =============================================================================
# APP LAUNCH TESTS
# =============================================================================
Verify App Launches Successfully
    [Documentation]    Verifies the app opens without crash
    [Tags]             smoke    critical    launch
    Verify Welcome Screen Is Displayed
    Verify Text Is Displayed    Your AI Assistant

Verify Main UI Elements Are Present
    [Documentation]    Verifies all main UI elements are visible
    [Tags]             smoke    critical    ui
    # Input area
    Wait Until Element Is Visible    ${INPUT_FIELD}    timeout=${DEFAULT_TIMEOUT}
    Wait Until Element Is Visible    ${ADD_BUTTON}    timeout=${DEFAULT_TIMEOUT}
    Wait Until Element Is Visible    ${MIC_BUTTON}    timeout=${DEFAULT_TIMEOUT}
    # Toolbar
    Wait Until Element Is Visible    ${HISTORY_BUTTON}    timeout=${DEFAULT_TIMEOUT}
    Wait Until Element Is Visible    ${SETTINGS_BUTTON}    timeout=${DEFAULT_TIMEOUT}

# =============================================================================
# BASIC CHAT TESTS
# =============================================================================
Verify User Can Send Message
    [Documentation]    Verifies basic send message functionality
    [Tags]             smoke    critical    chat
    User Sends A Message    Hello
    Verify AI Is Typing
    Verify AI Response Received

Verify Input Field Clears After Send
    [Documentation]    Verifies input field is cleared after sending
    [Tags]             smoke    chat
    User Sends A Message    Test message
    Verify Input Field Is Empty

# =============================================================================
# DRAWER TESTS
# =============================================================================
Verify History Drawer Opens
    [Documentation]    Verifies history drawer can be opened
    [Tags]             smoke    history
    Open History Drawer
    Verify History Drawer Is Open

Verify Settings Drawer Opens
    [Documentation]    Verifies settings drawer can be opened
    [Tags]             smoke    settings
    Open Settings Drawer
    Verify Settings Drawer Is Open
    Verify Settings Title Is Displayed

# =============================================================================
# VOICE TESTS
# =============================================================================
Verify Mic Button Is Available
    [Documentation]    Verifies microphone button is visible
    [Tags]             smoke    voice
    Verify Mic Button Is Visible
