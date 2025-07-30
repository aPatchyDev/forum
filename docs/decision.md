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

## API 설계

### Backend DTO Nested Record Class

DTO는 오직 데이터만을 담기 위한 클래스이므로 record를 사용하는 것이 가장 적절하다.

그러나 5줄도 안 되는 코드를 별개의 파일로 놓으면 한 눈에 잘 안 보인다. 그래서 하나의 파일에 여러 record를 담고 싶은데 Java는 하나의 파일에 하나의 public 타입만을 허용한다. 이를 해결하기 위해 nested record를 사용했다.

```java
public final class DTO {
    private DTO() {} // 내부 static record를 하나의 public 타입으로 묶기 위한 컨테이너이므로 생성하지 못하게 private constructor 선언

    public record Request() {}
    public record Response(Object data) {}
}
```

Nested record는 implicitly static이다. 그러나 static 변수 / singleton처럼 실제 데이터가 생성되는 것이 아니라 타입 정의만 하고 있으므로 바깥 클래스는 단순 namespace 역할만 하고 있는 것이다. 아래의 C++ 코드 구조를 모방한 셈이다.

> Lombok으로 생성자를 대체할 수 있을까?
> ---
> @NoArgsConstructor(access = AccessLevel.PRIVATE)
>
> 실제 생성자를 작성하는 것보다 더 길다. 그래도 클래스 밖에 작성할 수 있어 클래스 내부의 구조체를 읽을 때 가독성은 좋다.
>
> @UtilityClass
>
> https://projectlombok.org/features/experimental/UtilityClass  
> Experimental + import에 제약이 걸린다.  
> 오류를 던지는 private 생성자 + final 클래스 + 내부의 모든 field / function / inner class에 static이 붙는다.  
> Javac의 한계로 wildcard 없이는 static import를 할 수 없다.
>
> Import에서 이름 충돌이 절대 발생하지 않도록 관리할 수 있다면 `@UtilityClass`가 좋겠지만 아직은 `@NoArgsConstructor`를 사용하는 게 더 좋아보인다.

```cpp
namespace DTO {
    struct Request {};
    struct Response {
        Object data;
    };
}

// using namespace DTO; -> import -.DTO.*;
```

물론, 너무 많은 record를 하나의 파일에 담으면 다시 가독성이 떨어진다. 그래서 클래스가 너무 복잡해지지 않게 도메인에 따라 관련있는 DTO만 묶어야 한다.  도메인형 패키지 구조를 채택한 것에서 그치지 않고 필요에 따라 더 세부적으로 나눌 수도 있다.

예를 들어 도메인이 사용자 계정이라고 해도 `가입 / 탈퇴 / 계정 정보 변경` 등 관리에 관한 DTO와 `친구 / 구독` 등 사용자 간 관계에 관한 DTO로 나눌 수도 있는 것이다.

만약 여러 API의 DTO를 묶기 애매하다면 하나의 endpoint에 대한 요청과 응답만 묶어도 파일이 절반으로 줄어들 것이다.

### Timestamp 객체

Java는 시간을 관리하는 클래스가 여러 개 있다. [출처](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/time/package-summary.html)

- `java.util.Date`: 옛날 API (Java 8 이전부터)
- `java.time.*`: 최신 API (Java 8)
    - `Instant`: Unix time (ms)
    - `LocalDate`: 날짜만
    - `LocalTime`: 시간만
    - `LocalDateTime`: 날짜 + 시간
    - `ZonedDateTime`: 날짜 + 시간 + timezone

`java.time`을 선택해야 한다는 것은 당연하다. Immutability + thread-safety에 더 정돈된 API를 제공하기 때문이다.

그렇다면 게시글 / 댓글의 작성 및 수정 시간은 어떤 클래스를 사용해야 할까? 3가지 선택지가 있지만 사실 정답은 하나뿐이다.

