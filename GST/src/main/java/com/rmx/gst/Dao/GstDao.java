/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmx.gst.Dao;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;

public class GstDao {

    JdbcTemplate template;

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    public JSONObject getGstData(String start, String end, HttpSession session) {
        JSONObject js = new JSONObject();
        System.out.println("start---------" + start);
        SimpleDateFormat dt = new SimpleDateFormat("dd-MMM-yy");
        SimpleDateFormat dtparse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        String start_date = "", end_date = "", sql1, vno, vdt, vtype;
        Date d = null;
        int yearst = Integer.parseInt(session.getAttribute("year").toString().substring(2));
        //int yeared=Integer.parseInt(session.getAttribute("year").toString().substring(2))+1;
        String comp = session.getAttribute("comp").toString();
        //System.out.println("year ====="+yearst+comp);
        Date startd;
        try {
            startd = dtparse.parse(start);
            start_date = dt.format(startd);
            Date endd = dtparse.parse(end);
            end_date = dt.format(endd);
        } catch (ParseException ex) {
            System.out.println("ex---" + ex);
        }
        System.out.println("start----" + start_date);
        System.out.println("end------" + end_date);
        String sql = "select vbillno,vbilldt,vamt,vrcode,vcode,vno,vdt,vtype from vou" + comp + yearst + (yearst + 1) + " v WHERE VDT > = '" + start_date + "' AND VDT < = '" + end_date + "' and VNO IN ('566','567') AND vtype='PB' and vRcode like '100%'  ";
        System.out.println("sql-----" + sql);
        List<Map<String, Object>> li = template.queryForList(sql);
        System.out.println("li---" + li);
        List al = new ArrayList();

        if (!li.isEmpty()) {
            for (Map row : li) {
                JSONObject gs = new JSONObject();
                List al2 = new ArrayList();
                gs.put("VBILLNO", row.get("VBILLNO"));
                gs.put("VBILLDT", dt.format(row.get("VBILLDT")));
                gs.put("VAMT", row.get("VAMT"));
                gs.put("VCODE", row.get("VCODE"));
                gs.put("vrcode", row.get("vrcode"));
                // System.out.println("vdt-----"+row.get("vdt"));
                vno = row.get("vno").toString();
                vdt = dt.format(row.get("vdt"));
                vtype = row.get("vtype").toString();
                sql1 = "select CGST_PERC,CGST_amount,sGST_PERC,sGST_amount,IGST_PERC,IGST_AMOUNT from gstvou" + comp + yearst + (yearst + 1) + " where vno='" + vno + "' and vdt='" + vdt + "' and vtype='" + vtype + "'";
                System.out.println("sql1----" + sql1);
                List<Map<String, Object>> li1 = template.queryForList(sql1);
                System.out.println("li1===" + li1);
                if (!li1.isEmpty()) {
                    for (Map row1 : li1) {
                        JSONObject j2 = new JSONObject();
                        j2.put("CGST_PERC", row1.get("CGST_PERC"));
                        j2.put("CGST_amount", row1.get("CGST_amount"));
                        j2.put("sGST_PERC", row1.get("sGST_PERC"));
                        j2.put("sGST_amount", row1.get("sGST_amount"));
                        j2.put("IGST_PERC", row1.get("IGST_PERC"));
                        j2.put("IGST_AMOUNT", row1.get("IGST_AMOUNT"));
                        System.out.println("-----");
                        al2.add(j2);
                        // System.out.println("al2=====" + al2);
                    }
                }
                gs.put("gs", al2);
                al.add(gs);
            }
            js.put("gstdata", al);
            // System.out.println("js-----" + js);
        }
        return js;
    }

    public List getyearCompcode() {
        String sql = "select comp_code,year,comp_name from control where year >= '2017' order by comp_code";
        List li = template.queryForList(sql);
        return li;
    }

    public void selectyear(String year, String comp, HttpSession session) {
        session.setAttribute("comp", comp);
        session.setAttribute("year", year);
        System.out.println("set---------session");

    }

    public List jsonComparison1(JSONObject json) {
        String js1 = json.toString();
        List list = new ArrayList();
        String gstin = null, sql, sql1, vcode = "", idt, state_code, inum, flag = "0";
        float iamt, txtval, samt, csamt, camt, vamt;
        int num, vstform = 0, rt;
        List<Map<String, Object>> livcode = new ArrayList();
        com.eclipsesource.json.JsonArray bcodeitems = Json.parse(js1).asObject().get("b2b").asArray();
        //System.out.println("size-----------" + bcodeitems.size());
        for (com.eclipsesource.json.JsonValue bitem : bcodeitems) {
            JSONObject js = new JSONObject();
            System.out.println("-ctin---" + bitem.asObject().getString("ctin", "null"));
            gstin = bitem.asObject().getString("ctin", "null");
            js.put("ctin", gstin);
            state_code = gstin.substring(0, 2);
            //   System.out.println("state-----" + state_code);
            sql = "select p_code,p_sub from party where gstin='" + gstin + "'";
            livcode = template.queryForList(sql);
            HashSet g = new HashSet();
            HashSet notmatchhash = new HashSet();
            if (!livcode.isEmpty()) {
                for (Map r : livcode) {
                    vcode = r.get("p_code").toString() + r.get("p_sub").toString();
                    // System.out.println("vcode----------"+vcode);
                    // System.out.println("bitem-----"+bitem);
                    sql1 = "select vstform,vbillno,vbilldt,vamt,vrcode,vcode,vno,vdt,vtype from vou61718 where  vtype='PB' and  vcode='" + vcode + "' and vstform like '%GST%'";
                    // System.out.println("sql1===="+sql1); 
                    List<Map<String, Object>> al = new ArrayList();
                    al = template.queryForList(sql1);
                    // System.out.println("al size=====" + al);
                    com.eclipsesource.json.JsonArray invo = bitem.asObject().get("inv").asArray();
                    for (com.eclipsesource.json.JsonValue inv : invo) {
                        // JSONObject gst = new JSONObject();
                        // System.out.println("inv----" + inv);

                        // System.out.println("inv----" + inv);
                        idt = inv.asObject().getString("idt", "");
                        inum = inv.asObject().getString("inum", "");
                        // gst.put("idt", idt);
                        // System.out.println("inum====="+inum);
                        com.eclipsesource.json.JsonArray it = inv.asObject().get("itms").asArray();
                        for (com.eclipsesource.json.JsonValue itms : it) {
                            // System.out.println("itms----"+itms); 
                            num = itms.asObject().getInt("num", 0);
                            // js.put("num", num);

                            js.put("vcode", vcode);
                            // System.out.println("numm---"+num);
                            JsonObject itm_det = itms.asObject().get("itm_det").asObject();
                            // System.out.println("itm_det======="+itm_det);
                            //System.out.println("iamt======="+itm_det.asObject().getFloat("iamt", 0));
                            JSONObject gst = new JSONObject();
                            JSONObject notmatchgst = new JSONObject();
                            if (!al.isEmpty()) {
                                for (Map row : al) {
                                    gst.put("inum", inum);
                                    gst.put("idt", idt);
                                    vamt = Math.round(Float.parseFloat(row.get("vamt").toString()));
                                    // System.out.println("vamt---" + vamt);
                                    try {
                                        vstform = Integer.parseInt(row.get("vstform").toString().replaceAll("[^0-9]", ""));
                                        //  System.out.println("vstform----" + vstform);
                                    } catch (Exception e) {
                                        vstform = 0;
                                    }

                                    if (!state_code.equals("09")) {

                                        iamt = Math.round(itm_det.asObject().getFloat("iamt", 0));
                                        txtval = Math.round(itm_det.asObject().getFloat("txval", 0));
                                        rt = itm_det.asObject().getInt("rt", 0);
                                        //  System.out.println("iamt--" + iamt + "  txtval---" + txtval + "  vamt" + vamt);
                                        if ((iamt == vamt) || (txtval == vamt) && (rt == vstform)) {
                                            // System.out.println("equal---in out state");
                                            notmatchgst.clear();
                                            gst.put("iamt", iamt);
                                            gst.put("txtval", txtval);
                                            gst.put("vamt", vamt);
                                            gst.put("rt", rt);
                                            flag = "1";
                                            gst.put("comp_result", "OK");
                                        } else {
                                            notmatchgst.clear();
                                            notmatchgst.put("iamt", iamt);
                                            notmatchgst.put("txtval", txtval);
                                            notmatchgst.put("vamt", vamt);
                                            notmatchgst.put("rt", rt);
                                            flag = "0";
                                            //gst.put("comp_result", "Not Match");
                                        }
                                    } else {
                                        samt = Math.round(itm_det.asObject().getFloat("samt", 0));
                                        camt = Math.round(itm_det.asObject().getFloat("camt", 0));
                                        txtval = Math.round(itm_det.asObject().getFloat("txval", 0));
                                        rt = itm_det.asObject().getInt("rt", 0);
                                        //System.out.println("samt--" + samt + "  camt----" + camt + " txtval---" + txtval + "  vamt" + vamt);
                                        if ((samt == vamt) || (camt == vamt) || (txtval == vamt) && (rt == vstform)) {
                                            //  System.out.println("equal---in  up");
                                            notmatchgst.clear();
                                            gst.put("samt", samt);
                                            gst.put("camt", camt);
                                            gst.put("txtval", txtval);
                                            gst.put("vamt", vamt);
                                            gst.put("rt", rt);
                                            flag = "1";
                                            gst.put("comp_result", "OK");
                                            // g.add(gst);

                                        } else {
                                            notmatchgst.clear();
                                            notmatchgst.put("samt", samt);
                                            notmatchgst.put("camt", camt);
                                            notmatchgst.put("txtval", txtval);
                                            notmatchgst.put("vamt", vamt);
                                            notmatchgst.put("rt", rt);
                                            flag = "0";
                                            // gst.put("comp_result", "Not Match");
                                        }
                                    }

                                }

                                g.add(gst);
                                //System.out.println("notmatchgst----------"+notmatchgst);
                                notmatchhash.add(notmatchgst);
                                System.out.println("notmatchgst----------" + notmatchgst);
                                // System.out.println("g---"+g);

                            }

                        }

                    }
                    js.put("gst", g);
                    // js.put("notmatchgst", notmatchhash);
                    //System.out.println("js---" + js);

                }
            }
            list.add(js);
        }
        return list;
    }

