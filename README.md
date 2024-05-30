# Httpstarter - Spring Boot starter для логирования HTTP запросов

## Применение

Добавить зависимость стартера в приложение

```
<dependency>
     <groupId>com.t1.task3</groupId>
    <artifactId>Httpstarter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Активировать логирование

Чтобы активировать логирование, пропишите в application.properties следующие свойства:

```
http.aop.interceptor.enabled=true
http.aop.interceptor.level=INFO
``` 
## Настройка свойств
- *http.aop.interceptor.enabled* - включает или выключает логирование. 
Допустимые значения: **true**, **false**. По умолчанию: **false**.
- *http.aop.interceptor.level* - уровень логирования.
Допустимые значения: **TRACE, DEBUG, INFO, WARN, ERROR**. По умолчанию: **INFO**.


## Использовался стек
- *Java 17*
- *Spring Boot 3*
- *Maven*
- *Filters*
