#!/bin/bash

echo "================================"
echo "  AICogniBlog 测试套件"
echo "================================"
echo ""

echo "[1/4] 运行应用启动测试..."
mvn test -Dtest=AICogniBlogApplicationTest
if [ $? -ne 0 ]; then
    echo "❌ 应用启动测试失败"
    exit 1
fi
echo "✅ 应用启动测试通过"
echo ""

echo "[2/4] 运行 Mapper 层测试..."
mvn test -Dtest=ArticleMapperTest
if [ $? -ne 0 ]; then
    echo "❌ Mapper 测试失败"
    exit 1
fi
echo "✅ Mapper 测试通过"
echo ""

echo "[3/4] 运行 Service 层测试..."
mvn test -Dtest=ArticleServiceTest
if [ $? -ne 0 ]; then
    echo "❌ Service 测试失败"
    exit 1
fi
echo "✅ Service 测试通过"
echo ""

echo "[4/4] 运行 Controller 集成测试..."
mvn test -Dtest=ArticleControllerIntegrationTest
if [ $? -ne 0 ]; then
    echo "❌ Controller 测试失败"
    exit 1
fi
echo "✅ Controller 测试通过"
echo ""

echo "================================"
echo "  所有测试通过！"
echo "================================"
echo ""
echo "测试报告位置: target/surefire-reports/"

