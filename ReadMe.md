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

### Java Socket Server

- NIO (Blocking)
- ThreadPool

### 멀티룸구조

RoomManager

```
RoomManager -> Room(A)  -> Client(A)
                        -> Client(B)
                        -> Client(C)
            -> Room(B)  -> Client(D)
                        -> Client(E)
                        -> Client(F)
            -> Room(C)  -> Client(G)
                        -> Client(H)
                        -> Client(I)
```

ClientManager

```
ClientManager -> Client(A)
              -> Client(B)
              -> Client(C)
              -> Client(D)
              -> Client(E)
              -> Client(F)
              -> Client(G)
              -> Client(H)
              -> Client(I)
```

### 추가기능

- 채팅방 비밀번호 기능 (서버에서비교처리)
- 방장기능 강퇴등등
- 실시간 인원수 확인기능 
- 블로킹서버와 넌블로킹서버의 비교


---

**Created by SDM**

==작성자 정보==

e-mail :: jm921106@naver.com || jm921106@gmail.com

github :: https://github.com/moonscoding
