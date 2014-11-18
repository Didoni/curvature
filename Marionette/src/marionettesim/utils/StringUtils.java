/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package marionettesim.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 *
 * @author Asier
 */
public class StringUtils {
    private static StringUtils _instance = null;
    public static StringUtils get(){
        if(_instance == null){ //sync...
            _instance = new StringUtils();
        }
        return _instance;
    }
    
    private final DecimalFormat decimalFormat;
    private final DecimalFormat decimalFormat2;
    private StringUtils(){
        decimalFormat = new DecimalFormat("0.0000");
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        decimalFormat.setDecimalFormatSymbols(otherSymbols);
        decimalFormat2 = new DecimalFormat("0.00");
        decimalFormat2.setDecimalFormatSymbols(otherSymbols);
    }
    
    public String fourDecs(float f){
        return decimalFormat.format( f );
    }
    
    public String twoDecs(float f){
        return decimalFormat2.format( f );
    }
    
    public static String getBetween(String sourceString, String a, String b){
        int lA = a.length();
        int indexStart = sourceString.indexOf(a);
        int indexEnd;
        if (b != null){
            indexEnd = sourceString.indexOf(b, indexStart + lA);
        }else{
            indexEnd = sourceString.length();
        }
        if(indexStart != -1 && indexEnd != -1){
            return sourceString.substring(indexStart + lA, indexEnd);
        }
        return null;
    }
}
