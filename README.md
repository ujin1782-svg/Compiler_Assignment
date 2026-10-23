# Compiler_Assignment01

1.minijava_ast_project1에 javacc.jar 설치
'''
curl -L -o ..\javacc.jar https://repo1.maven.org/maven2/net/java/dev/javacc/javacc/7.0.13/javacc-7.0.13.jar
'''

2.build & 8개 예제 일괄 실행
'''
java -cp ../javacc.jar javacc MiniJavaParser.jj<br>
javac -encoding UTF-8 *.java syntaxtree/*.java visitor/*.java
'''