    public List jsonComparison(JSONObject json) {
        String js1 = json.toString();
        List list = new ArrayList();
        String gstin = null, sql, sql1, vcode = "", idt, state_code, inum, flag = "0", comp = "", state, comp_name, mainstate_code = "", sqlup;
        float iamt, txtval, samt, csamt, camt, vamt;
        int num, vstform = 0, rt, res, cnt = 0;
        String compgstin = Json.parse(js1).asObject().get("gstin").asString();
        System.out.println("gstin-----" + compgstin);
        if (compgstin.equals("09AAACR1008A1Z5")) {
            comp = "6";
            comp_name = "FASHIONS";
            state = "(UP)";
            mainstate_code = "09";
        } else if (compgstin.equals("09AAGPM3196B1ZQ")) {
            comp = "1";
            state = "(UP)";
            comp_name = "JOSS";
            mainstate_code = "09";
        } else if (compgstin.equals("07AAGPM3196B1ZU")) {
            comp = "1";
            state = "(DELHI)";
            comp_name = "JOSS";
            mainstate_code = "07";
        } else if (compgstin.equals("07AAACR1008A1Z9")) {
            comp = "6";
            state = "(DELHI)";
            comp_name = "FASHIONS";
            mainstate_code = "07";
        } else if (compgstin.equals("07AAACR4021R1Z7")) {
            comp = "2";
            state = "";
            comp_name = "MERCHANTS";
            mainstate_code = "07";
        }

        List<Map<String, Object>> livcode = new ArrayList();
        com.eclipsesource.json.JsonArray bcodeitems = Json.parse(js1).asObject().get("b2b").asArray();
        //System.out.println("size-----------" + bcodeitems.size());
        for (com.eclipsesource.json.JsonValue bitem : bcodeitems) {
            JSONObject js = new JSONObject();
            System.out.println("-ctin---" + bitem.asObject().getString("ctin", "null"));
            gstin = bitem.asObject().getString("ctin", "null");
            js.put("ctin", gstin);
            state_code = gstin.substring(0, 2);
            //  System.out.println("state-----" + state_code);
            sql = "select p_code,p_sub from party where gstin='" + gstin + "'";
            livcode = template.queryForList(sql);
            HashSet g = new HashSet();
            HashSet notmatchhash = new HashSet();
            if (!livcode.isEmpty()) {
                for (Map r : livcode) {
                    vcode = r.get("p_code").toString() + r.get("p_sub").toString();
                    // System.out.println("vcode----------"+vcode);
                    // System.out.println("bitem-----"+bitem);
                    sql1 = "select v.vstform,v.vbillno,v.vbilldt,v.vamt,v.vrcode,v.vcode,v.vno,v.vdt,v.vtype,p.P_NAME from vou" + comp + "1718 v,party p where  v.vtype='PB' and  v.vcode='" + vcode + "' and v.vcode=P_CODE||P_SUB and v.vstform like '%GST%'";
                    // System.out.println("sql1===="+sql1); 
                    List<Map<String, Object>> al = new ArrayList();
                    al = template.queryForList(sql1);
                    // System.out.println("al size=====" + al);
                    com.eclipsesource.json.JsonArray invo = bitem.asObject().get("inv").asArray();
                    for (com.eclipsesource.json.JsonValue inv : invo) {
                        // JSONObject gst = new JSONObject();
                        // System.out.println("inv----" + inv);

                        // System.out.println("inv----" + inv);
                        idt = inv.asObject().getString("idt", "");
                        inum = inv.asObject().getString("inum", "");
                        // gst.put("idt", idt);
                        // System.out.println("inum====="+inum);
                        com.eclipsesource.json.JsonArray it = inv.asObject().get("itms").asArray();
                        for (com.eclipsesource.json.JsonValue itms : it) {
                            // System.out.println("itms----"+itms); 
                            num = itms.asObject().getInt("num", 0);
                            // js.put("num", num);

                            js.put("vcode", vcode);

                            // System.out.println("numm---"+num);
                            JsonObject itm_det = itms.asObject().get("itm_det").asObject();
                            // System.out.println("itm_det======="+itm_det);
                            //System.out.println("iamt======="+itm_det.asObject().getFloat("iamt", 0));
                            JSONObject gst = new JSONObject();
                            JSONObject notmatchgst = new JSONObject();
                            if (!al.isEmpty()) {
                                for (Map row : al) {
                                    // js.put("inum", row.get("vbillno"));
                                    //System.out.println("vbill no----------" + row.get("vbillno"));

                                    gst.put("P_NAME", row.get("P_NAME"));
                                    gst.put("vcode", vcode);
                                    // System.out.println("p_name----------"+row.get("P_NAME"));
                                    gst.put("idt", idt);
                                    vamt = Math.round(Float.parseFloat(row.get("vamt").toString()));
                                    // System.out.println("vamt---" + vamt);
                                    try {
                                        vstform = Integer.parseInt(row.get("vstform").toString().replaceAll("[^0-9]", ""));
                                        //  System.out.println("vstform----" + vstform);
                                    } catch (Exception e) {
                                        vstform = 0;
                                    }

                                    if (!state_code.equals(mainstate_code)) {

                                        iamt = Math.round(itm_det.asObject().getFloat("iamt", 0));
                                        txtval = Math.round(itm_det.asObject().getFloat("txval", 0));
                                        rt = itm_det.asObject().getInt("rt", 0);
                                        //  System.out.println("iamt--" + iamt + "  txtval---" + txtval + "  vamt" + vamt);
                                        if ((iamt == vamt) || ((iamt + 1) == vamt) || ((txtval == vamt)) || ((txtval + 1) == vamt) && (rt == vstform)) {
                                            // System.out.println("equal---in out state");
                                            gst.put("inum", row.get("vbillno"));
                                            gst.put("comp_result", "");
                                            gst.put("iamt", iamt);
                                            gst.put("txtval", txtval);
                                            gst.put("vamt", vamt);
                                            gst.put("rt", rt);
                                            flag = "1";
                                            gst.put("comp_result", "OK");
                                            sqlup = "update vou" + comp + "1718 set GSN_MATCH='Y' where vtype='PB' and vstform like '%GST%' and vstform ='" + row.get("vstform") + "' and vbillno='" + row.get("vbillno") + "' and vamt='" + row.get("vamt") + "' ";
                                            System.out.println("sqlup------------" + sqlup);
                                            res = template.update(sqlup);
                                            System.out.println("cnt--------------" + cnt);
                                            cnt++;
                                            // gst.put("comp_result1","");
                                        } else {
                                            /*notmatchgst.put("iamt", iamt);
                                             notmatchgst.put("txtval", txtval);
                                             notmatchgst.put("vamt", vamt);
                                             notmatchgst.put("rt", rt);*/
                                            gst.put("inum", inum);
                                            gst.put("iamt", iamt);
                                            gst.put("txtval", txtval);
                                            gst.put("rt", rt);
                                            gst.put("vamt", vamt);
                                            // gst.put("vstform", vstform);
                                            flag = "0";

                                            // gst.put("comp_result1", "Amount Not Match");
                                        }
                                    } else {
                                        samt = Math.round(itm_det.asObject().getFloat("samt", 0));
                                        camt = Math.round(itm_det.asObject().getFloat("camt", 0));
                                        txtval = Math.round(itm_det.asObject().getFloat("txval", 0));
                                        rt = itm_det.asObject().getInt("rt", 0);
                                        //System.out.println("samt--" + samt + "  camt----" + camt + " txtval---" + txtval + "  vamt" + vamt);
                                        if (((samt == vamt)) || ((samt + 1) == vamt) || ((camt == vamt)) || ((camt + 1) == vamt) || ((txtval == vamt)) || ((txtval + 1) == vamt) && (rt == vstform)) {
                                            //  System.out.println("equal---in  up");
                                            // gst.put("comp_result1","");
                                            gst.put("inum", row.get("vbillno"));
                                            gst.put("samt", samt);
                                            gst.put("camt", camt);
                                            gst.put("txtval", txtval);
                                            gst.put("vamt", vamt);
                                            gst.put("rt", rt);
                                            flag = "1";
                                            gst.put("comp_result", "OK");
                                            sqlup = "update vou" + comp + "1718 set GSN_MATCH='Y' where vtype='PB' and vstform like '%GST%' and vstform ='" + row.get("vstform") + "' and vbillno='" + row.get("vbillno") + "' and vamt='" + row.get("vamt") + "' ";
                                            System.out.println("sqlup------------" + sqlup);
                                            res = template.update(sqlup);
                                            System.out.println("cnt--------------" + cnt);
                                            cnt++;
                                            // g.add(gst);

                                        } else {
                                            /*notmatchgst.put("samt", samt);
                                             notmatchgst.put("camt", camt);
                                             notmatchgst.put("txtval", txtval);
                                             notmatchgst.put("vamt", vamt);
                                             notmatchgst.put("rt", rt);*/

                                            flag = "0";
                                            gst.put("inum", inum);
                                            gst.put("samt", samt);
                                            gst.put("camt", camt);
                                            gst.put("vamt", vamt);
                                            gst.put("txtval", txtval);
                                            gst.put("rt", rt);

                                            //gst.put("vstform", vstform);
                                            // gst.put("comp_result1", "Amount Not Match");
                                        }

                                    }

                                }
                                g.add(gst);

                                //  notmatchhash.add(notmatchgst);
                                // System.out.println("g---"+g);
                            }

                        }

                    }
                    js.put("gst", g);

                    // js.put("notmatchgst", notmatchhash);
                    //System.out.println("js---" + js);
                }
            }
            list.add(js);
        }
        return list;
    }

