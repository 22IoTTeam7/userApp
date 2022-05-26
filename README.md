구현 필요 사항
####
1. CheckFloor class에 스캔한 AP의 맥주소를 앞의 94:64:24: 9개 문자를 지우고 념겨주어 층 수를 받아옵니다.
2. 받아온 층 수에 따라 ManagerApp 과 같은 방식으로 층별 모델에 Mapping (room 번호는 없습니다.)
3. ManagerApp과 같은 방식으로 서버에 데이터 전송 (API URI는 세팅을 해두어서 그냥 층별로 전송만 해주면 됩니다.)
4. 전송 후 응답에 predict 라벨에 예측한 위치가 int로 들어있습니다. 해당 값을 가져와서 UI
