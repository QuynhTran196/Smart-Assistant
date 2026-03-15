*** Settings ***
Documentation     Sanity Check - Test logic directly (No CSV)
Library           AppiumLibrary
Resource          ../Resources/Common.resource
Resource          ../Resources/PageObjects/ChatPage.resource

Suite Setup       Start Chat Application
Suite Teardown    Close Chat Application

# Template này bảo Robot: "Mỗi dòng trong Test Cases sẽ chạy keyword này"
Test Template     Verify Chat Cycle

*** Test Cases ***
# Cột 1: Tên Test Case       # Cột 2: Dữ liệu gửi vào (${message_content})
Chat Hello AI                Hello AI
Chat Write Poem              Write a poem about Kotlin
Chat Math Question           What is 100% of $500?

*** Keywords ***
Verify Chat Cycle
    [Arguments]    ${message_content}
    Log    Running test with: ${message_content}
    # Đây là keyword chat chính của bạn
    User Sends A Message    ${message_content}
    Verify AI Is Typing
    Verify AI Response Received