    public JSONObject getUnmatched(JSONObject json) {
        String js1 = json.toString();
        List list = new ArrayList();
        JSONObject j1 = new JSONObject();
        Date cdt = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("dd-MMM-yy");
        String curr_date = dt.format(cdt);
        String gstin = null, sql, sql1, vcode = "", idt, state_code, mainstate_code = "", inum, flag = "0", P_NAME = "", comp = "", state = "", comp_name = "";
        float iamt, txtval, samt, csamt, camt, vamt;
        int num, vstform = 0, rt, i = 0;
        String compgstin = Json.parse(js1).asObject().get("gstin").asString();
        System.out.println("gstin-----" + compgstin);
        if (compgstin.equals("09AAACR1008A1Z5")) {
            comp = "6";
            comp_name = "FASHIONS";
            mainstate_code = "09";
        } else if (compgstin.equals("09AAGPM3196B1ZQ")) {
            comp = "1";
            state = "(UP)";
            comp_name = "JOSS";
            mainstate_code = "09";
        } else if (compgstin.equals("07AAGPM3196B1ZU")) {
            comp = "1";
            state = "(DELHI)";
            comp_name = "JOSS";
            mainstate_code = "07";
        } else if (compgstin.equals("07AAACR1008A1Z9")) {
            comp = "6";
            state = "(DELHI)";
            comp_name = "FASHIONS";
            mainstate_code = "07";
        } else if (compgstin.equals("07AAACR4021R1Z7")) {
            comp = "2";
            state = "";
            comp_name = "MERCHANTS";
            mainstate_code = "07";
        }
        List<Map<String, Object>> livcode = new ArrayList();
        com.eclipsesource.json.JsonArray bcodeitems = Json.parse(js1).asObject().get("b2b").asArray();
        //System.out.println("size-----------" + bcodeitems.size());
        for (com.eclipsesource.json.JsonValue bitem : bcodeitems) {
            JSONObject js = new JSONObject();
            // System.out.println("-ctin---" + bitem.asObject().getString("ctin", "null"));
            gstin = bitem.asObject().getString("ctin", "");

            state_code = gstin.substring(0, 2);
            //   System.out.println("state-----" + state_code);
            sql = "select p_code,p_sub from party where gstin='" + gstin + "'";
            livcode = template.queryForList(sql);
            HashSet g = new HashSet();
            HashSet notmatchhash = new HashSet();
            if (!livcode.isEmpty()) {
                for (Map r : livcode) {
                    vcode = r.get("p_code").toString() + r.get("p_sub").toString();
                    // System.out.println("vcode----------"+vcode);
                    // System.out.println("bitem-----"+bitem);
                    sql1 = "select v.vstform,v.vbillno,v.vbilldt,v.vamt,v.vrcode,v.vcode,v.vno,v.vdt,v.vtype,p.P_NAME from vou" + comp + "1718 v,party p where  v.vtype='PB' and  v.vcode='" + vcode + "' and v.vcode=P_CODE||P_SUB and v.vstform like '%GST%'";
                    // System.out.println("sql1===="+sql1); 
                    List<Map<String, Object>> al = new ArrayList();
                    al = template.queryForList(sql1);
                    // System.out.println("al size=====" + al);
                    com.eclipsesource.json.JsonArray invo = bitem.asObject().get("inv").asArray();
                    for (com.eclipsesource.json.JsonValue inv : invo) {
                        // JSONObject gst = new JSONObject();
                        // System.out.println("inv----" + inv);

                        // System.out.println("inv----" + inv);
                        idt = inv.asObject().getString("idt", "");
                        inum = inv.asObject().getString("inum", "");
                        // gst.put("idt", idt);
                        // System.out.println("inum====="+inum);
                        com.eclipsesource.json.JsonArray it = inv.asObject().get("itms").asArray();
                        for (com.eclipsesource.json.JsonValue itms : it) {
                            // System.out.println("itms----"+itms); 
                            num = itms.asObject().getInt("num", 0);
                            // js.put("num", num);

                            // System.out.println("numm---"+num);
                            JsonObject itm_det = itms.asObject().get("itm_det").asObject();
                            // System.out.println("itm_det======="+itm_det);
                            //System.out.println("iamt======="+itm_det.asObject().getFloat("iamt", 0));
                            // js.put("ctin", gstin);

                            if (!al.isEmpty()) {
                                if (!state_code.equals(mainstate_code)) {
                                    flag = "0";
                                    iamt = Math.round(itm_det.asObject().getFloat("iamt", 0));
                                    txtval = Math.round(itm_det.asObject().getFloat("txval", 0));
                                    rt = itm_det.asObject().getInt("rt", 0);
                                    // System.out.println("iamt---"+iamt);
                                    i++;
                                    for (Map row : al) {
                                        P_NAME = row.get("P_NAME").toString();

                                        vamt = Math.round(Float.parseFloat(row.get("vamt").toString()));
                                        //System.out.println("vamt---" + vamt);
                                        try {
                                            vstform = Integer.parseInt(row.get("vstform").toString().replaceAll("[^0-9]", ""));
                                            //  System.out.println("vstform----" + vstform);
                                        } catch (Exception e) {
                                            vstform = 0;
                                        }
                                        //  System.out.println("iamt--" + iamt + "  txtval---" + txtval + "  vamt" + vamt);
                                        if (((iamt == vamt) || ((iamt + 1) == vamt) || ((txtval == vamt)) || ((txtval + 1) == vamt) && (rt == vstform)) || ((iamt == 0) && (rt == 0))) {
                                            // if (((iamt == vamt) || (txtval == vamt) && (rt == vstform)) || ((iamt == 0) && (rt == 0))) {
                                            flag = "1";
                                        }
                                    }
                                    if (flag.equals("0")) {
                                        JSONObject gst = new JSONObject();
                                        System.out.println("not equal=================================" + i);
                                        gst.put("iamt", iamt);
                                        gst.put("txtval", txtval);
                                        gst.put("rt", rt);
                                        gst.put("comp_result", "Not Found");
                                        gst.put("P_NAME", P_NAME);
                                        gst.put("vcode", vcode);
                                        // System.out.println("p_name----------"+row.get("P_NAME"));
                                        gst.put("idt", idt);
                                        js.put("ctin", gstin);
                                        gst.put("vcode", vcode);
                                        gst.put("inum", inum);
                                        g.add(gst);
                                        // gst.put("vamt", vamt);
                                    }
                                } else {
                                    flag = "0";
                                    samt = Math.round(itm_det.asObject().getFloat("samt", 0));
                                    camt = Math.round(itm_det.asObject().getFloat("camt", 0));
                                    txtval = Math.round(itm_det.asObject().getFloat("txval", 0));
                                    rt = itm_det.asObject().getInt("rt", 0);
                                    // System.out.println("iamt---"+samt);
                                    i++;
                                    for (Map row : al) {

                                        P_NAME = row.get("P_NAME").toString();
                                        vamt = Math.round(Float.parseFloat(row.get("vamt").toString()));
                                        // System.out.println("vamt---" + vamt);
                                        try {
                                            vstform = Integer.parseInt(row.get("vstform").toString().replaceAll("[^0-9]", ""));
                                            //  System.out.println("vstform----" + vstform);
                                        } catch (Exception e) {
                                            vstform = 0;
                                        }

                                        //System.out.println("samt--" + samt + "  camt----" + camt + " txtval---" + txtval + "  vamt" + vamt);
                                        if (((samt == vamt)) || ((samt + 1) == vamt) || ((camt == vamt)) || ((camt + 1) == vamt) || ((txtval == vamt)) || ((txtval + 1) == vamt) && (rt == vstform)) {
                                            //if (((samt == vamt) || (camt == vamt) || (txtval == vamt) && (rt == vstform))) {
                                            flag = "1";

                                        }

                                    }
                                    if (flag.equals("0")) {
                                        JSONObject gst = new JSONObject();
                                        System.out.println("not equal=================================" + i);
                                        gst.put("samt", samt);
                                        gst.put("camt", camt);
                                        //gst.put("vamt", vamt);
                                        gst.put("rt", rt);
                                        gst.put("txtval", txtval);
                                        gst.put("comp_result", "Not Found");
                                        gst.put("P_NAME", P_NAME);
                                        gst.put("vcode", vcode);
                                        // System.out.println("p_name----------"+row.get("P_NAME"));
                                        gst.put("idt", idt);
                                        js.put("ctin", gstin);
                                        gst.put("vcode", vcode);
                                        gst.put("inum", inum);
                                        g.add(gst);
                                    }

                                }

                                //  notmatchhash.add(notmatchgst);
                                // System.out.println("g---"+g);
                            }

                        }

                    }
                    js.put("gst", g);

                    // js.put("notmatchgst", notmatchhash);
                    //System.out.println("js---" + js);
                }
            }

            list.add(js);
            j1.put("unmatch", list);
            j1.put("unmatchgstin", getGstinNotFound(json));
            j1.put("comp_name", comp_name);
            j1.put("state", state);
            j1.put("curr_date", curr_date);

        }
        String rsn, nt_dt;
        List list1 = new ArrayList();
        com.eclipsesource.json.JsonArray cdn;
        try {
            cdn = Json.parse(js1).asObject().get("cdn").asArray();
        } catch (NullPointerException n) {
            cdn = null;
        }
        if (cdn != null) {
            for (com.eclipsesource.json.JsonValue bitem : cdn) {
                JSONObject js = new JSONObject();
                // System.out.println("-ctin---" + bitem.asObject().getString("ctin", "null"));
                gstin = bitem.asObject().getString("ctin", "");

                state_code = gstin.substring(0, 2);
                //   System.out.println("state-----" + state_code);
                sql = "select p_code,p_sub from party where gstin='" + gstin + "'";
                livcode = template.queryForList(sql);
                HashSet g = new HashSet();
                HashSet notmatchhash = new HashSet();
                if (!livcode.isEmpty()) {
                    for (Map r : livcode) {
                        vcode = r.get("p_code").toString() + r.get("p_sub").toString();
                        // System.out.println("vcode----------"+vcode);
                        // System.out.println("bitem-----"+bitem);
                        sql1 = "select v.vstform,v.vbillno,v.vbilldt,v.vamt,v.vrcode,v.vcode,v.vno,v.vdt,v.vtype,p.P_NAME from vou" + comp + "1718 v,party p where  v.vtype='PB' and  v.vcode='" + vcode + "' and v.vcode=P_CODE||P_SUB and v.vstform like '%GST%'";
                        // System.out.println("sql1===="+sql1); 
                        List<Map<String, Object>> al = new ArrayList();
                        al = template.queryForList(sql1);
                        // System.out.println("al size=====" + al);
                        com.eclipsesource.json.JsonArray nt = bitem.asObject().get("nt").asArray();
                        for (com.eclipsesource.json.JsonValue inv : nt) {
                        // JSONObject gst = new JSONObject();
                            // System.out.println("inv----" + inv);

                            // System.out.println("inv----" + inv);
                            idt = inv.asObject().getString("idt", "");
                            rsn = inv.asObject().getString("rsn", "");
                            inum = inv.asObject().getString("inum", "");
                            nt_dt = inv.asObject().getString("nt_dt", "");
                            // gst.put("idt", idt);
                            // System.out.println("inum====="+inum);
                            com.eclipsesource.json.JsonArray it = inv.asObject().get("itms").asArray();
                            for (com.eclipsesource.json.JsonValue itms : it) {
                                // System.out.println("itms----"+itms); 
                                num = itms.asObject().getInt("num", 0);
                            // js.put("num", num);

                                // System.out.println("numm---"+num);
                                JsonObject itm_det = itms.asObject().get("itm_det").asObject();
                                // System.out.println("itm_det======="+itm_det);
                                //System.out.println("iamt======="+itm_det.asObject().getFloat("iamt", 0));
                                // js.put("ctin", gstin);

                                if (!al.isEmpty()) {
                                    if (!state_code.equals(mainstate_code)) {
                                        flag = "0";
                                        iamt = Math.round(itm_det.asObject().getFloat("iamt", 0));
                                        txtval = Math.round(itm_det.asObject().getFloat("txval", 0));
                                        rt = itm_det.asObject().getInt("rt", 0);
                                        // System.out.println("iamt---"+iamt);
                                        i++;
                                        for (Map row : al) {
                                            P_NAME = row.get("P_NAME").toString();

                                            vamt = Math.round(Float.parseFloat(row.get("vamt").toString()));
                                            //System.out.println("vamt---" + vamt);
                                            try {
                                                vstform = Integer.parseInt(row.get("vstform").toString().replaceAll("[^0-9]", ""));
                                                //  System.out.println("vstform----" + vstform);
                                            } catch (Exception e) {
                                                vstform = 0;
                                            }
                                            //  System.out.println("iamt--" + iamt + "  txtval---" + txtval + "  vamt" + vamt);
                                            if (((iamt == vamt) || ((iamt + 1) == vamt) || ((txtval == vamt)) || ((txtval + 1) == vamt) && (rt == vstform)) || ((iamt == 0) && (rt == 0))) {
                                                //if (((iamt == vamt) || (txtval == vamt) && (rt == vstform)) || ((iamt == 0) && (rt == 0))) {
                                                flag = "1";
                                            }
                                        }
                                        if (flag.equals("0")) {
                                            JSONObject gst = new JSONObject();
                                            System.out.println("not equal=================================" + i);
                                            gst.put("iamt", iamt);
                                            gst.put("txtval", txtval);
                                            gst.put("rt", rt);
                                            gst.put("comp_result", "Not Found");
                                            gst.put("P_NAME", P_NAME);
                                            gst.put("vcode", vcode);
                                            // System.out.println("p_name----------"+row.get("P_NAME"));
                                            gst.put("idt", idt);
                                            js.put("ctin", gstin);
                                            gst.put("vcode", vcode);
                                            gst.put("inum", inum);
                                            gst.put("rsn", rsn);
                                            gst.put("nt_dt", nt_dt);
                                            g.add(gst);
                                            // gst.put("vamt", vamt);
                                        }
                                    } else {
                                        flag = "0";
                                        samt = Math.round(itm_det.asObject().getFloat("samt", 0));
                                        camt = Math.round(itm_det.asObject().getFloat("camt", 0));
                                        txtval = Math.round(itm_det.asObject().getFloat("txval", 0));
                                        rt = itm_det.asObject().getInt("rt", 0);
                                        // System.out.println("iamt---"+samt);
                                        i++;
                                        for (Map row : al) {

                                            P_NAME = row.get("P_NAME").toString();
                                            vamt = Math.round(Float.parseFloat(row.get("vamt").toString()));
                                            // System.out.println("vamt---" + vamt);
                                            try {
                                                vstform = Integer.parseInt(row.get("vstform").toString().replaceAll("[^0-9]", ""));
                                                //  System.out.println("vstform----" + vstform);
                                            } catch (Exception e) {
                                                vstform = 0;
                                            }

                                            //System.out.println("samt--" + samt + "  camt----" + camt + " txtval---" + txtval + "  vamt" + vamt);
                                            if (((samt == vamt)) || ((samt + 1) == vamt) || ((camt == vamt)) || ((camt + 1) == vamt) || ((txtval == vamt)) || ((txtval + 1) == vamt) && (rt == vstform)) {
                                                // if (((samt == vamt) || (camt == vamt) || (txtval == vamt) && (rt == vstform))) {
                                                flag = "1";

                                            }

                                        }
                                        if (flag.equals("0")) {
                                            JSONObject gst = new JSONObject();
                                            System.out.println("not equal=================================" + i);
                                            gst.put("samt", samt);
                                            gst.put("camt", camt);
                                            //gst.put("vamt", vamt);
                                            gst.put("rt", rt);
                                            gst.put("txtval", txtval);
                                            gst.put("comp_result", "Not Found");
                                            gst.put("P_NAME", P_NAME);
                                            gst.put("vcode", vcode);
                                            // System.out.println("p_name----------"+row.get("P_NAME"));
                                            gst.put("idt", idt);
                                            js.put("ctin", gstin);
                                            gst.put("vcode", vcode);
                                            gst.put("inum", inum);
                                            gst.put("rsn", rsn);
                                            gst.put("nt_dt", nt_dt);
                                            g.add(gst);
                                        }

                                    }

                                    //  notmatchhash.add(notmatchgst);
                                    // System.out.println("g---"+g);
                                }

                            }

                        }
                        js.put("gst", g);

                        // js.put("notmatchgst", notmatchhash);
                        //System.out.println("js---" + js);
                    }
                }

                list1.add(js);
                j1.put("unmatchcdn", list1);

            }
        }
        return j1;
    }

