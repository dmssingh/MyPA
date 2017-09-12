package com.dmss.mypa;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by LingaBhairavi on 7/25/2017.
 */

public final class ExpanseDto {
    public int Amount;
    public String PayMode;
    public String ExpenseDateString;
    public String Description;
    public Date ExpenseDate;
}

class ExpanseDtoDateComp implements Comparator<ExpanseDto>
{
    @Override
    public int compare(ExpanseDto entry1, ExpanseDto entry2){
        if(entry1.ExpenseDate.after(entry2.ExpenseDate)){
            return 1;
        }
        else {
            return -1;
        }
    }
}