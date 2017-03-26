package ru.myocr.util;


import java.util.List;

import ru.myocr.api.ocr.Match;
import ru.myocr.api.ocr.ReceiptItemMatches;

public class OcrUtil {
    public static boolean isComparable(ReceiptItemMatches matches) {
        final List<Match> matchList = matches.getMatches();
        if (matchList.isEmpty()) {
            return false;
        }
        final Match bestMatch = matchList.get(0);
        final String source = matches.getSource();
        return (bestMatch.getScore() * 3) < source.length();
    }
}