    public List getGstinNotFound(JSONObject json) {
        String js1 = json.toString();
        List list = new ArrayList();
        String gstin = null, sql, sql1, vcode = "", idt, state_code, inum, flag = "0", P_NAME = "", comp = "", comp_name = "", mainstate_code = "", state;
        float iamt, txtval, samt, csamt, camt, vamt;
        int num, vstform = 0, rt, i = 0;
        String compgstin = Json.parse(js1).asObject().get("gstin").asString();
        System.out.println("gstin-----" + compgstin);
        if (compgstin.equals("09AAACR1008A1Z5")) {
            comp = "6";
            comp_name = "FASHIONS";
            mainstate_code = "09";
        } else if (compgstin.equals("09AAGPM3196B1ZQ")) {
            comp = "1";
            state = "(UP)";
            comp_name = "JOSS";
            mainstate_code = "09";
        } else if (compgstin.equals("07AAGPM3196B1ZU")) {
            comp = "1";
            state = "(DELHI)";
            comp_name = "JOSS";
            mainstate_code = "07";
        } else if (compgstin.equals("07AAACR1008A1Z9")) {
            comp = "6";
            state = "(DELHI)";
            comp_name = "FASHIONS";
            mainstate_code = "07";
        } else if (compgstin.equals("07AAACR4021R1Z7")) {
            comp = "2";
            state = "";
            comp_name = "MERCHANTS";
            mainstate_code = "07";
        }
        List<Map<String, Object>> livcode = new ArrayList();
        com.eclipsesource.json.JsonArray bcodeitems = Json.parse(js1).asObject().get("b2b").asArray();
        //System.out.println("size-----------" + bcodeitems.size());
        for (com.eclipsesource.json.JsonValue bitem : bcodeitems) {
            JSONObject js = new JSONObject();
            // System.out.println("-ctin---" + bitem.asObject().getString("ctin", "null"));
            gstin = bitem.asObject().getString("ctin", "null");

            state_code = gstin.substring(0, 2);
            //   System.out.println("state-----" + state_code);
            sql = "select p_code,p_sub from party where gstin='" + gstin + "'";
            livcode = template.queryForList(sql);
            HashSet g = new HashSet();
            HashSet notmatchhash = new HashSet();
            if (livcode.isEmpty()) {

                com.eclipsesource.json.JsonArray invo = bitem.asObject().get("inv").asArray();
                for (com.eclipsesource.json.JsonValue inv : invo) {

                    idt = inv.asObject().getString("idt", "");
                    inum = inv.asObject().getString("inum", "");
                    // gst.put("idt", idt);
                    // System.out.println("inum====="+inum);
                    com.eclipsesource.json.JsonArray it = inv.asObject().get("itms").asArray();
                    for (com.eclipsesource.json.JsonValue itms : it) {
                        // System.out.println("itms----"+itms); 
                        num = itms.asObject().getInt("num", 0);
                            // js.put("num", num);

                        // System.out.println("numm---"+num);
                        JsonObject itm_det = itms.asObject().get("itm_det").asObject();
                        // System.out.println("itm_det======="+itm_det);
                        //System.out.println("iamt======="+itm_det.asObject().getFloat("iamt", 0));
                        // js.put("ctin", gstin);

                        if (!state_code.equals(mainstate_code)) {
                            flag = "0";
                            iamt = Math.round(itm_det.asObject().getFloat("iamt", 0));
                            txtval = Math.round(itm_det.asObject().getFloat("txval", 0));
                            rt = itm_det.asObject().getInt("rt", 0);
                            // System.out.println("iamt---"+iamt);
                            i++;

                            //  System.out.println("iamt--" + iamt + "  txtval---" + txtval + "  vamt" + vamt);
                            JSONObject gst = new JSONObject();

                            gst.put("iamt", iamt);
                            gst.put("txtval", txtval);
                            gst.put("rt", rt);
                            gst.put("comp_result", "Not Found");
                            gst.put("P_NAME", P_NAME);
                            gst.put("vcode", vcode);
                            // System.out.println("p_name----------"+row.get("P_NAME"));
                            gst.put("idt", idt);
                            js.put("ctin", gstin);
                            gst.put("vcode", vcode);
                            gst.put("inum", inum);
                            g.add(gst);
                            // gst.put("vamt", vamt);
                        } else {
                            flag = "0";
                            samt = Math.round(itm_det.asObject().getFloat("samt", 0));
                            camt = Math.round(itm_det.asObject().getFloat("camt", 0));
                            txtval = Math.round(itm_det.asObject().getFloat("txval", 0));
                            rt = itm_det.asObject().getInt("rt", 0);
                            // System.out.println("iamt---"+samt);

                            JSONObject gst = new JSONObject();

                            gst.put("samt", samt);
                            gst.put("camt", camt);
                            //gst.put("vamt", vamt);
                            gst.put("rt", rt);
                            gst.put("txtval", txtval);
                            gst.put("comp_result", "Not Found");
                            gst.put("P_NAME", P_NAME);
                            gst.put("vcode", vcode);
                            // System.out.println("p_name----------"+row.get("P_NAME"));
                            gst.put("idt", idt);
                            js.put("ctin", gstin);
                            gst.put("vcode", vcode);
                            gst.put("inum", inum);
                            g.add(gst);
                        }
                    }
                    js.put("gst", g);
                    //  notmatchhash.add(notmatchgst);
                    // System.out.println("g---"+g);
                }
                list.add(js);
            }
        }

        return list;
        // js.put("notmatchgst", notmatchhash);
        //System.out.println("js---" + js);
    }

