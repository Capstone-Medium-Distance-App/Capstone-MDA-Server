package com.mda.server.web;
import com.mda.server.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import com.mda.server.domain.evalDetail.QEvalDetail;
import com.mda.server.domain.place.Place;
import com.mda.server.domain.place.QPlace;
import com.mda.server.web.dto.LocInitSet;
import com.mda.server.web.dto.midAndPlace;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class LocationController{
    private @Autowired
    LocationService locationService;
    LocInitSet locSet = new LocInitSet();

    @RequestMapping(value = "/locationInitSet", method= RequestMethod.POST) //place 뽑을때 참고할 Data
    public LocInitSet locInitSet(HttpServletRequest request) {
        locSet.setSchName(request.getParameter("schName"));
        locSet.setSchAge(request.getParameter("schAge"));
        locSet.setSchGender(request.getParameter("schGender"));
        locSet.setSchPeople(request.getParameter("schPeople"));
        locSet.setSchType(request.getParameter("schType"));
        locSet.setSchPlaceCate(request.getParameter("schPlaceCate"));

        System.out.println(locSet.getSchName() + "/" + locSet.getSchAge() + "/" + locSet.getSchType() + "/" + locSet.getSchGender() + "/" + locSet.getSchPeople() + "/" +
                locSet.getSchPlaceCate());
        return locSet;
    }
    //바로 위에서 입력받은 값들을 확인하기 위해서 만든 컨트롤러
    @GetMapping("/locationInitSet")
    public LocInitSet showLocInitSet(){
        return locSet;
    }

    @GetMapping(value = "/getMidAndPlace")
    public midAndPlace getMidAndPlace(HttpServletRequest request) throws IOException {
        midAndPlace map = new midAndPlace();
        /* 실제받아올 데이터

        //보낼 데이터
        map.setLatitude1(request.getParameter("latitude1"));
        map.setLatitude2(request.getParameter("latitude2"));
        map.setLatitude3(request.getParameter("latitude3"));
        map.longitude1(request.getParameter("longitude1"));
        map.longitude2(request.getParameter("longitude2"));
        map.longitude3(request.getParameter("longitude3"));
        map.setUserName1(request.getParameter("userName1");
        map.setUserName1(request.getParameter("userName2");
        map.setUserName1(request.getParameter("userName3");
        double midLat = 0.0;
        double midLong = 0.0;
        Integer placeId1 = 0;
        Integer placeId2 = 0;
        Integer placeId3 = 0;
        String placeName1 = "";
        String placeName2 = "";
        String placeName3 = "";
        String placeArea1 = "";
        String placeArea2 = "";
        String placeArea3 = "";
        String placeType1 = "";
        String placeType2 = "";
        String placeType3 = "";

        // 참고용 데이터
        String stnNm = "" //최종 중간위치 역 이름

        */


        //test용
        double latitude1 = 37.504198; //user1위도
        double latitude2 = 37.501025; //user2위도
        double latitude3 = Double.parseDouble(request.getParameter("latitude3")); //user3위도
        double longitude1 = 127.047967; //user1경도
        double longitude2 = 127.037701; //user2경도
        double longitude3 = Double.parseDouble(request.getParameter("longitude3")); //user3경도
        String UserName1 = "koo"; //user1이름 (HOST)
        String UserName2 = "lee"; //user2이름
        String UserName3 = "kim"; //user2이름
        String midLat = "";
        String midLong = "";
        String stnNm = ""; //최종 중간위치 역 이름

        //1. 중간 위도경도 구하기

        double dLon = Math.toRadians(longitude2 - longitude1);

        //convert to radians
        latitude1 = Math.toRadians(latitude1);
        latitude2 = Math.toRadians(latitude2);
        longitude1 = Math.toRadians(longitude1);

        double Bx = Math.cos(latitude2) * Math.cos(dLon);
        double By = Math.cos(latitude2) * Math.sin(dLon);
        double tempMidLat = Math.atan2(Math.sin(latitude1) + Math.sin(latitude2), Math.sqrt((Math.cos(latitude1) + Bx) * (Math.cos(latitude1) + Bx) + By * By));
        double tempMidLong = longitude1 + Math.atan2(By, Math.cos(latitude1) + Bx);



        //2. API사용해서 가까운역으로 위치 셋팅

        // ODsay 인증키
        String apiKey = "";

        // 파싱해온 데이터
        String rst = "";

        try {

            URL url = new URL("https://api.odsay.com/v1/api/pointSearch?lang=0&x=126.987179&y=37.568217&radius=500&stationClass=2&apiKey="+apiKey);
            BufferedReader bf;
            bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            rst = bf.readLine();

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject)jsonParser.parse(rst);
            JSONObject result = (JSONObject)jsonObject.get("result"); //resut Data
            JSONArray station = (JSONArray)result.get("station"); //반경 내 station정보
            JSONObject stationInfo = null; //최종 선정된 역 정보
            System.out.println("1 : " + station);


            int stationSz = 0; //근처 역 3개로 제한
            if(station.size() > 3){
                stationSz = 3;
            }else{
                stationSz = station.size();
            }

            System.out.println("2 : " + stationSz);

            double[] distance = new double[stationSz]; //중간위치에서 역까지의 거리계산후 배열에 넣어줌
            for(int i=0; i<stationSz; i++){
                stationInfo = (JSONObject)station.get(i);
                double x = (double) stationInfo.get("x"); //역의 경도
                double y = (double) stationInfo.get("y"); // 역의 위도
                distance[i] = distance(tempMidLat, tempMidLong, x, y, "meter");
                System.out.println("3 - " + i + distance[i]);
            }

            double min = 1000;
            for(int i=0; i<stationSz; i++) { //중간위치에서 가장 가까운역 찾기 (배열사이즈가 1일경우 대비해 min값 반경거리로 설정)
                if(distance[i] < min) stationInfo = (JSONObject)station.get(i);
                System.out.println("4 : " + stationInfo);
            }

            midLat = (String) stationInfo.get("x"); //최종역의 경도
            midLong = (String)stationInfo.get("y"); //최종역의 위도
            stnNm = (String)stationInfo.get("stationName"); //최종역의 이름
            System.out.println("5 : " + midLat +"||"+ midLong);

            //최종위치 위도경도 리턴할 객체에 셋팅
            map.setMidLat(midLat);
            map.setMidLong(midLong);
        }catch(Exception e) {
            e.printStackTrace();
        }

        //3. locInitSet 정보 기반으로 place 찾기

        List<Place> placeList = new ArrayList<Place>();
        //placeList = locationService.getPlaceDetailInfo(locSet, stnNm);
        //4. 값 셋팅해서 보내주기

        // placeList.get()
        return map;

    }

/*
    @GetMapping(value = "/testApi")
    public String getMidAndPlace(HttpServletRequest request) throws IOException {
        String apiKey = "9if76bfpjJjxcws6twPhkPfKHbQecu3JLLgD23UpjpQ";
        StringBuilder urlBuilder = new StringBuilder("https://api.odsay.com/v1/api/searchBusLane?busNo=10&CID=1000&apiKey="+apiKey);
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        System.out.println(sb.toString());

        return
    }
*/



    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return (dist);
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }




}
