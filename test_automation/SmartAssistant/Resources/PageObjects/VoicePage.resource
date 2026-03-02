*** Settings ***
Documentation    Voice Input locators and keywords
Library          AppiumLibrary
Resource         ../Common.resource
Resource         ChatPage.resource

*** Variables ***
# =============================================================================
# VOICE INPUT
# =============================================================================
${MIC_BUTTON}                  accessibility_id=chat_mic_button
${VOICE_OVERLAY}               accessibility_id=voice_listening_overlay
${LISTENING_TEXT}              xpath=//*[contains(@text, 'Listening')]
${LISTENING_DOTS}              accessibility_id=listening_dots

*** Keywords ***
# =============================================================================
# VOICE ACTIONS
# =============================================================================
Start Voice Recording
    [Documentation]    Taps the microphone button to start recording
    Smart Click    ${MIC_BUTTON}
    Wait Until Element Is Visible    ${VOICE_OVERLAY}    timeout=${DEFAULT_TIMEOUT}

Stop Voice Recording
    [Documentation]    Taps the microphone button again to stop recording
    Smart Click    ${MIC_BUTTON}
    Wait Until Page Does Not Contain Element    ${VOICE_OVERLAY}    timeout=${DEFAULT_TIMEOUT}

Cancel Voice Recording
    [Documentation]    Cancels voice recording by pressing back
    Press Keycode    4    # Android back button
    Wait Until Page Does Not Contain Element    ${VOICE_OVERLAY}    timeout=${DEFAULT_TIMEOUT}

# =============================================================================
# VERIFICATION KEYWORDS
# =============================================================================
Verify Voice Overlay Is Displayed
    [Documentation]    Verifies the voice listening overlay is shown
    Wait Until Element Is Visible    ${VOICE_OVERLAY}    timeout=${DEFAULT_TIMEOUT}

Verify Voice Overlay Is Not Displayed
    [Documentation]    Verifies the voice listening overlay is hidden
    Wait Until Page Does Not Contain Element    ${VOICE_OVERLAY}    timeout=${DEFAULT_TIMEOUT}

Verify Listening Text Is Displayed
    [Documentation]    Verifies "Listening" text is shown
    Wait Until Element Is Visible    ${LISTENING_TEXT}    timeout=${DEFAULT_TIMEOUT}

Verify Mic Button Is Visible
    [Documentation]    Verifies mic button is available
    Wait Until Element Is Visible    ${MIC_BUTTON}    timeout=${DEFAULT_TIMEOUT}

# =============================================================================
# COMPOSITE KEYWORDS
# =============================================================================
Record Voice And Send
    [Documentation]    Starts recording, waits, and stops to send
    [Arguments]    ${duration}=3
    Start Voice Recording
    Sleep    ${duration}s    # Wait for user to speak
    Stop Voice Recording

Verify Voice Input Flow Works
    [Documentation]    Verifies complete voice input flow
    Verify Mic Button Is Visible
    Start Voice Recording
    Verify Voice Overlay Is Displayed
    Verify Listening Text Is Displayed
    Cancel Voice Recording
    Verify Voice Overlay Is Not Displayed