    public JSONObject getDatabtoJsonComp(JSONObject json, String comp) {
        String sql;
        String gstin, compcode = "", gst = "", compname = "";
        String gstin1;
        List al = new ArrayList();

        int iamt = (int) Math.round(1444.28);
        System.out.println("iamt------------" + iamt);

        Date d1 = new Date();
        String js1 = json.toString();
        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("company----" + comp);
        String curr = dt.format(d1);
        JSONObject j = new JSONObject();
        j.put("curr_date", curr);
        if (comp.equals("fashup")) {
            compcode = "6";
            compname = "Fashions UP";
            gst = "09AAACR1008A1Z5";

        } else if (comp.equals("jdl")) {
            compcode = "1";
            compname = "Joss DELHI";
            gst = "07AAGPM3196B1ZU";
        } else if (comp.equals("jup")) {
            compcode = "1";
            compname = "Joss UP";

            gst = "09AAGPM3196B1ZQ";
        } else if (comp.equals("jup")) {
            compcode = "1";
            compname = "Joss UP";

            gst = "09AAGPM3196B1ZQ";
        } else if (comp.equals("fashdl")) {
            compcode = "6";
            compname = "Fashions Delhi";

            gst = "07AAACR1008A1Z9";
        } else if (comp.equals("mer")) {
            compcode = "2";
            compname = "Merchants";

            gst = "07AAACR4021R1Z7";
        }
        System.out.println("gst----" + gst);
        j.put("compname", compname);
        //  String sql1="select v.vstform,v.vbillno,v.vbilldt,v.vamt,v.vrcode,v.vcode,v.vno,v.vtype,gsn_match,p.gstin, p.P_NAME from vou"+compcode+"1718 v,party p where  v.vtype='PB' and v.vstform like '%GST%' and  v.vRcode like '100%'  and v.vcode like '248%' and v.vbilldt >= '21-AUG-17' and v.vsub='A'  and p.p_code=SUBSTR(v.vrcode,1,3) and p.p_sub=SUBSTR(v.vrcode,4)";
        String sql1 = "select v.vstform,v.vbillno,v.vbilldt,v.vamt,v.vrcode,v.vcode,v.vno,v.vtype,gsn_match,p.gstin, p.P_NAME from vou" + compcode + "1718 v,party p where  v.vtype='PB' and v.vstform like '%GST%' and  v.vcode like '248%' and v.vsub='A'  and v.vRcode like '100%'  and v.vbilldt LIKE '%JUL-17'  and v.gsn_match is NULL and p.p_code=SUBSTR(v.vrcode,1,3) and p.p_sub=SUBSTR(v.vrcode,4) AND   vno not in (select vno from vou61718 where vcode like '081%' or vrcode like '081%')  ";
        // String sql1 = "select v.vstform,v.vbillno,v.vbilldt,v.vamt,v.vrcode,v.vcode,v.vno,v.vdt,v.vtype,v.gsn_match,p.gstin from vou11718 v,party p, gstvou11718 g where  v.vtype='PB' and g.gstin1='09AAGPM3196B1ZQ' and v.vstform like '%GST%' and v.vRcode like '100%' and v.gsn_match is null and v.vcode like '248%' and v.vbilldt LIKE '%JUL-17' and p.p_code=SUBSTR(v.vrcode,1,3) and p.p_sub=SUBSTR(v.vrcode,4)";
        System.out.println("sql1-----------" + sql1);
        List<Map<String, Object>> li = template.queryForList(sql1);
        System.out.println("li---" + li);
        if (!li.isEmpty()) {
            for (Map row : li) {
                JSONObject js = new JSONObject();
                sql = "select gstin1 from gstvou" + compcode + "1718 where vno='" + row.get("vno") + "' and vamt='" + row.get("vamt") + "' AND VRCODE='" + row.get("vrcode") + "' and rownum=1";
                try {
                    //gstin1=template.queryForObject(sql,new Object[]{row.get("vno"),row.get("vamt")},String.class);
                    gstin1 = template.queryForObject(sql, String.class);
                } catch (Exception e) {
                    gstin1 = "";
                }
                System.out.println("sql---------" + sql);
                System.out.println("gstin1---------" + gstin1);
                //  if(gstin1.equals("09AAACR1008A1Z5")){ //fashions
                if (gst.equals(gstin1)) {
                    //  if(gstin1.equals("07AAGPM3196B1ZU")){ //joss dl
                    //if(gstin1.equals("09AAGPM3196B1ZQ")){ //joss up
                /* sql = "select gstin from party where p_code='" + row.get("VRCODE").toString().substring(0, 3) + "' and p_sub='" + row.get("VRCODE").toString().substring(3) + "'";
                     System.out.println("sql---" + sql);
                     try {
                     gstin = template.queryForObject(sql, String.class);
                     } catch (Exception e) {
                     gstin = "";
                     }
                     System.out.println("gstin----" + gstin);*/
                    js.put("gstin", row.get("gstin"));
                    js.put("gstin1", gstin1);
                    js.put("vbillno", row.get("vbillno"));
                    js.put("vbilldt", dt.format(row.get("vbilldt")));
                    js.put("vamt", row.get("vamt"));
                    js.put("vrcode", row.get("vrcode"));
                    js.put("vcode", row.get("vcode"));
                    js.put("rt", row.get("vstform"));
                    js.put("partyname", row.get("P_NAME"));
                    js.put("vno", row.get("vno"));

                    al.add(js);
                }

            }
            System.out.println("al----" + al);
            j.put("data", al);
        }
        return j;
    }

