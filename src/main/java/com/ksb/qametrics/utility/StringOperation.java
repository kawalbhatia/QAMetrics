package com.ksb.qametrics.utility;

public class StringOperation {
	
	
	public static boolean containsConsecutiveWords(String text, String searchQuery, int numConsecutiveWords) {
        String[] searchWords = searchQuery.split(" ");
        String[] textWords = text.split(" ");

        // Loop through each set of numConsecutiveWords words in text
        for (int i = 0; i <= textWords.length - numConsecutiveWords; i++) {
            // Check if searchWords are a subset of the current set of numConsecutiveWords words in text
            boolean foundConsecutiveWords = true;
            for (int j = 0; j < numConsecutiveWords; j++) {
                if (!textWords[i + j].equals(searchWords[j])) {
                    foundConsecutiveWords = false;
                    break;
                }
            }
            if (foundConsecutiveWords) {
                return true;
            }
        }
        return false;
    }

}
