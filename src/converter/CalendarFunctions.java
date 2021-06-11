package converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Pattern;

import com.csvreader.CsvReader;

public class CalendarFunctions implements Serializable {

    /**
     *
     * @author Sijan Bhandari
     */
    private static final long serialVersionUID = 1L;
    CsvReader cvsDate;
    CsvReader cvsIntializeDate;
    public static String strSeprator = "/";

    public enum Diff {
        YEAR, MONTH, DAY;
    }

    public CalendarFunctions() {
    }

    public boolean isDate(CharSequence date) {

        String time = "(\\s(([01]?\\d)|(2[0123]))[:](([012345]\\d)|(60))"
                + "[:](([012345]\\d)|(60)))?"; // with a space before, zero or
        // one time

        // no check for leap years (Schaltjahr)
        // and 31.02.2006 will also be correct
        String day = "(([12]\\d)|(3[01])|(0?[1-9]))"; // 01 up to 31
        String month = "((1[012])|(0\\d))"; // 01 up to 12
        String year = "\\d{4}";

        // define here all date format
        ArrayList<String> patterns = new ArrayList<String>();
        patterns.add(day + "[-.]" + month + "[-.]" + year + time);
        patterns.add(year + "-" + month + "-" + day + time);
        patterns.add(year + "/" + month + "/" + day + time);
        // here you can add more date formats if you want

        // check dates
        for (Object objPa : patterns) {
            String strPa = objPa.toString();
            Pattern p = Pattern.compile(strPa);
            if (p.matcher(date).matches()) {
                return true;
            }
        }
        return false;

    }

