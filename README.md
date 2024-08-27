# 🗝️ Escape Sparta 🗝️


![title](https://synge-st.notion.site/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F893b8654-e780-476c-b862-9eb07e2ddfa7%2Fde24d517-aa16-411e-920a-34fcd7c50af0%2F002.png?table=block&id=bf34a9cd-e423-47da-a9b3-080f141efbf0&spaceId=893b8654-e780-476c-b862-9eb07e2ddfa7&width=1420&userId=&cache=v2)   

📌  [도메인 바로가기](https://www.escapesparta.com)   
📌  [프론트엔드 바로가기](https://github.com/escapeSparta/escapeSpartaFront)   
📌  [시연영상 바로가기](https://youtu.be/3Ei7E6TrH5s?si=439pfSRhYFvLZKFb)

      개발 기간 : 2024.07.16 ~ 2024.08.21
      배포 기간 : 2024.08.15 ~ 2024.08.19

# 👋 프로젝트 소개

**Escape Sparta** 는 전국의 방탈출 카페를 모아보고, 원하는 테마의 예약을 진행할 수 있는 중개 사이트입니다

![title](https://synge-st.notion.site/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F893b8654-e780-476c-b862-9eb07e2ddfa7%2F40d3735b-d094-4cf5-9541-2441e61a49d3%2FScreenshot_2024-08-15_at_16.27.22.jpg?table=block&id=09ceeb21-f694-4f0a-9e9f-3ba4b9cb8575&spaceId=893b8654-e780-476c-b862-9eb07e2ddfa7&width=2000&userId=&cache=v2)   


### 주요기능
    - 예약 수가 많은 인기 방탈출 카페를 조회할 수 있습니다.
    - 방탈출 카페를 지역 별로 모아보고, 키워드를 사용해 검색할 수 있습니다.
    - 방탈출 테마를 예약하고, 카카오페이로 결제를 진행할 수 있습니다.
    - 프로필을 확인하고, 수정할 수 있습니다.
    - 본인의 예약 내역을 확인하고, 예약을 취소할 수 있습니다.
    - 완료된 테마 예약에 대해 리뷰를 작성할 수 있습니다.
    - 관심이 가는 방탈출 카페를 팔로우할 수 있습니다.



### 구현기능
<details><summary> 공통기능
</summary>

- 회원가입
- 이메일 인증
- 회원탈퇴
- 로그인
- 로그아웃
- 방탈출 카페 조회
- TOP4 방탈출 카페 조회
- 방탈출 카페 테마 조회
- 방탈출 카페 시간 조회
- 리뷰 조회
- 예약내역 조회
- 팔로우 내역 조회

</details>

<details><summary> Admin 기능 : 웹사이트 총 관리자
</summary>

- 방탈출 카페 등록(허가상태)
- 방탈출 카페 수정
- 방탈출 카페 삭제
- 방탈출 카페 테마 등록
- 방탈출 카페 테마 수정
- 방탈출 카페 테마 삭제
- 방탈출 카페 테마 시간 등록
- 방탈출 카페 테마 시간 수정
- 방탈출 카페 테마 시간 삭제
- 리뷰 삭제
</details>


<details><summary> Manager 기능 : 방탈출 카페 점주 
</summary>

- 방탈출 카페 등록(비 허가상태)
- 본인의 방탈출 카페 조회
- 방탈출 카페 수정
- 방탈출 카페 삭제
- 방탈출 카페 테마 등록
- 방탈출 카페 테마 수정
- 방탈출 카페 테마 삭제
- 방탈출 카페 테마 시간 등록
- 방탈출 카페 테마 시간 수정
- 방탈출 카페 테마 시간 삭제
</details>

<details><summary> Consumer 기능 : 방탈출 카페 이용자
</summary>

- 방탈출 예약
- 방탈출 예약 취소
- 결제(카카오페이)
- 환불
- 프로필 조회
- 프로필수정
- 방탈출 팔로우
- 방탈출 팔로우 취소
- 리뷰 작성
- 리뷰 수정
- 리뷰 삭제
- 내가 쓴 리뷰 조회
</details>



## ⚙️ 시스템 아키텍쳐
![S.A](https://synge-st.notion.site/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F893b8654-e780-476c-b862-9eb07e2ddfa7%2F3aa0846d-ebe6-457e-870c-116737e4acff%2Fimage.png?table=block&id=dbc300b7-671f-44cf-9e64-c9a85fddbe7e&spaceId=893b8654-e780-476c-b862-9eb07e2ddfa7&width=2000&userId=&cache=v2)   


## ⚙️ ERD
![title](https://teamsparta.notion.site/image/https%3A%2F%2Fprod-files-secure.s3.us-west-2.amazonaws.com%2F83c75a39-3aba-4ba4-a792-7aefe4b07895%2F4fca7ded-cfa8-4446-bbba-daf20eb9da01%2FEscape_Sparta.png?table=block&id=e15ab8b9-4166-43b5-89bd-b0bebe0ec883&spaceId=83c75a39-3aba-4ba4-a792-7aefe4b07895&width=2000&userId=&cache=v2)   


## 🛠️ 기술스택

#### 백엔드
- Java 17
- Spring Boot 3.1.2
- Gradle
- Spring Security
- JWT
- MySQL
- Redis
- Kafka
- kakaopayment
- Google SMTP

#### 프론트엔드
- Vue
- node.js
- Nginx

#### 인프라
- Git Actions
- Docker
- EC2
- S3
- RDS
- Vercel


#### 협업도구
- Slack
- ZEP
- Notion
- Figma
- Erd Cloud
- Postman
- Github



## 👨‍💻👩‍💻👩‍💻👨‍💻 팀원소개

| 김윤재 | 박세미 | 이서연 | 박성균 |
| --- | --- | --- | --- |
| <img src="https://avatars.githubusercontent.com/u/163832566?v=4" width="200" />  | <img src="https://avatars.githubusercontent.com/u/69128154?s=400&v=4" width="200" /> | <img src="https://avatars.githubusercontent.com/u/152502639?v=4" width="200" /> | <img src="https://avatars.githubusercontent.com/u/102494118?v=4" width="200" />   
| [@backendINFJ](https://github.com/backendINFJ)| [@Sem](https://github.com/tpal0719) | [@Tichall](https://github.com/tichall)| [@tjdrbs0712](https://github.com/tjdrbs0712)|
