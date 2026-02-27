@echo off
chcp 65001 >nul
echo ================================
echo   运行 ArticleController 单元测试
echo ================================
echo.

call mvn test -Dtest=ArticleControllerTest

if %errorlevel% neq 0 (
    echo.
    echo ❌ 单元测试失败
    pause
    exit /b 1
)

echo.
echo ✅ 单元测试通过
echo.
echo 测试报告位置: target\surefire-reports\
pause

