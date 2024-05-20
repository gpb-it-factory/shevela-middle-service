<h1 align="center">
  <a><img src="https://w7.pngwing.com/pngs/45/990/png-transparent-robots-exclusion-standard-graphy-robot-electronics-photography-3-d-thumbnail.png" width="300"></a>
  <br>
  Middle Service
  <br>
</h1>

<p align="center">
  <a href="#Описание">Описание</a> •
  <a href="#Обрабатываемые-запросы">Обрабатываемые запросы</a> •
  <a href="#Запуск">Запуск</a> •
  <a href="#Структура-проекта">Структура проекта</a> • 
  <a href="#Стэк">Стэк</a> • 
  <a href="#Полезные-ссылки">Полезные ссылки</a> • 
</p>

---

## Описание

Middle Service - java-приложение, являющееся middle-слоем проекта "Мини-банк", разрабатываемого в рамках GPB IT Factory Backend 2024. Middle Service принимает запросы от telegram-бота, выступающего в качестве UI, выполняет валидацию, бизнес-логику и маршрутизирует запросы в backend-слой.

---

## Обрабатываемые запросы
* /start - начало работы с ботом, первичная инструкция пользователю
* /help - справка
* /register - первичное оформление пользователя в банке
* /createaccount - открытие счёта в Мини-банке, у клиента может быть только один счёт
* /currentbalance - получить текущий баланс открытого пользователем счёта
* /transfer [toTelegramUser] [amount] - перевод средств со счёта текущего пользователя на другой счёт по имени пользователя. toTelegramUser - пользователь, на счёт которого совершается перевод, amount - сумма перевода

---

## Запуск

* _Раздел в разработке..._

---

## Структура проекта

#### Проект имеет трехслойную архитектуру:
* Frontend - telegram-бот, выступает как клиентское приложение, инициирует запросы пользователей
* Middle - java-сервис, принимает запросы от tg-бота, выполняет валидацию и бизнес-логику, маршрутизирует запросы
* Backend - автоматизированная банковская система (АБС), обрабатывает транзакции, хранит клиентские данные

```plantuml
actor User
User -> Telegram : Command

activate Telegram
Telegram -> JavaApplication : Http-request

activate JavaApplication
JavaApplication -> ABS : Http-request

activate ABS
ABS -> JavaApplication : Http-response
deactivate ABS

JavaApplication -> Telegram : Http-response
deactivate JavaApplication

Telegram -> User : Information
deactivate Telegram
```

---

## Стэк
* [Java 17](https://www.java.com/ru/)
* [Gradle 8.7](https://gradle.org/)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Jnit5](https://junit.org/junit5/)
* [Testcontainers](https://testcontainers.com/)
* [AssertJ](https://assertj.github.io/doc/)
* [SlF4J](https://www.slf4j.org/)

---


## Полезные ссылки

* <https://core.telegram.org/bots>
* <https://handbook.tmat.me/ru/>
* <https://mvnrepository.com/artifact/org.telegram/telegrambots>