    public JSONObject getDateWiseGst(String comp, String start_date1, String end_date1) {
        String sql, sql1;
        String gstin, compcode = "", gst = "", compname = "";
        String gstin1;
        List al = new ArrayList();
        int iamt = (int) Math.round(1444.28);
        System.out.println("iamt------------" + iamt);

        SimpleDateFormat dt = new SimpleDateFormat("dd-MMM-yy");
        SimpleDateFormat dtparse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        String start_date = "", end_date = "";
        Date start;
        try {
            start = dtparse.parse(start_date1);
            start_date = dt.format(start);
            Date end = dtparse.parse(end_date1);
            end_date = dt.format(end);
        } catch (ParseException ex) {
            System.out.println("error---------");
        }
        Date d1 = new Date();

        SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("company----" + comp);
        String curr = dt1.format(d1);
        JSONObject j = new JSONObject();
        j.put("curr_date", curr);
        if (comp.equals("fashup")) {
            compcode = "6";
            compname = "Fashions UP";
            gst = "09AAACR1008A1Z5";
        } else if (comp.equals("jdl")) {
            compcode = "1";
            compname = "Joss DELHI";
            gst = "07AAGPM3196B1ZU";
        } else if (comp.equals("jup")) {
            compcode = "1";
            compname = "Joss UP";
            gst = "09AAGPM3196B1ZQ";
        } else if (comp.equals("jup")) {
            compcode = "1";
            compname = "Joss UP";
            gst = "09AAGPM3196B1ZQ";
        } else if (comp.equals("fashdl")) {
            compcode = "6";
            compname = "Fashions Delhi";
            gst = "07AAACR1008A1Z9";
        } else if (comp.equals("mer")) {
            compcode = "2";
            compname = "Merchants";
            gst = "07AAACR4021R1Z7";
        }
        System.out.println("gst----" + gst);
        j.put("compname", compname);
        if (end_date1.equals("")) {
            sql1 = "select v.vstform,v.vbillno,v.vbilldt,v.vamt,v.vrcode,v.vcode,v.vno,v.vtype,gsn_match,p.gstin, p.P_NAME from vou" + compcode + "1718 v,party p where  v.vtype='PB' and v.vstform like '%GST%' and  v.vRcode like '100%'  and v.vcode like '248%' and v.vbilldt >= '" + start_date + "'  and v.vsub='A'  and p.p_code=SUBSTR(v.vrcode,1,3) and p.p_sub=SUBSTR(v.vrcode,4)";
        } else {
            sql1 = "select v.vstform,v.vbillno,v.vbilldt,v.vamt,v.vrcode,v.vcode,v.vno,v.vtype,gsn_match,p.gstin, p.P_NAME from vou" + compcode + "1718 v,party p where  v.vtype='PB' and v.vstform like '%GST%' and  v.vRcode like '100%'  and v.vcode like '248%' and v.vbilldt >= '" + start_date + "' and v.vbilldt <= '" + end_date + "' and v.vsub='A'  and p.p_code=SUBSTR(v.vrcode,1,3) and p.p_sub=SUBSTR(v.vrcode,4)";
        }
        System.out.println("sql1-----------" + sql1);
        List<Map<String, Object>> li = template.queryForList(sql1);
        System.out.println("li---" + li);
        if (!li.isEmpty()) {
            for (Map row : li) {
                JSONObject js = new JSONObject();
                sql = "select gstin1 from gstvou" + compcode + "1718 where vno='" + row.get("vno") + "' and vamt='" + row.get("vamt") + "' AND VRCODE='" + row.get("vrcode") + "' and rownum=1";
                try {
                    //gstin1=template.queryForObject(sql,new Object[]{row.get("vno"),row.get("vamt")},String.class);
                    gstin1 = template.queryForObject(sql, String.class);
                } catch (Exception e) {
                    gstin1 = "";
                }
                System.out.println("sql---------" + sql);
                System.out.println("gstin1---------" + gstin1);
                //  if(gstin1.equals("09AAACR1008A1Z5")){ //fashions
                if (gst.equals(gstin1)) {
                    //  if(gstin1.equals("07AAGPM3196B1ZU")){ //joss dl
                    //if(gstin1.equals("09AAGPM3196B1ZQ")){ //joss up
                /* sql = "select gstin from party where p_code='" + row.get("VRCODE").toString().substring(0, 3) + "' and p_sub='" + row.get("VRCODE").toString().substring(3) + "'";
                     System.out.println("sql---" + sql);
                     try {
                     gstin = template.queryForObject(sql, String.class);
                     } catch (Exception e) {
                     gstin = "";
                     }
                     System.out.println("gstin----" + gstin);*/
                    js.put("gstin", row.get("gstin"));
                    js.put("gstin1", gstin1);
                    js.put("vbillno", row.get("vbillno"));
                    js.put("vbilldt", dt.format(row.get("vbilldt")));
                    js.put("vamt", row.get("vamt"));
                    js.put("vrcode", row.get("vrcode"));
                    js.put("vcode", row.get("vcode"));
                    js.put("rt", row.get("vstform"));
                    js.put("partyname", row.get("P_NAME"));
                    al.add(js);
                }

            }
            System.out.println("al----" + al);
            j.put("data", al);
        } else {
            li = null;
        }
        return j;
    }

