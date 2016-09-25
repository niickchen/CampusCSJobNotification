import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.MessagingException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JobSeeker {
	public static void main(String[] args)
			throws IOException, SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, MessagingException {
		
		System.out.println("Connecting to Job Center...");
		Document docNonUW = Jsoup
				.connect(
						"https://jobcenter.wisc.edu/jobs/categoryBrowse/Computers/0/")
				.get();
		Document docUW = Jsoup
				.connect(
						"https://jobcenter.wisc.edu/jobs/categoryBrowse/Computers/1/")
				.get();
		System.out.println("Connected.");

		Element nonUWJobParent = docNonUW.select("tbody").get(0);
		Element UWJobParent = docUW.select("tbody").get(0);

		Map<String, String[]> jobMap = new HashMap<String, String[]>();

		Elements nonUWJobs = nonUWJobParent.children();
		Elements UWJobs = UWJobParent.children();

		String jobNum;
		String[] job;
		String jobDescription;
		String jobTitle;
		Document jobDetailsPage;
		Element jobDetailsBody;

		List<String> subscribers;

		final String db_ip = "52.38.140.193";
		final String db_name = "alljobs";
		final String db_user = "nickdbmaster";
		final String db_pass = "X94-LEu-Cxu-7fU";

		// TD: get more info attributes from website
		// TD: add text version description

		// connect db
		System.out.println("Connecting to database...");
		JobDB db = new JobDB(db_ip, db_name, db_user, db_pass);
		int con = db.connect();
		System.out.println("Connected.");

		for (int i = 1; i < nonUWJobs.size(); i++) {
			jobNum = nonUWJobs.get(i).child(0).text().substring(0, 5); // job ID

			if (jobMap.containsKey(jobNum))
				continue;
			else {

				// add jobs
				jobDetailsPage = Jsoup.connect(
						"https://jobcenter.wisc.edu/jobs/detail/" + jobNum)
						.get();
				
				jobDetailsBody = jobDetailsPage.select("tbody").get(0);
				
//				jobDescription = jobDetailsPage
//						.getElementsContainingOwnText("Description:").parents()
//						.get(0).text().replace("\u00a0","");
				jobDescription = jobDetailsPage
						.getElementsContainingOwnText("Description:").parents()
						.get(0).toString();
				
				jobTitle = jobDetailsBody.child(0).child(0).text();
				
				job = new String[4];
				job[0] = jobTitle; // job title
				job[2] = nonUWJobs.get(i).child(2).text(); // salary
				job[1] = jobDescription; // job description
				job[3] = "https://jobcenter.wisc.edu/jobs/detail/" + jobNum; // job
																				// link
				
				
				// TD: check edit time, only when edit time > last software
				// running time, put into jobMap
				jobMap.put(jobNum, job); // put into map

				// put into db
				// TD: check edit time, only when edit time > last software
				// running time, put into db
				if (con == 1) {
					db.insert("csjobs", jobNum, job[0], "nonUW", job[2],
							job[1]);
					// System.out.println("Added");
				}
			}
		}

		for (int i = 1; i < UWJobs.size(); i++) {
			jobNum = UWJobs.get(i).child(0).text().substring(0, 5); // job ID

			if (jobMap.containsKey(jobNum))
				continue;
			else {
				jobDetailsPage = Jsoup.connect(
						"https://jobcenter.wisc.edu/jobs/detail/" + jobNum)
						.get();
				
				jobDetailsBody = jobDetailsPage.select("tbody").get(0);
				
				jobDescription = jobDetailsPage
						.getElementsContainingOwnText("Description:").parents()
						.get(0).toString();

				jobTitle = jobDetailsBody.child(0).child(0).text();
				
				job = new String[4];
				job[0] = jobTitle; // job title
				job[2] = UWJobs.get(i).child(2).text(); // salary
				job[1] = jobDescription; // job description
				job[3] = "https://jobcenter.wisc.edu/jobs/detail/" + jobNum; // job
																				// link
				
				
				// TD: check edit time, only when edit time > last software
				// running time, put into jobMap
				jobMap.put(jobNum, job); // put into map

				// put into db
				// TD: check edit time, only when edit time > last software
				// running time, put into db
				if (con == 1) {
					db.insert("csjobs", jobNum, job[0], "UW", job[2], job[1]);
				}

			}
		}

		// send emails to subscribers
		if (con == 1) {
			// get subscribers
			subscribers = db.selectOneColumn("subscribers", "Email");
			EmailSender es = new EmailSender(subscribers);

			if (jobMap.size() == 0) { // exit if no new posts
				db.close();
				System.exit(0);
			}

			System.out.println("Sending emails...");
			es.sendEmail(jobMap);
		}

		db.close();

	}
}
