# Compiler_Assignment01

과제 수행을 위한 JavaCC 환경 설정 및 빌드/실행 가이드입니다.

## 1. JavaCC 설치 (`javacc.jar`)

`minijava_ast_project1` 디렉토리로 이동한 후, 아래 명령어를 실행하여 `javacc.jar` 파일을 다운로드합니다.

```bash
curl -L -o ..\javacc.jar https://repo1.maven.org/maven2/net/java/dev/javacc/javacc/7.0.13/javacc-7.0.13.jar
```

## 2. 컴파일

```bash
java -cp ..\javacc.jar javacc MiniJavaParser.jj
```

## 3. 빌드 및 예제 일괄 실행

Parser 코드를 생성한 후, 전체 자바 파일을 컴파일합니다.

```bash
# 1. JavaCC를 사용하여 파서 소스코드 생성
java -cp ../javacc.jar javacc MiniJavaParser.jj

# 2. 패키지 전체 컴파일 (UTF-8 인코딩)
javac -encoding UTF-8 *.java syntaxtree/*.java visitor/*.java

# 3. 예제 8개 일괄 파싱·AST 빌드·재출력 검증
for %f in (..\programs\*.java) do @(echo ===== [%~nxf] ===== & java MiniJavaParser "%f")
```
