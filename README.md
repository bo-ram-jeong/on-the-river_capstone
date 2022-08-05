<p align="right">
<a href="https://github.com/bo-ram-jeong/on-the-river_capstone"><img src="https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fbo-ram-jeong&count_bg=%23F12793&title_bg=%23171617&icon=github.svg&icon_color=%23E7E7E7&title=GitHub&edge_flat=false)"/></a>
</p>
</br>
<p align="center"><img src="https://user-images.githubusercontent.com/84834172/183089231-3dfb8170-101c-485b-b6c6-1b3e09a623f6.png" alt="온더리버 로고" width=130px /></p>
<p align="center"><img src="https://user-images.githubusercontent.com/84834172/183088508-0195e6fa-762c-48d8-8f37-0fef31256cdd.png" alt="온더리버 텍스트로고" width=250px /></p>

## Table of Contents
1. [About The Project](#about-the-project)
    - [Built With](#built-with)
2. [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
3. [Contact](#contact)
4. [Acknowledgments](#acknowledgments)

## About The Project
[(back to top)](#table-of-contents)<br/><br/>
<img src="https://user-images.githubusercontent.com/84834172/183121502-5863535b-639a-45b7-b295-f670483f5030.png" alt="온더리버 앱 화면1" />
<img src="https://user-images.githubusercontent.com/84834172/183121508-5a3e4abf-1da3-4cc3-8dff-7c05ce68c5cf.png" alt="온더리버 앱 화면2" />
<img src="https://user-images.githubusercontent.com/84834172/183122645-29f592b0-8f3f-4ae1-beb5-08d9cd18de83.png" alt="온더리버 앱 화면3" />

> <a href="https://cafe.naver.com/mjcs337">명지전문대 캡스톤 경진대회</a>에서 총 5명의 팀원구성으로 진행한 프로젝트로 [AR 게임을 이용한 running App] 을 개발하였습니다.

[기획 배경]
- 코로나19로 인해 개방된 공간인 한강을 운동 장소로 찾는 사람들 증가
- 사람들의 건강에 대한 관심은 확대 → 그러나 운동 입문자들의 동기부여를 만들기 어려움
- 게임과 운동의 관계에서의 긍정적 기대효과

[기획 목적]
- 운동에 관심이 없던 사람들을 유입, 이후 지속적인 참여를 유도함으로써 운동 활동을 촉진
- 서울이라는 도시의 매력 중 하나인 한강에 대한 적극적 활용

[기능]
- tracking 앱의 기본 기능들 (GPS, 경로 추적, 기록 측정 등) 제공
- ‘여의도 한강 시민공원’ 기준,  일정 지점 혹은 장소마다 AR 활용 미션 제공
- 미션 장소에 도착해 운동데이터 기록 / AR 미션 수행
- 장소 별 운동기록, 개인 수집 요소 등에 따른 순위 경쟁 서비스
- 개인 운동데이터 / 미션데이터를 통해 각 항목에 대한 일정 수준 도달 시 배지 수여
- 사용자의 기록이나 배지 공유 가능(SNS)

[내가 개발한 부분]
  - logo design
  - splash screen
  - [My Page] 나의 활동
  - [My Page] 나의 배지
  - [My Page] AR 순위
  - [My Page] 설정
	
### Built With
<a href="https://developer.android.com/?hl=ko"><img src="https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=Android&logoColor=white"/></a>
<a href="https://developer.android.com/studio?gclid=CjwKCAjw3K2XBhAzEiwAmmgrApuGSt607re8P5ghbZZFyMB4FnREisCQPJNChxWbvRAU0QCSrQz2GBoCtYUQAvD_BwE&gclsrc=aw.ds"><img src="https://img.shields.io/badge/Android Studio-3DDC84?style=flat-square&logo=Android Studio&logoColor=white"/></a>
<br/>
<a href="https://www.java.com/ko/"><img src="https://user-images.githubusercontent.com/84834172/182914035-4bd5d509-cf68-40ba-a641-5c1bf76fc5d9.svg"/></a>
<a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=Kotlin&logoColor=white"/></a>
<br/>
<a href="https://firebase.google.com/?hl=ko&gclid=CjwKCAjw3K2XBhAzEiwAmmgrAmKmQPnuGPgCoOKuVQyW-5iqhSE9MIsh96di7zZCR5qvgTdqAiCkdRoC9JQQAvD_BwE&gclsrc=aw.ds"><img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=Firebase&logoColor=black"/></a>
<br/>
<a href="https://www.figma.com//"><img src="https://img.shields.io/badge/Figma-F24E1E?style=flat-square&logo=Figma&logoColor=white"/></a>


## Getting Started
[(back to top)](#table-of-contents)<br/>

### Installation 
download
1. the java jdk
<a href="https://devyurim.github.io/development%20environment/java/2018/05/02/java-1.html">→ here</a>
2. Android Studio
<a href="https://developer.android.com/studio/install?hl=ko">→ here</a>
3. Git
<a href="http://git-scm.com/download/win">→ here</a>

###  SetUp
1. Clone the repo<br/>
```git clone https://github.com/bo-ram-jeong/on-the-river_capstone.git```

2. Execution(Android-Studio 설치됨 가정)

```  
[Emulator 실행]
- Android Studio에서 Emulator가 앱을 설치하고 실행하는데 사용할 수 있는 Android Virtual Device(AVD)를 만든다
- Toolbar의 실행/디버그 구성 dropdown에서 앱을 선택
- 대상 기기 dropdown에서 앱을 실행하려는 AVD를 선택
→ AVD run(실행)
```

## Usage
[(back to top)](#table-of-contents)<br/>
### Demo

## Contact
[(back to top)](#table-of-contents)<br/>

정보람(Jeong Bo Ram)
<br/><br/>
[![Tech Blog Badge](http://img.shields.io/badge/-Git%20Hub-black?style=flat-square&logo=github&link=https://github.com/bo-ram-jeong)](https://github.com/bo-ram-jeong)
[![Tech Blog Badge](http://img.shields.io/badge/-Tech%20blog-black?style=flat-square&logo=github&link=https://bo-ram-jeong.github.io/)](https://bo-ram-jeong.github.io/)
[![Gmail Badge](https://img.shields.io/badge/boram33377@gmail.com-d14836?style=flat-square&logo=Gmail&logoColor=white&link=mailto:boram33377@gmail.com)](mailto:boram33377@gmail.com)
[![Naver Badge](https://img.shields.io/badge/brj34@naver.com-03C75A?style=flat-square&logo=Naver&logoColor=white&link=mailto:brj34@naver.com)](mailto:brj34@naver.com)


## Complement
[(back to top)](#table-of-contents)<br/>

## Acknowledgments
[(back to top)](#table-of-contents)<br/>
- <a href="https://www.data.go.kr/tcs/dss/selectDataSetList.do?keyword=%EA%B8%B0%EC%83%81%EC%B2%AD">기상청 - 공공데이터포털</a>
- <a href="https://developers.naver.com/docs/login/api/api.md">Naver developers</a>
- <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api">kakao developers</a>