    public List getPartyCode(String comp) {
        String compcode = "", compname = "", gst = "";
        if (comp.equals("fashup")) {
            compcode = "6";
            compname = "Fashions UP";
            gst = "09AAACR1008A1Z5";

        } else if (comp.equals("jdl")) {
            compcode = "1";
            compname = "Joss DELHI";
            gst = "07AAGPM3196B1ZU";
        } else if (comp.equals("jup")) {
            compcode = "1";
            compname = "Joss UP";
            gst = "09AAGPM3196B1ZQ";
        } else if (comp.equals("jup")) {
            compcode = "1";
            compname = "Joss UP";
            gst = "09AAGPM3196B1ZQ";
        } else if (comp.equals("fashdl")) {
            compcode = "6";
            compname = "Fashions Delhi";
            gst = "07AAACR1008A1Z9";
        } else if (comp.equals("mer")) {
            compcode = "2";
            compname = "Merchants";
            gst = "07AAACR4021R1Z7";
        }
        String sql1;
        String sql = "select vrcode,p.p_name from gstvou" + compcode + "1718, party p where gstin1='" + gst + "' and  p.p_code=SUBSTR(vrcode,1,3) and p.p_sub=SUBSTR(vrcode,4) and vrcode like '100%' ";
        System.out.println("sql---------" + sql);
        List<Map<String, Object>> li = template.queryForList(sql);
        HashSet hs = new HashSet();
        hs.addAll(li);
        li.clear();
        li.addAll(hs);
        System.out.println("li-----" + li);
        return li;
    }

