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
