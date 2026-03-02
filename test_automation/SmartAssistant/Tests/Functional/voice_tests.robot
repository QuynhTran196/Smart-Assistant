*** Settings ***
Documentation     Functional tests for Voice input features
...               Tests the voice recording functionality
...               Note: Requires RECORD_AUDIO permission granted

Library           AppiumLibrary
Resource          ../../Resources/Common.resource
Resource          ../../Resources/PageObjects/ChatPage.resource
Resource          ../../Resources/PageObjects/VoicePage.resource

Test Setup        Start Chat Application
Test Teardown     Close Chat Application

*** Test Cases ***
# =============================================================================
# BASIC VOICE UI TESTS
# =============================================================================
TC_VOICE_001 - Mic Button Is Visible
    [Documentation]    Verifies microphone button is displayed
    [Tags]             functional    voice    regression
    Verify Mic Button Is Visible

TC_VOICE_002 - Start Voice Recording Shows Overlay
    [Documentation]    Tapping mic button should show listening overlay
    [Tags]             functional    voice    regression
    Start Voice Recording
    Verify Voice Overlay Is Displayed
    Verify Listening Text Is Displayed
    # Clean up
    Cancel Voice Recording

TC_VOICE_003 - Stop Voice Recording Hides Overlay
    [Documentation]    Stopping recording should hide the overlay
    [Tags]             functional    voice    regression
    Start Voice Recording
    Verify Voice Overlay Is Displayed
    Stop Voice Recording
    Verify Voice Overlay Is Not Displayed

TC_VOICE_004 - Cancel Voice Recording
    [Documentation]    User can cancel voice recording with back button
    [Tags]             functional    voice    regression
    Start Voice Recording
    Verify Voice Overlay Is Displayed
    Cancel Voice Recording
    Verify Voice Overlay Is Not Displayed

# =============================================================================
# VOICE INPUT FLOW TESTS
# =============================================================================
TC_VOICE_005 - Complete Voice Input Flow
    [Documentation]    Verifies the complete voice input flow works
    [Tags]             functional    voice    regression
    Verify Voice Input Flow Works

TC_VOICE_006 - Voice Button Changes To Mic During Input
    [Documentation]    Button shows mic icon when text field is empty
    [Tags]             functional    voice    regression
    # With empty input, mic button should be visible
    Verify Mic Button Is Visible
    # Type something
    User Types A Message    test
    # Mic should change to send (verify send button is now visible)
    Wait Until Element Is Visible    ${SEND_BUTTON}    timeout=${DEFAULT_TIMEOUT}

# =============================================================================
# VOICE AND CHAT INTEGRATION
# =============================================================================
TC_VOICE_007 - Voice Overlay Hides Welcome Screen
    [Documentation]    Voice overlay should appear over welcome screen
    [Tags]             functional    voice    regression
    Verify Welcome Screen Is Displayed
    Start Voice Recording
    Verify Voice Overlay Is Displayed
    # Welcome should still be there but under overlay
    Cancel Voice Recording
    Verify Welcome Screen Is Displayed

TC_VOICE_008 - Can Use Voice After Text Chat
    [Documentation]    Voice input should work after sending text messages
    [Tags]             functional    voice    regression
    # Send text message first
    User Sends A Message    Hello via text
    Verify AI Response Received
    # Now try voice
    Start Voice Recording
    Verify Voice Overlay Is Displayed
    Cancel Voice Recording

# =============================================================================
# EDGE CASES
# =============================================================================
TC_VOICE_009 - Rapid Mic Button Taps
    [Documentation]    App should handle rapid mic button taps
    [Tags]             functional    voice    stress
    Start Voice Recording
    Stop Voice Recording
    Start Voice Recording
    Stop Voice Recording
    Start Voice Recording
    Cancel Voice Recording
    # App should still be functional
    Verify Mic Button Is Visible

TC_VOICE_010 - Voice During AI Generation
    [Documentation]    Voice should not be available during AI generation
    [Tags]             functional    voice    regression
    User Sends A Message    Write a long story
    # During generation, stop button should be visible instead of mic
    Verify Stop Button Is Visible
    # Stop generation
    User Clicks Stop Button
    # Mic should be back
    Verify Mic Button Is Visible