    public List getCompData(String comp, String party) {
        SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yyyy");
        String gstin, gstin1 = "", compcode = "", gst = "", compname = "";
        List al = new ArrayList();
        JSONObject j = new JSONObject();
        SimpleDateFormat dt = new SimpleDateFormat("dd-MMM-yy");
        if (comp.equals("fashup")) {
            compcode = "6";
            compname = "Fashions UP";
            gst = "09AAACR1008A1Z5";
        } else if (comp.equals("jdl")) {
            compcode = "1";
            compname = "Joss DELHI";
            gst = "07AAGPM3196B1ZU";
        } else if (comp.equals("jup")) {
            compcode = "1";
            compname = "Joss UP";
            gst = "09AAGPM3196B1ZQ";
        } else if (comp.equals("jup")) {
            compcode = "1";
            compname = "Joss UP";
            gst = "09AAGPM3196B1ZQ";
        } else if (comp.equals("fashdl")) {
            compcode = "6";
            compname = "Fashions Delhi";
            gst = "07AAACR1008A1Z9";
        } else if (comp.equals("mer")) {
            compcode = "2";
            compname = "Merchants";
            gst = "07AAACR4021R1Z7";
        }
        String sql = "select v.vstform,v.vbillno,v.vbilldt,v.vamt,v.vrcode,v.vcode,v.vno,v.vtype,gsn_match,p.gstin,p.p_name from vou" + compcode + "1718 v,party p where  v.vtype='PB' and v.vstform like '%GST%' and  v.vRcode like '100%' and v.vrcode='" + party + "' and v.vcode like '248%'   and v.vsub='A'  and p.p_code=SUBSTR(v.vrcode,1,3) and p.p_sub=SUBSTR(v.vrcode,4)";
        System.out.println("sql1-----------" + sql);
        List<Map<String, Object>> li = template.queryForList(sql);
        System.out.println("li---" + li);
        if (!li.isEmpty()) {
            for (Map row : li) {
                JSONObject js = new JSONObject();
                sql = "select gstin1 from gstvou" + compcode + "1718 where vno='" + row.get("vno") + "' and vamt='" + row.get("vamt") + "' AND VRCODE='" + row.get("vrcode") + "' and rownum=1";
                try {
                    //gstin1=template.queryForObject(sql,new Object[]{row.get("vno"),row.get("vamt")},String.class);
                    gstin1 = template.queryForObject(sql, String.class);
                } catch (Exception e) {
                    gstin1 = "";
                }
                System.out.println("sql---------" + sql);
                System.out.println("gstin1---------" + gstin1);
                //  if(gstin1.equals("09AAACR1008A1Z5")){ //fashions
                if (gst.equals(gstin1)) {
                    js.put("gstin", row.get("gstin"));
                    js.put("gstin1", gstin1);
                    js.put("vbillno", row.get("vbillno"));
                    js.put("vbilldt", dt.format(row.get("vbilldt")));
                    js.put("vamt", row.get("vamt"));
                    js.put("vrcode", row.get("vrcode"));
                    js.put("vcode", row.get("vcode"));
                    js.put("rt", row.get("vstform"));
                    js.put("partyname", row.get("P_NAME"));
                    al.add(js);
                }

            }
            System.out.println("al----" + al);
            j.put("data", al);
        }
        return al;
    }

    public JSONObject getCompDatafromGST(String comp) {
        SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yyyy");
        float vbillamt = 0, vbillres;
        int vstform;
        String gstin, gstin1 = "", compcode = "", gst = "", compname = "", sqlll;
        List al = new ArrayList();
        JSONObject j = new JSONObject();
        SimpleDateFormat dt = new SimpleDateFormat("dd-MMM-yy");
        if (comp.equals("fashup")) {
            compcode = "6";
            compname = "Fashions UP";
            gst = "09AAACR1008A1Z5";
        } else if (comp.equals("jdl")) {
            compcode = "1";
            compname = "Joss DELHI";
            gst = "07AAGPM3196B1ZU";
        } else if (comp.equals("jup")) {
            compcode = "1";
            compname = "Joss UP";
            gst = "09AAGPM3196B1ZQ";
        } else if (comp.equals("jup")) {
            compcode = "1";
            compname = "Joss UP";
            gst = "09AAGPM3196B1ZQ";
        } else if (comp.equals("fashdl")) {
            compcode = "6";
            compname = "Fashions Delhi";
            gst = "07AAACR1008A1Z9";
        } else if (comp.equals("mer")) {
            compcode = "2";
            compname = "Merchants";
            gst = "07AAACR4021R1Z7";
        }
        System.out.println("gst----"+gst+"  "+compname+"  "+compcode+"  "+"  ");
        System.out.println("company name----"+compname);
        int vrow;
        String sql = "select v.vstform,v.vbillno,v.vbilldt,v.vamt,v.vrcode,v.vcode,v.vno,v.vrow,v.vtype,gsn_match,p.gstin,p.p_name from vou" + compcode + "1718 v,party p where  v.vtype='PB' and v.vstform like '%GST%' and  v.vRcode like '100%'  and v.vcode like '248%'   and v.vsub='A'  and p.p_code=SUBSTR(v.vrcode,1,3) and p.p_sub=SUBSTR(v.vrcode,4)";
        // System.out.println("sql1-----------" + sql);
        List<Map<String, Object>> li = template.queryForList(sql);
        //  System.out.println("li---" + li);
        if (!li.isEmpty()) {
            for (Map row : li) {
                JSONObject js = new JSONObject();
                vrow = 0;
                sql = "select gstin1 from gstvou" + compcode + "1718 where vno='" + row.get("vno") + "' and vamt='" + row.get("vamt") + "' AND VRCODE='" + row.get("vrcode") + "' and rownum=1";
                try {
                    //gstin1=template.queryForObject(sql,new Object[]{row.get("vno"),row.get("vamt")},String.class);
                    gstin1 = template.queryForObject(sql, String.class);
                } catch (Exception e) {
                    gstin1 = "";
                    //System.out.println("gstin1----"+gstin1);
                }
                //System.out.println("sql---------" + sql);
                // System.out.println("gstin1---------" + gstin1);
                //  if(gstin1.equals("09AAACR1008A1Z5")){ //fashions
                if (gst.equals(gstin1)) {
                    sqlll = "select vamt,vstform from vou" + compcode + "1718 where VRCODE='" + row.get("vrcode") + "' and vbillno='" + row.get("vbillno") + "' and vbilldt='" + dt.format(row.get("vbilldt")) + "' and vno='" + row.get("vno") + "' and vstform ='" + row.get("vstform") + "' and (vcode) not like('248%' ||'100%')  and vamt !='" + row.get("vamt") + "' and vdrcr='DR'";
                    System.out.println("sql11------" + sqlll);
                    List<Map<String, Object>> vbilamt = template.queryForList(sqlll);
                    for (Map r : vbilamt) {
                        vbillamt = Float.parseFloat(r.get("vamt").toString());
                       // System.out.println("vbillamt-----" + vbillamt);
                        vstform = Integer.parseInt(r.get("vstform").toString().replaceAll("[^0-9]", ""));
                        //  System.out.println("vstform----"+vstform);
                    }
                    /* try {
                     vbillamt = template.queryForObject(sqlll, Integer.class);
                     } catch (Exception e) {
                     vbillamt = 0;
                     }*/
                    js.put("gstin", row.get("gstin"));
                    js.put("gstin1", gstin1);
                    js.put("vbillno", row.get("vbillno"));
                    js.put("vbilldt", dt.format(row.get("vbilldt")));
                    js.put("vamt", row.get("vamt"));
                    js.put("vbillamt", vbillamt);
                    js.put("vrcode", row.get("P_NAME") + " (" + row.get("vrcode") + " )");
                    js.put("vcode", row.get("vcode"));
                    js.put("rt", row.get("vstform"));
                    js.put("partyname", row.get("P_NAME"));
                    //System.out.println("js----"+js);
                    System.out.println("js--"+js);
                    al.add(js);
                }
            }
            // System.out.println("al----" + al);
            j.put("data", al);
            j.put("compname", compname);
        }
        return j;
    }

    public JSONObject getCurrentDate() {
        Date cdt = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("dd-MMM-yy");
        String curr_date = dt.format(cdt);
        JSONObject js = new JSONObject();
        js.put("curr_date", curr_date);
        return js;
    }

    public JSONObject getdata() {
        JSONObject js = new JSONObject();
        boolean b=true;
        js.put(js, js);
        System.out.println("js---");
        return js;
    }
}
