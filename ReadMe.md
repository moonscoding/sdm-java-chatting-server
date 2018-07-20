# Java
## Chatting Socket Server
<div class="pull-right">  업데이트 :: 2018.07.19 </div><br>

---

<!-- @import "[TOC]" {cmd="toc" depthFrom=1 depthTo=6 orderedList=false} -->
<!-- code_chunk_output -->

* [Java](#java)
	* [Chatting Socket Server](#chatting-socket-server)
		* [Java Socket Server](#java-socket-server)
		* [멀티룸구조](#멀티룸구조)

<!-- /code_chunk_output -->

### 환경

##### 구현환경

- Mac OS X
- Java 1.8
- IntelliJ

##### 라이브러리

- [json-parser]()

### NIO - Blocking 방식

블로킹방식은 언제 데이터가 보내질지 모르기때문에 "accept()"와 "read()"에서 블로킹됩니다.

그래서 스레드를 할당해야 합니다. 이런 방식을 해결하기 위해서 스레드풀을 사용하지만,

연결된 client마다 "read()"에 스레드를 할당해야 한다는 점은 변하지 않습니다.

> [NonBlocking Chatting 프로젝트 바로가기](https://github.com/moonscoding/sdm-java-chatting-server-nonblocking)


---

**Created by SDM**

==작성자 정보==

e-mail :: jm921106@naver.com || jm921106@gmail.com

github :: https://github.com/moonscoding
