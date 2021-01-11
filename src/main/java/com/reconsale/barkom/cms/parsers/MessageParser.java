package com.reconsale.barkom.cms.parsers;

public class MessageParser {

    public String getParentMessage(String category, String content) {
        String result = "";
        String[] arr = content.split("\n");
        Integer position = null;
        String cat = "";

        for (int i = 0; i < arr.length; i++) {
            if (arr[i].trim().equals(category)) {
                position = i;
                cat = arr[i];
                break;
            }
        }

        int length = cat.length() - cat.trim().length();

        if (position != null) {
            for (int j = position - 1; j >= 0; j--) {
                if (arr[j].trim().startsWith("-")){
                    continue;
                }
                if (arr[j].length() - arr[j].trim().length() < length){
                    result = arr[j];
                    break;
                }
            }
        }
        return result;
    }
}
