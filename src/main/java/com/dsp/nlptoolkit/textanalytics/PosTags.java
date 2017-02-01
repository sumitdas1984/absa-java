package com.dsp.nlptoolkit.textanalytics;

/**
 * Created by sumitd on 1/22/16.
 */
public class PosTags {

    public static boolean isNoun(String posTag) {

        boolean answ = false;
        if (posTag == null) {
            return answ;
        } else {
            answ = posTag.contains("NN");
        }

        return answ;
    }

    public static boolean isCommonNoun(String posTag) {

        boolean answ = false;
        if (posTag == null) {
            return answ;
        } else {
            answ = posTag.equals("NN");
        }

        return answ;
    }

    public static boolean isProperNoun(String posTag) {

        boolean answ = false;
        if (posTag == null) {
            return answ;
        } else {
            answ = posTag.equals("NNP");
        }

        return answ;
    }

    public static boolean isAdjective(String posTag) {

        boolean answ = false;
        if (posTag == null) {
            return answ;
        } else {
            answ = posTag.contains("JJ");
        }

        return answ;
    }

    public static boolean isComparativeAdjective(String posTag) {

        boolean answ = false;
        if (posTag == null) {
            return answ;
        } else {
            answ = posTag.equals("JJR");
        }

        return answ;
    }

    public static boolean isComparativeAdverb(String posTag) {

        boolean answ = false;
        if (posTag == null) {
            return answ;
        } else {
            answ = posTag.equals("RBR");
        }

        return answ;
    }

    public static boolean isVerb(String posTag) {

        boolean answ = false;
        if (posTag == null) {
            return answ;
        } else {
            answ = posTag.matches("(VB[A-Z]*)");
        }

        return answ;
    }

    public static boolean isMainVerb(String posTag) {

        boolean answ = false;
        if (posTag == null) {
            return answ;
        } else {
            answ = posTag.contains("VB");
        }

        return answ;
    }

    public static boolean isVerbBase(String posTag) {

        boolean answ = false;
        if (posTag == null) {
            return answ;
        } else {
            answ = posTag.equals("VB");
        }

        return answ;
    }

    public static boolean isPastVerb(String posTag) {

        boolean answ = false;
        if (posTag == null) {
            return answ;
        } else {
            answ = posTag.equals("VBD");
        }

        return answ;
    }

    public static boolean isAdverb(String posTag) {

        boolean answ = false;
        if (posTag == null) {
            return answ;
        } else {
            answ = posTag.contains("RB");
        }

        return answ;
    }

    public static boolean isPersonalPronoun(String posTag) {

        boolean answ = false;
        if (posTag == null) {
            return answ;
        } else {
            answ = posTag.contains("PRP");
        }

        return answ;
    }

    public static boolean isNumeral(String posTag) {

        boolean answ = false;
        if (posTag == null) {
            return answ;
        } else {
            answ = posTag.contains("CD");
        }

        return answ;
    }

    public static String getValidTag(String posTag) {

        String newTag = null;
        if (posTag == null) {
            return newTag;
        } else {
            newTag = (posTag.matches("VB[A-Z]*")) ? "verb" : newTag;
            newTag = (posTag.matches("NN[PS]*")) ? "noun" : newTag;
            newTag = (posTag.matches("JJ[RS]*")) ? "adjective" : newTag;
            newTag = (posTag.matches("RB[RS]*")) ? "adverb" : newTag;
            newTag = (posTag.matches("CC")) ? "conjunction" : newTag;
            newTag = (posTag.matches("PRP[\\$]*")) ? "pronoun" : newTag;
            newTag = (posTag.matches("UH")) ? "interjection" : newTag;
            newTag = (posTag.matches("(IN)|(TO)")) ? "preposition" : newTag;
            newTag = (posTag.matches("(MD)")) ? "modal_verb" : newTag;
        }

        return newTag;
    }

}
