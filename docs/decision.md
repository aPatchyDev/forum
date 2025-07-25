# 개발 배경

개발 중 선택한 방식에 대한 배경 / 이유를 기록하기 위한 문서

## REST API

### Authentication

로그인을 RESTful하게 구현하기 위해 API endpoint를 어떻게 명명할지에 대한 질문글은 많지만 명확한 답은 없다.  
주로 사용하는 패턴은 다음과 같다:

| Login | Logout |
| --- | --- |
| POST /login | POST /logout |
| POST /sessions | DELETE /sessions/current |

첫 번째는 매우 직관적이지만 URI에 동사를 사용한다.  
REST의 기본 원칙은 리소스에 접근하는 것이라 명사만 사용할 것을 권장한다는 점에서 동사를 사용하는 이 방법은 완벽히 RESTful하지 않다.

두 번째는 리소스를 지칭하지만 세션을 API 디자인에 직접적으로 노춣시킨다.  
RESTful API는 stateless를 지향하기 때문에 서로 다른 요청 간 상태를 보존해야 하는 세션은 RESTful하지 않다는 의견이 있다.  

그러나 세션은 JWT / OAuth 등 토큰 방식으로 stateless하게 구현할 수도 있고, API에 보내는 요청들은 stateless하다고 볼 수도 있다.  
또한, RESTful하지 않다고 하더라도 실용성을 따져 필요한 만큼만 REST 원칙을 따르는 타협안으로 해석해도 괜찮다고 생각한다.  
어차피 세션 자체에 문제를 삼는다면 두 방식 모두 문제가 있고 유일한 해결책은 매 요청마다 인증을 해야 되기 때문에 비효율적이다.

그래서 이 프로젝트에서는 그나마 REST에 가까운 두 번째 방식을 채택했다.

## Spring Boot 프로젝트 초기 설정

### 패키지 구조

대표적으로 계층형과 도메인형 구조가 있다.

- 계층형 구조
    - controller
        - ControllerA
        - ControllerB
        - ...
    - service
        - ServiceA
        - ServiceB
        - ...
    - repository
        - EntityA
        - RepositoryA
        - EntityB
        - RepositoryB
        - ...
- 도메인형 구조
    - A
        - controller
        - service
        - repository
    - B
        - controller
        - service
        - repository
    - ...

패키지 / 모듈을 나누는 것은 캡슐화의 경계를 긋는 것이다. 따라서 되도록 서로 연관된 코드만 같은 패키지로 묶고 visibility를 제일 좁게 설정하는 것이 바람직하다.

계층형 구조는 프로젝트 전반적인 구조를 파악하기에는 쉽지만 코드의 응집도가 떨어진다.
일반적으로 코드는 같은 계층보다는 같은 도메인끼리 연관되어 있기 때문이다. 따라서 캡슐화가 잘 이루어지지 않는다. 게다가 도메인이 증가할수록 패키지의 파일들이 늘어나 원하는 코드를 찾기 어려워진다.

이러한 이유로 이 프로젝트는 도메인형 구조를 채택했다.

### JSpecify Nullability

Java는 기본적으로 모든 객체 타입이 nullable하다. Null safety가 중요시되기 이전부터 발전한 언어라 다른 현대적인 언어보다 null safety에 관한 언어 차원의 안전장치가 부족했었다.

이를 보완하기 위해 각종 라이브러리들이 `@NonNull`과 같은 검증용 annotation을 만들었지만 통일되지 않았고 모든 변수에 전부 추가하기 번거로웠다. 그래서 이를 해결하기 위해 JSpecify가 만들어졌다.

사실 다른 라이브러리보다 JSpecify를 선택한 이유는 한 가지다. 패키지 / 클래스 단위로 `@NullMarked`를 붙이면 그 안의 코드는 기본이 non-null이 된다. `@NullMarked`로 기본값을 변경하고 `@Nullable`만 필요한 1~2개에 추가하는 것이 `@NonNull` 10개를 붙히는 것보다 훨씬 편리하고 null-safety를 놓치는 실수를 줄일 수 있다.

단순히 annotation만 있다면 IDE hint밖에 받지 못하지만 `uber`에서 만든 `NullAway`를 사용하면 컴파일하며 annotation을 검증해주기 때문에 nullable 타입을 지원하는 다른 언어처럼 개발할 수 있다.
