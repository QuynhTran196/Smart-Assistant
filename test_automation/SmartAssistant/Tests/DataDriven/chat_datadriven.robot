*** Settings ***
Documentation     Data-driven tests for Chat functionality
...               Runs multiple chat scenarios from test data

Library           AppiumLibrary
Library           DataDriver    file=${CURDIR}/../../Data/chat_messages.csv    encoding=utf-8-sig
Resource          ../../Resources/Common.resource
Resource          ../../Resources/PageObjects/ChatPage.resource

Suite Setup       Start Chat Application
Suite Teardown    Close Chat Application

Test Template     Verify Chat Message Flow

*** Test Cases ***
Chat Scenario: ${test_name}
    [Tags]    datadriven    chat
    No Operation

*** Keywords ***
Verify Chat Message Flow
    [Documentation]    Sends a message and verifies AI response
    [Arguments]    ${test_name}    ${message}    ${expected_behavior}
    Log    Testing: ${test_name}
    Log    Message: ${message}
    Log    Expected: ${expected_behavior}

    User Sends A Message    ${message}

    Run Keyword If    '${expected_behavior}' == 'response'
    ...    Verify AI Response Received
    ...    ELSE IF    '${expected_behavior}' == 'typing'
    ...    Verify AI Is Typing
