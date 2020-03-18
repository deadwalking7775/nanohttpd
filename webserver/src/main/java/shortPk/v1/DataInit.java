package shortPk.v1;

/*
 * #%L
 * NanoHttpd-Webserver
 * %%
 * Copyright (C) 2012 - 2020 nanohttpd
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the nanohttpd nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataInit {

    // public static Map<String, List<Double>>getRangeTable(){
    // Map<String, List<Double>> res = new ConcurrentHashMap<>();
    // Globals globals = JsePlatform.standardGlobals();
    // LuaValue chunk =
    // globals.loadfile("webserver/src/main/lua/shortV1DataInit.lua");
    // chunk.call();
    // // LuaValue func =
    // globals.get(LuaValue.valueOf("helloWithoutTranscoder"));
    //
    // return res;
    // }

    // 读取
    // A8s:1.0,A9s:1.0,ATs:1.0,ATo:0.13,AJs:1.0,AJo:0.592,AQs:1.0,AQo:1.0,AKs:0.192,AKo:1.0,
    // 转化为Table: {A8s: 1.0, A9s: 1.0}
    // // 所有值概率是总概率，如果需要条件概率，需要 huuu_c_aaa[1] / huuu_c[1] = huuu_c_aaa[1] /
    // huuu[2]
    public static String md5Encode(MessageDigest messageDigest, String data){
        byte[] digest = messageDigest.digest((data).getBytes());
        BigInteger bigInt = new BigInteger(1,digest);
        String hashtext = bigInt.toString(16);
        // Now we need to zero pad it if you actually want the full 32 chars.
        while(hashtext.length() < 32 ){
            hashtext = "0"+hashtext;
        }
        return hashtext;
    }
    public static String md5EncodeSalty(MessageDigest messageDigest, String data){
        byte[] digest = messageDigest.digest(("?"+data+"?_4869").getBytes());
        BigInteger bigInt = new BigInteger(1,digest);
        String hashtext = bigInt.toString(16);
        // Now we need to zero pad it if you actually want the full 32 chars.
        while(hashtext.length() < 32 ){
            hashtext = "0"+hashtext;
        }
        return hashtext;
    }


    public static Map<String, Double> formatShortV1HandsRangeData(String dataStr){
        Map<String, Double> res = new ConcurrentHashMap<>();

//        System.out.println(dataStr);
        String lowDataStr = dataStr.toLowerCase();
        String[] tmpDataPairSplit = lowDataStr.split(",");
        for (String dataPair : tmpDataPairSplit) {
            String[] tmp = dataPair.split(":");
            res.put(tmp[0], Double.parseDouble(tmp[1]));
        }
        return res;
    }

    // //player act status 2nd rnd
    // //utg play huuuuu a
    // A8s:1.0,A9s:1.0,ATs:1.0,ATo:0.13,AJs:1.0,AJo:0.592,AQs:1.0,AQo:1.0,AKs:0.192,AKo:1.0,T9s:0.806,TT:0.678,JTs:1.0,JTo:0.624,QTs:1.0,KTs:1.0,JJ:0.816,QJs:1.0,KJs:1.0,QQ:0.592,KQs:1.0,KQo:1.0,KK:0.528
    // //huuuuu c
    // AA:1.0,A6s:0.216,A7s:1.0,A9o:0.272,ATo:0.87,AJo:0.408,AKs:0.808,98s:1.0,T8s:1.0,99:0.57,T9s:0.194,T9o:0.036,J9s:0.536,TT:0.322,JTo:0.376,QTo:0.524,KTo:0.212,JJ:0.184,QJo:0.806,KJo:0.886,QQ:0.408,KK:0.472
    // //huuuuu c acfff
    // AA:1.0,ATo:0.688,AJo:0.408,AKs:0.808,TT:0.322,JJ:0.184,QQ:0.408,KK:0.472
    //
    // // 生成结果 {query: handsTable}
    // // handsTable[1]: p(a) handsTable[2]: p（c） handsTable[3]: p（f）
    public static Map<String, Map<String, List<Double>>>  formatShortV1PreflopData(String filePath) {
        Map<String, Map<String, List<Double>>> shortActionV1Table = new ConcurrentHashMap<>();

        String line = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            FileReader dataFile = new FileReader(filePath);
            BufferedReader br = new BufferedReader(dataFile);
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                // "%s" stands for whitespace in lua
                line = line.toLowerCase();
                // 分隔符

                List<String> tmpFileSplit = Arrays.asList(line.split("\t"));
//
                String query2, query1, act1 = "";
                if (tmpFileSplit.size() > 2 && tmpFileSplit.get(1) != null && tmpFileSplit.get(2) != null) {
                    query1 = tmpFileSplit.get(1);
                    act1 = tmpFileSplit.get(2);
                    if (act1.equals("a")  || act1.startsWith("r") || act1.equals("ca")) {
                        act1 = "a";
                    }
                    String rangeData = tmpFileSplit.get(4);
                    // print("4: "..tmpFileSplit[4])
                    if (tmpFileSplit.get(3).equals("n")) {
                        // 有效输入:第一轮行动表
                        // 更新表
                        String action = tmpFileSplit.get(2);
                        Map<String, Double> rangePairMap = formatShortV1HandsRangeData(rangeData);
                        if (!shortActionV1Table.containsKey(query1)) {
                            shortActionV1Table.put(query1, new ConcurrentHashMap<String, List<Double>>());
                        }
                        Iterator<String> itr = rangePairMap.keySet().iterator();
                        while (itr.hasNext()) {
                            String hands = itr.next();

                            Double value = rangePairMap.get(hands);


                            if (shortActionV1Table.get(query1).get(hands) == null) {
                                shortActionV1Table.get(query1).put(hands, new ArrayList<Double>(3));
                                shortActionV1Table.get(query1).get(hands).add(0D);
                                shortActionV1Table.get(query1).get(hands).add(0D);
                                shortActionV1Table.get(query1).get(hands).add(0D);
                            }


                            if (action.equals("a")  || action.startsWith("r") || action.equals("ca")) {
                                shortActionV1Table.get(query1).get(hands).set(0, shortActionV1Table.get(query1).get(hands).get(0) + value);
                            } else if (action.equals("c")) {
                                shortActionV1Table.get(query1).get(hands).set(1, shortActionV1Table.get(query1).get(hands).get(1) + value);
                            }

                            if (shortActionV1Table.get(query1).get(hands).get(0) + shortActionV1Table.get(query1).get(hands).get(1) > 1.05) {
//                            System.out("data err: %s_%s, a: %f, c: %f", query1, hands, shortActionV1Table.get(query1).get(hands).get(1), shortActionV1Table.get(query1).get(hands).get(2));
                            }
                        }



                    } else {

                        // 有效输入：第二轮行动表
                        // 更新表
                        // 第二轮意味着有人raise，因此返回的act都是raise
                        query2 = tmpFileSplit.get(3);
                        String query = query1+"_"+act1+"_"+query2;

                        Map<String, Double> rangePairMap = formatShortV1HandsRangeData(rangeData);

                        // print("query "..query)
                        if (!shortActionV1Table.containsKey(query)) {
                            shortActionV1Table.put(query, new ConcurrentHashMap<String, List<Double>>());
                        }
                        Iterator<String> itr = rangePairMap.keySet().iterator();
                        while (itr.hasNext()) {
                            String hands = itr.next();

                            Double value = rangePairMap.get(hands);
                            shortActionV1Table.get(query).put(hands, new ArrayList<Double>(3));
                            shortActionV1Table.get(query).get(hands).add(0, value);
                            shortActionV1Table.get(query).get(hands).add(1, 0D);
                            shortActionV1Table.get(query).get(hands).add(2, 1D-value);
                        }
                    }
                }
            }
        } catch  (Exception e) {
            System.out.println(line);
            System.out.println("err "+e.toString());
        }
        return  shortActionV1Table;
    }



    // //player act status 2nd rnd
    // //utg play huuuuu a
    // A8s:1.0,A9s:1.0,ATs:1.0,ATo:0.13,AJs:1.0,AJo:0.592,AQs:1.0,AQo:1.0,AKs:0.192,AKo:1.0,T9s:0.806,TT:0.678,JTs:1.0,JTo:0.624,QTs:1.0,KTs:1.0,JJ:0.816,QJs:1.0,KJs:1.0,QQ:0.592,KQs:1.0,KQo:1.0,KK:0.528
    // //huuuuu c
    // AA:1.0,A6s:0.216,A7s:1.0,A9o:0.272,ATo:0.87,AJo:0.408,AKs:0.808,98s:1.0,T8s:1.0,99:0.57,T9s:0.194,T9o:0.036,J9s:0.536,TT:0.322,JTo:0.376,QTo:0.524,KTo:0.212,JJ:0.184,QJo:0.806,KJo:0.886,QQ:0.408,KK:0.472
    // //huuuuu c acfff
    // AA:1.0,ATo:0.688,AJo:0.408,AKs:0.808,TT:0.322,JJ:0.184,QQ:0.408,KK:0.472
    //
    // // 生成结果 {query: handsTable}
    // // handsTable[1]: p(a) handsTable[2]: p（c） handsTable[3]: p（f）
    public static Map<String, Map<String, List<Double>>>  formatShortV1PreflopDataV2(String filePath) {
        Map<String, Map<String, List<Double>>> shortActionV1Table = new ConcurrentHashMap<>();

        String line = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            FileReader dataFile = new FileReader(filePath);
            BufferedReader br = new BufferedReader(dataFile);
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                // "%s" stands for whitespace in lua
                line = line.toLowerCase();
                // 分隔符

                List<String> tmpFileSplit = Arrays.asList(line.split("\t"));
//
                String query2, query1, act1 = "";
                if (tmpFileSplit.size() > 2 && tmpFileSplit.get(1) != null && tmpFileSplit.get(2) != null) {
                    query1 = tmpFileSplit.get(1);
                    act1 = tmpFileSplit.get(2);
                    if (act1.equals("a")  || act1.startsWith("r") || act1.equals("ca")) {
                        act1 = "a";
                    }
                    String rangeData = tmpFileSplit.get(4);
                    // print("4: "..tmpFileSplit[4])
                    if (tmpFileSplit.get(3).equals("n")) {
                        // 有效输入:第一轮行动表
                        // 更新表
                        String action = tmpFileSplit.get(2);
                        Map<String, Double> rangePairMap = formatShortV1HandsRangeData(rangeData);
                        if (!shortActionV1Table.containsKey(query1)) {
                            shortActionV1Table.put(query1, new ConcurrentHashMap<String, List<Double>>());
                        }
                        Iterator<String> itr = rangePairMap.keySet().iterator();
                        while (itr.hasNext()) {
                            String hands = itr.next();

                            Double value = rangePairMap.get(hands);


                            if (shortActionV1Table.get(query1).get(hands) == null) {
                                shortActionV1Table.get(query1).put(hands, new ArrayList<Double>(5));
                                shortActionV1Table.get(query1).get(hands).add(0D);
                                shortActionV1Table.get(query1).get(hands).add(0D);
                                shortActionV1Table.get(query1).get(hands).add(0D);
                                shortActionV1Table.get(query1).get(hands).add(0D);
                                shortActionV1Table.get(query1).get(hands).add(0D);
                            }

                            if (action.equals("a")  || action.equals("ca")) {
                                shortActionV1Table.get(query1).get(hands).set(0, value);
                            } else if (action.startsWith("r")){
                                Double raiseCnt = Double.parseDouble(action.substring(1));
                                shortActionV1Table.get(query1).get(hands).set(1, value);
                                shortActionV1Table.get(query1).get(hands).set(4, raiseCnt);
                            } else if (action.equals("c")) {
                                shortActionV1Table.get(query1).get(hands).set(2, value);
                            }

                            if (shortActionV1Table.get(query1).get(hands).get(0)+shortActionV1Table.get(query1).get(hands).get(1) + shortActionV1Table.get(query1).get(hands).get(2) > 1.05) {
                            System.out.println(String.format("data err: %s_%s, %s", query1, hands, shortActionV1Table.get(query1).get(hands).toString()));
                            }
                        }



                    } else {

                        // 有效输入：第二轮行动表
                        // 更新表
                        // 第二轮意味着有人raise，因此返回的act都是raise
                        query2 = tmpFileSplit.get(3);
                        String query = query1+"_"+act1+"_"+query2;

                        Map<String, Double> rangePairMap = formatShortV1HandsRangeData(rangeData);

                        // print("query "..query)
                        if (!shortActionV1Table.containsKey(query)) {
                            shortActionV1Table.put(query, new ConcurrentHashMap<String, List<Double>>());
                        }
                        Iterator<String> itr = rangePairMap.keySet().iterator();
                        while (itr.hasNext()) {
                            String hands = itr.next();

                            Double value = rangePairMap.get(hands);
                            shortActionV1Table.get(query).put(hands, new ArrayList<Double>(5));
                            shortActionV1Table.get(query).get(hands).add(0, value);// allin
                            shortActionV1Table.get(query).get(hands).add(1, 0D);// raise
                            shortActionV1Table.get(query).get(hands).add(2, 0D);// call
                            shortActionV1Table.get(query).get(hands).add(3, 1D-value);// fold
                            shortActionV1Table.get(query).get(hands).add(4, 0D);// raiseCnt
                        }
                    }
                }
            }
        } catch  (Exception e) {
            System.out.println(line);
            System.out.println("err "+e.toString());
        }
        return  shortActionV1Table;
    }




//    jason1 55A0013
//    jason2 F5DA0B1
//    jason3 D455616
//    jsonWb WBWB123
//    ZXZX ZX1234
//    testwzx testwzx



//    b5996087043319bac567ddf1c81115ff bc34f48b37c34a36ebc7644e97204219
//    f8ea44204bd130c04908288c6661e44e f8ea44204bd130c04908288c6661e44e
//  4b2f495481a4d4ff22cc6a22683b298c 88a35f0c2cba4d8d9ee573b2efeec99c
//  08bbdc5234d80d8e270f31b2fe038488 6bd7d943af1e7773a90aefe31577de03
//    fc3f86537b4fb8f49df54852440fd4f3 d9d14ea527bc57e7631e7647be59998a
//    a31bd57fdf296f71e9c531589760ba44 23dcac11bf03e85e4298de30dffa3ff0
    public static Map<String, String> getUserPassword() {
        Map<String, String> res =  new ConcurrentHashMap<>();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            res.put(md5EncodeSalty(messageDigest, "jason1"), md5EncodeSalty(messageDigest, "55A0013"));
            res.put(md5EncodeSalty(messageDigest, "jason2"), md5EncodeSalty(messageDigest, "F5DA0B1"));
            res.put(md5EncodeSalty(messageDigest, "jason3"), md5EncodeSalty(messageDigest, "D455616"));
            res.put(md5EncodeSalty(messageDigest, "jsonWb"), md5EncodeSalty(messageDigest, "WBWB123"));
            res.put(md5EncodeSalty(messageDigest, "ZXZX"), md5EncodeSalty(messageDigest, "ZX1234"));
            res.put(md5EncodeSalty(messageDigest, "testwzx"), md5EncodeSalty(messageDigest, "testwzx"));
        } catch (Exception e){
            System.out.println(e.toString());
        }
        return res;
    }

    public static Map<String, String> getUserName() {
        Map<String, String> res =  new ConcurrentHashMap<>();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            res.put(md5EncodeSalty(messageDigest, "jason1"), "jason1");
            res.put(md5EncodeSalty(messageDigest, "jason2"), "jason2");
            res.put(md5EncodeSalty(messageDigest, "jason3"), "jason3");
            res.put(md5EncodeSalty(messageDigest, "jsonWb"), "jsonWb");
            res.put(md5EncodeSalty(messageDigest, "ZXZX"), "ZXZX");
            res.put(md5EncodeSalty(messageDigest, "testwzx"), "testwzx");
        } catch (Exception e){
            System.out.println(e.toString());
        }
        return res;
    }

    public static Map<String, String> formatMd5Data(String filePath) {
//        Map<String, Map<String, List<Double>>> shortActionV1Table = new ConcurrentHashMap<>();
        Map<String, String> md5Table = new ConcurrentHashMap<>();

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            FileReader dataFile = new FileReader(filePath);
            BufferedReader br = new BufferedReader(dataFile);
            String line;
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {

                // "%s" stands for whitespace in lua
                line = line.toLowerCase();
                // 分隔符

                List<String> tmpFileSplit = Arrays.asList(line.split("\t"));
//
                String query2, query1, act1 = "";
                if (tmpFileSplit.size() > 2 && tmpFileSplit.get(1) != null && tmpFileSplit.get(2) != null) {
                    query1 = tmpFileSplit.get(1);

                    // save md5
                    md5Table.putIfAbsent(md5EncodeSalty(messageDigest, query1), query1);

                    act1 = tmpFileSplit.get(2);
                    if (act1.equals("a")  || act1.startsWith("r") || act1.equals("ca")) {
                        act1 = "a";
                    }
                    String rangeData = tmpFileSplit.get(4);
                    // print("4: "..tmpFileSplit[4])
                    if (tmpFileSplit.get(3).equals("n")) {
                        // 有效输入:第一轮行动表
                        // 更新表
                        String action = tmpFileSplit.get(2);
                        Map<String, Double> rangePairMap = formatShortV1HandsRangeData(rangeData);
                        Iterator<String> itr = rangePairMap.keySet().iterator();
                        while (itr.hasNext()) {
                            String hands = itr.next();
                            // save md5
                            md5Table.putIfAbsent(md5EncodeSalty(messageDigest, hands), hands);
                        }
                    } else {
                        // 有效输入：第二轮行动表
                        // 更新表
                        // 第二轮意味着有人raise，因此返回的act都是raise
                        query2 = tmpFileSplit.get(3);
                        String query = query1+"_"+act1+"_"+query2;

                        // save md5
                        md5Table.putIfAbsent(md5EncodeSalty(messageDigest, query), query);

                        Map<String, Double> rangePairMap = formatShortV1HandsRangeData(rangeData);

                        Iterator<String> itr = rangePairMap.keySet().iterator();
                        while (itr.hasNext()) {
                            String hands = itr.next();
                            md5Table.putIfAbsent(md5EncodeSalty(messageDigest, hands), hands);
                        }
                    }
                }
            }

            md5Table.putIfAbsent(md5EncodeSalty(messageDigest, "query"), "query");
            md5Table.putIfAbsent(md5EncodeSalty(messageDigest, "error"), "error");


        } catch  (Exception e) {
            System.out.println("md5 err "+e.toString());
        }
        return  md5Table;
    }
}
