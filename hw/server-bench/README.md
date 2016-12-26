# About

Каждый тест запускался несколько раз, затем время усреднялось, чтобы исключить влияние всяких аномалий.


## Меняем **N** в пределах от 1 до 7k с шагом 500
```M = 30```, ```X = 3```, ```delta = 0```
![n-worktime](https://raw.githubusercontent.com/fbocharov/au-java/server-bench/hw/server-bench/doc/average_client_worktime_arsize.png)
![n-client-time](https://raw.githubusercontent.com/fbocharov/au-java/server-bench/hw/server-bench/doc/server_time_for_client_arsize.png)
![n-request-time](https://raw.githubusercontent.com/fbocharov/au-java/server-bench/hw/server-bench/doc/server_time_for_request_arsize.png)

## Меняем **M** в пределах от 1 до 200 с шагом 15
```N = 300```, ```X = 2```, ```delta = 0```
![m-worktime](https://raw.githubusercontent.com/fbocharov/au-java/server-bench/hw/server-bench/doc/average_client_worktime_client_cnt.png)
![m-client-time](https://raw.githubusercontent.com/fbocharov/au-java/server-bench/hw/server-bench/doc/server_time_for_client_client_cnt.png)
![m-request-time](https://raw.githubusercontent.com/fbocharov/au-java/server-bench/hw/server-bench/doc/server_time_for_request_client_cnt.png)

## Меняем **delta** в пределах от 1 до 100 с шагом 10
```N = 100```, ```M = 50```, ```X = 10```
![delta-worktime](https://raw.githubusercontent.com/fbocharov/au-java/server-bench/hw/server-bench/doc/average_client_worktime_delta.png)
![delta-client-time](https://raw.githubusercontent.com/fbocharov/au-java/server-bench/hw/server-bench/doc/server_time_for_client_delta.png)
![delta-request-time](https://raw.githubusercontent.com/fbocharov/au-java/server-bench/hw/server-bench/doc/server_time_for_request_delta.png)