    public int getDaysBetween(java.util.Calendar d1, java.util.Calendar d2) {
        if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end
            java.util.Calendar swap = d1;
            d1 = d2;
            d2 = swap;
        }
        int days = d2.get(java.util.Calendar.DAY_OF_YEAR)
                - d1.get(java.util.Calendar.DAY_OF_YEAR);
        int y2 = d2.get(java.util.Calendar.YEAR);
        if (d1.get(java.util.Calendar.YEAR) != y2) {
            d1 = (java.util.Calendar) d1.clone();
            do {
                days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
                d1.add(java.util.Calendar.YEAR, 1);
            } while (d1.get(java.util.Calendar.YEAR) != y2);
        }
        return days;
    } // getDaysBetween()

    public int getDaysBetween(java.util.Date dateFrom, java.util.Date dateTo) {
        Calendar d1 = Calendar.getInstance();
        Calendar d2 = Calendar.getInstance();

        // set the time on d1 and d2
        d1.setTime(dateFrom);
        d2.setTime(dateTo);

        if (d1.after(d2)) {
            // swap dates so that d1 is start and d2 is end
            java.util.Calendar swap = d1;
            d1 = d2;
            d2 = swap;
        }

        int days = d2.get(java.util.Calendar.DAY_OF_YEAR)
                - d1.get(java.util.Calendar.DAY_OF_YEAR);
        int y2 = d2.get(java.util.Calendar.YEAR);

        if (d1.get(java.util.Calendar.YEAR) != y2) {
            d1 = (java.util.Calendar) d1.clone();
            do {
                days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
                d1.add(java.util.Calendar.YEAR, 1);
            } while (d1.get(java.util.Calendar.YEAR) != y2);
        }
        return days;
    } // getDaysBetween()

    public int getMonthsBetween(Date BigEngdate, Date SmallEngdate) {
        Calendar BigCal = Calendar.getInstance();
        Calendar SmallCal = Calendar.getInstance();
        int month1, month2;

        BigCal.setTime(BigEngdate);
        SmallCal.setTime(SmallEngdate);

        month1 = BigCal.get(Calendar.YEAR) * 12 + BigCal.get(Calendar.MONTH);
        month2 = SmallCal.get(Calendar.YEAR) * 12
                + SmallCal.get(Calendar.MONTH);

        return month1 - month2;
    }

    public int getYearBetween(Date BigEngdate, Date SmallEngdate) {
        Calendar BigCal = Calendar.getInstance();
        Calendar SmallCal = Calendar.getInstance();

        BigCal.setTime(BigEngdate);
        SmallCal.setTime(SmallEngdate);
        return BigCal.get(Calendar.YEAR) - SmallCal.get(Calendar.YEAR);
    }

    public int GetDateDiff() {
        return 1;
    }

    public String getSeperator() {
        return strSeprator;
    }

    public void setSeperator(String seperator) {
        strSeprator = seperator;// "Ã¯Â¿Â½";
    }

    public Date GetEnglishDate(String strNepaliDate) {
        // BufferedReader readDateNepali = new BufferedReader(new
        // InputStreamReader(
        // this.getClass().getClassLoader().getResourceAsStream("DateNepali.csv")));
        // BufferedReader readInitializeDate = new BufferedReader(new
        // InputStreamReader(
        // this.getClass().getClassLoader().getResourceAsStream("initializedate.csv")));
        ClassLoader CLDR = this.getClass().getClassLoader();

        BufferedReader readDateNepali = new BufferedReader(
                new InputStreamReader(
                        CLDR.getResourceAsStream("DateNepali.csv")));
        BufferedReader readInitializeDate = new BufferedReader(
                new InputStreamReader(
                        CLDR.getResourceAsStream("initializedate.csv")));

        cvsDate = new CsvReader(readDateNepali);
        cvsIntializeDate = new CsvReader(readInitializeDate);
        // cvsIntializeDate= new CsvReader(FileCvsInitializeDate);
        long daysDiff = 0;
        String[] splitDate = null;
        Date EngDate;
        String strEngDate = null;
        strNepaliDate = strNepaliDate.replaceAll("/", getSeperator());
        if (strNepaliDate == "" || strNepaliDate == null) {
            return null;
        }
        if (strNepaliDate.contains(this.getSeperator()) == false) {
            return null;
        }

        splitDate = strNepaliDate.split(this.getSeperator());

        if (splitDate[0] == "" || splitDate[1] == "" || splitDate[2] == "") {
            return null;
        }

        // cnn = jconn.getconnection();
        try {
            cvsDate.readHeaders();
            while (cvsDate.readRecord()) {
                // year,baishak,jestha,ashad,shrawan,bhadra,aaswin,kartik,mangshir,poush,magh,falgun,chaitra,
                if (Integer.parseInt(cvsDate.get("year")) < Integer
                        .parseInt(splitDate[0])) {
                    String day1 = cvsDate.get("baishak");
                    String day2 = cvsDate.get("jestha");
                    String day3 = cvsDate.get("ashad");
                    String day4 = cvsDate.get("shrawan");
                    String day5 = cvsDate.get("bhadra");
                    String day6 = cvsDate.get("aaswin");
                    String day7 = cvsDate.get("kartik");
                    String day8 = cvsDate.get("mangshir");
                    String day9 = cvsDate.get("poush");
                    String day10 = cvsDate.get("magh");
                    String day11 = cvsDate.get("falgun");
                    String day12 = cvsDate.get("chaitra");
                    daysDiff = daysDiff + Long.parseLong(day1)
                            + Long.parseLong(day2) + Long.parseLong(day3)
                            + Long.parseLong(day4) + Long.parseLong(day5)
                            + Long.parseLong(day6) + Long.parseLong(day7)
                            + Long.parseLong(day8) + Long.parseLong(day9)
                            + Long.parseLong(day10) + Long.parseLong(day11)
                            + Long.parseLong(day12);
                } else if (Integer.parseInt(cvsDate.get("year")) == Integer
                        .parseInt(splitDate[0])) {
                    for (int i = 1; i < Integer.parseInt(splitDate[1]); i++) {
                        // System.out.println("please check this date"+cvsDate.get(i));
                        daysDiff = daysDiff + Long.parseLong(cvsDate.get(i));
                    }
                } else {
                    break;
                }
                // cvsDate.close();
            }
        } catch (IOException e1) {
            cvsDate.close();
            cvsIntializeDate.close();
            try {
                readDateNepali.close();
                readInitializeDate.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            e1.printStackTrace();
        }
        daysDiff = (daysDiff + Long.parseLong(splitDate[2])) - 1;

        try {
            cvsIntializeDate.readHeaders();
            while (cvsIntializeDate.readRecord()) {
                strEngDate = cvsIntializeDate.get("startengdate");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            cvsDate.close();
            cvsIntializeDate.close();
            try {
                readDateNepali.close();
                readInitializeDate.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        try {
            EngDate = df.parse(strEngDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(EngDate);
        cal.add(Calendar.DATE, (int) daysDiff);
        return cal.getTime();
    }

    public static String getMonthNameNepali(int month) {
        switch (month) {
            case 1:
                return "Baishak";
            case 2:
                return "Jestha";

            case 3:
                return "Ashad";

            case 4:
                return "Shrawan";

            case 5:
                return "Bhadra";

            case 6:
                return "Aaswin";

            case 7:
                return "Kartik";

            case 8:
                return "Mangshir";

            case 9:
                return "Poush";

            case 10:
                return "Magh";

            case 11:
                return "Falgun";

            case 12:
                return "Chaitra";

            default:
                break;
        }

        return "";
    }

    public Date GetFormattedEnglishDate(String strNepaliDate) {
        Date dtFormattedDate = new Date();

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");

        try {
            dtFormattedDate = df.parse(df.format(this
                    .GetEnglishDate(strNepaliDate)));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dtFormattedDate;

    }

    public String replaceSeparator(String date, String separator) {
        return date.replaceAll(separator, getSeperator());
    }

    public String getNepaliDate(Date dateEnglishDate) {
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        return GetNepaliDate(s.format(dateEnglishDate));
    }

    public String GetNepaliDate(String strEnglishDate) {

        ClassLoader CLDR = this.getClass().getClassLoader();

        BufferedReader readDateNepali = new BufferedReader(
                new InputStreamReader(
                        CLDR.getResourceAsStream("DateNepali.csv")));
        BufferedReader readInitializeDate = new BufferedReader(
                new InputStreamReader(
                        CLDR.getResourceAsStream("initializedate.csv")));

        cvsDate = new CsvReader(readDateNepali);
        cvsIntializeDate = new CsvReader(readInitializeDate);

        strEnglishDate = strEnglishDate.replace("-", "/");
        long lngDaysDiff, tmpDaysDiff;
        int intcounter;
        Date dtEngDate, dtInitDate;
        // String[] splitdate;
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        NumberFormat formatter = new DecimalFormat("##00");

        if (strEnglishDate == "" || strEnglishDate == null) {
            return null;
        }

        if (isDate(strEnglishDate) == false) {
            return null;
        }
        try {
            cvsIntializeDate.readHeaders();
            while (cvsIntializeDate.readRecord()) {
                try {
                    dtEngDate = df.parse(strEnglishDate);
                    dtInitDate = df.parse(cvsIntializeDate.get("startengdate"));
                    Calendar EngDate = Calendar.getInstance();
                    Calendar InitDate = Calendar.getInstance();
                    EngDate.setTime(dtEngDate);
                    InitDate.setTime(dtInitDate);
                    lngDaysDiff = getDaysBetween(EngDate, InitDate);
                    if (lngDaysDiff == 0) {
                        return cvsIntializeDate.get("startengdate").replace(
                                "/", getSeperator());
                    } else {
                        tmpDaysDiff = lngDaysDiff;
                        while (tmpDaysDiff > 0) {
                            cvsDate.readHeaders();
                            while (cvsDate.readRecord()) {
                                for (intcounter = 1; intcounter <= 12; intcounter++) {
                                    if (tmpDaysDiff > Long.parseLong(cvsDate
                                            .get(intcounter))) {
                                        tmpDaysDiff = tmpDaysDiff
                                                - Long.parseLong(cvsDate
                                                        .get(intcounter));
                                    } else if (tmpDaysDiff == Long
                                            .parseLong(cvsDate.get(intcounter))) {
                                        if (intcounter > 11) {
                                            cvsDate.readRecord();
                                            return cvsDate.get(0)
                                                    + getSeperator() + "01"
                                                    + getSeperator() + "01";

                                        } else {
                                            return cvsDate.get(0)
                                                    + getSeperator()
                                                    + formatter
                                                            .format(intcounter + 1)
                                                    + getSeperator() + "01";
                                        }
                                    } else {
                                        return cvsDate.get(0)
                                                + getSeperator()
                                                + formatter.format(intcounter)
                                                + getSeperator()
                                                + formatter
                                                        .format(tmpDaysDiff + 1);
                                    }
                                }
                            }
                        }
                    }
                } catch (ParseException e) {
                    cvsDate.close();
                    cvsIntializeDate.close();
                    try {
                        readDateNepali.close();
                        readInitializeDate.close();
                    } catch (IOException y) {
                        // TODO Auto-generated catch block
                        y.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            cvsDate.close();
            cvsIntializeDate.close();
            try {
                readDateNepali.close();
                readInitializeDate.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;

    }

    public String nepaliDateAdd(long intervalue, String nepaliDateValue) {
        Date engDateValue, tmpDateValue;
        String resultDate;
        if (nepaliDateValue.isEmpty()) {
            return "";
        }
        Format formatter = new SimpleDateFormat("yyyy/MM/dd");

        Calendar cal = Calendar.getInstance();

        engDateValue = GetEnglishDate(nepaliDateValue);
        cal.setTime(engDateValue);
        cal.add(Calendar.DATE, (int) intervalue);
        tmpDateValue = cal.getTime();
        resultDate = GetNepaliDate(formatter.format(tmpDateValue));
        return resultDate;
    }

    public boolean CompareDate(String BigNepDate, String SmallNepDate) {
        Date NewDate1, NewDate2;
        NewDate1 = GetEnglishDate(BigNepDate.trim());
        NewDate2 = GetEnglishDate(SmallNepDate.trim());

        if (NewDate1.equals(NewDate2)) {
            return false;
        } else if (NewDate1.after(NewDate2)) {
            return true;
        } else {
            return false;
        }
    }

    public int GetWeekDay(String NepaliDate) {
        Date newDate;
        Calendar cal = Calendar.getInstance();

        newDate = GetEnglishDate(NepaliDate);
        cal.setTime(newDate);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public int GetFirstDayOfWeek(int NepYear, int NepMonth) {
        String NepDate;
        NepDate = Integer.toString(NepYear) + this.getSeperator()
                + Integer.toString(NepMonth) + this.getSeperator() + "01";
        return GetWeekDay(NepDate);
    }

    public String Today() {
        Date DtDate;
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        DtDate = cal.getTime();
        return GetNepaliDate(df.format(DtDate));
    }

    public int CurrentMonth() {
        String month;
        // System.out.println("CalendarFunction " + Today().toString());
        month = Today().substring(5, 7);
        // System.out.println("CalendarFunction " + month);
        return Integer.parseInt(month);
    }

    public int CurrentYear() {
        String year;
        year = Today().substring(0, 4);
        return Integer.parseInt(year);
    }

    public int CurrentDay() {
        String day;
        day = Today().substring(8, 10);
        return Integer.parseInt(day);
    }

    public boolean IsSaturday(String NepaliDate) {
        if (GetWeekDay(NepaliDate) == 7) {
            return true;
        } else {
            return false;
        }
    }

    public int DateDifference(String BigNepDate, String SmallNepDate,
            Diff difference) {
        int retValue = 0;
        Date BigDate, SmallDate;
        BigDate = GetEnglishDate(BigNepDate.trim());
        SmallDate = GetEnglishDate(SmallNepDate.trim());
        Calendar BC = Calendar.getInstance();
        Calendar SC = Calendar.getInstance();

        BC.setTime(BigDate);
        SC.setTime(SmallDate);

        if (difference == CalendarFunctions.Diff.YEAR) {
            retValue = getYearBetween(BigDate, SmallDate);
        } else if (difference == CalendarFunctions.Diff.MONTH) {
            retValue = getMonthsBetween(BigDate, SmallDate);
        } else if (difference == CalendarFunctions.Diff.DAY) {
            retValue = getDaysBetween(BC, SC);
        }

        return retValue;
    }

    public int NoOfDaysInMonth(int NepaliYear, int NepaliMonth) {
        ClassLoader CLDR = this.getClass().getClassLoader();

        BufferedReader readDateNepali = new BufferedReader(
                new InputStreamReader(
                        CLDR.getResourceAsStream("DateNepali.csv")));
        cvsDate = new CsvReader(readDateNepali);

        int DaysInMonth;
        if (NepaliMonth > 0 && NepaliMonth < 13) {
            try {
                cvsDate.readHeaders();
                while (cvsDate.readRecord()) {// year,baishak,jestha,ashad,shrawan,bhadra,aaswin,kartik,mangshir,poush,magh,falgun,chaitra,
                    if (Integer.parseInt(cvsDate.get("year")) == NepaliYear) {
                        DaysInMonth = Integer
                                .parseInt(cvsDate.get(NepaliMonth));
                        return DaysInMonth;

                    }
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return 0;
            } finally {
                cvsDate.close();
                try {
                    readDateNepali.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        } else {
            cvsDate.close();
            try {
                readDateNepali.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return 0;

        }
        return 0;
    }

    public int getNepYear(String sDate) {
        String sDates[] = sDate.split(getSeperator());
        return Integer.parseInt(sDates[0]);
    }

    public int getNepMonth(String sDate) {
        String sDates[] = sDate.split(getSeperator());
        return Integer.parseInt(sDates[1]);
    }

    public int getNepDay(String sDate) {
        String sDates[] = sDate.split(getSeperator());
        return Integer.parseInt(sDates[2]);
    }

    public String formatEngDate(Calendar eCal) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        // System.out.println("this formatEngDate "
        // + dateFormat.format(eCal.getTime()));
        return dateFormat.format(eCal.getTime());
    }

    public static String main(String date, String sep) {
        CalendarFunctions jcalfunction = new CalendarFunctions();
        // System.out.println("one "+jcalfunction.GetEnglishDate("2009Ã¯Â¿Â½12Ã¯Â¿Â½30"));
        jcalfunction.setSeperator(sep);
        String strDate = date;
        Calendar clnd = Calendar.getInstance();
        clnd.setTime(jcalfunction.GetEnglishDate(strDate));
        // System.out.println("English date for " + strDate + " is "
        // + jcalfunction.formatEngDate(clnd));
        // System.out.println("English date from new fxn for " + strDate +
        // " is "
        // + jcalfunction.GetFormattedEnglishDate(strDate));
        jcalfunction.formatEngDate("2014-02-01");
        // System.out.println(jcalfunction.getFormattedEnglishDate("Sat Feb 01 00:00:00 NPT 2014"));
        // System.out.println(jcalfunction.addMonthToEngDate("2014-02-01",
        // "plus", 5));
        return jcalfunction.formatEngDate(clnd);
    }

    public String addSubractDaysFrmEngDate(Date engDate, String operator,
            int days, String format) {
        Date dd = new Date();
        dd = engDate;
        String reqDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        int MILLIS_IN_DAY = 1000 * 60 * 60 * 24 * days;
        if (operator == "minus") {
            reqDate = dateFormat.format(dd.getTime() - MILLIS_IN_DAY);

        }
        if (operator == "plus") {
            reqDate = dateFormat.format(dd.getTime() + MILLIS_IN_DAY);
        }
        return reqDate;
    }

    public Date addSubractDaysFrmEngDate(Date engDate, String operator, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(engDate);
        if (operator == "minus") {
            cal.add(Calendar.DAY_OF_MONTH, -days);
        }
        if (operator == "plus") {
            cal.add(Calendar.DAY_OF_MONTH, days);
        }
        return cal.getTime();
    }

    public String getCurrentNepaliDate() {
        String strEnDt = "";
        String dt = "";
        try {
            SimpleDateFormat sdfSource = new SimpleDateFormat(
                    "EEE MMM dd hh:mm:ss z yyyy");
            Date curRawEngDate = new Date();
            strEnDt = curRawEngDate.toString();

            // System.out.println("Raw current Date:" + curRawEngDate);
            Date Fromdate = sdfSource.parse(strEnDt);

            SimpleDateFormat sdfDestination = new SimpleDateFormat("yyyy/MM/dd");
            strEnDt = sdfDestination.format(Fromdate);

            dt = this.GetNepaliDate(strEnDt);

        } catch (Exception e1) {

            e1.printStackTrace();

        }

        return dt;
    }

    public String getCurrentEnglishDateTime(String format) {
        if (format == "") {
            format = "yyyy/MM/dd HH:mm:ss";
        }
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public boolean IsEnSaturday(Date EnDate) {
        if (GetEnWeekDay(EnDate) == 7) {
            return true;
        } else {
            return false;
        }
    }

    public int GetEnWeekDay(Date EnDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(EnDate);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public String npDtToEnDt(String npClosingDt) {
        String strDt = "";
        try {

            SimpleDateFormat sdfSource = new SimpleDateFormat(
                    "EEE MMM dd hh:mm:ss z yyyy");
            Date curRawEngDate = GetEnglishDate(npClosingDt);
            strDt = curRawEngDate.toString();

            Date Fromdate = sdfSource.parse(strDt);
            // System.out.println("From Date:" + Fromdate);

            SimpleDateFormat sdfDestination = new SimpleDateFormat(
                    "yyyy/MM/dd HH:mm:ss");
            strDt = sdfDestination.format(Fromdate);

            // System.out.println("StrDt:" + strDt);
        } catch (Exception e1) {

            e1.printStackTrace();

        }
        return strDt;

    }

    public int getDaysBetween(String baseEnDate, String nextEnDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        int days = 0;

        try {
            Calendar baseC = Calendar.getInstance();
            Calendar nextC = Calendar.getInstance();

            baseC.setTime(sdf.parse(baseEnDate));
            nextC.setTime(sdf.parse(nextEnDate));

            days = baseC.get(java.util.Calendar.DAY_OF_YEAR)
                    - nextC.get(java.util.Calendar.DAY_OF_YEAR);
            int y2 = baseC.get(java.util.Calendar.YEAR);
            if (nextC.get(java.util.Calendar.YEAR) != y2) {
                nextC = (java.util.Calendar) nextC.clone();
                do {
                    days += nextC
                            .getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
                    nextC.add(java.util.Calendar.YEAR, 1);
                } while (nextC.get(java.util.Calendar.YEAR) != y2);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;
    }

    public boolean CompareDateEqual(String BigNepDate, String SmallNepDate) {
        Date NewDate1, NewDate2;
        NewDate1 = GetEnglishDate(BigNepDate);
        NewDate2 = GetEnglishDate(SmallNepDate);

        if (NewDate1.equals(NewDate2)) {
            return true;
        } else {
            return false;
        }
    }

    public int CurrentYearFor(String Date) {
        String year;
        year = Date.substring(0, 4);
        return Integer.parseInt(year);
    }

    // this functions returns the desired date in either nepali or english by
    // simpling passing the date parameter that we get from the date picker
    public String getParsedDateOfDatePicker(Date date, boolean rtnNep) {
        String reqdate = "";
        SimpleDateFormat sdfSource = new SimpleDateFormat(
                "EEE MMM dd hh:mm:ss z yyyy");
        String strDate = date.toString();
        Date Date = null;
        try {
            Date = sdfSource.parse(strDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SimpleDateFormat sdfDestination = new SimpleDateFormat("yyyy/MM/dd");
        strDate = sdfDestination.format(Date);

        if (rtnNep) {

            reqdate = this.GetNepaliDate(strDate);
        } else {
            reqdate = strDate;
        }

        return reqdate;
    }

    public String currentDate() {
        Calendar cal = new GregorianCalendar();

        // Get the components of the date
        // int era = cal.get(Calendar.ERA); // 0=BC, 1=AD
        int year = cal.get(Calendar.YEAR); // 2002
        int month = cal.get(Calendar.MONTH) + 1; // 0=Jan, 1=Feb, ...
        int day = cal.get(Calendar.DAY_OF_MONTH); // 1...
        // int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1=Sunday, 2=Monday,

        String mn = String.valueOf(month);
        String dy = String.valueOf(day);
        if (month < 10) {
            mn = "0" + month;
        }
        if (day < 10) {
            dy = "0" + day;
        }
        // ...
        String curDate = year + "-" + mn + "-" + dy;
        return curDate;
    }

    public int CurrentMonthFor(String Date) {
        String month;
        month = Date.substring(5, 7);
        return Integer.parseInt(month);
    }

    public int CurrentDayFor(String Date) {
        String day;
        day = Date.substring(8, 10);
        return Integer.parseInt(day);
    }

    /**
     * This function takes english unformatted date (EEE MMM dd hh:mm:ss z yyyy)
     * and return formatted date.
     *
     * @param engLishDate (Unformatted)
     * @return Formatted English date in this (yyyy/MM/dd) format.
     * @author Ganesh Joshi
     */
    public String getFormattedEnglishDate(String engLishDate) {
        Date frmtdDate = null;
        SimpleDateFormat sdfSource = new SimpleDateFormat(
                "EEE MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);

        SimpleDateFormat dt = new SimpleDateFormat("yyyy" + getSeperator()
                + "MM" + getSeperator() + "dd", Locale.ENGLISH);
        if (engLishDate.equals("")) {
            return "";
        }
        try {
            frmtdDate = sdfSource.parse(engLishDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // System.out.println("Formatted date "+dt.format(frmtdDate));
        return dt.format(frmtdDate);
    }

    public Date formatEngDate(String EngDate) {
        // System.out.println("Eng date" + EngDate);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        try {
            startDate = df.parse(EngDate);
            // System.out.println(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startDate;
    }

    public Date addMonthToEngDate(String EngDate, String sign, int month) {

        Calendar calNow = Calendar.getInstance();
        calNow.setTime(formatEngDate(EngDate));
        if (sign.equals("plus")) {
            calNow.add(Calendar.MONTH, month);
        } else if (sign.equals("minus")) {
            calNow.add(Calendar.MONTH, -month);
        }
        // fetching updated time
        Date dateBeforeAMonth = calNow.getTime();
        return dateBeforeAMonth;
    }

    public String getValidNepDate(int yearadd, String nepIssDtString) {
        String generatedNepDate = "";
        String day = nepIssDtString.substring(8, 10);
        // System.out.println("the day is"+day);
        String mnth = nepIssDtString.substring(5, 7);
        // System.out.println("the month is"+mnth);
        String yr = nepIssDtString.substring(0, 4);
        // System.out.println("the year is"+yr);
        if (Integer.valueOf(day) == 1) {
            if (Integer.valueOf(mnth) == 1) {
                int year = Integer.valueOf(yr);
                int month = 12;
                int days = NoOfDaysInMonth(year, month);
                generatedNepDate = year + "-" + month + "-" + days;
                // System.out.println("the generated nepali date is"+generatedNepDate);
            } else {
                int year = Integer.valueOf(yr) + yearadd;
                int month = Integer.valueOf(mnth) - 1;
                int days = NoOfDaysInMonth(year, month);
                generatedNepDate = year + "-" + month + "-" + days;
                // System.out.println("the generated nepali date is"+generatedNepDate);
            }
        } else {
            int year = Integer.valueOf(yr) + yearadd;
            int month = Integer.valueOf(mnth);
            if (month < 10) {
                mnth = "0" + month;
            } else {
                mnth = String.valueOf(month);
            }
            int days = Integer.valueOf(day) - 1;
            if (days < 10) {
                day = "0" + days;
            } else {
                day = String.valueOf(days);
            }
            generatedNepDate = year + "-" + mnth + "-" + day;
            // System.out.println("the generated nepali date is"+generatedNepDate);
        }

        return generatedNepDate;

    }

    public Date getValidEngDate(int yearAdd, Date datePassed) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date generatedDt = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(datePassed);
        cal.add(Calendar.YEAR, yearAdd);
        generatedDt = cal.getTime();
        String date = df.format(datePassed);
        String day = (date.substring(8, 10));
        String date1 = df.format(generatedDt);
        String day1 = (date1.substring(8, 10));
        if (Integer.valueOf(day1) == (Integer.valueOf(day) - 1)) {

        } else {
            cal.add(Calendar.DATE, -1);
            generatedDt = cal.getTime();
        }

        return generatedDt;

    }

    public Date addYeartoDate(Date EngDate, int year) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(EngDate);
        cal.add(Calendar.YEAR, year);
        Date generatedDt = cal.getTime();
        return generatedDt;
    }

    public String getMonthNameEng(int month) {
        switch (month) {
            case 1:
                return "January";
            case 2:
                return "February";

            case 3:
                return "March";

            case 4:
                return "April";

            case 5:
                return "May";

            case 6:
                return "June";

            case 7:
                return "July";

            case 8:
                return "August";

            case 9:
                return "September";

            case 10:
                return "October";

            case 11:
                return "November";

            case 12:
                return "December";

            default:
                break;
        }

        return "";
    }

    public int getSaturdayBetweenTwoDates(Date startDate, Date endDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        int saturday = 0;

        if (startCal.getTimeInMillis() >= endCal.getTimeInMillis()) {
            startCal.setTime(endDate);
            endCal.setTime(startDate);
        }

        do {
            if (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                ++saturday;
            }

            // Goto the next day.
            startCal.add(Calendar.DAY_OF_MONTH, 1);
        } while (startCal.getTimeInMillis() <= endCal.getTimeInMillis());

        return saturday;
    }

    public String getFullDateinNp(String nepaliDt) {
        String monthNep = "";
        String fullDateNp = "";
        int month = Integer.valueOf(nepaliDt.substring(5, 7));
        int year = Integer.valueOf(nepaliDt.substring(0, 4));
        int day = Integer.valueOf(nepaliDt.substring(8, 10));
        switch (month) {
            case 1:
                monthNep = "a};fv";
                break;
            case 2:
                monthNep = "h]i&";
                break;
            case 3:
                monthNep = "cfiff(";
                break;
            case 4:
                monthNep = ">fj0f";
                break;

            case 5:
                monthNep = "efb";
                break;

            case 6:
                monthNep = "cflZjg";
                break;

            case 7:
                monthNep = "sflt{s";
                break;

            case 8:
                monthNep = "d+l;/";
                break;
            case 9:
                monthNep = "kf}if";
                break;
            case 10:
                monthNep = "df#";
                break;
            case 11:
                monthNep = "KfmfNu'g";
                break;
            case 12:
                monthNep = "r}q";
                break;
            default:
                break;
        }
        fullDateNp = year + " " + monthNep + " " + day;
        return fullDateNp;
    }
}
