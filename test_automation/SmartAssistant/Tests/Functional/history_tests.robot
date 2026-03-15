*** Settings ***
Documentation     Functional tests for History drawer features
...               Tests the chat history management functionality

Library           AppiumLibrary
Resource          ../../Resources/Common.resource
Resource          ../../Resources/PageObjects/ChatPage.resource
Resource          ../../Resources/PageObjects/HistoryPage.resource

Test Setup        Start Chat Application
Test Teardown     Close Chat Application

*** Test Cases ***
# =============================================================================
# DRAWER OPEN/CLOSE TESTS
# =============================================================================
TC_HISTORY_001 - Open History Drawer
    [Documentation]    Verifies user can open history drawer
    [Tags]             functional    history    regression
    Open History Drawer
    Verify History Drawer Is Open
    Verify Text Is Displayed    HISTORY

TC_HISTORY_002 - Close History Drawer With Back Button
    [Documentation]    Verifies user can close history drawer with back
    [Tags]             functional    history    regression
    Open History Drawer
    Verify History Drawer Is Open
    Close History Drawer
    Verify History Drawer Is Closed

# =============================================================================
# NEW CHAT TESTS
# =============================================================================
TC_HISTORY_003 - Create New Chat From Drawer
    [Documentation]    User creates a new chat session from history drawer
    [Tags]             functional    history    regression
    # First send a message to have something in history
    User Sends A Message    Test message for history
    Verify AI Response Received
    # Open drawer and create new chat
    Open History Drawer
    Start New Chat From Drawer
    # Verify new chat screen
    Verify Welcome Screen Is Displayed

TC_HISTORY_004 - New Chat Resets Conversation
    [Documentation]    Creating new chat should show clean welcome screen
    [Tags]             functional    history    regression
    User Sends A Message    Old conversation message
    Verify AI Response Received
    Verify Message Is Displayed    Old conversation message
    # Create new chat
    Open History Drawer
    Start New Chat From Drawer
    # Old message should not be visible
    Verify Message Is Not Displayed    Old conversation message

# =============================================================================
# SESSION MANAGEMENT TESTS
# =============================================================================
TC_HISTORY_005 - Session Appears In History After Chat
    [Documentation]    Chat session should appear in history after sending message
    [Tags]             functional    history    regression
    ${unique_msg}=    Generate Unique String    test_msg
    User Sends A Message    ${unique_msg}
    Verify AI Response Received
    # Check history
    Open History Drawer
    Verify Session Exists In History    ${unique_msg}

TC_HISTORY_006 - Switch Between Sessions
    [Documentation]    User can switch between different chat sessions
    [Tags]             functional    history    regression
    # Create first session
    User Sends A Message    First session message
    Verify AI Response Received
    # Create second session
    Open History Drawer
    Start New Chat From Drawer
    User Sends A Message    Second session message
    Verify AI Response Received
    # Switch back to first session
    Open History Drawer
    Select Session By Index    1    # First session is at index 1 (oldest first)
    Verify Message Is Displayed    First session message

# =============================================================================
# DELETE SESSION TESTS
# =============================================================================
TC_HISTORY_007 - Delete Session From History
    [Documentation]    User can delete a chat session
    [Tags]             functional    history    regression    critical
    ${unique_msg}=    Generate Unique String    delete_test
    User Sends A Message    ${unique_msg}
    Verify AI Response Received
    # Delete the session
    Open History Drawer
    Delete Session By Index    1
    # Close drawer and verify session text is gone
    Close History Drawer
    Verify Message Is Not Displayed    ${unique_msg}

TC_HISTORY_008 - Delete Active Session Clears Chat
    [Documentation]    Deleting active session should clear the chat screen
    [Tags]             functional    history    regression    critical
    ${unique_msg}=    Generate Unique String    active_delete
    User Sends A Message    ${unique_msg}
    Verify AI Response Received
    Verify Message Is Displayed    ${unique_msg}
    # Delete the active session
    Open History Drawer
    Delete Session By Index    1
    # Chat should be cleared
    Close History Drawer
    Verify Message Is Not Displayed    ${unique_msg}

# =============================================================================
# MULTIPLE SESSIONS TESTS
# =============================================================================
TC_HISTORY_009 - Multiple Sessions In History
    [Documentation]    Multiple chat sessions appear in history
    [Tags]             functional    history    regression
    # Create multiple sessions
    User Sends A Message    Session A message
    Verify AI Response Received
    Open History Drawer
    Start New Chat From Drawer

    User Sends A Message    Session B message
    Verify AI Response Received
    Open History Drawer
    Start New Chat From Drawer

    User Sends A Message    Session C message
    Verify AI Response Received

    # Verify all sessions in history
    Open History Drawer
    Verify Session Exists In History    Session A
    Verify Session Exists In History    Session B
    Verify Session Exists In History    Session C
