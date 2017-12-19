package com.rmx.gst.Controller;

import com.rmx.gst.Dao.GstDao;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GstController {

    @Autowired
    GstDao gst;

    @RequestMapping(value = "/Gst", method = RequestMethod.GET)
    public String getGSTPage(Model model) {
        return "Gst";
    }

    @RequestMapping(value = "getGstData", method = RequestMethod.GET)
    public @ResponseBody
    JSONObject getGstDataAcc(@RequestParam(value = "start_date") String start, @RequestParam(value = "end_date") String end, HttpSession session) {
        System.out.println("gst---");
        return gst.getGstData(start, end, session);
        // return null;
    }

    @RequestMapping(value = "getyearCompcode", method = RequestMethod.GET)
    public @ResponseBody
    List getyearCompcode() {
        return gst.getyearCompcode();
    }

    @RequestMapping(value = "selectyear", method = RequestMethod.GET)
    public @ResponseBody
    String selectyear(@RequestParam(value = "year") String year, @RequestParam(value = "comp") String comp, HttpSession session) {
        gst.selectyear(year, comp, session);
        return "";
    }

    @RequestMapping(value = "getjson", method = RequestMethod.GET)
    public @ResponseBody
    JSONObject getjson(@RequestParam(value = "comp") String comp, @RequestParam(value = "month") String month, @RequestParam(value = "year") String year) {
        JSONParser parser = new JSONParser();
        String compname = "", gstin = "";
        JSONObject data = new JSONObject();
        if (comp.equals("fashup")) {
            compname = "Fashions";
            gstin = "09AAACR1008A1Z5";
        } else if (comp.equals("jdl")) {
            compname = "Joss";
            gstin = "07AAGPM3196B1ZU";
        } else if (comp.equals("jup")) {
            compname = "Joss";
            gstin = "09AAGPM3196B1ZQ";
        } else if (comp.equals("fashdl")) {
            compname = "Fashions";
            gstin = "07AAACR1008A1Z9";
        } else if (comp.equals("mer")) {
            compname = "Merchants";
            gstin = "07AAACR4021R1Z7";
        }
        try {
            String filename="F:/GST Data/"+compname+"/" + year + "/" + month + "/"+gstin+".json";
            data = (JSONObject) parser.parse(new FileReader(filename));
            
            System.out.println("read from file=========="+filename);
        } catch (IOException ex) {
            Logger.getLogger(GstController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(GstController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    @RequestMapping(value = "/JsonParse", method = RequestMethod.GET)
    public String JsonParse() {
        return "JsonParse";
    }

    @RequestMapping(value = "/GstComparison", method = RequestMethod.GET)
    public String GstComparison() {
        return "GstComparison";
    }

    @RequestMapping(value = "getjsonGst", method = RequestMethod.GET)
    public @ResponseBody
    List getjsonGst(@RequestParam(value = "comp") String comp, @RequestParam(value = "month") String month, @RequestParam(value = "year") String year) {
        JSONParser parser = new JSONParser();
        String compname = "", gstin = "";
        JSONObject data = new JSONObject();
        if (comp.equals("fashup")) {
            compname = "Fashions";
            gstin = "09AAACR1008A1Z5";
        } else if (comp.equals("jdl")) {
            compname = "Joss";
            gstin = "07AAGPM3196B1ZU";
        } else if (comp.equals("jup")) {
            compname = "Joss";
            gstin = "09AAGPM3196B1ZQ";
        } else if (comp.equals("fashdl")) {
            compname = "Fashions";
            gstin = "07AAACR1008A1Z9";
        } else if (comp.equals("mer")) {
            compname = "Merchants";
            gstin = "07AAACR4021R1Z7";
        }
        try {
            String filename="F:/GST Data/"+compname+"/" + year + "/" + month + "/"+gstin+".json";
            data = (JSONObject) parser.parse(new FileReader(filename));
            
            System.out.println("read from file=========="+filename);
        } catch (IOException ex) {
            Logger.getLogger(GstController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(GstController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return gst.jsonComparison(data);
    }

    @RequestMapping(value = "getUnmatched", method = RequestMethod.GET)
    public @ResponseBody
    JSONObject getUnmatched(@RequestParam(value = "comp") String comp, @RequestParam(value = "month") String month, @RequestParam(value = "year") String year) {
        JSONParser parser = new JSONParser();
         String compname = "", gstin = "";
        JSONObject data = new JSONObject();
        if (comp.equals("fashup")) {
            compname = "Fashions";
            gstin = "09AAACR1008A1Z5";
        } else if (comp.equals("jdl")) {
            compname = "Joss";
            gstin = "07AAGPM3196B1ZU";
        } else if (comp.equals("jup")) {
            compname = "Joss";
            gstin = "09AAGPM3196B1ZQ";
        } else if (comp.equals("fashdl")) {
            compname = "Fashions";
            gstin = "07AAACR1008A1Z9";
        } else if (comp.equals("mer")) {
            compname = "Merchants";
            gstin = "07AAACR4021R1Z7";
        }
        try {
            String filename="F:/GST Data/"+compname+"/" + year + "/" + month + "/"+gstin+".json";
            data = (JSONObject) parser.parse(new FileReader(filename));
            
            System.out.println("read from file=========="+filename);
        } catch (IOException ex) {
            Logger.getLogger(GstController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(GstController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return gst.getUnmatched(data);
    }

    @RequestMapping(value = "getDatabtoJsonComp", method = RequestMethod.GET)
    public @ResponseBody
    JSONObject getDatabtoJsonComp(@RequestParam(value = "comp") String comp, @RequestParam(value = "month") String month, @RequestParam(value = "year") String year) {
        JSONParser parser = new JSONParser();
        String compname = "", gstin = "";
        JSONObject data = new JSONObject();
        if (comp.equals("fashup")) {
            compname = "Fashions";
            gstin = "09AAACR1008A1Z5";
        } else if (comp.equals("jdl")) {
            compname = "Joss";
            gstin = "07AAGPM3196B1ZU";
        } else if (comp.equals("jup")) {
            compname = "Joss";
            gstin = "09AAGPM3196B1ZQ";
        } else if (comp.equals("fashdl")) {
            compname = "Fashions";
            gstin = "07AAACR1008A1Z9";
        } else if (comp.equals("mer")) {
            compname = "Merchants";
            gstin = "07AAACR4021R1Z7";
        }
        try {
            String filename="F:/GST Data/"+compname+"/" + year + "/" + month + "/"+gstin+".json";
            data = (JSONObject) parser.parse(new FileReader(filename));
            
            System.out.println("read from file=========="+filename);
        } catch (IOException ex) {
            Logger.getLogger(GstController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(GstController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return gst.getDatabtoJsonComp(data, comp);
    }

    @RequestMapping(value = "/DatabasetoJsonComparsn", method = RequestMethod.GET)
    public String getDatabasetoJsonComparsn() {
        return "DatabasetoJsonComparsn";
    }

    @RequestMapping(value = "/DateWiseGst", method = RequestMethod.GET)
    public String getDateWiseGst() {
        return "DateWiseGst";
    }

    @RequestMapping(value = "getDateWiseGst", method = RequestMethod.GET)
    public @ResponseBody
    JSONObject getDateWiseGst(@RequestParam(value = "comp") String comp, @RequestParam(value = "startdate") String startdate, @RequestParam(value = "enddate") String enddate) {
        return gst.getDateWiseGst(comp, startdate, enddate);
    }
    @RequestMapping(value = "getPartyCode", method = RequestMethod.GET)
    public @ResponseBody
    List getPartyCode(@RequestParam(value = "comp") String comp) {
        return gst.getPartyCode(comp);
    }
    @RequestMapping(value = "/GSTAngular", method = RequestMethod.GET)
    public String getGSTAngular() {
        return "GSTAngular";
    }
    @RequestMapping(value = "getCompData", method = RequestMethod.GET)
    public @ResponseBody
    List getCompData(@RequestParam(value = "comp") String comp,@RequestParam(value = "party") String party) {
        return gst.getCompData(comp,party);
    }
     @RequestMapping(value = "getCompDatafromGST", method = RequestMethod.GET)
    public @ResponseBody
    JSONObject getCompDatafromGST(@RequestParam(value = "comp") String comp) {
        return gst.getCompDatafromGST(comp);
    }
    @RequestMapping(value = "getCurrentDate", method = RequestMethod.GET)
    public @ResponseBody    JSONObject getCurrentDate() {
        return gst.getCurrentDate();
    }
}
