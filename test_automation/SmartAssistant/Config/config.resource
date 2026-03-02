*** Settings ***
Documentation    Global configuration for Smart Assistant test automation

*** Variables ***
# =============================================================================
# APPIUM CONFIGURATION
# =============================================================================
${APPIUM_SERVER}              http://127.0.0.1:4723

# =============================================================================
# DEVICE CONFIGURATION
# =============================================================================
${ANDROID_AUTOMATION_NAME}    UiAutomator2
${ANDROID_PLATFORM_NAME}      Android
${DEVICE_NAME}                X25B00039

# =============================================================================
# APP CONFIGURATION
# =============================================================================
${APP_PACKAGE}                com.smartassistant.app
${APP_ACTIVITY}               com.smartassistant.app.MainActivity

# =============================================================================
# TIMEOUTS (in seconds)
# =============================================================================
${DEFAULT_TIMEOUT}            10s
${LONG_TIMEOUT}               30s
${SHORT_TIMEOUT}              5s
${AI_RESPONSE_TIMEOUT}        60s

# =============================================================================
# TEST DATA PATHS
# =============================================================================
${DATA_DIR}                   ${CURDIR}/../Data
${TEST_FILES_DIR}             ${DATA_DIR}/test_files
