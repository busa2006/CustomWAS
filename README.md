# 실행 순서
1. 압축 해제
2. 압축 해제 경로로 이동
3. mvn clean package (JUnit 수행)
4. target 경로로 이동
5. java -jar was.jar ../(파라미터는 root 경로)
6. http://localhost/Hello?name=nam     
   http://localhost/Hello.html  
   http://localhost/file.exe  
   http://localhost/nonExistPath  
   http://localhost/IntendedError  
   
