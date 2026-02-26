@echo off
chcp 65001 >nul
echo ================================
echo   AICogniBlog 测试套件
echo ================================
echo.

echo [1/4] 运行应用启动测试...
call mvn test -Dtest=AICogniBlogApplicationTest
if %errorlevel% neq 0 (
    echo ❌ 应用启动测试失败
    exit /b 1
)
echo ✅ 应用启动测试通过
echo.

echo [2/4] 运行 Mapper 层测试...
call mvn test -Dtest=ArticleMapperTest
if %errorlevel% neq 0 (
    echo ❌ Mapper 测试失败
    exit /b 1
)
echo ✅ Mapper 测试通过
echo.

echo [3/4] 运行 Service 层测试...
call mvn test -Dtest=ArticleServiceTest
if %errorlevel% neq 0 (
    echo ❌ Service 测试失败
    exit /b 1
)
echo ✅ Service 测试通过
echo.

echo [4/4] 运行 Controller 集成测试...
call mvn test -Dtest=ArticleControllerIntegrationTest
if %errorlevel% neq 0 (
    echo ❌ Controller 测试失败
    exit /b 1
)
echo ✅ Controller 测试通过
echo.

echo ================================
echo   所有测试通过！
echo ================================
echo.
echo 测试报告位置: target\surefire-reports\
pause

