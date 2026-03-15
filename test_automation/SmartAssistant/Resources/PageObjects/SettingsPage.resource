*** Settings ***
Documentation    Settings Page/Drawer locators and keywords
Library          AppiumLibrary
Resource         ../Common.resource
Resource         ChatPage.resource

*** Variables ***
# =============================================================================
# SETTINGS DRAWER
# =============================================================================
${SETTINGS_DRAWER}             accessibility_id=settings_drawer
${SETTINGS_TITLE}              xpath=//*[contains(@text, 'SETTINGS')]
${ONLINE_MODE_SWITCH}          accessibility_id=online_mode_switch
${ONLINE_MODE_LABEL}           xpath=//*[contains(@text, 'Online Mode')]
${CLOUD_AI_TEXT}               xpath=//*[contains(@text, 'Using cloud AI')]
${DEVICE_AI_TEXT}              xpath=//*[contains(@text, 'Using on-device AI')]

*** Keywords ***
# =============================================================================
# DRAWER ACTIONS
# =============================================================================
Open Settings Drawer
    [Documentation]    Opens the settings drawer
    Smart Click    ${SETTINGS_BUTTON}
    Wait Until Element Is Visible    ${SETTINGS_DRAWER}    timeout=${DEFAULT_TIMEOUT}

Close Settings Drawer
    [Documentation]    Closes the settings drawer by clicking outside
    Press Keycode    4    # Android back button

Toggle Online Mode
    [Documentation]    Toggles the online/offline mode switch
    Smart Click    ${ONLINE_MODE_SWITCH}
    Sleep    0.5s    # Wait for animation

Enable Online Mode
    [Documentation]    Ensures online mode is enabled
    ${is_online}=    Run Keyword And Return Status
    ...    Wait Until Element Is Visible    ${CLOUD_AI_TEXT}    timeout=2s
    Run Keyword Unless    ${is_online}    Toggle Online Mode

Enable Offline Mode
    [Documentation]    Ensures offline mode is enabled
    ${is_offline}=    Run Keyword And Return Status
    ...    Wait Until Element Is Visible    ${DEVICE_AI_TEXT}    timeout=2s
    Run Keyword Unless    ${is_offline}    Toggle Online Mode

# =============================================================================
# VERIFICATION KEYWORDS
# =============================================================================
Verify Settings Drawer Is Open
    [Documentation]    Verifies settings drawer is visible
    Wait Until Element Is Visible    ${SETTINGS_DRAWER}    timeout=${DEFAULT_TIMEOUT}

Verify Settings Drawer Is Closed
    [Documentation]    Verifies settings drawer is not visible
    Wait Until Page Does Not Contain Element    ${SETTINGS_DRAWER}    timeout=${DEFAULT_TIMEOUT}

Verify Online Mode Is Enabled
    [Documentation]    Verifies app is in online mode
    Wait Until Element Is Visible    ${CLOUD_AI_TEXT}    timeout=${DEFAULT_TIMEOUT}

Verify Offline Mode Is Enabled
    [Documentation]    Verifies app is in offline mode
    Wait Until Element Is Visible    ${DEVICE_AI_TEXT}    timeout=${DEFAULT_TIMEOUT}

Verify Settings Title Is Displayed
    [Documentation]    Verifies Settings title is shown
    Wait Until Element Is Visible    ${SETTINGS_TITLE}    timeout=${DEFAULT_TIMEOUT}
