import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.*;

/**
 * оПНЦПЮЛЛЮ ДКЪ ЯЮЛНЯРНЪРЕКЭМНЦН СВЕРЮ БПЕЛЕМХ ПЮАНРШ
 * 
 * опнцпюллю онярюбкъеряъ он опхмжхос "йюй еярэ" ("AS IS"). мхйюйху цюпюмрхи ме опхкюцюеряъ х ме опедсялюрпхбюеряъ. бш
 * хяонкэгсере щрн опнцпюллмне наеяоевемхе мю ябни ярпюу х пхяй. юбрнп ме асдер нрбевюрэ мх гю йюйхе онрепх хкх
 * хяйюфемхъ дюммшу, кчасч сосыеммсч бшцндс б опнжеяяе хяонкэгнбюмхъ хкх меопюбхкэмнцн хяонкэгнбюмхъ щрнцн опнцпюллмнцн
 * наеяоевемхъ.
 * 
 * @author akv
 * @version 1.1.0
 */
public class Timecon {
	private static final String NL = "\r\n";
	String OUT_FILE = "ev.txt";
	String IN_FILE_1 = "events_1.txt";
	String IN_FILE_2 = "events_2.txt";
	int NORMA_8_30 = 30600; // 8:30

	SimpleDateFormat sfEvents = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH:mm:ss");
	SimpleDateFormat sfTime = new SimpleDateFormat("HH:mm:ss");
	SimpleDateFormat ymd = new SimpleDateFormat("yyyy.MM.dd");
	SimpleDateFormat ym = new SimpleDateFormat("yyyy.MM");
	long sum;
	int daysCounter;

	public static void main(String[] args) throws Exception {
		new Timecon().parse();
	}

	void parse() throws Exception {
		Writer out = new BufferedWriter(new FileWriter(OUT_FILE));
		BufferedReader in1 = new BufferedReader(new FileReader(IN_FILE_1));
		BufferedReader in2 = new BufferedReader(new FileReader(IN_FILE_2));

		Date now = new Date();
		String ymNow = ym.format(now);
		String ymdNow = ymd.format(now);

		Date prev = null;
		Date pred2 = null;
		Date dt = null;

		List<Date> dtList = new ArrayList<Date>();

		String l;
		Pattern p = Pattern.compile("\\s*Date:\\s(\\d+-\\d+-\\d+T\\d+:\\d+:\\d+).*");
		while ((l = in1.readLine()) != null) {
			Matcher m = p.matcher(l);
			if (m.find()) {
				dtList.add(sfEvents.parse(m.group(1)));
			}
		}

		while ((l = in2.readLine()) != null) {
			Matcher m = p.matcher(l);
			if (m.find()) {
				dtList.add(sfEvents.parse(m.group(1)));
			}
		}

		Collections.sort(dtList);
		for (Date date : dtList) {
			dt = date;
			if (!ymNow.equals(ym.format(dt))){
				if (prev != null && ymdNow.equals(ymd.format(prev))) {
					print(out, prev, now);
					prev = null;
				}
				continue;
			}
			if (prev != null && ymd.format(dt).equals(ymd.format(prev))) {
				pred2 = dt;
				continue;
			}
			if (prev != null) {
				print(out, prev, pred2);
			}
			prev = dt;
		}

		if (ymdNow.equals(ymd.format(dt))) {
			print(out, prev, now);
		}
		out.write(NL + NL);
        out.write("\tTotal overworked time:\t" + pt(durr) + NL);
		out.write("\tTotal worked time:\t" + pt(sum) + NL);
		out.write("\tDays:\t" + daysCounter + " Avarage:\t" + pt(sum / daysCounter) + NL);


		in1.close();
		in2.close();
		out.close();
		System.out.println("Done");
	}



    private long durr = 0;
    private Calendar calDay = Calendar.getInstance();


	void print(Writer out, Date p, Date p2) throws Exception {
		if (p2 == null || p.getTime() >= p2.getTime() || !ymd.format(p2).equals(ymd.format(p))) {
			System.out.println(p2 + "-" + p);
			out.write(String.format("dt:%s only one date %s\r\n", ymd.format(p),sfTime.format(p)));
			return;
		}

		long dur = (p2.getTime() - p.getTime()) / 1000;
		sum += dur;

		calDay.setTime(p);
		if (calDay.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && calDay.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
			daysCounter++;
		else out.write("!!!");

		out.write("\t"); //akv first column for info (like !!!)

		durr += dur - NORMA_8_30;
		out.write(String.format("dt:%s;\t%s-%s;\t%s;\t%s\r\n", ymd.format(p), sfTime.format(p), sfTime.format(p2),
				pt(dur), pt(dur - NORMA_8_30)));
	}

	String pt(long d) { // print time
		String pref = "";
		if (d < 0) {
			d *= -1;
			pref = "-";
		}
		return String.format("%s%s:%s", pref, fi(d / 3600), fi(d % 3600 / 60));
	}

	String fi(long i) { // format int
		return i > 9 ? "" + i : "0" + i;
	}
}
