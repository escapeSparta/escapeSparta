
🏆Escape Sparta
=============
미래 지향적인 방탈출 카페 예약 서비스를 하고 있습니다.<br><br>
4개월간 기본기를 다져온 실력을 기반으로 새로운 기술을 도입해보고자 도전적인 마음으로 프로젝트를 설계하게 되었습니다<br><br>
자세한 구현 내용은 README 흐름에 따라 천천히 소개하겠습니다.<br><br>

## 📑 프로젝트의 전체적인 구조
 // System Architecture image
- github hook을 받아 Jenkins에서 CI/CD를 진행합니다.
- 모든 서버는 AWS EC2에 올라가 있습니다.<br>
- 구동중인 서버  Kafka Server, Redis Server, MySQL Server


## 📑 프로젝트의 주요 관심사
<b>공통사항</b><br>
- 챌린지적인 마인드로 새로운 기술스택 도입을 통한 대규모 트래픽을 가정한 대용량 처리
- 유지보수 가능한, 확장성이 높은 코드 구현목표
<br><br>
<b>📑 코드 컨벤션</b><br>
- Google code Style을 준수
- 팀 자체에서 협의한 코드 컨벤션, 구글링을 통해 가장 통용되는 코드 컨벤션을 유지 
- 팀 내에서 코드리뷰를 적극적으로 참여하며 최소한 2명의 코드리뷰를 받기 전 까지는 merge x
<br><br>
<b>📑 성능 최적화</b><br>
- 서버 부하를 줄이기 위해 캐싱(Redis) 적극 활용
- DB서버와의 통신을 최소화(당연한 이야기지만 N+1 쿼리를 지양)
- 인덱스와 쿼리 튜닝을 활용
- 비동기를 활용하여 빠른 시간 내에 외부 API 호출
<br><br>

<br><br>

### ✏️ 브랜치 관리 전략
Git Flow를 사용하여 브랜치를 관리합니다.<br>
모든 브랜치는 Pull Request에 리뷰를 진행한 후(최소 2명) merge를 진행합니다.<br>
메인 브렌치인 Develop에는 아직 많은 내용이 merge되지 않았습니다. 현재 개발 진행사항을 확인하고 싶다면 PR를 확인해주세요.<br><br>
EscapeSparta PR : [https://github.com/escapeSparta/escapeSparta/pulls]
<br>
<br><br>
![image](https://user-images.githubusercontent.com/46917538/72450182-44475300-37fd-11ea-8a1b-ecce20fd6fcb.png)
<br><br>
- main : 최종 배포시 사용되는 브랜치 입니다.
- develop : 완전히 개발이 끝난 부분에 대해서만 Merge된 브랜치 입니다.
- feature : 기능 개발을 진행할 때 사용합니다.
- refactor : 리팩토링 진행시 사용하는 브랜치 입니다.
- fix,chore : 중요도가 떨어지는, 긴급한 수정이 필요할 때 사용되는 브랜치 입니다.
<br><br>
<b>브랜치 관리 전략 참고 문헌</b><br><br>
- 우아한 형제들 기술 블로그(http://woowabros.github.io/experience/2017/10/30/baemin-mobile-git-branch-strategy.html)<br><br>
- Bitbucket Gitflow Workflow(https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)

### 테스트

추가 구현예정 입니다.

### 성능 테스트

추가 구현예정 입니다.


## 사용 기술 및 환경
Java 17, Spring boot 3.1.2, Spring Security, Gradle, JPA, Hibernate, Redis, MySQL, Redis, Docker, Jenkins
<br>

## 트러블 슈팅

현재는 종합되지 않았습니다. 팀 회의를 거쳐 트러블 슈팅을 종합적으로 검토하여 수정할 예정입니다.

<br>
## CI
Jenkins : 젠킨스를 이용하여 서버 운영을 하고 있습니다. <br>
PR시마다 자동 Build 및 Test 적용<br>
비로그인 상태로도 확인이 가능합니다.<br>

## CD
Docker 이미지를 제작하여 배포합니다.<br>
CI 서버에서 빌드 완료시 Shell script가 작동하여 빌드된 이미지가 docker hub에 저장됩니다.<br>
Push 완료시 EscapeSparta 메인 서버에서 docker hub에 올라간 이미지를 받아 실행시킵니다.<br>


<br>

## Database
- MySQL
EscapeSpara에 중요한 정보들을 담고 있습니다.
- Redis<br>
서버의 부하를 줄이기 위해 certificateCode,refreshToken을 Redis에서 관리하고 있습니다.
<br>

## 클라이언트 화면 설계
![image](https://github.com/user-attachments/assets/24a60887-b962-4fb7-91d1-3c85b3854e26)

## 사장님 화면

(추가 구현예정)

## 프로젝트 DB ERD
2024-08-02 (수정예정)
![image](https://github.com/user-attachments/assets/f4540f47-7f7f-48d7-a11c-204e73d59aab)

