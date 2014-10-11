그동안 미뤄왔던 Database Connection Pool 성능 측정을 수행해 봤습니다. 저는 그동안 [Tomcat CP](http://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html), [BoneCP](https://github.com/wwadge/bonecp) 를 주로 써 왔는데, 올 초에 [HikariCP](https://github.com/brettwooldridge/HikariCP) 를 알게되어 사용하게 되었습니다.

그동안 BoneCP의 benchmark 결과를 보고, 상당히 빠르구나... 했는데, HikariCP 에서 제공하는 Benchmark 결과를 보고, 이건 뭐야? 이렇게 빨라? 믿기지 않을 정도였지요...

하지만, 공식사이트의 사용법 중 maximumPoolSize 를 10 으로 놓고 작업해야 한다는 것과 minimumIdle 크기를 지정하지 않아도 된다는 것을 충실히 따라서 테스트 해보니, 실제 환경에서는 쓸 수 없을 정도로 Connection Pool timeout 이 생겨, 결국 테스용으로 전락했습니다.

HikariCP가 너무 빨라 pool size 가 10이라도 충분하다는 말이었는데, 실제 테스트 해보니 부족하여 32, 64 등으로 증가시켜 봤습니다만, 쓸데 없이 많이 만드는 것 아닌가 싶기도 하고, 결국 minimumIdle 의 크기 2~4 로 지정하고 테스트 하니 제대로 원하는 결과를 얻었습니다.

이에 고무되어, 내 나름대로 설정한 값들로 성능 측정을 해보자! 라는 생각이 들어, 부랴부랴 제작해 봤습니다.

소스 : [conectionpool-benchmark](https://github.com/debop/connectionpool-benchmark) 

제작은 [scala](http://www.scala-lang.org/) 2.11 을 사용했고, benchmark 는 [scalameter](http://scalameter.github.io/) 를 이용했습니다.

##### 측정 방법

1. cycle connection : 단순히 connection 을 얻고, 즉시 닫기
2. cycle statememt : connection 얻기, statement 실행, statement 닫기, connection 닫기

두 가지를 수행했습니다. 다른 benchmark 랑 다른 점은 단순 loop 로 반복테스트를 한 것이 아니라, scala 의 병렬 프로그래밍 기능을 이용하여 multi-thread 환경에서 동시에 작업을 수행할 수 있도록 했습니다. 이 방식이 실제 application에서 동작하는 방식과 가장 유사할 듯 합니다.

결과는 상상 이상입니다.

##### cycle connection
HikariCP가 비정상으로 보일 정도로 빠르네요. 거의 100배 이상 빠릅니다.


##### cycle statement : 
실제 운영환경과 가장 유사한 테스트라고 생각되는데, HikariCP가 2배 정도 빠르네요. 생각보다 BoneCP가 TomcatCP보다도 느리네요.

ConnectionPool 성능의 영향이 미미할 수도 있지만, 아주 많은 Tx를 발생시키는 환경에서는 중요성이 커지기 마련입니다. 앞으로는 HikariCP만을 사용할 것 같네요^^