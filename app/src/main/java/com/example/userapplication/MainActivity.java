package com.example.userapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
 *         운동장쪽 엘리베이터 → 440
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

    int counter = 0;
    int[] loca_code = new int[3];
    int result_code = 0;
    //TODO 받은 결과


    CheckFloor checkFloor = new CheckFloor();

    Floor2List floor2 = new Floor2List();
    Floor4List floor4 = new Floor4List();
    Floor5List floor5 = new Floor5List();

    int[] RSSI_Array;
    List<ScanResult> wifiList = new ArrayList();
    ArrayList wifiFormatList = new ArrayList();
    callRetrofit manager;
    WifiManager wifiManager;
    IntentFilter intentFilter = new IntentFilter();

    boolean isScanning = false;
    boolean wifiStartFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Locatebtn = findViewById(R.id.LocateButton);
        LocateTxt = findViewById(R.id.LocateText);
        Arrays.fill(loca_code,0);
        //HTTP 통신 Manager
        manager = new callRetrofit();

        //권한 요청
        ActivityCompat.requestPermissions(com.example.userapplication.MainActivity.this,
                new String[]{Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CHANGE_NETWORK_STATE,
                        Manifest.permission. ACCESS_COARSE_LOCATION},
                1000);

        //버튼 눌렀을 때 스켄 시작
        Locatebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(!isScanning) {
                    Toast.makeText(com.example.userapplication.MainActivity.this, "Scan On", Toast.LENGTH_SHORT).show();
                    isScanning = true;
                    try {
                        if(wifiStartFlag == true){
                            //TODO [와이파이 스캔이 실행 중인 경우]
                            Log.d("","\n"+"[A_WifiScan > 실시간 와이파이 스캐닝이 이미 동작 중입니다 ...]");
                        }
                        else {
                            //TODO [와이파이 스캔 시작]
                            WifiScanStart();
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                } else{
                    Toast.makeText(com.example.userapplication.MainActivity.this, "Scan Off", Toast.LENGTH_SHORT).show();
                    isScanning = false;
                }
            }
        });
    }



    //TODO ===== [와이파이 스캔 시작 실시] =====
    public void WifiScanStart(){
        Log.d("","\n"+"[A_WifiScan > WifiScanStart() 메소드 : 실시간 와이파이 스캐닝 시작- "+(counter+1)+"번째]");
        try {
            //TODO [와이파이 스캔 시작 플래그 설정]
            wifiStartFlag = true;
            //TODO [Wifi Scan 관련 객체 선언]
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

            registerReceiver(wifiScanReceiver, intentFilter); //TODO 리시버 시작

            //TODO [와이파이 스캔 상태 확인]
            boolean success = wifiManager.startScan();

            if(!success) {
                Log.d("","\n"+"[A_WifiScan > WifiScanStart() 메소드 : 실시간 와이파이 스캐닝 시작 할 수없는 상태]");
                Log.d("","\n"+"[로직 : 와이파이 스캔 기능이 정지 상태입니다. 와이파이 설정에서 비활성 후 다시 활성 필요]");

                try {
                    //TODO 실시간 와이파이 스캐닝 종료
                    WifiScanStop();
                    //TODO : 버튼을 한번 누르면 3번의 스캔을 진행 : 스캔을 끄기 위해서 버튼을 한번 더 누르는 과정이 없어야 합니다. -> 버튼을 누르고 결과를 받고 다시 버튼을 누르면 또 스캔을 할 수 있게 구현 부탁드립니다.
                    //TODO : WiFi 스캔 -> 서버에 보내서 결과 받고 X 3
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else {
                Log.d("","\n"+"[A_WifiScan > WifiScanStart() 메소드 : 실시간 와이파이 스캐닝 진행 중인 상태 - "+(counter+1)+"번째]");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            ArrayList<String> listAP = new ArrayList<>();
            //TODO wifiManager.startScan(); 시 발동되는 메소드 (실시간 와이파이 목록 감지)
            try {
                //TODO [스캔 성공 여부 값 반환]
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                //TODO [실시간 와이파이 목록 스캔 성공한 경우]
                if (success) {
                    //TODO [기존에 저장된 리스트 초기화 실시]
                    if(wifiList != null){
                        if (wifiList.size() > 0){
                            wifiList.clear();
                        }
                    }
                    else {
                        wifiList = new ArrayList();
                    }
                    if(wifiFormatList != null){
                        if (wifiFormatList.size() > 0){
                            wifiFormatList.clear();
                        }
                    }
                    else {
                        wifiFormatList = new ArrayList();
                    }

                    //TODO [실시간 스캔된 와이파이 리스트 결과 얻어옴]
                    wifiList = wifiManager.getScanResults();
                    String ssid_format = "";
                    String mac_format = "";

                    int[] level_format = new int[50];
                    int j = 0;
                    //TODO [for 반복문을 수행하면서 데이터 확인 실시]
                    for(int i=0; i<wifiList.size(); i++){
                        ssid_format = wifiList.get(i).SSID;
                        //TODO SSID 값 바꿔주기! - 완료
                        //[SSID 값 확인] - Gachon Free WiFi 만 받도록!
                        if(ssid_format.equals("GC_free_WiFi")){
                            mac_format = wifiList.get(i).BSSID.trim();
                            mac_format = mac_format.substring(9);
                            //TODO [MAC 값 확인] - 해당 인덱스에 RSSI 값 기록
                            level_format[j] = Integer.valueOf(wifiList.get(i).level);
                            listAP.add(j, mac_format);
                            j++;
                        }
                    }
                    Log.d("확인","\n"+"[실시간 와이파이 스캐닝 목록 확인 성공: 목록 매핑 시작 - "+(counter+1)+"번째");

                    //일단 임시로 조치해두었습니다. 수요일 테스트할때 제가 바꿔서 테스트 할게요
                    int floor = checkFloor.getFloor(listAP);
                    Log.d("Test", Integer.toString(floor));

                    switch (floor){
                        case 2:
                            RSSI_Array = new int[33];
                            Arrays.fill(RSSI_Array,0);
                            for(String verify_MAC : listAP){
                                if(floor2.AP2F.contains(verify_MAC)){
                                    RSSI_Array[floor2.AP2F.indexOf(verify_MAC)] = level_format[listAP.indexOf(verify_MAC)];
                                }
                            }

                            loca_code[counter] = manager.callFloor2(RSSI_Array);
                            Log.d("Test", Integer.toString(loca_code[counter]));
                            break;
                        case 4:
                            RSSI_Array = new int[44];
                            Arrays.fill(RSSI_Array,0);
                            for(String verify_MAC : listAP){
                                if(floor4.AP4F.contains(verify_MAC)){
                                    RSSI_Array[floor4.AP4F.indexOf(verify_MAC)] = level_format[listAP.indexOf(verify_MAC)];
                                }
                            }
                            loca_code[counter] = manager.callFloor4(RSSI_Array);
                            Log.d("Test", Integer.toString(loca_code[counter]));
                            break;
                        case 5:
                            RSSI_Array = new int[49];
                            Arrays.fill(RSSI_Array,0);
                            for(String verify_MAC : listAP){
                                if(floor5.AP5F.contains(verify_MAC)){
                                    RSSI_Array[floor5.AP5F.indexOf(verify_MAC)] = level_format[listAP.indexOf(verify_MAC)];
                                }
                            }
                            loca_code[counter] = manager.callFloor5(RSSI_Array);
                            Log.d("Test", Integer.toString(loca_code[counter]));
                            break;
                    }
                    //TODO [실시간 와이파이 스캐닝 종료]
                } else {
                    Log.d("","\n"+"[A_WifiScan > onReceive() 메소드 : 실시간 와이파이 스캐닝 목록 확인 실패]");
                }
                Log.d("---","---");
                WifiScanStop();

                //받은 결과를 loca_code arr에 저장 (총 3개)
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    //TODO ===== [와이파이 스캔 종료 실시] =====
    public void WifiScanStop(){
        Log.d("","\n"+"[A_WifiScan > WifiScanStop() 메소드 : 실시간 와이파이 스캐닝 종료- "+(counter+1)+"번째]");
        try {
            //TODO [실시간 와이파이 목록 스캔 플래그값 초기화]
            wifiStartFlag = false;
            isScanning = false;
            //TODO [등록한 리시버 해제 실시]
            if(wifiScanReceiver != null){
                unregisterReceiver(wifiScanReceiver);
            }
            //세번까지 돌려보자
            if(counter<2){
                counter++;
                WifiScanStart();
            }else{

                //TODO : 3번의 결과 중 가장 많은 결과를 UI에 프린트
                //세 결과가 다 다들 경우
                if(loca_code[0] != loca_code[1] && loca_code[0] != loca_code[2] && loca_code[1] != loca_code[2]){
                    result_code = loca_code[2];
                }
                //세 결과 중 0,1 같거나 1,2같거나 0,1,2 다 같거나
                else if(loca_code[0] == loca_code[1] || loca_code[1] == loca_code[2]){
                    result_code = loca_code[1];
                }
                // 0,2만 같을 경우
                else if(loca_code[0] == loca_code[2]){
                    result_code = loca_code[0];
                }
                //TODO [받아온 result 표시]
                /*  각 호신 번호끝에 1이 붙은 숫자는 그 호실 앞 복도입니다. 예) 2011 -> 201호 앞 복도
                 *  특수 위치 매핑은 다음과 같습니다.
                 *     1. 2층
                 *         운동장쪽 엘리베이터 → 232
                 *         중앙 엘리베이터 → 233
                 *         복정동쪽 엘리베이터 → 234
                 *         복정동쪽 빈공간 → 231
                 *     2. 4층
                 *         운동장쪽 엘리베이터 → 440
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

                if(result_code > 2000) {
                    if (result_code == 4072 || result_code == 5072) {
                        LocateTxt.setText("현재 위치는"+ Integer.toString(result_code / 10) + "A 입니다");
                    } else {
                        LocateTxt.setText("현재 위치는"+Integer.toString(result_code / 10) + "호 앞 복도입니다");
                    }
                }else if(result_code > 200 && result_code < 600){
                    LocateTxt.setText("현재 위치는"+Integer.toString(result_code) + "호 입니다");
                }else{
                    LocateTxt.setText("오류: 스캔을 다시 해주세요");
                }
                //카운터 초기화
                counter = 0;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}