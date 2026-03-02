*** Settings ***
Documentation     Functional tests for Chat features
...               Tests the main chat functionality of Smart Assistant

Library           AppiumLibrary
Resource          ../../Resources/Common.resource
Resource          ../../Resources/PageObjects/ChatPage.resource

Test Setup        Start Chat Application
Test Teardown     Close Chat Application

*** Test Cases ***
# =============================================================================
# SEND MESSAGE TESTS
# =============================================================================
TC_CHAT_001 - Send Simple Message
    [Documentation]    User sends a simple greeting message
    [Tags]             functional    chat    regression
    User Sends A Message    Hello, how are you?
    Verify AI Is Typing
    Verify AI Response Received
    Verify Message Is Displayed    Hello, how are you?

TC_CHAT_002 - Send Long Message
    [Documentation]    User sends a long message
    [Tags]             functional    chat    regression
    ${long_message}=    Set Variable    This is a very long message to test how the application handles lengthy input. It should be able to process and display this message correctly without any truncation or errors.
    User Sends A Message    ${long_message}
    Verify AI Is Typing
    Verify AI Response Received

TC_CHAT_003 - Send Multiple Messages
    [Documentation]    User sends multiple messages in sequence
    [Tags]             functional    chat    regression
    User Sends A Message    First message
    Verify AI Response Received
    User Sends A Message    Second message
    Verify AI Response Received
    User Sends A Message    Third message
    Verify AI Response Received
    # Verify all messages are displayed
    Verify Message Is Displayed    First message
    Verify Message Is Displayed    Second message
    Verify Message Is Displayed    Third message

TC_CHAT_004 - Send Message With Special Characters
    [Documentation]    User sends message with special characters
    [Tags]             functional    chat    regression
    User Sends A Message    Hello! How are you? @#$%^&*()
    Verify AI Is Typing
    Verify AI Response Received

TC_CHAT_005 - Send Message With Numbers
    [Documentation]    User sends message containing numbers
    [Tags]             functional    chat    regression
    User Sends A Message    What is 100 + 200?
    Verify AI Is Typing
    Verify AI Response Received

# =============================================================================
# STOP GENERATION TESTS
# =============================================================================
TC_CHAT_006 - Stop AI Generation
    [Documentation]    User stops AI while it's generating response
    [Tags]             functional    chat    regression
    User Sends A Message    Write a very long essay about artificial intelligence and its impact on society.
    Verify Stop Button Is Visible
    User Clicks Stop Button
    Verify Stop Button Is Not Visible

TC_CHAT_007 - Stop Generation And Send New Message
    [Documentation]    User stops generation and sends a new message
    [Tags]             functional    chat    regression
    User Sends A Message    Write a long poem about technology.
    Verify Stop Button Is Visible
    User Clicks Stop Button
    Verify Stop Button Is Not Visible
    # Send new message
    User Sends A Message    Hello again
    Verify AI Is Typing
    Verify AI Response Received

# =============================================================================
# WELCOME SCREEN TESTS
# =============================================================================
TC_CHAT_008 - Welcome Screen Disappears After First Message
    [Documentation]    Welcome screen should hide after sending first message
    [Tags]             functional    chat    regression
    Verify Welcome Screen Is Displayed
    User Sends A Message    Hello
    Verify Welcome Screen Is Not Displayed

# =============================================================================
# ADD DIALOG TESTS
# =============================================================================
TC_CHAT_009 - Open Add Dialog
    [Documentation]    User opens the add action dialog
    [Tags]             functional    chat    regression
    Open Add Dialog
    Verify Text Is Displayed    Upload file
    Verify Text Is Displayed    New chat

TC_CHAT_010 - Create New Chat From Dialog
    [Documentation]    User creates new chat from add dialog
    [Tags]             functional    chat    regression
    # Send a message first
    User Sends A Message    Initial message
    Verify AI Response Received
    # Open add dialog and create new chat
    Open Add Dialog
    Select New Chat Option
    # Verify new chat started
    Verify Welcome Screen Is Displayed

# =============================================================================
# EDGE CASES
# =============================================================================
TC_CHAT_011 - Rapid Message Sending
    [Documentation]    User sends messages rapidly
    [Tags]             functional    chat    stress
    User Sends A Message    Quick message 1
    Sleep    1s
    User Sends A Message    Quick message 2
    Sleep    1s
    User Sends A Message    Quick message 3
    # Wait for all responses
    Sleep    30s
    Verify Message Is Displayed    Quick message 1
    Verify Message Is Displayed    Quick message 2
    Verify Message Is Displayed    Quick message 3