1. 날짜 + 시간
    - 직관적이다
    - 2개 이상의 timezone이 섞이는 순간 모든 것이 꼬인다
        - 하나의 timezone으로 통일하면 임의로 (또는 배포 환경에 의존) timezone을 선택해야 한다
        - 비즈니스 로직이나 요구사항이 변경되어도 "기본값 timezone"을 추후 변경하기 어렵다
    - leap second를 지원하지 않는다
2. 날짜 + 시간 + timezone
    - 직관적이다
    - 1번의 timezone 문제가 없다
    - offset 계산이 어려워진다
        - 시점을 기록하는 이유가 시간상 순서를 파악하기 위함이므로 이는 큰 단점이다
    - leap second를 지원하지 않는다
3. Unix time
    - 위 2 방법의 문제점이 없다
    - 사용자에게 정보를 표시하기 전에 이해하기 쉬운 형식으로 먼저 변환해야 한다
        - unix time은 거의 표준처럼 보편적이기 때문에 문제가 될 리가 없다

약속 시간같은 좁은 맥락 내에서의 `시간`을 표현할 때는 LocalDateTime이나 ZonedDateTime을 쓰면 되지만 어떤 사건이 발생한 정확한 `시점`을 기록할 때에는 unix timestamp를 활용하는 것이 바람직하다. 따라서 `Instant` 클래스를 쓰는 것이 맞다.

### DTO 속성

DTO는 client와 실제로 주고 받을 데이터를 정의하기 때문에 객체의 속성은 API의 형상에 직접적으로 영향을 준다.  
언제 어떤 정보를 주고 받는지에 따라 client가 어떤 방식으로 상태를 관리해야 하는지, 특정 UI/UX를 구현하기 위해서는 어떤 순서로 API를 호출해야 하는지 등 시스템의 흐름과 개발자 경험이 달라진다.

만약 사용자에게 노출이 되는 정보만을 교환해도 된다면 그것만큼 이상적인 것은 없을 것이다. 네트워크를 오가는 데이터의 100%가 유용한 데이터이므로 오버헤드가 0이니까.  
하지만 지속되는 상태가 추가되고 중복 및 수정가능 데이터를 허용하게 되면 상태 및 데이터를 구분하기 위한 별도의 식별자가 필요해진다.

기본적으로 stateless인 HTTP 서비스에서 지속적인 상태가 필요해지는 경우는 로그인된 세션을 구현할 때다.  
그러나 쿠키 / 토큰 모두 요청 헤더에 담기므로 body의 구조체를 모델링하는 DTO와는 관련이 없다.

중복이나 수정이 가능한 데이터를 구분하기 위한 식별자는 피할 방법이 없다. 내용의 의미로는 식별이 되지 않거나 (중복) 접근성이 달라지기 (변조) 때문이다.  
하지만 사용자 입장에서 이 식별자는 서비스를 이용하는 데에 의미가 없기 때문에 내부 구현의 디테일이 새어나가는 `leaky abstraction`이라고 볼 수도 있다. 게다가 이런 식별자로 원치 않는 정보 유출이 발생할 수도 있으니 `최소 권한의 원칙`을 최대한 준수하기 위해서는 이런 부가적인 정보가 API를 통해 client에게 노출되는 것을 최소한으로 줄여야 한다.

> 왜 유일성이 성립해도 수정이 가능한 리소스에 별도의 식별자를 부여해야 하는가?
>
> `alice`라는 사용자가 `bob`으로 이름을 변경한다면  
> `/user/alice` -> `/user/bob`으로 바꾸기만 하면 식별자가 없어도 구분은 할 수 있다.
>
> 하지만 이런 시스템은 사칭에 취약해진다. 데이터의 참조 무결성이 깨진다.  
> 위 예시에서 다른 사용자가 `alice`로 이름을 바꾸면 `(구)alice`를 지칭하는 참조값이 (링크) 전혀 다른 대상을 지칭하게 된다.
>
> 참조 무결성을 보장하기 위해서 식별자는 유일성뿐만 아니라 불변성까지 충족해야 한다.
>
> ---
> 하지만 실제로는 참조 무결성이 덜 중요하거나 그 이상으로 사용자 편의성이 더 중요한 경우에는 그 위험을 감수하고 별도의 식별자 없이 수정이 가능한 리소스 경로를 그대로 사용하는 경우도 있다.
>
> Github의 경우, 한 번 사용된 아이디의 재사용을 허용하며, 이는 URL에도 적용된다. 여기에서는 사용자에게 노출되는 URL이 직관적인 것을 더 중요시한 것으로 보인다.  
>
> 웹 서비스보다 더 큰 개념에서는 도메인 이름도 편의성을 위해 참조 무결성을 포기한 것으로 해석할 수도 있다. 그리고 도메인 하이재킹, 사이버스쿼팅 같은 문제들이 이런 시스템의 취약점을 고스란히 보여준다.

