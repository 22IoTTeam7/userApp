package com.example.userapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
/**
 * 0. 버튼을 누르고 난 후 아래의 순서로 로직이 돌아가야 합니다.
 * 1. WiFi 스캔을 진행해서 주변의 GC_free_WiFi들의 MAC주소를 가져옵니다.
 * 2. 가져온 MAC 주소 리스트를 CheckFloor class의 getFloor(ArrayList<String> listAP)에 넣어서 현재 몇층인지 가져옵니다. (반환 int)
 * 3. 2에서 받은 층 수에 맞게 callRetrofit class에서 통신 메서드를 가져와 서버에 보냅니다.
 * 4. 3에서 보내고 받은 Response에서 "predict" 라벨의 값을 뽑아와서 규칙대로 UI에 뿌려줍니다.
 * 위치 변환 규칙
 *  각 호실 번호는 3자리 입니다.
 *  각 호신 번호끝에 1이 붙은 숫자는 그 호실 앞 복도입니다. 예) 2011 -> 201호 앞 복도
 *  특수 위치 매핑은 다음과 같습니다.
 *     1. 2층
 *         운동장쪽 엘리베이터 → 232
 *         중앙 엘리베이터 → 233
 *         복정동쪽 엘리베이터 → 234
 *         복정동쪽 빈공간 → 231
 *     2. 4층
 *         운동장쪽 엘리베이터 → 436
 *         중앙 엘리베이터 → 437
 *         복정동쪽 엘리베이터 → 438
 *         운동장쪽 엘리베이터 복도 → 439
 *         복정동쪽 빈공간 → 436
 *         407A -> 4072
 *     3. 5층
 *         운동장쪽 엘리베이터 → 535
 *         중앙 엘리베이터 → 536
 *         복정동쪽 엘리베이터 → 537
 *         운동장쪽 엘리베이터 복도 → 538
 *         복정동 쪽 빈공간 → 533
 *         큐브 → 534
 *         507A -> 5072
 * */
public class MainActivity extends AppCompatActivity {

    Button Locatebtn;
    TextView LocateTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Locatebtn = findViewById(R.id.LocateButton);
        LocateTxt = findViewById(R.id.LocateText);
    }
    //TODO : 버튼을 한번 누르면 3번의 스캔을 진행 : 스캔을 끄기 위해서 버튼을 한번 더 누르는 과정이 없어야 합니다. -> 버튼을 누르고 결과를 받고 다시 버튼을 누르면 또 스캔을 할 수 있게 구현 부탁드립니다.
    //TODO : WiFi 스캔 -> 서버에 보내서 결과 받고 X 3
    //TODO : 3번의 결과 중 가장 많은 결과를 UI에 프린트
}