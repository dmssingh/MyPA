package com.dmss.mypa;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by LingaBhairavi on 7/25/2017.
 */

public final class ArtsOdcDto {
    public int id;
    public String ArtsOrOdc;
    public String SwipeInOrOut;
    public String SwipeDateString;
    public Date SwipeDate;

    public static final String ArtsEntry= "ARTS";
    public static final String OdcEntry= "ODC";
    public static final String InEntry= "IN";
    public static final String OutEntry= "OUT";
}

class ArtsOdcDtoDateComp implements Comparator<ArtsOdcDto>
{
    @Override
    public int compare(ArtsOdcDto entry1, ArtsOdcDto entry2){
        if(entry1.SwipeDate.after(entry2.SwipeDate)){
            return 1;
        }
        else {
            return -1;
        }
    }
}
