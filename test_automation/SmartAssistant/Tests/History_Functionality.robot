# =============================================================================
# DEPRECATED - Use Tests/Functional/history_tests.robot instead
# =============================================================================

*** Settings ***
Documentation     [DEPRECATED] History tests - Use Tests/Functional/history_tests.robot
Library           AppiumLibrary

Resource          ../Resources/Common.resource
Resource          ../Resources/PageObjects/ChatPage.resource
Resource          ../Resources/PageObjects/HistoryPage.resource

# Setup and Teardown run before/after every single test case
Test Setup        Start Chat Application
Test Teardown     Close Chat Application

*** Test Cases ***
Verify Deleting Active Session Clears Chat Screen
    [Documentation]    Delete active session should reset chat
    [Tags]             functional    history    deprecated
    ${msg_unique}=    Set Variable    Hello Smart Assistant ${TEST_NAME}
    User Sends A Message    ${msg_unique}
    Verify AI Response Received
    Page Should Contain Text    ${msg_unique}
    Open History Drawer
    Delete Session By Index    1
    Wait Until Page Does Not Contain    ${msg_unique}    timeout=5s
