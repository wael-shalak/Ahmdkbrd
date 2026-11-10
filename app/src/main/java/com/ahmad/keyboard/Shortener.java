package com.ahmad.keyboard;

import java.util.*;

/** Lebanese Arabizi shortening rules (same as the Ahmad Keyboard web page). */
public final class Shortener {
  private static final Map<String,String> DICT = new HashMap<>();
  private static final Set<String> EN = new HashSet<>(Arrays.asList(
      "hi hey hello bro bros sis dude man guys okay ok yes yeah no lol omg please thanks thank you your are what when where why how good great nice cool love miss come going home work today tomorrow tonight call later sorry happy party baby honey dear sweet coffee boss meeting the and but with from this that have just like see soon done wait".split(" ")));
  static {
    String[] p = {
      "kifak","kfk",
      "kifik","kfk",
      "kifkon","kfkn",
      "kifkun","kfkn",
      "tamem","tmm",
      "tmem","tmm",
      "mni7","mn7",
      "mni7a","mn7a",
      "mne7","mn7",
      "7elo","7lo",
      "7ilo","7lo",
      "7elwe","7lwe",
      "7ilwe","7lwe",
      "hala2","hl2",
      "halla2","hl2",
      "ba3den","b3dn",
      "ba3dein","b3dn",
      "ba3d","b3d",
      "inshallah","isa",
      "nshalla","isa",
      "inshalla","isa",
      "mashallah","mshl",
      "wallah","wlh",
      "walla","wlh",
      "yalla","yla",
      "yallah","yla",
      "habibi","7bb",
      "7abibi","7bb",
      "habibti","7bbt",
      "7abibte","7bbt",
      "7abibti","7bbt",
      "habibte","7bbt",
      "7abibe","7bb",
      "shu","sh",
      "chou","sh",
      "shou","sh",
      "wen","wn",
      "ween","wn",
      "leh","lh",
      "lesh","lsh",
      "lech","lsh",
      "emta","mta",
      "ade","2de",
      "adesh","2dsh",
      "3am","3m",
      "ma3lesh","m3lsh",
      "ma3leh","m3lh",
      "akid","akd",
      "akeed","akd",
      "bas","bs",
      "kteer","ktr",
      "ktir","ktr",
      "kter","ktr",
      "bukra","bkra",
      "bokra","bkra",
      "lyom","lym",
      "lyoum","lym",
      "mbere7","mbr7",
      "mbare7","mbr7",
      "hayde","hyd",
      "hayda","hyd",
      "haydol","hydl",
      "shukran","shkrn",
      "merci","mrc",
      "ahla","ahl",
      "ahlan","ahln",
      "marhaba","mr7ba",
      "mar7aba","mr7ba",
      "la2","l2",
      "khalas","5ls",
      "5alas","5ls",
      "khalset","5lst",
      "sa7","s7",
      "sa7i7","s77",
      "mazbout","mzbt",
      "mazboot","mzbt",
      "3anjad","3njd",
      "tayeb","tyb",
      "ya3ne","y3ne",
      "ya3ni","y3ne",
      "mesh","msh",
      "mish","msh",
      "ma3e","m3e",
      "ma3ak","m3k",
      "ma3ik","m3k",
      "ba3ref","b3rf",
      "ba3rif","b3rf",
      "mba3ref","mb3rf",
      "mb3rif","mb3rf",
      "baddak","bdk",
      "baddik","bdk",
      "badde","bde",
      "baddi","bde",
      "3ayzak","3yzk",
      "3ayzik","3yzk",
      "ta3a","t3a",
      "ta3o","t3o",
      "ta3e","t3e",
      "rou7","r7",
      "ro7","r7",
      "jeye","jye",
      "jeyi","jye",
      "shi","sh2",
      "shway","shwy",
      "shwayy","shwy",
      "ktiir","ktr",
      "2albe","2lbe",
      "albe","2lbe",
      "7ayete","7yte",
      "3omre","3mre",
      "omre","3mre",
      "3amel","3ml",
      "3amle","3mle",
      "3amil","3ml",
      "3ambe2ra","3mb2ra",
      "bi2oul","b2l",
      "baddo","bdo",
      "fiye","fy",
      "fini","fn",
      "fike","fk",
      "fik","fk",
      "ma3loum","m3lm",
      "ma3loume","m3lme",
      "sha8le","sh8l",
      "shighel","sh8l",
      "shoghol","sh8l",
      "beit","bt",
      "bayt","bt",
      "ahle","ahl",
      "ra7","r7",
      "7ada","7d",
      "wala","wl",
      "3ande","3nde",
      "3andak","3ndk",
      "3andik","3ndk",
      "hek","hk",
      "heik","hk",
      "kamen","kmn",
      "kaman","kmn",
      "3ade","3de",
      "3adi","3de",
      "sabe7","sb7",
      "masa","msa",
      "tisba7","tsb7",
      "2abel","2bl",
      "abel","2bl",
      "ba3ed","b3d",
      "mabsout","mbst",
      "ta3ban","t3bn",
      "ta3bene","t3bne",
      "nayem","nym",
      "fe2","f2",
      "sahran","shrn"
    };
    for (int i = 0; i + 1 < p.length; i += 2) DICT.put(p[i], p[i + 1]);
  }

  /** Returns the short form of one word, or the word unchanged. */
  public static String shorten(String w) {
    if (w == null || w.isEmpty()) return w;
    String low = w.toLowerCase(Locale.ROOT);
    String d = DICT.get(low);
    if (d != null) return keepCase(w, d);
    if (w.length() < 4 || EN.contains(low)) return w;
    // drop vowels after the first letter; keep a final e/a (feminine ending: 3amle -> 3mle)
    String core = low.substring(1, low.length() - 1).replaceAll("[aeiou]", "");
    String last = low.substring(low.length() - 1);
    if (last.matches("[iou]")) last = "";
    String out = (low.charAt(0) + core + last).replaceAll("(.)\\1+", "$1");
    return out.length() < w.length() ? keepCase(w, out) : w;
  }

  private static String keepCase(String src, String rep) {
    if (rep.isEmpty()) return rep;
    if (src.length() > 1 && src.equals(src.toUpperCase(Locale.ROOT)) && src.matches(".*[A-Za-z].*"))
      return rep.toUpperCase(Locale.ROOT);
    if (Character.isUpperCase(src.charAt(0)))
      return Character.toUpperCase(rep.charAt(0)) + rep.substring(1);
    return rep;
  }

  private Shortener() {}
}
