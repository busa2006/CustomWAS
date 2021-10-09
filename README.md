# 개발 환경
- window 10
- java 8
- junit 4
- maven 3.8

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
   
# 스펙 구현 여부
1. 일부 구현
2. 구현
3. 구현
4. 구현
5. 구현
6. 일부 구현
7. 구현
8. 구현

# 1. HTTP/1.1 의 Host 헤더를 해석하세요. - 일부 구현
- 예를 들어, a.com 과 b.com 의 IP 가 같을지라도 설정에 따라 서버에서 다른 데이터를
제공할 수 있어야 합니다.
- 아파치 웹 서버의 VirtualHost 기능을 참고하세요.
```
(정확한 의도 파악 못함)
request -> GET /nonExistPath HTTP/1.1
           Host: a.com
           
response -> HTTP/1.1 404

            <h1> a 404!</h1>
            
request -> GET /nonExistPath HTTP/1.1
           Host: b.com
           
response -> HTTP/1.1 404

            <h1> b 404!</h1>

```
# 2. 다음 사항을 설정 파일로 관리하세요. - 구현
- 파일 포맷은 JSON 으로 자유롭게 구성하세요.
- 몇 번 포트에서 동작하는지
- HTTP/1.1 의 Host 별로
  - HTTP_ROOT 디렉터리를 다르게
  - 403, 404, 500 오류일 때 출력할 HTML 파일 이름
```
src/main/resources/properties.json에 명시된 설정으로 적용
예시)
{
  "port": "80",
  "hosts":{
  	"default":{
  	 "root":"",
  	 "403":"403.html",
  	 "404":"404.html",
  	 "500":"500.html"
  	},
  	"a.com":{
  	 "root":"/a",
  	 "403":"403.html",
  	 "404":"404.html",
  	 "500":"500.html"
  	},
  	"b.com":{
  	 "root":"/b/c",
  	 "403":"403.html",
  	 "404":"404.html",
  	 "500":"500.html"
  	}	
  }
}
```
# 3. 403, 404, 500 오류를 처리합니다. - 구현
- 해당 오류 발생 시 적절한 HTML 을 반환합니다.
- 설정 파일에 적은 파일 이름을 이용합니다.
```

request -> GET /file.exe HTTP/1.1
           Host: a.com
           
response -> HTTP/1.1 403

            <h1> a 403!</h1> //403.html의 내용
            
request -> GET /nonExistPath HTTP/1.1
           Host: a.com
           
response -> HTTP/1.1 404

            <h1> a 404!</h1> //404.html의 내용

request -> GET /IntendedError HTTP/1.1
           Host: a.com
           
response -> HTTP/1.1 500

            <h1> a 500!</h1> //500.html의 내용

```
# 4. 다음과 같은 보안 규칙을 둡니다. - 구현
- 다음 규칙에 걸리면 응답 코드 403 을 반환합니다.
  - HTTP_ROOT 디렉터리의 상위 디렉터리에 접근할 때,
예, http://localhost:8000/../../../../etc/passwd
  - 확장자가 .exe 인 파일을 요청받았을 때
- 추후 규칙을 추가할 것을 고려해주세요.
```

// root "/b/c"

request -> GET /b/c/file.exe HTTP/1.1
           Host: b.com
           
response -> HTTP/1.1 403

            <h1> b 403!</h1> //403.html의 내용
            
request -> GET /b/a.html HTTP/1.1
           Host: b.com
           
response -> HTTP/1.1 403

            <h1> b 403!</h1> //403.html의 내용

```

# 5. logback 프레임워크 http://logback.qos.ch/를 이용하여 다음의 로깅 작업을 합니다. - 구현
- 로그 파일을 하루 단위로 분리합니다.
- 로그 내용에 따라 적절한 로그 레벨을 적용합니다.
- 오류 발생 시, StackTrace 전체를 로그 파일에 남깁니다.
```

src/main/resources/logs/file에 로그 파일 저장
예시) 
파일명 : was.log.2021-08-27.log
오류 발생시 :
2021-08-27 20:29:07.242 [pool-1-thread-2] ERROR com.dooray.was.RequestProcessor - FileNotFoundException 
java.io.FileNotFoundException: [경로] (지정된 파일을 찾을 수 없습니다)
	at java.base/java.io.FileInputStream.open0(Native Method)
	at java.base/java.io.FileInputStream.open(FileInputStream.java:219)
	at java.base/java.io.FileInputStream.<init>(FileInputStream.java:157)
	at java.base/java.io.FileInputStream.<init>(FileInputStream.java:112)
	at java.base/java.io.FileReader.<init>(FileReader.java:60)
	at com.dooray.was.response.HttpResponse.getFile(HttpResponse.java:52)
	at com.dooray.was.response.HttpResponse.returnFile(HttpResponse.java:33)
	at com.dooray.was.RequestProcessor.run(RequestProcessor.java:82)
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)

```
# 6. 간단한 WAS 를 구현합니다. - 일부 구현
- 다음과 같은 SimpleServlet 구현체가 동작해야 합니다.
  - 다음 코드에서 SimpleServlet, HttpRequet, HttpResponse 인터페이스나
객체는 여러분이 보다 구체적인 인터페이스나 구현체를 제공해야 합니다. 표준
Java Servlet 과는 무관합니다.
```
public Hello implements SimpleServlet {
  public void service(HttpRequest req, HttpResponse res) {
    java.io.Writer writer = res.getWriter()
    writer.write("Hello, ");
    writer.write(req.getParameter("name"));
  }
}
```
- URL 을 SimpleServlet 구현체로 매핑합니다. 규칙은 다음과 같습니다.
  - http://localhost:8000/Hello --> Hello.java 로 매핑
  - http://localhost:8000/service.Hello --> service 패키지의 Hello.java 로 매핑
- 과제는 URL 을 바로 클래스 파일로 매핑하지만, 추후 설정 파일을 이용해서 매핑하는 것도
고려해서 개발하십시오.
  - 추후 확장을 고려하면 됩니다. 설정 파일을 이용한 매핑을 구현할 필요는
없습니다.
  - 설정 파일을 이용한 매핑에서 사용할 수 있는 설정의 예, {“/Greeting”: “Hello”,
“/super.Greeting”: “service.Hello”}
```

(정확한 의도 파악 못함)
확장성을 고려해 
reflection으로 요청mapping 


request -> GET /Hello?name=nam HTTP/1.1
           Host: a.com
           
response -> HTTP/1.1 200

            Hello, nam
            
request -> GET /service.Hello?name=nam HTTP/1.1
           Host: a.com
           
response -> HTTP/1.1 200

            Hello, nam

```
# 7. 현재 시각을 출력하는 SimpleServlet 구현체를 작성하세요. - 구현
- 앞서 구현한 WAS 를 이용합니다.
- WAS 와 SimpleServlet 인터페이스를 포함한 SimpleServlet 구현 객체가 하나의 JAR 에
있어도 괜찮습니다.
  - 분리하면 더 좋습니다.
```

request -> GET /NowTime HTTP/1.1
           Host: a.com
           
response -> HTTP/1.1 200

            2021/08/28 02:11:11

```
# 8. 앞에서 구현한 여러 스펙을 검증하는 테스트 케이스를 JUnit4 를 이용해서 작성하세요. - 구현
```
src/test/java에 Spec1Test ~ Spect7Test 작성

```
