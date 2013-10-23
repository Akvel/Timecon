import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Ïðîãðàììà äëÿ ñàìîñòîÿòåëüíîãî ó÷åòà âðåìåíè ðàáîòû
 * 
 * ÏÐÎÃÐÀÌÌÀ ÏÎÑÒÀÂËßÅÒÑß ÏÎ ÏÐÈÍÖÈÏÓ "ÊÀÊ ÅÑÒÜ" ("AS IS"). ÍÈÊÀÊÈÕ ÃÀÐÀÍÒÈÉ ÍÅ ÏÐÈËÀÃÀÅÒÑß È ÍÅ ÏÐÅÄÓÑÌÀÒÐÈÂÀÅÒÑß. ÂÛ
 * ÈÑÏÎËÜÇÓÅÒÅ ÝÒÎ ÏÐÎÃÐÀÌÌÍÎÅ ÎÁÅÑÏÅ×ÅÍÈÅ ÍÀ ÑÂÎÉ ÑÒÐÀÕ È ÐÈÑÊ. ÀÂÒÎÐ ÍÅ ÁÓÄÅÒ ÎÒÂÅ×ÀÒÜ ÍÈ ÇÀ ÊÀÊÈÅ ÏÎÒÅÐÈ ÈËÈ
 * ÈÑÊÀÆÅÍÈß ÄÀÍÍÛÕ, ËÞÁÓÞ ÓÏÓÙÅÍÍÓÞ ÂÛÃÎÄÓ Â ÏÐÎÖÅÑÑÅ ÈÑÏÎËÜÇÎÂÀÍÈß ÈËÈ ÍÅÏÐÀÂÈËÜÍÎÃÎ ÈÑÏÎËÜÇÎÂÀÍÈß ÝÒÎÃÎ ÏÐÎÃÐÀÌÌÍÎÃÎ
 * ÎÁÅÑÏÅ×ÅÍÈß.
 * 
 * @author akvel
 * @version 2.0.0
 */
public class TimeconXML {
	private static final String NL = "\r\n";
	String OUT_FILE = "ev.txt";
	String IN_FILE_1 = "events_1.xml";
	String IN_FILE_2 = "events_2.xml";
	int NORMA_8_30 = 30600; // 8:30

	SimpleDateFormat sfEvents = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH:mm:ss");
	SimpleDateFormat sfTime = new SimpleDateFormat("HH:mm:ss");
	SimpleDateFormat ymd = new SimpleDateFormat("yyyy.MM.dd");
	SimpleDateFormat ym = new SimpleDateFormat("yyyy.MM");
	long sum;
	int daysCounter;
	
	TimeconXML(){
		sfEvents.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static void main(String[] args) throws Exception {
		new TimeconXML().parse();
		
	}
	
	
	public class SAXPars extends DefaultHandler { 
		List<Date> dtList = new ArrayList<Date>();
		private boolean isEvent;

		private Date TimeCreated;
		
		private String eventId;
		private boolean isEventId;
		private boolean isSubjectDomainName;
		private boolean isBadEvent;
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (qName.equals("Event")){
				isEvent = true;
				isBadEvent = false;
			}
			
			if (qName.equals("EventID")){
				isEventId = true;
			}
			
			if (qName.equals("Data") && "SubjectDomainName".equals(attributes.getValue("Name"))){
				isSubjectDomainName = true;
			}
			
			
			
			if (qName.equals("TimeCreated")){
				try {
					TimeCreated = sfEvents.parse(attributes.getValue("SystemTime"));
				} catch (ParseException e) {
					System.out.println("EventID=" + eventId);
					e.printStackTrace();
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equals("Event")){
				isEvent = false;
				
				if (!isBadEvent)
					dtList.add(TimeCreated);
			}
			
			if (qName.equals("EventID")){
				isEventId = false;
			}
			
			if (qName.equals("Data")){
				isSubjectDomainName = false;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (isEventId){
				eventId = new String(ch, start, length);
			}
			
			if (isSubjectDomainName){
				isBadEvent =  "NT AUTHORITY".equals(new String(ch, start, length));
			}
		}
		
		
		
	}

	
	
	
	private InputSource getFile(String name) throws FileNotFoundException{
		InputStream middle = new FileInputStream(name);
		String beginning = "<root>\n";
		String end = "\n</root>";
		List<InputStream> streams = Arrays.asList(
		    new ByteArrayInputStream(beginning.getBytes()),
		    middle,
		    new ByteArrayInputStream(end.getBytes()));
		InputStream story = new SequenceInputStream(Collections.enumeration(streams));
		
		Reader isr = new InputStreamReader(story);
		InputSource is = new InputSource();
		is.setCharacterStream(isr);
		is.setEncoding(Charset.defaultCharset().name());
		
		return is;
	}

	
	void parse() throws Exception {
		Writer out = new BufferedWriter(new FileWriter(OUT_FILE));
		
		SAXParserFactory factory = SAXParserFactory.newInstance(); 
		SAXParser parser = factory.newSAXParser(); 
		SAXPars saxp = new SAXPars(); 
		
		
		
		
		parser.parse(getFile(IN_FILE_1), saxp); 
		
		parser.parse(getFile(IN_FILE_2), saxp); 

		Date now = new Date();
		String ymNow = ym.format(now);
		String ymdNow = ymd.format(now);

		Date prev = null;
		Date pred2 = null;
		Date dt = null;

		
		String l;

		Collections.sort(saxp.dtList);
		for (Date date : saxp.dtList) {
			
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

		out.write("\t"); // first column for info (like !!!)

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