> 식별자로 어떻게 정보가 유출되는가?
>
> 다음 상황을 가정해보자.
> - Sequential integer id로 식별자를 부여한다.
> - Client가 어떤 리소스를 요청했더니 식별자가 100이라는 답변을 받았다.
>
> Client는 이제 같은 종류의 리소스에 대해 최소한 99개가 서버에는 존재한다는 것을 알게 된다.  
> 즉, 특정 리소스 하나를 요청했는데 전체 리소스의 개수라는 추가 정보까지 유출해버린 것이다.
>
> 현재까지의 리소스 개수가 유출되는 것이 문제가 되는가?  
> 온라인 경매 플랫폼을 생각해보자.  
> 첫 입찰의 거래 id가 100이었는데 1주일 뒤에 더 높은 가격을 입찰한 사람이 있다고 가정해보자.  
> 다시 입찰했을 때의 거래 id가 102인 경우와 id가 1000인 경우 모두 거래의 개수를 통해 경매의 참여자 수를 간접적으로 유추할 수 있다. 이런 거래 식별자가 client에게 전달이 된다면 불공정 경쟁으로 이어질 수 있다.
>
> 식별자를 노출해야 하지만 추가 정보의 유출을 방지하고자 할 때는 UUID를 사용하면 된다.

게시판 시스템에서의 아래의 리소스들이 있다.

- 사용자 (계정)
    - 사용자명으로 식별 가능
- 게시글
    - 제목은 중복이 가능하므로 별도의 식별자 필요
- 댓글
    - 본문이 길고 중복이 가능하므로 별도의 식별자 필요

별도의 식별자가 필요한 리소스는 게시글과 댓글이다. 이 리소스들은 숨길 필요가 없기 때문에 순차 정수 ID를 부여해도 문제가 없다.

계정 정보 변경은 비밀번호로 재인증을 하여 client의 세션이 탈취당해도 계정을 보호할 수 있도록 설계했다.

### Request Validation

JSpecify를 쓰면 deserialization 과정에서 자동으로 null check가 들어가는 줄 알았는데 [Yaak](https://yaak.app/)로 실제 요청을 보내보니 null check가 전혀 이루어지지 않고 있었다.

JSpecify는 runtime에 동작하지 않는다는 것은 알았지만 컴파일 시점에서 null을 지정하는 코드가 없으면 차이가 없을 것이라 생각했다. 그러나 spring이 JSpecify로 옮겨가고는 있지만 spring boot 3에서는 아직 완전히 전환을 마친 것이 아니며 spring boot 4가 나오더라도 런타임 DTO validation에는 도움이 되지 못 할 것으로 보인다.

호기심에 Kotlin Spring으로 non-nullable 타입을 사용하면 어떻게 되나 확인해보니 null check는 바로 적용되었다. 그러나 단순 null check 이상의 제약 검증은 여전히 Bean validation을 해야 되기 때문에 실질적으로는 큰 차이가 나지 않을 것 같다.

하지만 Bean validation은 주로 요청에 대해서만 검증을 하기 때문에 앞써 여러 DTO를 같은 파일에 묶었을 때 일관성이 떨어진다. 복합 객체의 경우, 어떤 객체가 요청에만 관련이 있는지 헷갈린다. 그래서 요청과 관련된 (=검증이 필요한) DTO와 응답에 관련된 (=검증이 필요없는) DTO 클래스로 나누었다.
