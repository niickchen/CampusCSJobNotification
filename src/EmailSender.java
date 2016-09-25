
import java.io.UnsupportedEncodingException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class EmailSender {

	private List<String> subscribers;
	static final String FROM = "services@nickxchen.com";
	static final String SMTP_USERNAME = "AKIAJPWICZVYWJ2LVRDQ";
	static final String SMTP_PASSWORD = "AnH+JECsBq9K7sD0vn8JQLfpPfUCia7NRTYmTARSwHnK"; // TD:
																						// Change
																						// to
																						// code
																						// generated
																						// SMTP
																						// auth
	static final String HOST = "email-smtp.us-west-2.amazonaws.com"; // the US
																		// West
																		// (Oregon)
																		// region
	static final int PORT = 25; // STARTTLS

	public EmailSender(List<String> subscribers) {
		this.subscribers = subscribers;
	}

	public void sendEmail(Map<String, String[]> jobMap)
			throws UnsupportedEncodingException, MessagingException {

		// final EmailAccount eml = getEmailAccount();

		Map<String, String[]> jobs = jobMap;

		// Setup mail server
		Properties properties = System.getProperties();
		properties.put("mail.transport.protocol", "smtps");
		properties.put("mail.smtp.host", "smtpout.secureserver.net");
		properties.put("mail.smtp.port", PORT);

		// The SMTP session will begin on an unencrypted connection, and then
		// the client
		// will issue a STARTTLS command to upgrade to an encrypted connection.
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.starttls.required", "true");

		// Get the Session object.
		Session session = Session.getDefaultInstance(properties);

		// Create a transport.
		Transport transport = session.getTransport();

		// Create a default MimeMessage object.
		MimeMessage message = new MimeMessage(session);

		// Set From: header field of the header.
		message.setFrom(
				new InternetAddress("services@nickxchen.com", "NXC Services"));

		// send to every subscriber
		for (String account : subscribers) {
			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO,
					new InternetAddress(account));
		}

		try {
			// Connect to Amazon SES using the SMTP username and password
			// specified above.
			transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD); // TD: HTML failure. If use Transport.send, speed is a problem.

			// send every post
			for (Map.Entry<String, String[]> entry : jobs.entrySet()) {
				String[] job = entry.getValue();

				// Set Subject: header field
				message.setSubject("New Job Posted: " + job[0]);

				message.setContent("<br/><h2>" + job[0] + "</h2><br/>" + "<p>"
						+ job[1] + "</p><br/>" + "<p><h4>Salary:</h4> " + job[2]
						+ "</p><br/>" + "<br/><p><h4>Link: </h4><a href=\""
						+ job[3] + "\">" + job[3] + "</a></p>", "text/html");
				// Send the email.
				transport.sendMessage(message, message.getAllRecipients());
				System.out.println("The email was sent successfully.");
			}

		} catch (MessagingException e) {
			System.out.println("The email was not sent.");
			System.out.println("Error message: " + e.getMessage());
		} finally {
			// Close and terminate the connection.
			transport.close();
		}

	}

}
