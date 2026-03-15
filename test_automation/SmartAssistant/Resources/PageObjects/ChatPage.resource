# Purpose: This file contains the Locators (the IDs you found in Appium Inspector) and Keywords specific to the Chat Screen

*** Settings ***
Documentation    Chat Page locators and keywords
Library    AppiumLibrary
Resource    ../Common.resource  # Import Common to get Smart Keywords

*** Variables ***
# =============================================================================
# CHAT INPUT AREA
# =============================================================================
${INPUT_FIELD}              xpath=//android.widget.EditText
${SEND_BUTTON}              accessibility_id=send_button
${ADD_BUTTON}               accessibility_id=chat_add_button
${MIC_BUTTON}               accessibility_id=chat_mic_button
${STOP_BUTTON}              accessibility_id=stop_button

# =============================================================================
# TOP BAR
# =============================================================================
${TOP_BAR_TITLE}            accessibility_id=chat_top_bar_title
${AI_TYPING_IND}            accessibility_id=ai_typing
${AI_ONLINE_IND}            accessibility_id=ai_online
${AI_OFFLINE_IND}           accessibility_id=ai_offline

# =============================================================================
# TOOLBAR
# =============================================================================
${HISTORY_BUTTON}           accessibility_id=chat_history_button
${SETTINGS_BUTTON}          accessibility_id=chat_settings_button

# =============================================================================
# MESSAGES
# =============================================================================
${MESSAGE_LIST}             accessibility_id=chat_message_list
${USER_MESSAGE}             accessibility_id=user_message
${AI_MESSAGE}               accessibility_id=ai_message

# =============================================================================
# WELCOME SCREEN
# =============================================================================
${WELCOME_TITLE}            xpath=//*[contains(@text, 'Your AI Assistant')]
${WELCOME_TEXT}             xpath=//*[contains(@text, 'How can I help you')]

# =============================================================================
# ADD DIALOG
# =============================================================================
${UPLOAD_FILE_OPTION}       xpath=//*[contains(@text, 'Upload file')]
${NEW_CHAT_OPTION}          xpath=//*[contains(@text, 'New chat')]

# =============================================================================
# ATTACHMENT AREA
# =============================================================================
${PENDING_ATTACHMENT}       accessibility_id=pending_attachment
${REMOVE_ATTACHMENT}        accessibility_id=remove_attachment

*** Keywords ***
# =============================================================================
# MESSAGE ACTIONS
# =============================================================================
User Sends A Message
    [Documentation]    Types a message and sends it
    [Arguments]    ${message_text}
    Smart Input    ${INPUT_FIELD}    ${message_text}
    Smart Click    ${SEND_BUTTON}

User Types A Message
    [Documentation]    Types a message without sending
    [Arguments]    ${message_text}
    Smart Input    ${INPUT_FIELD}    ${message_text}

User Clicks Send Button
    [Documentation]    Clicks the send button
    Smart Click    ${SEND_BUTTON}

User Clicks Stop Button
    [Documentation]    Clicks the stop generation button
    Smart Click    ${STOP_BUTTON}

User Clicks Add Button
    [Documentation]    Clicks the add (+) button
    Smart Click    ${ADD_BUTTON}

# =============================================================================
# VERIFICATION KEYWORDS
# =============================================================================
Verify AI Is Typing
    [Documentation]    Verifies AI typing indicator is shown
    Wait Until Element Is Visible    ${AI_TYPING_IND}    timeout=${DEFAULT_TIMEOUT}

Verify AI Response Received
    [Documentation]    Verifies AI has finished responding (Online status)
    Wait Until Element Is Visible    ${AI_ONLINE_IND}    timeout=${AI_RESPONSE_TIMEOUT}

Verify AI Is Offline
    [Documentation]    Verifies AI is in offline mode
    Wait Until Element Is Visible    ${AI_OFFLINE_IND}    timeout=${DEFAULT_TIMEOUT}

Verify Stop Button Is Visible
    [Documentation]    Verifies stop button is displayed during generation
    Wait Until Element Is Visible    ${STOP_BUTTON}    timeout=${DEFAULT_TIMEOUT}

Verify Stop Button Is Not Visible
    [Documentation]    Verifies stop button is hidden after generation stops
    Wait Until Page Does Not Contain Element    ${STOP_BUTTON}    timeout=${DEFAULT_TIMEOUT}

Verify Welcome Screen Is Displayed
    [Documentation]    Verifies the welcome screen is shown
    Wait Until Element Is Visible    ${WELCOME_TITLE}    timeout=${DEFAULT_TIMEOUT}

Verify Welcome Screen Is Not Displayed
    [Documentation]    Verifies the welcome screen is hidden (chat has started)
    Wait Until Page Does Not Contain Element    ${WELCOME_TITLE}    timeout=${DEFAULT_TIMEOUT}

Verify Message Is Displayed
    [Documentation]    Verifies a specific message text is shown
    [Arguments]    ${message_text}
    Wait Until Page Contains    ${message_text}    timeout=${DEFAULT_TIMEOUT}

Verify Message Is Not Displayed
    [Documentation]    Verifies a specific message text is not shown
    [Arguments]    ${message_text}
    Wait Until Page Does Not Contain    ${message_text}    timeout=${DEFAULT_TIMEOUT}

Verify Input Field Is Empty
    [Documentation]    Verifies the input field is cleared after sending
    ${text}=    Get Text    ${INPUT_FIELD}
    Should Be Empty    ${text}

# =============================================================================
# ADD DIALOG KEYWORDS
# =============================================================================
Open Add Dialog
    [Documentation]    Opens the add action dialog
    User Clicks Add Button
    Wait Until Element Is Visible    ${UPLOAD_FILE_OPTION}    timeout=${DEFAULT_TIMEOUT}

Select Upload File Option
    [Documentation]    Selects upload file from add dialog
    Smart Click    ${UPLOAD_FILE_OPTION}

Select New Chat Option
    [Documentation]    Selects new chat from add dialog
    Smart Click    ${NEW_CHAT_OPTION}


# =============================================================================
# ATTACHMENT KEYWORDS
# =============================================================================
Verify Attachment Is Pending
    [Documentation]    Verifies an attachment chip is shown
    Wait Until Element Is Visible    ${PENDING_ATTACHMENT}    timeout=${DEFAULT_TIMEOUT}

Remove Pending Attachment
    [Documentation]    Removes a pending attachment
    Smart Click    ${REMOVE_ATTACHMENT}

Verify No Pending Attachments
    [Documentation]    Verifies no attachments are pending
    Wait Until Page Does Not Contain Element    ${PENDING_ATTACHMENT}    timeout=${SHORT_TIMEOUT}
