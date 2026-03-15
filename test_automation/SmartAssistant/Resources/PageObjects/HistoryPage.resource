*** Settings ***
Library     AppiumLibrary
Resource    ../Common.resource
Resource    ChatPage.resource

*** Variables ***
# =============================================================================
# HISTORY DRAWER
# =============================================================================
${HISTORY_DRAWER}              accessibility_id=history_drawer
${HISTORY_DRAWER_TITLE}        xpath=//*[contains(@text, 'HISTORY')]
${DRAWER_NEW_CHAT_BUTTON}      accessibility_id=history_drawer_new_chat_button
${HISTORY_SESSION_ITEM}        accessibility_id=history_session_item
${HISTORY_DELETE_BUTTON}       accessibility_id=history_delete_button

# Dynamic locators (use with Format String)
${HISTORY_ITEM_BY_INDEX}       xpath=(//android.view.View[@content-desc='history_session_item'])[{}]
${HISTORY_DELETE_BY_INDEX}     xpath=(//android.view.View[@content-desc='history_delete_button'])[{}]

*** Keywords ***
# =============================================================================
# DRAWER ACTIONS
# =============================================================================
Open History Drawer
    [Documentation]    Opens the history drawer
    Smart Click    ${HISTORY_BUTTON}
    Wait Until Element Is Visible    ${HISTORY_DRAWER}    timeout=${DEFAULT_TIMEOUT}

Close History Drawer
    [Documentation]    Closes the history drawer by clicking outside
    Press Keycode    4    # Android back button

Start New Chat From Drawer
    [Documentation]    Creates a new chat from history drawer
    Smart Click    ${DRAWER_NEW_CHAT_BUTTON}
    Wait Until Page Does Not Contain Element    ${HISTORY_DRAWER}    timeout=${DEFAULT_TIMEOUT}

Select Session By Index
    [Documentation]    Selects a session by its index (1-based)
    [Arguments]    ${index}=1
    ${locator}=    Format String    ${HISTORY_ITEM_BY_INDEX}    ${index}
    Smart Click    ${locator}
    Wait Until Page Does Not Contain Element    ${HISTORY_DRAWER}    timeout=${DEFAULT_TIMEOUT}

Delete Session By Index
    [Documentation]    Deletes a session by its index (1-based)
    [Arguments]    ${index}=1
    ${delete_locator}=    Format String    ${HISTORY_DELETE_BY_INDEX}    ${index}
    Smart Click    ${delete_locator}

# =============================================================================
# VERIFICATION KEYWORDS
# =============================================================================
Verify History Drawer Is Open
    [Documentation]    Verifies history drawer is visible
    Wait Until Element Is Visible    ${HISTORY_DRAWER}    timeout=${DEFAULT_TIMEOUT}

Verify History Drawer Is Closed
    [Documentation]    Verifies history drawer is not visible
    Wait Until Page Does Not Contain Element    ${HISTORY_DRAWER}    timeout=${DEFAULT_TIMEOUT}

Verify Session Exists In History
    [Documentation]    Verifies a session with specific text exists
    [Arguments]    ${session_text}
    Wait Until Page Contains    ${session_text}    timeout=${DEFAULT_TIMEOUT}

Verify Session Does Not Exist In History
    [Documentation]    Verifies a session with specific text does not exist
    [Arguments]    ${session_text}
    Wait Until Page Does Not Contain    ${session_text}    timeout=${DEFAULT_TIMEOUT}

Verify History Has Sessions
    [Documentation]    Verifies at least one session exists
    Wait Until Element Is Visible    ${HISTORY_SESSION_ITEM}    timeout=${DEFAULT_TIMEOUT}

Verify History Is Empty
    [Documentation]    Verifies no sessions exist
    Wait Until Page Does Not Contain Element    ${HISTORY_SESSION_ITEM}    timeout=${DEFAULT_TIMEOUT}

Get Session Count
    [Documentation]    Returns the number of sessions in history
    ${count}=    Get Matching Xpath Count    //android.view.View[@content-desc='history_session_item']
    RETURN    ${count}

# =============================================================================
# COMPOSITE KEYWORDS
# =============================================================================
Delete All Sessions
    [Documentation]    Deletes all sessions in history
    ${count}=    Get Session Count
    FOR    ${i}    IN RANGE    ${count}
        Delete Session By Index    1
        Sleep    0.5s
    END

Create Chat And Return To History
    [Documentation]    Creates a new chat, sends a message, and returns to history
    [Arguments]    ${message}
    Close History Drawer
    User Sends A Message    ${message}
    Verify AI Response Received
    Open History Drawer
