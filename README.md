## 🧑‍💻 Back Team

- BE: 김채민, 유성진

## 🌱 브랜치 전략

```bash
- main
- develop

- `main` : 배포 가능한 안정화 버전  
- `develop` : 다음 배포를 위한 통합 브랜치  
- `feature/*` : 기능 단위 작업용 브랜치  
```


## 📐 코드 컨벤션

### 🗂 도메인 기반 패키지 구조

```java
└── global
    ├── config
    ├── exception
    └── s3
    ...

└── domain(기능명)
    └── api
        ├── controller
        └── dto
            ├── request
            │   └── {어떤 dto인지}ReqDto
            └── response
                └── {어떤 dto인지}ResDto
    └── application
        └── service
    └── domain
        ├── repository
        │   └── Repository
        └── entity
```


## 🔗 API 엔드포인트 네이밍

- **기능**: `/quest/list`  
- **파라미터**: `{quest-id}`

## 🏷 Issue 네이밍 규칙

- 기능 추가 : `feat`
- 버그 수정 : `fix`
- 리팩토링 : `refactor`
- 문서 작업 : `docs`
- 코드 및 문서 수정 : `chore`
- CI/CD : `devops`


📌 이슈 이름 예시:  
`[Feat] 작업 내용`

## 📄 PR Template

```

📌 PR 개요
- 

🔧 작업 내용
- 

📷 테스트
- 

❗추가 내용
- 

```


## ✍️ 커밋 규칙

- feat: 로그인 API 구현

## 🛠 Backend

### 🛡 Security
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?logo=springsecurity&logoColor=white)  
![JWT](https://img.shields.io/badge/JWT-000000?logo=jsonwebtokens&logoColor=white)  
![CORS](https://img.shields.io/badge/CORS-FF6F00?logo=icloud&logoColor=white)  
![HTTPS](https://img.shields.io/badge/HTTPS-0052CC?logo=letsencrypt&logoColor=white)  

### 🧮 Database
![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white)  
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-59666C?logo=spring&logoColor=white)  
![Redis](https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white)  

### ☁️ Infra & Deployment
![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?logo=amazonaws&logoColor=white)  
![Nginx](https://img.shields.io/badge/Nginx-009639?logo=nginx&logoColor=white)   

### 🧑‍🔧 Dev Tools
![Java](https://img.shields.io/badge/Java-007396?logo=java&logoColor=white)  
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=white)  

### 🔄 CI/CD & Collaboration
![GitHub](https://img.shields.io/badge/GitHub-181717?logo=github&logoColor=white)  
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?logo=githubactions&logoColor=white)  

## 🏛 Architecture
<img width="804" height="579" alt="image" src="https://github.com/user-attachments/assets/0bc6eb9a-4a31-487f-8ebd-d2fd010ba679" />

