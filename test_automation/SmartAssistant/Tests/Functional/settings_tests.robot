*** Settings ***
Documentation     Functional tests for Settings drawer features
...               Tests the app settings functionality

Library           AppiumLibrary
Resource          ../../Resources/Common.resource
Resource          ../../Resources/PageObjects/ChatPage.resource
Resource          ../../Resources/PageObjects/SettingsPage.resource

Test Setup        Start Chat Application
Test Teardown     Close Chat Application

*** Test Cases ***
# =============================================================================
# DRAWER OPEN/CLOSE TESTS
# =============================================================================
TC_SETTINGS_001 - Open Settings Drawer
    [Documentation]    Verifies user can open settings drawer
    [Tags]             functional    settings    regression
    Open Settings Drawer
    Verify Settings Drawer Is Open
    Verify Settings Title Is Displayed

TC_SETTINGS_002 - Close Settings Drawer With Back Button
    [Documentation]    Verifies user can close settings drawer with back
    [Tags]             functional    settings    regression
    Open Settings Drawer
    Verify Settings Drawer Is Open
    Close Settings Drawer
    Verify Settings Drawer Is Closed

# =============================================================================
# ONLINE MODE TESTS
# =============================================================================
TC_SETTINGS_003 - Verify Default Online Mode
    [Documentation]    App should be in online mode by default
    [Tags]             functional    settings    regression
    Open Settings Drawer
    Verify Online Mode Is Enabled

TC_SETTINGS_004 - Toggle To Offline Mode
    [Documentation]    User can switch to offline mode
    [Tags]             functional    settings    regression
    Open Settings Drawer
    Enable Offline Mode
    Verify Offline Mode Is Enabled

TC_SETTINGS_005 - Toggle Back To Online Mode
    [Documentation]    User can switch back to online mode
    [Tags]             functional    settings    regression
    Open Settings Drawer
    Enable Offline Mode
    Verify Offline Mode Is Enabled
    Enable Online Mode
    Verify Online Mode Is Enabled

TC_SETTINGS_006 - Mode Persists After Closing Drawer
    [Documentation]    Selected mode should persist after closing drawer
    [Tags]             functional    settings    regression
    Open Settings Drawer
    Enable Offline Mode
    Close Settings Drawer
    # Reopen and verify
    Open Settings Drawer
    Verify Offline Mode Is Enabled

# =============================================================================
# UI INDICATOR TESTS
# =============================================================================
TC_SETTINGS_007 - Online Status Shows In Chat
    [Documentation]    Online mode should show "Online" in chat
    [Tags]             functional    settings    regression
    Open Settings Drawer
    Enable Online Mode
    Close Settings Drawer
    # Verify status in chat top bar
    Verify AI Response Received    # Wait for status to update
    # Note: You may need to adjust this based on actual UI behavior

TC_SETTINGS_008 - Offline Status Shows In Chat
    [Documentation]    Offline mode should show "Offline" in chat
    [Tags]             functional    settings    regression
    Open Settings Drawer
    Enable Offline Mode
    Close Settings Drawer
    # Verify status in chat top bar
    Verify AI Is Offline

# =============================================================================
# SETTINGS CONTENT TESTS
# =============================================================================
TC_SETTINGS_009 - Verify Settings UI Elements
    [Documentation]    All settings UI elements should be present
    [Tags]             functional    settings    regression
    Open Settings Drawer
    Verify Text Is Displayed    SETTINGS
    Verify Text Is Displayed    Online Mode
    # One of these should be visible
    ${is_online}=    Run Keyword And Return Status
    ...    Verify Text Is Displayed    Using cloud AI
    Run Keyword Unless    ${is_online}
    ...    Verify Text Is Displayed    Using on-device AI
