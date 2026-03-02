# =============================================================================
# DEPRECATED - Use Tests/Functional/chat_tests.robot instead
# =============================================================================
# This file is kept for backward compatibility
# New tests should be added to Tests/Functional/ folder

*** Settings ***
Documentation     [DEPRECATED] Basic chat tests - Use Tests/Functional/chat_tests.robot
Library           AppiumLibrary
Resource          ../Resources/Common.resource
Resource          ../Resources/PageObjects/ChatPage.resource

Test Setup        Start Chat Application
Test Teardown     Close Chat Application

*** Test Cases ***
Verify User Can Send Message And Receive Reply
    [Documentation]    Standard happy path for chat
    [Tags]             smoke    chat    deprecated
    User Sends A Message    Hello, how are you?
    Verify AI Is Typing
    Verify AI Response Received

Verify User Can Stop Generation
    [Documentation]    Ensures the Stop button works
    [Tags]             regression    deprecated
    User Sends A Message    Write a very long poem about technology.
    Wait Until Element Is Visible    ${STOP_BUTTON}    timeout=50s
    Click Element    ${STOP_BUTTON}
    Wait Until Page Does Not Contain Element    ${STOP_BUTTON}