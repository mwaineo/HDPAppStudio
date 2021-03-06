package com.hortonworks.digitalemil.hdpappstudio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Properties;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

public class Setup {
	public static final String MARKER1 = "DO NOT REMOVE ME NEEDED FOR THE SOLRWORKSHOP ITEM1";
	public static final String MARKER2 = "DO NOT REMOVE ME NEEDED FOR THE SOLRWORKSHOP ITEM2";
	public static final String MARKER3 = "<MYMARKER1/>";
	public static final String MARKER4 = "/*MYMARKER2*/";
	public static final String MARKER5 = "bg.jpg";
	public static final String MARKER6 = "<MYTITLE>";
	public static final String MARKER7 = "MYMARKER7";
	public static final String APPSFOLDER = "apps";
	public static final String OTHERS = " indexed=\"true\" stored=\"true\" multiValued=\"false\"";
	static String currentDir = System.getProperty("user.dir");
	static private BufferedReader br;
	static private BufferedWriter bw;
	static private String line;
	public final static String HDP_VERSION= "2.2.0.0-2041";

	public final static int STARTFIELDS = 6;

	public static void main(String[] args) throws IOException,
			InterruptedException {
		Properties props = new Properties();
		String appname = args[0];
		String path = APPSFOLDER + "/" + appname + "/";
		BufferedReader br;
		BufferedWriter bw;
		String line;

	//	var d= new Date();
    //    var dt= d.getFullYear()+"-"+(d.getUTCMonth()+1)+"-"+d.getUTCDate()+"T"+d.getUTCHours()+":"+d.getUTCMinutes()+":"+(d.getUTCMilliseconds()/1000.0)+"Z";
	//	Calendar cal= Calendar.getInstance();
	//	String time= cal.get(Calendar.YEAR)+"-"+(new Integer(cal.get(Calendar.MONTH))+1)+"-"+cal.get(Calendar.DAY_OF_MONTH)+"T"+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+"."+(""+(new Integer(cal.get(Calendar.MILLISECOND))/1000.0f)).substring(2)+"Z";
//		System.out.println("Time: "+time);
		
//		System.exit(1);
		
		// Reading Properties file
		try {
			InputStream is = new FileInputStream(args[1]);
			props.load(is);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Props: "+props);
		
		String ip = "127.0.0.1";
		if (args.length > 2)
			ip = args[2];
		String solrurl = props.getProperty("solrurl");
		if (solrurl == null) {
			solrurl = "http://127.0.0.1:8983/solr/";
		}
		if (!solrurl.endsWith("/"))
			solrurl = solrurl + "/";

		String solrcore = props.getProperty("solrcore");
		if (solrcore == null) {
			solrcore = "hdpcore";
		}

		createBananaDashboard(appname, props, solrurl, solrcore);
	
		// Creating app destination folder
		// Extracting jar with classes
		Runtime.getRuntime().exec("mkdir -p " + path + "jar/WEB-INF").waitFor();
		try {
			Runtime.getRuntime()
					.exec("cp -fr com WEB-INF META-INF  listdata.html map.html icon32.png icon64.png  "
							+ currentDir + "/" + path + "jar").waitFor();
			Runtime.getRuntime()
					.exec("rm -f " + currentDir + "/" + path
							+ "jar/WEB-INF/web.xml").waitFor();
		} catch (Exception e) {

		}
		Runtime.getRuntime()
				.exec("/usr/lib/jvm/java-1.7.0-openjdk.x86_64/bin/jar xvf ../../../target/HDPAppStudio-"+HDP_VERSION+"-distribution.jar",
						new String[0],
						new File(currentDir + "/" + path + "jar")).waitFor();

		// Reading settings from properties
		String showLocation = props.getProperty("showLocation");
		if (showLocation == null) {
			showLocation = "true";
		}

		String hbasetable = props.getProperty("hbasetable");
		if (hbasetable == null) {
			hbasetable = "mytab";
		}

		String hbasecf = props.getProperty("hbasecf");
		if (hbasecf == null) {
			hbasecf = "all";
		}

		String hbaserootdir = props.getProperty("hbase.rootdir");
		if (hbaserootdir == null) {
			hbaserootdir = "hdfs://127.0.0.1:8020/apps/hbase/data/";
		}

		String topic = props.getProperty("topic");
		if (topic == null) {
			topic = "default";
		}

		String hivetable = props.getProperty("hivetable");
		if (hivetable == null) {
			hivetable = "HDPAppStudio";
		}

		String pivotfield = props.getProperty("pivotfield");
		if (pivotfield == null) {
			pivotfield = "";
		}

		String brokerlist = props.getProperty("broker");
		if (brokerlist == null) {
			brokerlist = "sandbox:6667";
		}

		String fields = "\"id location event_timestamp ";

		String zookeeperZnodeParent = props.getProperty("zparent");
		if (zookeeperZnodeParent == null) {
			zookeeperZnodeParent = "/hbase-unsecure";
		}

		String zookeeper = props.getProperty("zookeeper");
		if (zookeeper == null) {
			zookeeper = "127.0.0.1:2181";
		}

		String war = props.getProperty("createWAR");

		boolean createWAR = false;

		// If WAR file should be created, mkdir & extract jar
		if ("true".equals(war)) {
			createWAR = true;
			Runtime.getRuntime()
					.exec("mkdir -p " + path + "war/WEB-INF/classes/com")
					.waitFor();
			try {
				Runtime.getRuntime()
						.exec("cp -fr com WEB-INF META-INF  listdata.html map.html icon32.png icon64.png "
								+ currentDir + "/" + path + "jar").waitFor();
			} catch (Exception e) {

			}
			Runtime.getRuntime()
					.exec("/usr/lib/jvm/java-1.7.0-openjdk.x86_64/bin/jar xvf ../../../target/HDPAppStudio-"+HDP_VERSION+"-distribution.jar",
							new String[0],
							new File(currentDir + "/" + path + "war"))
					.waitFor();
			Runtime.getRuntime()
					.exec("mv " + path + "war/com " + path
							+ "war/WEB-INF/classes/").waitFor();
		}

		// Creating fields for STORM and ddl for HIVE
		String ddl = "\"Create External Table " + hivetable
				+ " (id String, location String, event_timestamp String, ";

		int i = STARTFIELDS;
		do {
			String name = props.getProperty("name_" + i);
			if (name == null)
				break;
			String type = props.getProperty("type_" + i);
			if (type == null)
				type = "String";
			if ("long".equals(type))
				type = "BigInt";
			if (i > STARTFIELDS)
				ddl = ddl + ", ";
			ddl = ddl + name + " " + type;
			fields = fields + name + " ";
			i++;
		} while (true);
		fields = fields + "\"";
		ddl = ddl
				+ ") ROW FORMAT DELIMITED FIELDS TERMINATED BY '|' STORED AS TEXTFILE LOCATION '/user/guest/hdpappstudio/hive/"
				+ hivetable + "';\"";

		createView(appname, path);

		setupSolrSchema(props, path, solrcore);

		setupBanana(solrurl, solrcore);

		createWebXml(path, topic, hbasetable, hbasecf, pivotfield, solrurl,
				solrcore, brokerlist);

		createIndexHTML(path, showLocation);

		createDataHTML(path, props, showLocation);

		createSolrCore(path, solrcore);

		// Create Ambari-View jar file
		Runtime.getRuntime()
				.exec("/usr/lib/jvm/java-1.7.0-openjdk.x86_64/bin/jar cf /var/lib/ambari-server/resources/views/"
						+ appname
						+ ".jar -C "
						+ currentDir
						+ "/"
						+ path
						+ "jar/ .").waitFor();

		// Create app specific start script
		createStartScript(appname, path, hbasetable, solrcore, solrurl, topic,
				brokerlist, fields, ddl, hivetable);

		// Build war file, first copy necessary files from ambari-view jar
		// folder to war folder
		if (createWAR) {
			buildWAR(appname, path);
		}

		System.out.println("Please find your app in " + path);
		System.out.println("Start it: sh ./" + path + "start.sh");
	}

	private static void createDataHTML(String path, Properties props,
			String showLocation) {
		try {
			br = new BufferedReader(new InputStreamReader(new Setup()
					.getClass().getResourceAsStream("/data.html")));
			bw = new BufferedWriter(new FileWriter(path + "jar/data.html"));

			while ((line = br.readLine()) != null) {

				if (line.contains(MARKER5)) {
					String imgpath = props.getProperty("bgimg");
					Runtime.getRuntime()
							.exec("cp " + imgpath + " " + path + "jar/bg.jpg")
							.waitFor();
				}
				if (line.contains(MARKER6)) {
					String title = props.getProperty("title");
					line = line.replaceFirst(MARKER6, title);
				}
				if (line.contains(MARKER7)) {
					line = line.replaceFirst(MARKER7, showLocation);
				}
				if (line.contains(MARKER3)) {
					int i = STARTFIELDS;
					do {
						String name = props.getProperty("name_" + i);
						if (name == null)
							break;
						bw.write("<tr><td>" + name
								+ "</td><td><input type=\"text\" name=\""
								+ name + "\" id=\"" + name
								+ "\" size=\"40\"/></td></tr>");
						i++;
					} while (true);
				} else {
					if (line.contains(MARKER4)) {
						int i = STARTFIELDS;
						bw.write("+'");
						do {
							String name = props.getProperty("name_" + i);
							if (name == null) {
								bw.write("'");
								break;
							}
							bw.write(", \"" + name
									+ "\":\"'+document.getElementById(\""
									+ name + "\").value+'\"");
							i++;
						} while (true);
					} else {
						bw.write(line + "\n");
					}
				}
			}
			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createIndexHTML(String path, String showLocation) {

		try {
			br = new BufferedReader(new InputStreamReader(new Setup()
					.getClass().getResourceAsStream("/srcindex.html")));
			bw = new BufferedWriter(new FileWriter(path + "jar/index.html"));

			while ((line = br.readLine()) != null) {
				if (line.contains("MYMARKER")) {
					if (showLocation.equals("false")) {
						line = "var showLocation= false;";
					} else {
						line = "var showLocation= true;";
					}
				}
				bw.write(line + "\n");
			}
			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createWebXml(String path, String topic,
			String hbasetable, String hbasecf, String pivotfield,
			String solrurl, String solrcore, String brokerlist) {
		try {
			br = new BufferedReader(new InputStreamReader(new Setup()
					.getClass().getResourceAsStream("/srcweb.xml")));
			bw = new BufferedWriter(
					new FileWriter(path + "jar/WEB-INF/web.xml"));

			while ((line = br.readLine()) != null) {
				if (line.contains("TOPIC")) {
					line = "<param-value>" + topic + "</param-value>";
				}
				if (line.contains("BROKERLIST")) {
					line = "<param-value>" + brokerlist + "</param-value>";
				}
				if (line.contains("HBASETABLE")) {
					line = "<param-value>" + hbasetable + "</param-value>";
				}
				if (line.contains("HBASECF")) {
					line = "<param-value>" + hbasecf + "</param-value>";
				}
				if (line.contains("SOLRURL")) {
					line = "<param-value>" + solrurl + solrcore
							+ "</param-value>";
				}
				if (line.contains("PIVOTFIELD")) {
					line = "<param-value>" + pivotfield + "</param-value>";
				}
				bw.write(line + "\n");
			}
			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void createBananaDashboard(String appname, Properties props,
			String solrurl, String solrcore) {
		JSONObject jobj = null;
		String facet = props.getProperty("facetfield");

		if (facet == null) {
			facet = props.getProperty("name_" + STARTFIELDS);
		}

		try {
			Runtime.getRuntime()
					.exec("mv banana/src/app/dashboards/default.json banana/src/app/dashboards/default.orig")
					.waitFor();

			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					"banana/src/app/dashboards/default.orig")));
			bw = new BufferedWriter(new FileWriter(
					"banana/src/app/dashboards/default.json"));

			StringBuffer buf = new StringBuffer();
			do {
				line = br.readLine();
				if (line == null)
					break;
				buf.append(line);
			} while (true);
			jobj = new JSONObject(buf.toString());
			JSONObject welcome = jobj.getJSONArray("rows").getJSONObject(0)
					.getJSONArray("panels").getJSONObject(0);
			String text = welcome.getString("content");
			text = text.substring(text.indexOf("To"));
			text = "Welcome to Banana for " + appname + "\n\n" + text;
			// System.out.println("Text: "+text);
			welcome.put("content", text);
			jobj.put("title", "Dashboard for " + appname);

			JSONObject search = jobj.getJSONArray("rows").getJSONObject(1)
					.getJSONArray("panels").getJSONObject(1);
			search.put("query", facet + ":*");
			search.getJSONArray("history").put(0, facet + ":*");
			
			JSONObject terms = jobj.getJSONArray("rows").getJSONObject(4)
					.getJSONArray("panels").getJSONObject(1);
			terms.put("title", facet);
			terms.put("field", facet);
			String query = terms.getJSONObject("queries").getString("query");

			query = query.replace("field=message", "field=" + facet);
			terms.getJSONObject("queries").put("query", query);
			jobj.getJSONArray("rows").getJSONObject(3).getJSONArray("panels")
					.getJSONObject(0).put("content", "");
			jobj.getJSONArray("rows").getJSONObject(3).put("collapse", "true");
			jobj.getJSONArray("rows").getJSONObject(1).getJSONArray("panels")
					.getJSONObject(0).put("timespan", "2d");
			jobj.getJSONObject("solr").put("server", solrurl);
			jobj.getJSONObject("solr").put("core_name", solrcore);
			JSONObject events= jobj.getJSONArray("rows").getJSONObject(5);
			events.getJSONArray("panels")
					.getJSONObject(0).getJSONArray("fields").put(0, facet);
			
			String f0= props.getProperty("name_"+STARTFIELDS);
			String f1= props.getProperty("name_"+(STARTFIELDS+1));
			String locationpanel= " {"
			          +" 'error': false,"
			          +" 'span': 6,"
			         +"  'editable': true,"
			         +"  'type': 'bettermap',"
			          +" 'loadingEditor': false,"
			         +"  'queries': {"
			          +"   'mode': 'all',"
			           +"  'ids': ["
			            +"   0"
			           +"  ],"
			           +"  'query': 'q=*&df=message&wt=json&rows=1000&fq=event_timestamp:[NOW/DAY-2DAY%20TO%20NOW/DAY%2B1DAY]&sort=event_timestamp desc',"
			           +"  'custom': ''"
			         +"  },"
			          +" 'size': 1000,"
			          +" 'spyable': true,"
			          +" 'lat_start': '',"
			          +" 'lat_end': '',"
			          +" 'lon_start': '',"
			          +" 'lon_end': '',"
			          +" 'field': 'location',"
			          +" 'title': 'Locations',"
			         +"  'tooltip': '"+f1+"'"
			        +" }";
			String showLocations= props.getProperty("showLocation");
			boolean locs= true;
			if("false".equals(showLocations)) {
				locs= false;
			}
			String newrow= " { "
			     +" 'title': 'More',"
			      +" 'height': '300px',"
			      +" 'editable': true,"
			      +" 'collapse': false,"
			      +" 'collapsable': true,"
			      +" 'panels': ["
			       +"  {"
			        +"   'error': false,"
			        +"   'span': 6,"
			         +"  'editable': true,"
			         +"  'type': 'heatmap',"
			         +"  'loadingEditor': false,"
			          +" 'queries': {"
			           +"  'mode': 'all',"
			            +" 'ids': ["
			             +"  0"
			           +"  ],"
			           +"  'query': 'q=*&df=message&fq=event_timestamp:[NOW/DAY-2DAY%20TO%20NOW/DAY%2B1DAY]&wt=json&rows=0&facet=true&facet.pivot="+f0+","+f1+"&facet.limit=300&facet.pivot.mincount=0',"
			           +"  'custom': ''"
			         +"  },"
			          +" 'size': 0,"
			          +" 'row_field': '"+f0+"',"
			          +" 'col_field': '"+f1+"',"
			          +" 'row_size': 300,"
			          +" 'editor_size': 300,"
			          +" 'color': 'red',"
			          +" 'spyable': true,"
			          +" 'transpose_show': true,"
			          +" 'transposed': false,"
			          +" 'title': 'Heatmap'"
			        +" },"
			        +(locs?(locationpanel):"")
			      +" ]"
			    +" },";

			JSONObject more= new JSONObject(newrow);
			jobj.getJSONArray("rows").put(5, more);
			jobj.getJSONArray("rows").put(6, events);
			
			bw.write(jobj.toString());
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void buildWAR(String appname, String path) {
		try {
			Runtime.getRuntime()
					.exec("rm -f " + path + "war/index.html " + path
							+ "war/data.html " + path
							+ "war/WEB-INF/classes/map.html " + path
							+ "war/WEB-INF/classes/listdata.html " + path
							+ "war/WEB-INF/web.xml " + path + "war/bg.jpg")
					.waitFor();

			Runtime.getRuntime()
					.exec("cp -f " + path + "jar/index.html " + path
							+ "war/index.html").waitFor();
			Runtime.getRuntime()
			.exec("cp -f " + path + "jar/bg.jpg " + path
					+ "war/bg.jpg").waitFor();
			Runtime.getRuntime()
					.exec("cp -f " + path + "jar/data.html " + path
							+ "war/data.html").waitFor();
			Runtime.getRuntime()
					.exec("cp -f " + path + "jar/map.html " + path
							+ "war/WEB-INF/classes/map.html").waitFor();
			Runtime.getRuntime()
					.exec("cp -f " + path + "jar/listdata.html " + path
							+ "war/WEB-INF/classes/listdata.html").waitFor();

			Runtime.getRuntime()
					.exec("cp -f " + path + "jar/WEB-INF/web.xml " + path
							+ "war/WEB-INF/web.xml").waitFor();
			Runtime.getRuntime()
					.exec("cp -f " + path + "jar/bg.jpg " + path + "war/bg.jpg")
					.waitFor();

			System.out.println("Creating WAR: "
					+ "/usr/lib/jvm/java-1.7.0-openjdk.x86_64/bin/jar cf "
					+ path + appname + ".war -C " + currentDir + "/" + path
					+ "war/ .");
			Runtime.getRuntime()
					.exec("/usr/lib/jvm/java-1.7.0-openjdk.x86_64/bin/jar cf "
							+ path + appname + ".war -C " + currentDir + "/"
							+ path + "war/ .").waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createSolrCore(String path, String solrcore) {
		try {
			// Copy default core files to "our" core
			Runtime.getRuntime()
					.exec("cp -fr /opt/solr/solr/hdp/solr/hdp1 /opt/solr/solr/hdp/solr/"
							+ solrcore).waitFor();

			// Copy our generated schema to destination
			Runtime.getRuntime()
					.exec("cp " + path + "schema.xml /opt/solr/solr/" + "hdp"
							+ "/solr/" + solrcore + "/conf/schema.xml")
					.waitFor();

			// Read solrconfig and modifiy it to store index-files on HDFS
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					"/opt/solr/solr/" + "hdp" + "/solr/" + solrcore
							+ "/conf/solrconfig.xml")));
			bw = new BufferedWriter(new FileWriter(path + "solrconfig.xml"));
			boolean inDirFac = false;
			while ((line = br.readLine()) != null) {
				if (line.contains("<lockType>")) {
					line = "<lockType>hdfs</lockType>";
				}
				if (line.contains("<directoryFactory")) {
					inDirFac = true;
				}
				if (line.contains("</directoryFactory>")) {
					inDirFac = false;
					bw.write("<directoryFactory name=\"DirectoryFactory\" class=\"solr.HdfsDirectoryFactory\">\n"
							+ "<str name=\"solr.hdfs.home\">hdfs://sandbox:8020/user/solr</str>\n"
							+ "<bool name=\"solr.hdfs.blockcache.enabled\">true</bool>\n"
							+ "<int name=\"solr.hdfs.blockcache.slab.count\">1</int>\n"
							+ "<bool name=\"solr.hdfs.blockcache.direct.memory.allocation\">true</bool>\n"
							+ "<int name=\"solr.hdfs.blockcache.blocksperbank\">16384</int>\n"
							+ "<bool name=\"solr.hdfs.blockcache.read.enabled\">true</bool>\n"
							+ "<bool name=\"solr.hdfs.blockcache.write.enabled\">true</bool>\n"
							+ "<bool name=\"solr.hdfs.nrtcachingdirectory.enable\">true</bool>\n"
							+ "<int name=\"solr.hdfs.nrtcachingdirectory.maxmergesizemb\">16</int>\n"
							+ "<int name=\"solr.hdfs.nrtcachingdirectory.maxcachedmb\">192</int>\n");
				}
				if (inDirFac) {
					line = "";
				}
				bw.write(line + "\n");
			}
			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Copy generated solrconfig to destination folder
		try {
			Runtime.getRuntime()
					.exec("cp " + path + "solrconfig.xml /opt/solr/solr/"
							+ "hdp" + "/solr/" + solrcore
							+ "/conf/solrconfig.xml").waitFor();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Set core name
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("/opt/solr/solr/hdp/solr/" + solrcore
							+ "/core.properties")));
			bw.write("name=" + solrcore + "\n");
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createStartScript(String appname, String path,
			String hbasetable, String solrcore, String solrurl, String topic,
			String brokerlist, String fields, String ddl, String hivetable) {
		try {
			br = new BufferedReader(new InputStreamReader(new Setup()
					.getClass().getResourceAsStream("/start.sh")));
			bw = new BufferedWriter(new FileWriter(path + "start.sh"));

			while ((line = br.readLine()) != null) {
				if (line.contains("export APPNAME")) {
					line = "export APPNAME=" + appname;
				}
				if (line.contains("export HBASETABLE")) {
					line = "export HBASETABLE=" + hbasetable;
				}
				if (line.contains("export SOLRCORE")) {
					line = "export SOLRCORE=" + solrcore;
				}
				if (line.contains("export SOLRURL")) {
					line = "export SOLRURL=" + solrurl;
				}
				if (line.contains("export TOPIC")) {
					line = "export TOPIC=" + topic;
				}
				if (line.contains("export BROKERLIST")) {
					line = "export BROKDERLIST=" + brokerlist;
				}
				if (line.contains("export FIELDS")) {
					line = "export FIELDS=" + fields;
				}
				if (line.contains("export DDL")) {
					line = "export DDL=" + ddl;
				}
				if (line.contains("export HIVETABLE")) {
					line = "export HIVETABLE=" + hivetable;
				}
				bw.write(line + "\n");
			}
			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createView(String appname, String path) {
		try {
			br = new BufferedReader(new InputStreamReader(new Setup()
					.getClass().getResourceAsStream("/view.xml")));
			bw = new BufferedWriter(new FileWriter(path + "jar/view.xml"));

			while ((line = br.readLine()) != null) {
				if (line.contains("<name>HDPAppStudio</name>")) {
					line = "<name>" + appname + "</name>";
				}
				if (line.contains("<label>HDPAppStudio</label>")) {
					line = "<label>" + appname + "</label>";
				}
				bw.write(line + "\n");
			}
			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setupSolrSchema(Properties props, String path,
			String solrcore) {
		//
		// Modifying Solr schema.xml
		// Adding defined fields and copyFields
		//
		try {
			br = new BufferedReader(new InputStreamReader(props.getClass()
					.getResourceAsStream("/schema.xml")));
			bw = new BufferedWriter(new FileWriter(path + "schema.xml"));

			while ((line = br.readLine()) != null) {
				if (line.contains(MARKER1)) {
					int i = 0;
					do {
						String name = props.getProperty("name_" + i);
						String type = props.getProperty("type_" + i);
						String others = props.getProperty("other_" + i);

						if (name == null)
							break;
						if (type == null)
							type = "string";
						if (others == null)
							others = OTHERS;
						System.out.println("adding: " + name);
						bw.write("<field name=\"" + name + "\" type=\"" + type
								+ "\" " + others + "/>\n");
						i++;
					} while (true);
				} else {
					if (line.contains(MARKER2)) {
						int i = STARTFIELDS - 1;
						do {
							String name = props.getProperty("name_" + i);
							if (name == null)
								break;
							bw.write("<copyField source=\"" + name
									+ "\" dest=\"text\"/>\n");
							i++;
						} while (true);
					} else {
						bw.write(line + "\n");
					}
				}
			}
			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void setupBanana(String solrurl, String solrcore) {
		// /
		// Modifying Banana
		// Pointing to real IP & core
		//
		line= "";
		
		String BANANA1 = "solr: \"http://localhost:8983/solr/\",";
		String BANANA2 = "solr_core: \"logstash_logs\"";
		String BANANA3 = "server\": \"http://localhost:8983/solr/\"";
		String BANANA4 = "core_name\": \"collection1\",";

		// src/config.js
		try {
			Runtime.getRuntime()
					.exec("mv banana/src/config.js banana/src/config.orig")
					.waitFor();

			br = new BufferedReader(new FileReader("banana/src/config.orig"));
			bw = new BufferedWriter(new FileWriter("banana/src/config.js"));

			while ((line = br.readLine()) != null) {
				if (line.contains(BANANA1)) {
					line = "solr: \"" + solrurl + "\",";
				}
				if (line.contains(BANANA2)) {
					line = "solr_core: \"" + solrcore + "\",";
				}
				bw.write(line + "\n");
			}
			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

// storm jar HDPAppStudioStormTopology-0.1.1-distribution.jar
// com.hortonworks.digitalemil.hdpappstudio.storm.Topology HDPAppStudio
// 127.0.0.1:2181 http://127.0.0.1:8983/solr/hdp/update/json?commit=true mytab
// all default id location foo bar