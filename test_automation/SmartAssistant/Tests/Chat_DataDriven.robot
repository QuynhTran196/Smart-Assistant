#*** Settings ***
#Documentation     Runs multiple chat scenarios automatically.
## SỬA 1: Dùng ${CURDIR} để tìm file chính xác
#Library           DataDriver    file=${CURDIR}/../Data/ChatData.csv    encoding=utf-8-sig    dialect=excel
##Library    DataDriver    file=ChatData.csv    encoding=utf-8-sig
##Library           DataDriver    file=../Data/CleanData.csv    encoding=utf-8
#Resource          ../Resources/Common.resource
#Resource          ../Resources/PageObjects/ChatPage.resource
#
#Suite Setup       Start Chat Application
#Suite Teardown    Close Chat Application
#
## Keyword này sẽ chạy cho từng dòng trong CSV
#Test Template     Verify Chat Cycle
#
#*** Test Cases ***
#Scenario: ${message_content}
#    [Tags]    datadriver
#    No Operation
#*** Keywords ***
#Verify Chat Cycle
#    [Arguments]    ${message_content}
#    User Sends A Message    ${message_content}
#    Verify AI Is Typing
#    Verify AI Response Received

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